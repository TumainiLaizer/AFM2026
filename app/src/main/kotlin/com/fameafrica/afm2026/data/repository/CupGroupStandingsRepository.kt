package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.CupGroupStandingsDao
import com.fameafrica.afm2026.data.database.dao.CupGroupStatistics
import com.fameafrica.afm2026.data.database.dao.FullGroupStandingEntry
import com.fameafrica.afm2026.data.database.dao.GroupWinsStats
import com.fameafrica.afm2026.data.database.entities.CupGroupStandingsEntity
import com.fameafrica.afm2026.data.database.entities.FixturesResultsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CupGroupStandingsRepository @Inject constructor(
    private val cupGroupStandingsDao: CupGroupStandingsDao
) {

    // ============ BASIC CRUD ============

    fun getAllStandings(): Flow<List<CupGroupStandingsEntity>> = cupGroupStandingsDao.getAll()

    suspend fun getStandingById(id: Int): CupGroupStandingsEntity? = cupGroupStandingsDao.getById(id)

    suspend fun getTeamStanding(teamName: String, cupName: String, seasonYear: Int): CupGroupStandingsEntity? =
        cupGroupStandingsDao.getTeamStanding(teamName, cupName, seasonYear)

    suspend fun insertStanding(standing: CupGroupStandingsEntity) = cupGroupStandingsDao.insert(standing)

    suspend fun insertAllStandings(standings: List<CupGroupStandingsEntity>) = cupGroupStandingsDao.insertAll(standings)

    suspend fun updateStanding(standing: CupGroupStandingsEntity) = cupGroupStandingsDao.update(standing)

    suspend fun deleteStanding(standing: CupGroupStandingsEntity) = cupGroupStandingsDao.delete(standing)

    suspend fun deleteByCupAndSeason(cupName: String, seasonYear: Int) =
        cupGroupStandingsDao.deleteByCupAndSeason(cupName, seasonYear)

    // ============ GROUP STANDINGS QUERIES ============

    fun getGroupStandings(cupName: String, seasonYear: Int): Flow<List<CupGroupStandingsEntity>> =
        cupGroupStandingsDao.getGroupStandings(cupName, seasonYear)

    fun getStandingsByPosition(cupName: String, seasonYear: Int): Flow<List<CupGroupStandingsEntity>> =
        cupGroupStandingsDao.getStandingsByPosition(cupName, seasonYear)

    fun getQualifiedTeams(cupName: String, seasonYear: Int): Flow<List<CupGroupStandingsEntity>> =
        cupGroupStandingsDao.getQualifiedTeams(cupName, seasonYear)

    suspend fun getGroupWinner(cupName: String, seasonYear: Int): CupGroupStandingsEntity? =
        cupGroupStandingsDao.getGroupWinner(cupName, seasonYear)

    suspend fun getTeamPosition(cupName: String, seasonYear: Int, teamName: String): CupGroupStandingsEntity? =
        cupGroupStandingsDao.getTeamPosition(cupName, seasonYear, teamName)

    // ============ TEAM HISTORY ============

    fun getTeamCupHistory(teamName: String): Flow<List<CupGroupStandingsEntity>> =
        cupGroupStandingsDao.getTeamCupHistory(teamName)

    fun getTeamGroupWins(teamName: String): Flow<List<CupGroupStandingsEntity>> =
        cupGroupStandingsDao.getTeamGroupWins(teamName)

    // ============ STATISTICS ============

    suspend fun getCupGroupStatistics(cupName: String, seasonYear: Int): CupGroupStatistics? =
        cupGroupStandingsDao.getCupGroupStatistics(cupName, seasonYear)

    fun getMostGroupWins(cupName: String): Flow<List<GroupWinsStats>> =
        cupGroupStandingsDao.getMostGroupWins(cupName)

    // ============ JOIN QUERIES ============

    fun getFullGroupStandings(cupName: String, seasonYear: Int): Flow<List<FullGroupStandingEntry>> =
        cupGroupStandingsDao.getFullGroupStandings(cupName, seasonYear)

    // ============ STANDINGS MANAGEMENT ============

    /**
     * Initialize cup group standings for a new season
     */
    suspend fun initializeGroupStandings(
        cupName: String,
        seasonYear: Int,
        groupName: String,
        teamNames: List<String>
    ): List<CupGroupStandingsEntity> {
        val standings = teamNames.mapIndexed { index, teamName ->
            CupGroupStandingsEntity(
                cupName = "$cupName - $groupName",
                seasonYear = seasonYear,
                position = index + 1,
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
     * Update group standings after a match result
     */
    suspend fun updateGroupStandingsAfterMatch(result: FixturesResultsEntity) {
        // Only process cup group stage matches
        if (result.cupName == null || result.cupRound?.contains("Group") != true) return

        val seasonYear = result.season.split("/").first().toInt()

        // Update home team
        val homeStanding = cupGroupStandingsDao.getTeamStanding(
            teamName = result.homeTeam,
            cupName = result.cupName,
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
            cupGroupStandingsDao.update(updatedStanding)
        }

        // Update away team
        val awayStanding = cupGroupStandingsDao.getTeamStanding(
            teamName = result.awayTeam,
            cupName = result.cupName,
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
            cupGroupStandingsDao.update(updatedStanding)
        }

        // Recalculate positions
        recalculateGroupPositions(result.cupName, seasonYear)
    }

    /**
     * Recalculate all positions in the group based on points, GD, goals scored
     */
    suspend fun recalculateGroupPositions(cupName: String, seasonYear: Int) {
        val standings = cupGroupStandingsDao.getGroupStandings(cupName, seasonYear)
            .firstOrNull() ?: return

        // Sort by points, then goal difference, then goals scored, then head-to-head
        val sortedStandings = standings.sortedWith(
            compareByDescending<CupGroupStandingsEntity> { it.points }
                .thenByDescending { it.goalDifference }
                .thenByDescending { it.goalsScored }
        )

        // Update positions
        sortedStandings.forEachIndexed { index, standing ->
            if (standing.position != index + 1) {
                val updatedStanding = standing.updatePosition(index + 1)
                cupGroupStandingsDao.update(updatedStanding)
            }
        }
    }

    /**
     * Process qualification at end of group stage
     */
    suspend fun processGroupStageEnd(cupName: String, seasonYear: Int): GroupStageResult {
        val standings = cupGroupStandingsDao.getStandingsByPosition(cupName, seasonYear)
            .firstOrNull() ?: return GroupStageResult(emptyList(), emptyList())

        val qualified = standings.filter { it.position <= 2 }
        val eliminated = standings.filter { it.position > 2 }

        return GroupStageResult(qualified, eliminated)
    }
}

// ============ DATA CLASSES ============

data class GroupStageResult(
    val qualifiedTeams: List<CupGroupStandingsEntity>,
    val eliminatedTeams: List<CupGroupStandingsEntity>
)