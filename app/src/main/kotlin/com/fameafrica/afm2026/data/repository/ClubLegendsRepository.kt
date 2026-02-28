package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.ClubLegendStatistics
import com.fameafrica.afm2026.data.database.dao.ClubLegendsDao
import com.fameafrica.afm2026.data.database.dao.LegendStatusDistribution
import com.fameafrica.afm2026.data.database.dao.LegendWithDetails
import com.fameafrica.afm2026.data.database.entities.ClubLegendsEntity
import com.fameafrica.afm2026.data.database.entities.LegendStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClubLegendsRepository @Inject constructor(
    private val clubLegendsDao: ClubLegendsDao
) {

    // ============ BASIC CRUD ============

    fun getAllLegends(): Flow<List<ClubLegendsEntity>> = clubLegendsDao.getAll()

    suspend fun getLegendById(id: Int): ClubLegendsEntity? = clubLegendsDao.getById(id)

    suspend fun getLegendByPlayerName(playerName: String): ClubLegendsEntity? =
        clubLegendsDao.getByPlayerName(playerName)

    suspend fun insertLegend(legend: ClubLegendsEntity) = clubLegendsDao.insert(legend)

    suspend fun insertAllLegends(legends: List<ClubLegendsEntity>) = clubLegendsDao.insertAll(legends)

    suspend fun updateLegend(legend: ClubLegendsEntity) = clubLegendsDao.update(legend)

    suspend fun deleteLegend(legend: ClubLegendsEntity) = clubLegendsDao.delete(legend)

    suspend fun getLegendsCount(): Int = clubLegendsDao.getCount()

    // ============ CLUB-BASED ============

    fun getLegendsByClub(clubName: String): Flow<List<ClubLegendsEntity>> =
        clubLegendsDao.getLegendsByClub(clubName)

    fun getActiveLegendsByClub(clubName: String): Flow<List<ClubLegendsEntity>> =
        clubLegendsDao.getActiveLegendsByClub(clubName)

    fun getRetiredLegendsByClub(clubName: String): Flow<List<ClubLegendsEntity>> =
        clubLegendsDao.getRetiredLegendsByClub(clubName)

    suspend fun getLegendCountByClub(clubName: String): Int =
        clubLegendsDao.getLegendCountByClub(clubName)

    suspend fun getTotalTitlesByLegends(clubName: String): Int? =
        clubLegendsDao.getTotalTitlesByLegends(clubName)

    fun getTopLegendsByClub(clubName: String): Flow<List<ClubLegendsEntity>> =
        clubLegendsDao.getTopLegendsByClub(clubName)

    // ============ TITLES-BASED ============

    fun getLegendsByMinTitles(minTitles: Int): Flow<List<ClubLegendsEntity>> =
        clubLegendsDao.getLegendsByMinTitles(minTitles)

    fun getMostDecoratedLegends(limit: Int): Flow<List<ClubLegendsEntity>> =
        clubLegendsDao.getMostDecoratedLegends(limit)

    fun getLongestServingLegends(minYears: Int): Flow<List<ClubLegendsEntity>> =
        clubLegendsDao.getLongestServingLegends(minYears)

    // ============ STATUS-BASED ============

    fun getLegendsByStatus(status: String): Flow<List<ClubLegendsEntity>> =
        clubLegendsDao.getLegendsByStatus(status)

    // ============ SEARCH ============

    fun searchLegends(searchQuery: String): Flow<List<ClubLegendsEntity>> =
        clubLegendsDao.searchLegends(searchQuery)

    // ============ STATISTICS ============

    fun getClubLegendStatistics(): Flow<List<ClubLegendStatistics>> =
        clubLegendsDao.getClubLegendStatistics()

    fun getLegendStatusDistribution(): Flow<List<LegendStatusDistribution>> =
        clubLegendsDao.getLegendStatusDistribution()

    // ============ JOIN QUERIES ============

    suspend fun getLegendWithDetails(legendId: Int): LegendWithDetails? =
        clubLegendsDao.getLegendWithDetails(legendId)

    fun getClubLegendsWithDetails(clubName: String): Flow<List<LegendWithDetails>> =
        clubLegendsDao.getClubLegendsWithDetails(clubName)

    // ============ LEGEND MANAGEMENT ============

    suspend fun addClubLegend(
        playerName: String,
        clubName: String,
        yearsPlayed: Int,
        majorTitlesWon: Int = 0,
        status: String = LegendStatus.RETIRED.value
    ): ClubLegendsEntity {
        // Check if already a legend
        val existing = clubLegendsDao.getByPlayerName(playerName)

        if (existing != null) {
            // Update existing record
            val updatedLegend = existing.copy(
                clubName = clubName,
                yearsPlayed = yearsPlayed,
                majorTitlesWon = majorTitlesWon,
                status = status
            )
            clubLegendsDao.update(updatedLegend)
            return updatedLegend
        } else {
            // Create new legend
            val legend = ClubLegendsEntity(
                playerName = playerName,
                clubName = clubName,
                yearsPlayed = yearsPlayed,
                majorTitlesWon = majorTitlesWon,
                status = status
            )
            clubLegendsDao.insert(legend)
            return legend
        }
    }

    suspend fun updateLegendStatus(legendId: Int, newStatus: String) {
        val legend = clubLegendsDao.getById(legendId) ?: return
        val updatedLegend = legend.copy(status = newStatus)
        clubLegendsDao.update(updatedLegend)
    }

    suspend fun incrementTitles(legendId: Int, titlesToAdd: Int = 1) {
        val legend = clubLegendsDao.getById(legendId) ?: return
        val updatedLegend = legend.copy(majorTitlesWon = legend.majorTitlesWon + titlesToAdd)
        clubLegendsDao.update(updatedLegend)
    }

    suspend fun retireLegend(legendId: Int) {
        val legend = clubLegendsDao.getById(legendId) ?: return
        val updatedLegend = legend.copy(status = LegendStatus.RETIRED.value)
        clubLegendsDao.update(updatedLegend)
    }

    suspend fun honorLegend(legendId: Int) {
        val legend = clubLegendsDao.getById(legendId) ?: return
        val updatedLegend = legend.copy(status = LegendStatus.HONORED.value)
        clubLegendsDao.update(updatedLegend)
    }

    // ============ AUTOMATIC LEGEND DETECTION ============

    suspend fun checkAndAddLegend(
        playerName: String,
        clubName: String,
        yearsPlayed: Int,
        totalTitles: Int
    ): Boolean {
        // Criteria for becoming a club legend:
        // 1. Played at least 5 years at the club
        // 2. Won at least 3 major titles
        // 3. Or special cases (1-club man, captain, etc.)

        val meetsCriteria = when {
            yearsPlayed >= 10 && totalTitles >= 5 -> true // Iconic Legend
            yearsPlayed >= 7 && totalTitles >= 4 -> true  // Club Legend
            yearsPlayed >= 5 && totalTitles >= 3 -> true  // Cult Hero
            yearsPlayed >= 10 -> true                     // Loyal Servant
            totalTitles >= 5 -> true                      // Trophy Winner
            else -> false
        }

        if (meetsCriteria) {
            addClubLegend(
                playerName = playerName,
                clubName = clubName,
                yearsPlayed = yearsPlayed,
                majorTitlesWon = totalTitles,
                status = if (yearsPlayed >= 10) LegendStatus.LEGEND.value else LegendStatus.HONORED.value
            )
            return true
        }

        return false
    }

    // ============ DASHBOARD ============

    suspend fun getClubLegendsDashboard(clubName: String): ClubLegendsDashboard {
        val legends = clubLegendsDao.getLegendsByClub(clubName).firstOrNull() ?: emptyList()
        val topLegends = legends.sortedByDescending { it.majorTitlesWon }.take(5)
        val activeLegends = legends.filter { it.status == LegendStatus.ACTIVE.value }
        val totalTitles = legends.sumOf { it.majorTitlesWon }

        return ClubLegendsDashboard(
            clubName = clubName,
            totalLegends = legends.size,
            activeLegends = activeLegends.size,
            retiredLegends = legends.count { it.status == LegendStatus.RETIRED.value },
            honoredLegends = legends.count { it.status == LegendStatus.HONORED.value },
            iconicLegends = legends.count { it.legendStatus == "Iconic Legend" },
            totalTitlesWon = totalTitles,
            averageTitlesPerLegend = if (legends.isNotEmpty()) totalTitles.toDouble() / legends.size else 0.0,
            topLegends = topLegends
        )
    }
}

// ============ DATA CLASSES ============

data class ClubLegendsDashboard(
    val clubName: String,
    val totalLegends: Int,
    val activeLegends: Int,
    val retiredLegends: Int,
    val honoredLegends: Int,
    val iconicLegends: Int,
    val totalTitlesWon: Int,
    val averageTitlesPerLegend: Double,
    val topLegends: List<ClubLegendsEntity>
)