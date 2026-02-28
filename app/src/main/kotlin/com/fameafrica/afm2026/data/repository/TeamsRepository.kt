package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.LeagueStrength
import com.fameafrica.afm2026.data.database.dao.TeamWithCup
import com.fameafrica.afm2026.data.database.dao.TeamWithLeague
import com.fameafrica.afm2026.data.database.dao.TeamWithManager
import com.fameafrica.afm2026.data.database.dao.TeamWithStanding
import com.fameafrica.afm2026.data.database.dao.TeamsDao
import com.fameafrica.afm2026.data.database.entities.TeamsEntity
import com.fameafrica.afm2026.data.database.entities.FixturesResultsEntity
import com.fameafrica.afm2026.data.database.entities.PlayersEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamsRepository @Inject constructor(
    private val teamsDao: TeamsDao,
    private val playersRepository: PlayersRepository
) {

    // ============ BASIC CRUD ============

    fun getAllTeams(): Flow<List<TeamsEntity>> = teamsDao.getAll()

    suspend fun getTeamById(id: Int): TeamsEntity? = teamsDao.getById(id)

    suspend fun getTeamByName(name: String): TeamsEntity? = teamsDao.getByName(name)

    suspend fun insertTeam(team: TeamsEntity) = teamsDao.insert(team)

    suspend fun insertAllTeams(teams: List<TeamsEntity>) = teamsDao.insertAll(teams)

    suspend fun updateTeam(team: TeamsEntity) = teamsDao.update(team)

    suspend fun deleteTeam(team: TeamsEntity) = teamsDao.delete(team)

    suspend fun getTeamsCount(): Int = teamsDao.getCount()

    // ============ LEAGUE-BASED ============
    // Add these methods to your TeamsRepository.kt

    /**
     * Get league table for a specific season
     */
    fun getLeagueTable(leagueName: String, seasonYear: Int): Flow<List<TeamsEntity>> =
        teamsDao.getLeagueTable(leagueName, seasonYear)

    /**
     * Get current league table (most recent season)
     */
    fun getCurrentLeagueTable(leagueName: String): Flow<List<TeamsEntity>> =
        teamsDao.getCurrentLeagueTable(leagueName)

    /**
     * Get teams with full standing details (including points, GD, form)
     */
    fun getTeamsWithStandings(leagueName: String, seasonYear: Int): Flow<List<TeamWithStanding>> =
        teamsDao.getTeamsWithStandings(leagueName, seasonYear)

    /**
     * Get current league table with full details
     */
    suspend fun getCurrentLeagueTableWithDetails(leagueName: String): List<TeamWithStanding> {
        val currentSeason = getCurrentSeasonForLeague(leagueName)
        return teamsDao.getTeamsWithStandings(leagueName, currentSeason).firstOrNull() ?: emptyList()
    }

    /**
     * Helper to get current season for a league
     */
    private suspend fun getCurrentSeasonForLeague(leagueName: String): Int {
        // You can implement this based on your game logic
        // For example, from game settings or current date
        return 2024 // Replace with actual logic
    }

    fun getTeamsByLeague(leagueName: String): Flow<List<TeamsEntity>> =
        teamsDao.getTeamsByLeague(leagueName)

    fun getTeamsByLeagueElo(leagueName: String): Flow<List<TeamsEntity>> =
        teamsDao.getTeamsByLeagueElo(leagueName)

    suspend fun getAverageEloByLeague(leagueName: String): Double? =
        teamsDao.getAverageEloByLeague(leagueName)

    suspend fun getAverageReputationByLeague(leagueName: String): Double? =
        teamsDao.getAverageReputationByLeague(leagueName)

    fun getLeagueStrengthRanking(): Flow<List<LeagueStrength>> =
        teamsDao.getLeagueStrengthRanking()

    // ============ MANAGER-BASED ============

    suspend fun getTeamByManager(managerId: Int): TeamsEntity? =
        teamsDao.getTeamByManager(managerId)

    fun getTeamsWithoutManager(): Flow<List<TeamsEntity>> =
        teamsDao.getTeamsWithoutManager()

    suspend fun assignManager(teamId: Int, managerId: Int?) {
        val team = teamsDao.getById(teamId) ?: return
        val updatedTeam = team.assignManager(managerId)
        teamsDao.update(updatedTeam)
    }

    // ============ CUP-BASED ============

    fun getTeamsInCup(cupName: String): Flow<List<TeamsEntity>> =
        teamsDao.getTeamsInCup(cupName)

    fun getMostCupWinners(): Flow<List<TeamsEntity>> =
        teamsDao.getMostCupWinners()

    suspend fun updateTeamCupProgress(teamId: Int, cupName: String, stage: String, status: String) {
        val team = teamsDao.getById(teamId) ?: return
        val updatedTeam = team.updateCupProgress(cupName, stage, status)
        teamsDao.update(updatedTeam)
    }

    suspend fun recordCupWin(teamId: Int, cupName: String) {
        val team = teamsDao.getById(teamId) ?: return
        val updatedTeam = team.winCup(cupName)
        teamsDao.update(updatedTeam)
    }

    // ============ RANKINGS ============

    fun getTopTeamsByElo(limit: Int = 10): Flow<List<TeamsEntity>> =
        teamsDao.getTopTeamsByElo(limit)

    fun getMostReputableTeams(limit: Int = 10): Flow<List<TeamsEntity>> =
        teamsDao.getMostReputableTeams(limit)

    fun getRichestTeams(limit: Int = 10): Flow<List<TeamsEntity>> =
        teamsDao.getRichestTeams(limit)

    fun getTeamsWithBestFans(limit: Int = 10): Flow<List<TeamsEntity>> =
        teamsDao.getTeamsWithBestFans(limit)

    // ============ RIVALRIES ============

    fun getRivals(teamName: String): Flow<List<TeamsEntity>> =
        teamsDao.getRivals(teamName)

    suspend fun getDerbyTeams(team1: String, team2: String): List<TeamsEntity> =
        teamsDao.getDerbyTeams(team1, team2)

    // ============ SEARCH ============

    fun searchTeams(searchQuery: String): Flow<List<TeamsEntity>> =
        teamsDao.searchTeams(searchQuery)

    // ============ TEAM MANAGEMENT ============

    suspend fun updateTeamAfterMatch(result: FixturesResultsEntity) {
        // Update home team
        val homeTeam = teamsDao.getByName(result.homeTeam)
        homeTeam?.let {
            val updatedHomeTeam = it.updateAfterMatch(result)
            teamsDao.update(updatedHomeTeam)
        }

        // Update away team
        val awayTeam = teamsDao.getByName(result.awayTeam)
        awayTeam?.let {
            val updatedAwayTeam = it.updateAfterMatch(result)
            teamsDao.update(updatedAwayTeam)
        }
    }

    suspend fun updateTeamElo(teamName: String, newElo: Int) {
        val team = teamsDao.getByName(teamName) ?: return
        val updatedTeam = team.updateElo(newElo)
        teamsDao.update(updatedTeam)
    }

    suspend fun updateTeamMorale(teamId: Int, change: Int) {
        val team = teamsDao.getById(teamId) ?: return
        val updatedTeam = team.updateMorale(change)
        teamsDao.update(updatedTeam)
    }

    suspend fun updateTeamRevenue(teamId: Int, amount: Double) {
        val team = teamsDao.getById(teamId) ?: return
        val updatedTeam = team.updateRevenue(amount)
        teamsDao.update(updatedTeam)
    }

    suspend fun updateTeamFanLoyalty(teamId: Int, change: Int) {
        val team = teamsDao.getById(teamId) ?: return
        val updatedTeam = team.updateFanLoyalty(change)
        teamsDao.update(updatedTeam)
    }

    suspend fun recalculateTeamAbilities(teamId: Int) {
        val team = teamsDao.getById(teamId) ?: return
        val players = playersRepository.getPlayersByTeamId(teamId).firstOrNull() ?: emptyList()
        val updatedTeam = team.calculateAverageAbilities(players)
        teamsDao.update(updatedTeam)
    }

    // ============ JOIN QUERIES ============

    suspend fun getTeamWithLeague(teamId: Int): TeamWithLeague? =
        teamsDao.getTeamWithLeague(teamId)

    suspend fun getTeamWithManager(teamId: Int): TeamWithManager? =
        teamsDao.getTeamWithManager(teamId)

    fun getTeamsWithCupDetails(): Flow<List<TeamWithCup>> =
        teamsDao.getTeamsWithCupDetails()

    // ============ DASHBOARD ============

    suspend fun getTeamDashboard(teamId: Int): TeamDashboard {
        val team = teamsDao.getById(teamId) ?: throw IllegalArgumentException("Team not found")
        val players = playersRepository.getPlayersByTeamId(teamId).firstOrNull() ?: emptyList()

        val topScorer = players.maxByOrNull { it.goals }
        val mostValuable = players.maxByOrNull { it.marketValue }
        val captain = players.firstOrNull { it.isCaptain }

        return TeamDashboard(
            team = team,
            squadSize = players.size,
            averageRating = players.map { it.rating }.average(),
            totalMarketValue = players.sumOf { it.marketValue.toDouble() },
            topScorer = topScorer,
            mostValuablePlayer = mostValuable,
            captain = captain,
            leaguePosition = null, // Would be calculated from standings
            form = null // Would be calculated from recent results
        )
    }
}

// ============ DATA CLASSES ============

data class TeamDashboard(
    val team: TeamsEntity,
    val squadSize: Int,
    val averageRating: Double,
    val totalMarketValue: Double,
    val topScorer: PlayersEntity?,
    val mostValuablePlayer: PlayersEntity?,
    val captain: PlayersEntity?,
    val leaguePosition: Int?,
    val form: String?
)