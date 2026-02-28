package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.FullStandingEntry
import com.fameafrica.afm2026.data.database.dao.LeagueStandingsDao
import com.fameafrica.afm2026.data.database.dao.LeagueTrend
import com.fameafrica.afm2026.data.database.dao.TeamAllTimeStats
import com.fameafrica.afm2026.data.database.dao.TeamStandingWithDetails
import com.fameafrica.afm2026.data.database.entities.LeagueStandingsEntity
import com.fameafrica.afm2026.data.database.entities.FixturesResultsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeagueStandingsRepository @Inject constructor(
    private val leagueStandingsDao: LeagueStandingsDao
) {

    // ============ BASIC CRUD ============

    fun getAllStandings(): Flow<List<LeagueStandingsEntity>> = leagueStandingsDao.getAll()

    suspend fun getStandingById(id: Int): LeagueStandingsEntity? = leagueStandingsDao.getById(id)

    suspend fun getTeamStanding(teamName: String, leagueName: String, seasonYear: Int): LeagueStandingsEntity? =
        leagueStandingsDao.getTeamStanding(teamName, leagueName, seasonYear)

    suspend fun insertStanding(standing: LeagueStandingsEntity) = leagueStandingsDao.insert(standing)

    suspend fun insertAllStandings(standings: List<LeagueStandingsEntity>) = leagueStandingsDao.insertAll(standings)

    suspend fun updateStanding(standing: LeagueStandingsEntity) = leagueStandingsDao.update(standing)

    suspend fun deleteStanding(standing: LeagueStandingsEntity) = leagueStandingsDao.delete(standing)

    suspend fun deleteByLeagueAndSeason(leagueName: String, seasonYear: Int) =
        leagueStandingsDao.deleteByLeagueAndSeason(leagueName, seasonYear)

    suspend fun getTeamCount(leagueName: String, seasonYear: Int): Int =
        leagueStandingsDao.getTeamCount(leagueName, seasonYear)

    // ============ STANDINGS QUERIES ============

    fun getStandings(leagueName: String, seasonYear: Int): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getStandings(leagueName, seasonYear)

    fun getStandingsByPosition(leagueName: String, seasonYear: Int): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getStandingsByPosition(leagueName, seasonYear)

    fun getTopN(leagueName: String, seasonYear: Int, topN: Int): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getTopN(leagueName, seasonYear, topN)

    fun getBottomN(leagueName: String, seasonYear: Int, bottomStart: Int): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getBottomN(leagueName, seasonYear, bottomStart)

    suspend fun getTeamPosition(leagueName: String, seasonYear: Int, teamName: String): LeagueStandingsEntity? =
        leagueStandingsDao.getTeamPosition(leagueName, seasonYear, teamName)

    suspend fun getLeagueLeader(leagueName: String, seasonYear: Int): LeagueStandingsEntity? =
        leagueStandingsDao.getLeagueLeader(leagueName, seasonYear)

    suspend fun getChampion(leagueName: String, seasonYear: Int): LeagueStandingsEntity? =
        leagueStandingsDao.getChampion(leagueName, seasonYear)

    // ============ TEAM HISTORY ============

    fun getTeamHistory(teamName: String): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getTeamHistory(teamName)

    fun getTeamTitles(teamName: String): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getTeamTitles(teamName)

    suspend fun getTeamTitleCount(teamName: String): Int =
        leagueStandingsDao.getTeamTitleCount(teamName)

    // ============ STATISTICS ============

    suspend fun getLeagueStatistics(leagueName: String, seasonYear: Int): LeagueStatistics? =
        leagueStandingsDao.getLeagueStatistics(leagueName, seasonYear) as LeagueStatistics?

    suspend fun getTeamAllTimeStats(teamName: String): TeamAllTimeStats? =
        leagueStandingsDao.getTeamAllTimeStats(teamName)

    fun getLeagueTrends(leagueName: String): Flow<List<LeagueTrend>> =
        leagueStandingsDao.getLeagueTrends(leagueName)

    // ============ FORM QUERIES ============

    fun getTeamsOnWinningStreak(leagueName: String, seasonYear: Int): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getTeamsOnWinningStreak(leagueName, seasonYear)

    fun getTeamsOnLosingStreak(leagueName: String, seasonYear: Int): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getTeamsOnLosingStreak(leagueName, seasonYear)

    fun getTeamsOnDrawingStreak(leagueName: String, seasonYear: Int): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getTeamsOnDrawingStreak(leagueName, seasonYear)

    fun getUndefeatedTeams(leagueName: String, seasonYear: Int): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getUndefeatedTeams(leagueName, seasonYear)

    fun getWinlessTeams(leagueName: String, seasonYear: Int): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getWinlessTeams(leagueName, seasonYear)

    // ============ PROMOTION/RELEGATION ============

    fun getPromotionSpots(leagueName: String, seasonYear: Int): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getPromotionSpots(leagueName, seasonYear)

    fun getPlayoffSpots(leagueName: String, seasonYear: Int): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getPlayoffSpots(leagueName, seasonYear)

    fun getRelegationSpots(leagueName: String, seasonYear: Int): Flow<List<LeagueStandingsEntity>> =
        leagueStandingsDao.getRelegationSpots(leagueName, seasonYear)

    // ============ JOIN QUERIES ============

    fun getFullStandings(leagueName: String, seasonYear: Int): Flow<List<FullStandingEntry>> =
        leagueStandingsDao.getFullStandings(leagueName, seasonYear)

    suspend fun getTeamStandingWithDetails(teamName: String, seasonYear: Int): TeamStandingWithDetails? =
        leagueStandingsDao.getTeamStandingWithDetails(teamName, seasonYear)

    // ============ STANDINGS MANAGEMENT ============

    /**
     * Initialize league standings for a new season
     */
    suspend fun initializeLeagueStandings(
        leagueName: String,
        seasonYear: Int,
        teamNames: List<String>
    ): List<LeagueStandingsEntity> {
        val standings = teamNames.mapIndexed { index, teamName ->
            LeagueStandingsEntity(
                leagueName = leagueName,
                seasonYear = seasonYear,
                position = index + 1,  // Initial alphabetical order
                teamName = teamName,
                matchesPlayed = 0,
                wins = 0,
                draws = 0,
                losses = 0,
                goalsScored = 0,
                goalsConceded = 0,
                goalDifference = 0,
                points = 0,
                form = null
            )
        }

        insertAllStandings(standings)
        return standings
    }

    /**
     * Update standings after a match result
     */
    suspend fun updateStandingsAfterMatch(result: FixturesResultsEntity) {
        // Only process league matches
        if (result.leagueName == null) return

        val seasonYear = result.season.split("/").first().toInt()

        // Update home team
        val homeStanding = leagueStandingsDao.getTeamStanding(
            teamName = result.homeTeam,
            leagueName = result.leagueName,
            seasonYear = seasonYear
        )

        homeStanding?.let { standing ->
            val isWin = result.homeTeamWin
            val isDraw = result.isDraw
            val isLoss = result.awayTeamWin

            val updatedStanding = standing.updateFromMatchResult(
                goalsFor = result.homeScore,
                goalsAgainst = result.awayScore,
                isWin = isWin,
                isDraw = isDraw,
                isLoss = isLoss
            )
            leagueStandingsDao.update(updatedStanding)
        }

        // Update away team
        val awayStanding = leagueStandingsDao.getTeamStanding(
            teamName = result.awayTeam,
            leagueName = result.leagueName,
            seasonYear = seasonYear
        )

        awayStanding?.let { standing ->
            val isWin = result.awayTeamWin
            val isDraw = result.isDraw
            val isLoss = result.homeTeamWin

            val updatedStanding = standing.updateFromMatchResult(
                goalsFor = result.awayScore,
                goalsAgainst = result.homeScore,
                isWin = isWin,
                isDraw = isDraw,
                isLoss = isLoss
            )
            leagueStandingsDao.update(updatedStanding)
        }

        // Recalculate positions
        recalculatePositions(result.leagueName, seasonYear)
    }

    /**
     * Recalculate all positions in the league based on points, GD, goals scored
     */
    suspend fun recalculatePositions(leagueName: String, seasonYear: Int) {
        val standings = leagueStandingsDao.getStandings(leagueName, seasonYear)
            .firstOrNull() ?: return

        // Sort by points, then goal difference, then goals scored
        val sortedStandings = standings.sortedWith(
            compareByDescending<LeagueStandingsEntity> { it.points }
                .thenByDescending { it.goalDifference }
                .thenByDescending { it.goalsScored }
        )

        // Update positions
        sortedStandings.forEachIndexed { index, standing ->
            if (standing.position != index + 1) {
                val updatedStanding = standing.updatePosition(index + 1)
                leagueStandingsDao.update(updatedStanding)
            }
        }
    }

    /**
     * Process promotion and relegation at end of season
     */
    suspend fun processSeasonEnd(leagueName: String, seasonYear: Int): PromotionRelegationResult {
        val standings = leagueStandingsDao.getStandingsByPosition(leagueName, seasonYear)
            .firstOrNull() ?: return PromotionRelegationResult(emptyList(), emptyList(), emptyList())

        val promoted = standings.filter { it.position <= 2 }
        val playoffTeams = standings.filter { it.position in 3..4 }
        val relegated = standings.filter { it.position >= 14 }

        return PromotionRelegationResult(promoted, playoffTeams, relegated)
    }

    /**
     * Reset standings for new season (keep teams, reset stats)
     */
    suspend fun resetForNewSeason(leagueName: String, oldSeasonYear: Int, newSeasonYear: Int) {
        val oldStandings = leagueStandingsDao.getStandings(leagueName, oldSeasonYear)
            .firstOrNull() ?: return

        val newStandings = oldStandings.map { standing ->
            standing.resetForNewSeason().copy(
                seasonYear = newSeasonYear
            )
        }

        insertAllStandings(newStandings)
    }

    /**
     * Get league table with full details
     */
    suspend fun getLeagueTable(leagueName: String, seasonYear: Int): LeagueTable {
        val standings = leagueStandingsDao.getFullStandings(leagueName, seasonYear)
            .firstOrNull() ?: emptyList()

        val leader = standings.firstOrNull()
        val champion = standings.firstOrNull { it.standing.position == 1 }
        val topScorer = null // Would come from players repository

        return LeagueTable(
            leagueName = leagueName,
            seasonYear = seasonYear,
            standings = standings,
            leader = leader,
            champion = champion,
            topScorer = topScorer,
            averagePoints = standings.map { it.standing.points }.average(),
            totalGoals = standings.sumOf { it.standing.goalsScored }
        )
    }
}

// ============ DATA CLASSES ============

data class PromotionRelegationResult(
    val promoted: List<LeagueStandingsEntity>,
    val playoffTeams: List<LeagueStandingsEntity>,
    val relegated: List<LeagueStandingsEntity>
)

data class LeagueTable(
    val leagueName: String,
    val seasonYear: Int,
    val standings: List<FullStandingEntry>,
    val leader: FullStandingEntry?,
    val champion: FullStandingEntry?,
    val topScorer: Any?,  // Would be PlayerStats type
    val averagePoints: Double,
    val totalGoals: Int
)