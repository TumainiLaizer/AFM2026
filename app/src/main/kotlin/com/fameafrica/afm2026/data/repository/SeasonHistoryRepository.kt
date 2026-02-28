package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.AllTimeTeamStats
import com.fameafrica.afm2026.data.database.dao.SeasonHistoryDao
import com.fameafrica.afm2026.data.database.dao.LeagueStandingsDao
import com.fameafrica.afm2026.data.database.dao.SeasonStats
import com.fameafrica.afm2026.data.database.dao.TeamsDao
import com.fameafrica.afm2026.data.database.entities.SeasonHistoryEntity
import com.fameafrica.afm2026.data.database.entities.LeagueStandingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeasonHistoryRepository @Inject constructor(
    private val seasonHistoryDao: SeasonHistoryDao,
    private val leagueStandingsDao: LeagueStandingsDao,
    private val teamsDao: TeamsDao
) {

    // ============ BASIC CRUD ============

    fun getAllHistory(): Flow<List<SeasonHistoryEntity>> = seasonHistoryDao.getAll()

    suspend fun getHistoryById(id: Int): SeasonHistoryEntity? = seasonHistoryDao.getById(id)

    suspend fun getTeamSeason(teamName: String, season: String): SeasonHistoryEntity? =
        seasonHistoryDao.getTeamSeason(teamName, season)

    suspend fun insertHistory(history: SeasonHistoryEntity) = seasonHistoryDao.insert(history)

    suspend fun updateHistory(history: SeasonHistoryEntity) = seasonHistoryDao.update(history)

    suspend fun deleteHistory(history: SeasonHistoryEntity) = seasonHistoryDao.delete(history)

    // ============ END OF SEASON PROCESSING ============

    /**
     * Archive a completed season for all teams
     * Called at the end of each season
     */
    suspend fun archiveSeason(
        season: String,
        leagueName: String,
        finalStandings: List<LeagueStandingsEntity>,
        cupWinners: Map<String, String> = emptyMap(),
        continentalQualifiers: List<String> = emptyList()
    ): List<SeasonHistoryEntity> {

        val histories = mutableListOf<SeasonHistoryEntity>()

        for (standing in finalStandings) {
            val team = teamsDao.getByName(standing.teamName) ?: continue

            // Calculate trophies won
            var trophiesWon = 0
            var leagueTitles = 0
            var cupTitles = 0
            var continentalTitles = 0

            if (standing.position == 1) {
                trophiesWon++
                leagueTitles++
            }

            // Check cup wins
            cupWinners.forEach { (cupName, winner) ->
                if (winner == standing.teamName) {
                    trophiesWon++
                    cupTitles++
                }
            }

            // Check promotion/relegation
            val promoted = standing.position <= 2 && leagueName.contains("Championship")
            val relegated = standing.position >= 16 && leagueName.contains("Premier")

            // Check continental qualification
            val qualifiedForContinental = standing.position <= 2 || standing.teamName in continentalQualifiers

            val history = SeasonHistoryEntity(
                season = season,
                teamName = standing.teamName,
                teamId = team.id,
                leagueName = leagueName,
                position = standing.position,
                points = standing.points,
                wins = standing.wins,
                draws = standing.draws,
                losses = standing.losses,
                goalsFor = standing.goalsScored,
                goalsAgainst = standing.goalsConceded,
                goalDifference = standing.goalDifference,
                trophiesWon = trophiesWon,
                leagueTitles = leagueTitles,
                cupTitles = cupTitles,
                continentalTitles = continentalTitles,
                promoted = promoted,
                relegated = relegated,
                qualifiedForContinental = qualifiedForContinental
            )

            seasonHistoryDao.insert(history)
            histories.add(history)
        }

        return histories
    }

    /**
     * Get complete season summary
     */
    suspend fun getSeasonSummary(season: String): SeasonSummary {
        val standings = seasonHistoryDao.getSeasonStandings(season).firstOrNull() ?: emptyList()
        val champion = standings.firstOrNull { it.position == 1 }
        val promotions = standings.filter { it.promoted }
        val relegations = standings.filter { it.relegated }

        return SeasonSummary(
            season = season,
            champion = champion,
            promotions = promotions,
            relegations = relegations,
            totalTeams = standings.size,
            averageGoals = standings.mapNotNull { it.goalsFor }.average()
        )
    }

    // ============ TEAM HISTORY ============

    fun getTeamHistory(teamName: String): Flow<List<SeasonHistoryEntity>> =
        seasonHistoryDao.getTeamHistory(teamName)

    suspend fun getTeamTitleCount(teamName: String): Int =
        seasonHistoryDao.getTeamTitleCount(teamName)

    fun getTeamTitles(teamName: String): Flow<List<SeasonHistoryEntity>> =
        seasonHistoryDao.getTeamTitles(teamName)

    // ============ STATISTICS ============

    fun getAllTimeTeamStats(): Flow<List<AllTimeTeamStats>> =
        seasonHistoryDao.getAllTimeTeamStats()

    fun getSeasonStatistics(): Flow<List<SeasonStats>> =
        seasonHistoryDao.getSeasonStatistics()

    fun getSeasons(): Flow<List<String>> = seasonHistoryDao.getSeasons()

    // ============ DASHBOARD ============

    suspend fun getTeamHistoryDashboard(teamName: String): TeamHistoryDashboard {
        val history = seasonHistoryDao.getTeamHistory(teamName).firstOrNull() ?: emptyList()
        val titles = history.filter { it.position == 1 }
        val promotions = history.filter { it.promoted }
        val relegations = history.filter { it.relegated }

        val bestSeason = history.minByOrNull { it.position ?: 999 }
        val worstSeason = history.maxByOrNull { it.position ?: 0 }

        val totalTrophies = history.sumOf { it.trophiesWon }

        return TeamHistoryDashboard(
            teamName = teamName,
            seasons = history.size,
            titles = titles.size,
            promotions = promotions.size,
            relegations = relegations.size,
            totalTrophies = totalTrophies,
            bestSeason = bestSeason,
            worstSeason = worstSeason,
            history = history
        )
    }
}

// ============ DATA CLASSES ============

data class SeasonSummary(
    val season: String,
    val champion: SeasonHistoryEntity?,
    val promotions: List<SeasonHistoryEntity>,
    val relegations: List<SeasonHistoryEntity>,
    val totalTeams: Int,
    val averageGoals: Double
)

data class TeamHistoryDashboard(
    val teamName: String,
    val seasons: Int,
    val titles: Int,
    val promotions: Int,
    val relegations: Int,
    val totalTrophies: Int,
    val bestSeason: SeasonHistoryEntity?,
    val worstSeason: SeasonHistoryEntity?,
    val history: List<SeasonHistoryEntity>
)