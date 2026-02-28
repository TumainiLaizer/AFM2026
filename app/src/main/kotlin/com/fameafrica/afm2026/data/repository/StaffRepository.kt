package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.RoleDistribution
import com.fameafrica.afm2026.data.database.dao.SpecializationDistribution
import com.fameafrica.afm2026.data.database.dao.StaffDao
import com.fameafrica.afm2026.data.database.dao.StaffTypeStatistics
import com.fameafrica.afm2026.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepository @Inject constructor(
    private val staffDao: StaffDao,
    private val teamsRepository: TeamsRepository,
    private val playersRepository: PlayersRepository
) {

    // ============ BASIC CRUD ============

    fun getAllStaff(): Flow<List<StaffEntity>> = staffDao.getAll()

    suspend fun getStaffById(id: Int): StaffEntity? = staffDao.getById(id)

    suspend fun getStaffByName(name: String): StaffEntity? = staffDao.getByName(name)

    suspend fun insertStaff(staff: StaffEntity) = staffDao.insert(staff)

    suspend fun insertAllStaff(staffList: List<StaffEntity>) = staffDao.insertAll(staffList)

    suspend fun updateStaff(staff: StaffEntity) = staffDao.update(staff)

    suspend fun deleteStaff(staff: StaffEntity) = staffDao.delete(staff)

    suspend fun deleteStaffByTeam(teamName: String) = staffDao.deleteByTeam(teamName)

    suspend fun getStaffCount(): Int = staffDao.getCount()

    // ============ TEAM-BASED ============

    fun getStaffByTeam(teamName: String): Flow<List<StaffEntity>> =
        staffDao.getStaffByTeam(teamName)

    fun getCoachesByTeam(teamName: String): Flow<List<StaffEntity>> =
        staffDao.getStaffByTeamAndType(teamName, "COACHING")

    fun getScoutsByTeam(teamName: String): Flow<List<StaffEntity>> =
        staffDao.getStaffByTeamAndType(teamName, "SCOUTING")

    fun getMedicalStaffByTeam(teamName: String): Flow<List<StaffEntity>> =
        staffDao.getStaffByTeamAndType(teamName, "MEDICAL")

    fun getAdminStaffByTeam(teamName: String): Flow<List<StaffEntity>> =
        staffDao.getStaffByTeamAndType(teamName, "ADMIN")

    suspend fun getAssistantManager(teamName: String): StaffEntity? =
        staffDao.getAssistantManager(teamName)

    suspend fun getStaffCountByTeam(teamName: String): Int =
        staffDao.getStaffCountByTeam(teamName)

    // ============ STAFF HIRING ============

    /**
     * Hire new staff member
     */
    suspend fun hireStaff(
        name: String,
        role: StaffRole,
        teamName: String,
        specialization: String = Specialization.GENERAL.value,
        impactRating: Int = 70,
        salary: Int = 1200000,
        experienceLevel: Int = 0,
        nationality: String? = null,
        age: Int? = null,
        isHeadOfDepartment: Boolean = false
    ): StaffEntity {
        val staff = StaffEntity(
            name = name,
            role = role.value,
            staffType = role.staffType,
            teamName = teamName,
            specialization = specialization,
            impactRating = impactRating,
            salary = salary,
            experienceLevel = experienceLevel,
            nationality = nationality,
            age = age,
            isHeadOfDepartment = isHeadOfDepartment,
            mentoringAbility = calculateMentoringAbility(role, experienceLevel),
            loyalty = 70,  // Default loyalty for new hires
            adaptability = 70
        )

        staffDao.insert(staff)
        return staff
    }

    /**
     * Convert retired player to staff member
     * Called when a player retires and accepts a staff role
     */
    suspend fun convertPlayerToStaff(
        playerId: Int,
        role: StaffRole,
        teamName: String,
        specialization: String = Specialization.GENERAL.value
    ): StaffEntity? {

        val player = playersRepository.getPlayerById(playerId) ?: return null

        // Calculate impact rating based on player's playing career
        val impactRating = calculatePlayerToStaffImpact(player)

        // Calculate experience level (years played)
        val experienceLevel = player.age - 18

        // Determine if they should be head of department
        val isHeadOfDepartment = player.leadership >= 75

        val staff = StaffEntity(
            name = player.name,
            role = role.value,
            staffType = role.staffType,
            teamName = teamName,
            specialization = specialization,
            impactRating = impactRating,
            salary = calculateStaffSalary(role, impactRating, experienceLevel),
            experienceLevel = experienceLevel,
            faceImage = player.faceImage,
            previousPlayer = player.name,
            nationality = player.nationality,
            age = player.age,
            isHeadOfDepartment = isHeadOfDepartment,
            mentoringAbility = calculateMentoringAbility(role, experienceLevel),
            loyalty = calculateLoyaltyFromPlayer(player),
            adaptability = calculateAdaptabilityFromPlayer(player)
        )

        staffDao.insert(staff)
        return staff
    }

    /**
     * Calculate staff salary based on role, impact, and experience
     */
    private fun calculateStaffSalary(role: StaffRole, impactRating: Int, experienceLevel: Int): Int {
        val baseSalary = when (role) {
            StaffRole.SPORTING_DIRECTOR, StaffRole.TECHNICAL_DIRECTOR -> 5000000
            StaffRole.ASSISTANT_MANAGER -> 3000000
            StaffRole.CHIEF_SCOUT, StaffRole.HEAD_PHYSIO -> 2500000
            StaffRole.FIRST_TEAM_COACH -> 2000000
            StaffRole.YOUTH_COACH, StaffRole.SCOUT -> 1500000
            else -> 1200000
        }

        val impactBonus = (impactRating - 50) * 20000
        val experienceBonus = experienceLevel * 50000

        return (baseSalary + impactBonus + experienceBonus).coerceAtLeast(500000)
    }

    /**
     * Calculate mentoring ability based on role and experience
     */
    private fun calculateMentoringAbility(role: StaffRole, experienceLevel: Int): Int {
        val baseMentoring = when (role) {
            StaffRole.ASSISTANT_MANAGER, StaffRole.TECHNICAL_COACH -> 70
            StaffRole.YOUTH_COACH, StaffRole.ACADEMY_DIRECTOR -> 80
            StaffRole.FIRST_TEAM_COACH -> 60
            else -> 50
        }

        return (baseMentoring + experienceLevel).coerceIn(0, 100)
    }

    /**
     * Calculate loyalty from player's attributes
     */
    private fun calculateLoyaltyFromPlayer(player: PlayersEntity): Int {
        return when (player.personalityType) {
            PlayerPersonality.LOYAL.value -> 90
            PlayerPersonality.PROFESSIONAL.value -> 75
            PlayerPersonality.TEAM_PLAYER.value -> 80
            PlayerPersonality.AMBITIOUS.value -> 50
            PlayerPersonality.TEMPERAMENTAL.value -> 40
            else -> 60
        }
    }

    /**
     * Calculate adaptability from player's attributes
     */
    private fun calculateAdaptabilityFromPlayer(player: PlayersEntity): Int {
        return when (player.personalityType) {
            PlayerPersonality.AMBITIOUS.value -> 85
            PlayerPersonality.PROFESSIONAL.value -> 80
            PlayerPersonality.NATURAL_LEADER.value -> 90
            PlayerPersonality.TEAM_PLAYER.value -> 75
            PlayerPersonality.LOYAL.value -> 60
            PlayerPersonality.TEMPERAMENTAL.value -> 45
            else -> 65 // Default adaptability for any other personality
        }
    }

    /**
     * Calculate impact rating for player transitioning to staff
     * Based on playing career achievements
     */
    private fun calculatePlayerToStaffImpact(player: PlayersEntity): Int {
        var impact = 50  // Base impact

        // Add based on career statistics
        impact += (player.matches / 50).coerceAtMost(15)  // Up to +15 for appearances
        impact += (player.trophies * 2).coerceAtMost(10)  // Up to +10 for trophies
        impact += (player.goals / 20).coerceAtMost(10)    // Up to +10 for goals
        impact += (player.manOfMatch / 5).coerceAtMost(5) // Up to +5 for MOTM

        // Add based on leadership
        if (player.isCaptain) impact += 10
        if (player.isViceCaptain) impact += 5

        // Add based on personality
        when (player.personalityType) {
            PlayerPersonality.NATURAL_LEADER.value -> impact += 15
            PlayerPersonality.PROFESSIONAL.value -> impact += 10
            PlayerPersonality.TEAM_PLAYER.value -> impact += 8
        }

        return impact.coerceIn(40, 95)
    }

    // ============ STAFF MANAGEMENT ============

    /**
     * Fire staff member
     */
    suspend fun fireStaff(staffId: Int): Boolean {
        val staff = staffDao.getById(staffId) ?: return false
        staffDao.delete(staff)
        return true
    }

    /**
     * Transfer staff to another team
     */
    suspend fun transferStaff(staffId: Int, newTeamName: String): StaffEntity? {
        val staff = staffDao.getById(staffId) ?: return null

        val updated = staff.copy(
            teamName = newTeamName,
            loyalty = (staff.loyalty - 10).coerceIn(0, 100)  // Loyalty decreases when moving
        )

        staffDao.update(updated)
        return updated
    }

    /**
     * Renew staff contract
     */
    suspend fun renewContract(staffId: Int, years: Int, newSalary: Int? = null): StaffEntity? {
        val staff = staffDao.getById(staffId) ?: return null

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, years)
        val newEndDate = dateFormat.format(calendar.time)

        val updated = staff.copy(
            contractEndDate = newEndDate,
            salary = newSalary ?: staff.salary,
            loyalty = (staff.loyalty + 5).coerceIn(0, 100)  // Loyalty increases with new contract
        )

        staffDao.update(updated)
        return updated
    }

    /**
     * Increase staff experience (called at end of season)
     */
    suspend fun increaseExperience(staffId: Int, years: Int = 1): StaffEntity? {
        val staff = staffDao.getById(staffId) ?: return null

        val newExperience = staff.experienceLevel + years
        val newImpact = (staff.impactRating + 1).coerceAtMost(95)  // Small impact increase with experience

        val updated = staff.copy(
            experienceLevel = newExperience,
            impactRating = newImpact,
            mentoringAbility = (staff.mentoringAbility + 1).coerceAtMost(100)
        )

        staffDao.update(updated)
        return updated
    }

    /**
     * Promote staff member to head of department
     */
    suspend fun promoteToHeadOfDepartment(staffId: Int): StaffEntity? {
        val staff = staffDao.getById(staffId) ?: return null

        val updated = staff.copy(
            isHeadOfDepartment = true,
            impactRating = (staff.impactRating + 5).coerceAtMost(100),
            salary = (staff.salary * 1.2).toInt()  // 20% salary increase
        )

        staffDao.update(updated)
        return updated
    }

    // ============ RECOMMENDATION SYSTEM ============

    /**
     * Get recommended staff for a team based on needs
     */
    suspend fun getRecommendedStaff(
        teamName: String,
        staffType: String? = null,
        minImpact: Int = 60
    ): List<StaffEntity> {
        val allStaff = staffDao.getAll().firstOrNull() ?: return emptyList()

        return allStaff.filter { staff ->
            staff.teamName != teamName &&  // Not already at the team
                    (staffType == null || staff.staffType == staffType) &&
                    staff.impactRating >= minImpact
        }.sortedByDescending { it.impactRating }
    }

    /**
     * Get recommended former players who could become staff
     */
    suspend fun getRecommendedFormerPlayers(teamName: String): List<PlayersEntity> {
        val retiredPlayers = playersRepository.getRetiredPlayers().firstOrNull() ?: return emptyList()
        val existingStaff = staffDao.getStaffByTeam(teamName).firstOrNull()?.map { it.previousPlayer } ?: emptyList()

        return retiredPlayers.filter { player ->
            player.name !in existingStaff &&  // Not already converted
                    player.leadership >= 60  // Minimum leadership to become staff
        }.sortedByDescending { it.rating }
    }

    // ============ STATISTICS ============

    fun getStaffStatisticsByTeam(teamName: String): Flow<List<StaffTypeStatistics>> =
        staffDao.getStaffStatisticsByTeam(teamName)

    fun getRoleDistribution(): Flow<List<RoleDistribution>> =
        staffDao.getRoleDistribution()

    fun getSpecializationDistribution(): Flow<List<SpecializationDistribution>> =
        staffDao.getSpecializationDistribution()

    suspend fun getAverageImpactRating(teamName: String): Double? =
        staffDao.getAverageImpactRating(teamName)

    // ============ DASHBOARD ============

    suspend fun getStaffDashboard(teamName: String): StaffDashboard {
        val allStaff = staffDao.getStaffByTeam(teamName).firstOrNull() ?: emptyList()

        val coaches = allStaff.filter { it.staffType == "COACHING" }
        val scouts = allStaff.filter { it.staffType == "SCOUTING" }
        val medical = allStaff.filter { it.staffType == "MEDICAL" }
        val admin = allStaff.filter { it.staffType == "ADMIN" }

        val topPerformers = allStaff.sortedByDescending { it.impactRating }.take(5)
        val formerPlayers = allStaff.filter { it.isFormerPlayer }

        val totalSalary = allStaff.sumOf { it.salary }
        val averageImpact = if (allStaff.isNotEmpty())
            allStaff.map { it.impactRating }.average() else 0.0

        val departmentHeads = allStaff.filter { it.isHeadOfDepartment }

        return StaffDashboard(
            totalStaff = allStaff.size,
            coaches = coaches.size,
            scouts = scouts.size,
            medicalStaff = medical.size,
            adminStaff = admin.size,
            formerPlayers = formerPlayers.size,
            departmentHeads = departmentHeads.size,
            totalSalary = totalSalary,
            averageImpact = averageImpact,
            topPerformers = topPerformers,
            staffList = allStaff
        )
    }
}

// ============ DATA CLASSES ============

data class StaffDashboard(
    val totalStaff: Int,
    val coaches: Int,
    val scouts: Int,
    val medicalStaff: Int,
    val adminStaff: Int,
    val formerPlayers: Int,
    val departmentHeads: Int,
    val totalSalary: Int,
    val averageImpact: Double,
    val topPerformers: List<StaffEntity>,
    val staffList: List<StaffEntity>
)