package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.EmployedManagerWithTeam
import com.fameafrica.afm2026.data.database.dao.ManagerLevelDistribution
import com.fameafrica.afm2026.data.database.dao.ManagerStyleDistribution
import com.fameafrica.afm2026.data.database.dao.ManagerWithDetails
import com.fameafrica.afm2026.data.database.dao.ManagersDao
import com.fameafrica.afm2026.data.database.entities.ManagersEntity
import com.fameafrica.afm2026.data.database.entities.ReputationLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManagersRepository @Inject constructor(
    private val managersDao: ManagersDao
) {

    // ============ BASIC CRUD ============

    fun getAllManagers(): Flow<List<ManagersEntity>> = managersDao.getAll()

    suspend fun getManagerById(id: Int): ManagersEntity? = managersDao.getById(id)

    suspend fun getManagerByName(name: String): ManagersEntity? = managersDao.getByName(name)

    suspend fun getManagerByTeamId(teamId: Int): ManagersEntity? = managersDao.getByTeamId(teamId)

    suspend fun insertManager(manager: ManagersEntity) = managersDao.insert(manager)

    suspend fun insertAllManagers(managers: List<ManagersEntity>) = managersDao.insertAll(managers)

    suspend fun updateManager(manager: ManagersEntity) = managersDao.update(manager)

    suspend fun deleteManager(manager: ManagersEntity) = managersDao.delete(manager)

    suspend fun getManagersCount(): Int = managersDao.getCount()

    // ============ EMPLOYMENT STATUS ============

    fun getAvailableManagers(): Flow<List<ManagersEntity>> = managersDao.getAvailableManagers()

    fun getEmployedManagers(): Flow<List<ManagersEntity>> = managersDao.getEmployedManagers()

    suspend fun getManagerByTeam(teamId: Int): ManagersEntity? = managersDao.getManagerByTeam(teamId)

    fun getAvailableManagersByReputation(minRep: Int, maxRep: Int): Flow<List<ManagersEntity>> =
        managersDao.getAvailableManagersByReputation(minRep, maxRep)

    // ============ REPUTATION-BASED ============

    fun getManagersByReputationLevel(level: String): Flow<List<ManagersEntity>> =
        managersDao.getManagersByReputationLevel(level)

    fun getHighReputationManagers(minReputation: Int): Flow<List<ManagersEntity>> =
        managersDao.getHighReputationManagers(minReputation)

    fun getLowReputationManagers(maxReputation: Int): Flow<List<ManagersEntity>> =
        managersDao.getLowReputationManagers(maxReputation)

    fun getTopManagers(limit: Int): Flow<List<ManagersEntity>> =
        managersDao.getTopManagers(limit)

    // ============ NATIONALITY-BASED ============

    fun getManagersByNationality(nationality: String): Flow<List<ManagersEntity>> =
        managersDao.getManagersByNationality(nationality)

    fun getDistinctNationalities(): Flow<List<String>> = managersDao.getDistinctNationalities()

    // ============ AGE-BASED ============

    fun getYoungManagers(): Flow<List<ManagersEntity>> = managersDao.getYoungManagers()

    fun getPrimeAgeManagers(): Flow<List<ManagersEntity>> = managersDao.getPrimeAgeManagers()

    fun getVeteranManagers(): Flow<List<ManagersEntity>> = managersDao.getVeteranManagers()

    // ============ PERFORMANCE-BASED ============

    /**
     * Get managers sorted by win percentage (calculated in SQL)
     * Only includes managers with at least 1 match managed
     */
    fun getManagersByWinPercentage(limit: Int): Flow<List<ManagersEntity>> =
        managersDao.getManagersByWinPercentage(limit)

    /**
     * Get all managers sorted by win percentage (includes those with 0 matches)
     */
    fun getAllManagersByWinPercentage(limit: Int): Flow<List<ManagersEntity>> =
        managersDao.getAllManagersByWinPercentage(limit)

    /**
     * Get managers with minimum matches threshold sorted by win percentage
     */
    fun getManagersByWinPercentageWithMinMatches(minMatches: Int, limit: Int): Flow<List<ManagersEntity>> =
        managersDao.getManagersByWinPercentageWithMinMatches(minMatches, limit)

    /**
     * Get top active managers (employed, 50+ matches) by win percentage
     */
    fun getTopActiveManagersByWinPercentage(limit: Int): Flow<List<ManagersEntity>> =
        managersDao.getTopActiveManagersByWinPercentage(limit)

    fun getMostTrophyWinningManagers(limit: Int): Flow<List<ManagersEntity>> =
        managersDao.getMostTrophyWinningManagers(limit)

    fun getMostExperiencedManagers(limit: Int): Flow<List<ManagersEntity>> =
        managersDao.getMostExperiencedManagers(limit)

    fun getHighPerformingManagers(): Flow<List<ManagersEntity>> =
        managersDao.getHighPerformingManagers()

    // ============ WIN PERCENTAGE UTILITIES ============

    /**
     * Calculate win percentage for a single manager
     */
    fun calculateWinPercentage(manager: ManagersEntity): Double {
        return if (manager.matchesManaged > 0) {
            (manager.wins.toDouble() / manager.matchesManaged * 100)
        } else 0.0
    }

    /**
     * Get manager with win percentage as a flow
     */
    suspend fun getManagerWithWinPercentage(managerId: Int): Pair<ManagersEntity?, Double> {
        val manager = managersDao.getById(managerId)
        return Pair(manager, calculateWinPercentage(manager ?: return Pair(null, 0.0)))
    }

    // ============ LICENSE & ABILITY ============

    fun getManagersByLicense(licenses: List<String>): Flow<List<ManagersEntity>> =
        managersDao.getManagersByLicense(licenses)

    fun getManagersBySpecialAbility(ability: String): Flow<List<ManagersEntity>> =
        managersDao.getManagersBySpecialAbility(ability)

    fun getYouthDevelopmentSpecialists(): Flow<List<ManagersEntity>> =
        managersDao.getYouthDevelopmentSpecialists()

    fun getMediaFriendlyManagers(): Flow<List<ManagersEntity>> =
        managersDao.getMediaFriendlyManagers()

    fun getTacticallyFlexibleManagers(): Flow<List<ManagersEntity>> =
        managersDao.getTacticallyFlexibleManagers()

    fun getMotivationalManagers(): Flow<List<ManagersEntity>> =
        managersDao.getMotivationalManagers()

    fun getStrictManagers(): Flow<List<ManagersEntity>> =
        managersDao.getStrictManagers()

    fun getAdaptableManagers(): Flow<List<ManagersEntity>> =
        managersDao.getAdaptableManagers()

    // ============ FORMATION & STYLE ============

    fun getManagersByPreferredFormation(formation: String): Flow<List<ManagersEntity>> =
        managersDao.getManagersByPreferredFormation(formation)

    fun getManagersByStyle(style: String): Flow<List<ManagersEntity>> =
        managersDao.getManagersByStyle(style)

    // ============ SEARCH ============

    fun searchManagers(searchQuery: String): Flow<List<ManagersEntity>> =
        managersDao.searchManagers(searchQuery)

    fun advancedSearch(searchQuery: String): Flow<List<ManagersEntity>> =
        managersDao.advancedSearch(searchQuery)

    // ============ STATISTICS ============

    suspend fun getAverageReputation(): Double? = managersDao.getAverageReputation()

    suspend fun getAverageAge(): Double? = managersDao.getAverageAge()

    suspend fun getAverageMatchesManaged(): Double? = managersDao.getAverageMatchesManaged()

    suspend fun getAverageWinPercentage(): Double? = managersDao.getAverageWinPercentage()

    fun getManagerDistributionByLevel(): Flow<List<ManagerLevelDistribution>> =
        managersDao.getManagerDistributionByLevel()

    fun getManagerStyleDistribution(): Flow<List<ManagerStyleDistribution>> =
        managersDao.getManagerStyleDistribution()

    // ============ JOIN QUERIES ============

    suspend fun getManagerWithDetails(managerId: Int): ManagerWithDetails? =
        managersDao.getManagerWithDetails(managerId)

    fun getAllEmployedManagersWithTeams(): Flow<List<EmployedManagerWithTeam>> =
        managersDao.getAllEmployedManagersWithTeams()

    // ============ MANAGER MANAGEMENT ============

    suspend fun updateManagerAfterMatch(
        managerId: Int,
        won: Boolean,
        drew: Boolean,
        lost: Boolean
    ) {
        val manager = managersDao.getById(managerId) ?: return
        val updatedManager = manager.updateAfterMatch(won, drew, lost)
        managersDao.update(updatedManager)
    }

    suspend fun winTrophy(managerId: Int) {
        val manager = managersDao.getById(managerId) ?: return
        val updatedManager = manager.winTrophy()
        managersDao.update(updatedManager)
    }

    suspend fun signContract(managerId: Int, teamId: Int, salary: Int, contractYears: Int) {
        val manager = managersDao.getById(managerId) ?: return
        val updatedManager = manager.signContract(teamId, salary, contractYears)
        managersDao.update(updatedManager)
    }

    suspend fun leaveClub(managerId: Int) {
        val manager = managersDao.getById(managerId) ?: return
        val updatedManager = manager.leaveClub()
        managersDao.update(updatedManager)
    }

    suspend fun renewContract(managerId: Int, newSalary: Int, additionalYears: Int) {
        val manager = managersDao.getById(managerId) ?: return
        val updatedManager = manager.renewContract(newSalary, additionalYears)
        managersDao.update(updatedManager)
    }

    suspend fun updateReputation(managerId: Int, newReputation: Int) {
        val manager = managersDao.getById(managerId) ?: return
        val updatedManager = manager.updateReputation(newReputation)
        managersDao.update(updatedManager)
    }

    suspend fun earnAward(managerId: Int, awardType: String) {
        val manager = managersDao.getById(managerId) ?: return
        val updatedManager = manager.earnAward(awardType)
        managersDao.update(updatedManager)
    }

    suspend fun upgradeLicense(managerId: Int, newLicense: String) {
        val manager = managersDao.getById(managerId) ?: return
        val updatedManager = manager.upgradeLicense(newLicense)
        managersDao.update(updatedManager)
    }

    // ============ MANAGER CREATION ============

    suspend fun createNewManager(
        name: String,
        nationality: String,
        age: Int,
        coachingLicense: String = "NATIONAL_C",
        specialAbility: String? = null
    ): ManagersEntity {
        val manager = ManagersEntity(
            name = name,
            nationality = nationality,
            age = age,
            teamId = null,
            coachingLicense = coachingLicense,
            specialAbility = specialAbility,
            reputation = 30,
            reputationLevel = ReputationLevel.LOCAL.value,
            preferredFormation = "4-4-2",
            style = "Balanced",
            matchesManaged = 0,
            wins = 0,
            draws = 0,
            losses = 0,
            trophiesWon = 0,
            performanceRating = 50,
            youthDevelopmentFocus = 50,
            mediaHandling = 50,
            tacticalFlexibility = 50,
            playerMotivation = 50,
            disciplineLevel = 50,
            adaptability = 50
        )

        managersDao.insert(manager)
        return manager
    }

    // ============ DASHBOARD ============

    suspend fun getManagerDashboard(managerId: Int): ManagerDashboard {
        val manager = managersDao.getById(managerId) ?: throw IllegalArgumentException("Manager not found")
        val managerWithDetails = managersDao.getManagerWithDetails(managerId)

        val winRate = calculateWinPercentage(manager)

        val trophyRate = if (manager.matchesManaged > 0) {
            (manager.trophiesWon.toDouble() / manager.matchesManaged * 100)
        } else 0.0

        return ManagerDashboard(
            manager = manager,
            managerWithDetails = managerWithDetails,
            winPercentage = winRate,
            trophyPercentage = trophyRate,
            matchesPerTrophy = if (manager.trophiesWon > 0)
                (manager.matchesManaged / manager.trophiesWon) else 0,
            overallRating = manager.overallRating,
            careerStage = manager.careerStage,
            isEmployed = manager.isEmployed,
            availableManagersCount = managersDao.getAvailableManagers().firstOrNull()?.size ?: 0
        )
    }
}

// ============ DATA CLASSES ============

data class ManagerDashboard(
    val manager: ManagersEntity,
    val managerWithDetails: ManagerWithDetails?,
    val winPercentage: Double,
    val trophyPercentage: Double,
    val matchesPerTrophy: Int,
    val overallRating: Int,
    val careerStage: String,
    val isEmployed: Boolean,
    val availableManagersCount: Int
)