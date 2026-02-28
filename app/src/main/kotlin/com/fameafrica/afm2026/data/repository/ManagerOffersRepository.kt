package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.ManagerOffersDao
import com.fameafrica.afm2026.data.database.dao.ManagersDao
import com.fameafrica.afm2026.data.database.dao.TeamsDao
import com.fameafrica.afm2026.data.database.dao.LeaguesDao
import com.fameafrica.afm2026.data.database.dao.OfferWithDetails
import com.fameafrica.afm2026.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Calendar

@Singleton
class ManagerOffersRepository @Inject constructor(
    private val managerOffersDao: ManagerOffersDao,
    private val managersDao: ManagersDao,
    private val teamsDao: TeamsDao,
    private val leaguesDao: LeaguesDao
) {

    // ============ BASIC CRUD ============

    fun getAllOffers(): Flow<List<ManagerOffersEntity>> = managerOffersDao.getAll()

    suspend fun getOfferById(id: Int): ManagerOffersEntity? = managerOffersDao.getById(id)

    suspend fun insertOffer(offer: ManagerOffersEntity) = managerOffersDao.insert(offer)

    suspend fun insertAllOffers(offers: List<ManagerOffersEntity>) = managerOffersDao.insertAll(offers)

    suspend fun updateOffer(offer: ManagerOffersEntity) = managerOffersDao.update(offer)

    suspend fun deleteOffer(offer: ManagerOffersEntity) = managerOffersDao.delete(offer)

    suspend fun deleteExpiredOffers() {
        val currentTime = System.currentTimeMillis()
        managerOffersDao.deleteExpiredOffers(currentTime)
    }

    // ============ OFFER MANAGEMENT ============

    /**
     * GENERATE INITIAL OFFER FOR NEW USER
     *
     * This is the CRITICAL function that ensures a new user receives an offer
     * from a team in LEVEL 5 LEAGUE (lowest professional tier)
     *
     * This allows the user to start at the bottom and work their way up:
     * Level 5 → Level 4 → Level 3 → Level 2 → Level 1
     */
    suspend fun generateInitialOfferForNewUser(managerId: Int): ManagerOffersEntity? {
        val manager = managersDao.getById(managerId) ?: return null

        // Find all teams in Level 5 leagues
        val level5Leagues = leaguesDao.getLeaguesByLevel(5).firstOrNull() ?: emptyList()

        if (level5Leagues.isEmpty()) return null

        // Select a random Level 5 league
        val selectedLeague = level5Leagues.random()

        // Get teams in this league that need a manager
        val teamsInLeague = teamsDao.getTeamsByLeague(selectedLeague.name).firstOrNull() ?: emptyList()
        val teamsWithoutManager = teamsInLeague.filter { it.managerId == null }

        if (teamsWithoutManager.isEmpty()) return null

        // Select a random team from the league
        val selectedTeam = teamsWithoutManager.random()

        // Calculate offer salary (low for Level 5)
        val baseSalary = 500000 // 500k
        val salaryVariation = (100000..300000).random()
        val offeredSalary = baseSalary + salaryVariation

        // Create expiry date (7 days from now)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val expiryDate = calendar.timeInMillis

        val offer = ManagerOffersEntity(
            managerId = manager.id,
            managerName = manager.name,
            offeringTeam = selectedTeam.name,
            offeringTeamId = selectedTeam.id,
            leagueName = selectedLeague.name,
            leagueLevel = 5,
            offeredSalary = offeredSalary,
            contractYears = 2,
            transferFee = 0, // No transfer fee for first job
            offerType = ManagerOfferType.HEAD_COACH.value,
            offerDate = System.currentTimeMillis(),
            expiryDate = expiryDate,
            isMidSeason = false,
            isPromotion = false,
            message = "We've been following your career and believe you have the potential to grow with us. " +
                    "Join us in the ${selectedLeague.name} and help build something special!"
        )

        managerOffersDao.insert(offer)
        return offer
    }

    /**
     * GENERATE PROMOTION OFFERS
     *
     * This function generates offers for managers who are performing well
     * Offers come from teams in HIGHER LEVEL LEAGUES
     *
     * Level 5 → Level 4 offers (promotion)
     * Level 4 → Level 3 offers (promotion)
     * Level 3 → Level 2 offers (promotion)
     * Level 2 → Level 1 offers (promotion)
     */
    suspend fun generatePromotionOffers(managerId: Int): List<ManagerOffersEntity> {
        val manager = managersDao.getById(managerId) ?: return emptyList()
        if (!manager.isEmployed) return emptyList()

        // Get current team and league level
        val currentTeam = teamsDao.getById(manager.teamId ?: return emptyList()) ?: return emptyList()
        val currentLeague = leaguesDao.getByName(currentTeam.league)
        val currentLevel = currentLeague?.level ?: return emptyList()

        // Can't promote beyond Level 1
        if (currentLevel <= 1) return emptyList()

        val targetLevel = currentLevel - 1

        // Calculate win percentage
        val winRate = if (manager.matchesManaged > 0) {
            (manager.wins.toDouble() / manager.matchesManaged * 100)
        } else 0.0

        // Check if manager is performing well enough for promotion
        val performanceThreshold = when (targetLevel) {
            4 -> 55.0  // Need 55% win rate for Level 5 → 4
            3 -> 60.0  // Need 60% win rate for Level 4 → 3
            2 -> 65.0  // Need 65% win rate for Level 3 → 2
            1 -> 70.0  // Need 70% win rate for Level 2 → 1
            else -> 75.0
        }

        // Not performing well enough for promotion
        if (winRate < performanceThreshold) return emptyList()

        // Find teams in target level league that need managers
        val targetLeagues = leaguesDao.getLeaguesByLevel(targetLevel).firstOrNull() ?: emptyList()
        val offers = mutableListOf<ManagerOffersEntity>()

        targetLeagues.forEach { league ->
            val teamsInLeague = teamsDao.getTeamsByLeague(league.name).firstOrNull() ?: emptyList()
            val teamsWithoutManager = teamsInLeague.filter { it.managerId == null }

            // Take up to 2 random teams from each league
            val selectedTeams = teamsWithoutManager.shuffled().take(2)

            selectedTeams.forEach { team ->
                // Calculate salary (higher for higher levels)
                val baseSalary = when (targetLevel) {
                    1 -> 5000000  // 5M for Level 1
                    2 -> 3000000  // 3M for Level 2
                    3 -> 2000000  // 2M for Level 3
                    4 -> 1200000  // 1.2M for Level 4
                    else -> 800000 // 800k for Level 5
                }

                val salaryVariation = (200000..500000).random()
                val offeredSalary = baseSalary + salaryVariation

                // Calculate transfer fee if manager is under contract
                val transferFee = if (manager.contractEndDate != null) {
                    manager.calculateTransferFee()
                } else 0

                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 7)
                val expiryDate = calendar.timeInMillis

                val offer = ManagerOffersEntity(
                    managerId = manager.id,
                    managerName = manager.name,
                    offeringTeam = team.name,
                    offeringTeamId = team.id,
                    leagueName = league.name,
                    leagueLevel = targetLevel,
                    offeredSalary = offeredSalary,
                    contractYears = 3,
                    transferFee = transferFee,
                    offerType = ManagerOfferType.HEAD_COACH.value,
                    offerDate = System.currentTimeMillis(),
                    expiryDate = expiryDate,
                    isMidSeason = false,
                    isPromotion = true,
                    message = buildPromotionMessage(manager, league, targetLevel)
                )

                managerOffersDao.insert(offer)
                offers.add(offer)
            }
        }

        return offers
    }

    /**
     * GENERATE MID-SEASON OFFERS
     *
     * For exceptional performance, managers can receive offers mid-season
     * This allows for faster career progression
     */
    suspend fun generateMidSeasonOffers(managerId: Int): List<ManagerOffersEntity> {
        val manager = managersDao.getById(managerId) ?: return emptyList()
        if (!manager.isEmployed) return emptyList()

        // Only generate mid-season offers for managers with exceptional form
        if (manager.performanceRating < 80) return emptyList()

        val currentTeam = teamsDao.getById(manager.teamId ?: return emptyList())
        val currentLeague = currentTeam?.let { leaguesDao.getByName(it.league) }
        val currentLevel = currentLeague?.level ?: return emptyList()

        // Can't promote beyond Level 1
        if (currentLevel <= 1) return emptyList()

        // Mid-season offers can jump 1 level or sometimes 2 for exceptional performance
        val targetLevels = when {
            manager.performanceRating >= 90 && currentLevel >= 3 -> listOf(currentLevel - 2)
            else -> listOf(currentLevel - 1)
        }

        val offers = mutableListOf<ManagerOffersEntity>()

        targetLevels.forEach { targetLevel ->
            val targetLeagues = leaguesDao.getLeaguesByLevel(targetLevel).firstOrNull() ?: emptyList()

            targetLeagues.forEach { league ->
                val teamsInLeague = teamsDao.getTeamsByLeague(league.name).firstOrNull() ?: emptyList()

                // Find teams that are underperforming and might sack their manager
                val underperformingTeams = teamsInLeague.filter { team ->
                    // Teams in bottom 3 of the table
                    val position = getTeamLeaguePosition(team.id, league.name)
                    position != null && position >= 14
                }

                val teamsToApproach = underperformingTeams.take(1)

                teamsToApproach.forEach { team ->
                    val baseSalary = when (targetLevel) {
                        1 -> 5500000
                        2 -> 3500000
                        3 -> 2500000
                        4 -> 1500000
                        else -> 1000000
                    }

                    val offeredSalary = baseSalary + (300000..600000).random()

                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, 5) // Shorter expiry for mid-season
                    val expiryDate = calendar.timeInMillis

                    val offer = ManagerOffersEntity(
                        managerId = manager.id,
                        managerName = manager.name,
                        offeringTeam = team.name,
                        offeringTeamId = team.id,
                        leagueName = league.name,
                        leagueLevel = targetLevel,
                        offeredSalary = offeredSalary,
                        contractYears = 2,
                        transferFee = manager.calculateTransferFee() * 2, // Higher fee mid-season
                        offerType = ManagerOfferType.HEAD_COACH.value,
                        offerDate = System.currentTimeMillis(),
                        expiryDate = expiryDate,
                        isMidSeason = true,
                        isPromotion = true,
                        message = "We're in a difficult position and believe you're the man to turn things around. " +
                                "Join us immediately and save our season!"
                    )

                    managerOffersDao.insert(offer)
                    offers.add(offer)
                }
            }
        }

        return offers
    }

    /**
     * PROCESS END OF SEASON - Generate promotion offers for all eligible managers
     */
    suspend fun processEndOfSeason() {
        // Get all employed managers
        val employedManagers = managersDao.getEmployedManagers().firstOrNull() ?: emptyList()

        employedManagers.forEach { manager ->
            // Generate promotion offers
            generatePromotionOffers(manager.id)

            // Check if manager won the league
            val currentTeam = teamsDao.getById(manager.teamId ?: return@forEach)
            val league = currentTeam?.let { leaguesDao.getByName(it.league) }

            if (league != null) {
                val leaguePosition = getTeamLeaguePosition(currentTeam.id, league.name)

                // If manager won the league (position 1), generate offers from higher level
                if (leaguePosition == 1) {
                    generatePromotionOffers(manager.id)

                    // Also maybe generate a "big club" offer
                    generateBigClubOffer(manager)
                }

                // If manager got promoted (position 1-2 in lower leagues)
                if (league.level in 2..5 && leaguePosition in 1..2) {
                    generatePromotionOffers(manager.id)
                }
            }
        }
    }

    /**
     * GENERATE BIG CLUB OFFER - For managers who win top league
     */
    private suspend fun generateBigClubOffer(manager: ManagersEntity) {
        // Find top clubs in Level 1 with manager vacancies
        val topLeagues = leaguesDao.getLeaguesByLevel(1).firstOrNull() ?: emptyList()

        topLeagues.forEach { league ->
            val topTeams = teamsDao.getTeamsByLeague(league.name).firstOrNull()
                ?.filter { it.reputation >= 70 && it.managerId == null }
                ?.take(1)

            topTeams?.forEach { team ->
                val offer = ManagerOffersEntity(
                    managerId = manager.id,
                    managerName = manager.name,
                    offeringTeam = team.name,
                    offeringTeamId = team.id,
                    leagueName = league.name,
                    leagueLevel = 1,
                    offeredSalary = 8000000 + (1000000..3000000).random(),
                    contractYears = 4,
                    transferFee = manager.calculateTransferFee() * 3,
                    offerType = ManagerOfferType.HEAD_COACH.value,
                    offerDate = System.currentTimeMillis(),
                    expiryDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 10) }.timeInMillis,
                    isMidSeason = false,
                    isPromotion = true,
                    message = "Your achievements have not gone unnoticed. We want you to lead our club to glory."
                )

                managerOffersDao.insert(offer)
            }
        }
    }

    /**
     * Helper function to get team's league position
     */
    private suspend fun getTeamLeaguePosition(teamId: Int, leagueName: String): Int? {
        // This would normally query league_standings
        // For now, return a placeholder
        return 1
    }

    private fun buildPromotionMessage(manager: ManagersEntity, league: LeaguesEntity, level: Int): String {
        return when (level) {
            1 -> "Congratulations on your success! We believe you're ready for the top flight. " +
                    "Join us in the ${league.name} and compete for championships."
            2 -> "Your tactical acumen has impressed us. We'd love you to take the next step in your career " +
                    "with us in the ${league.name}."
            3 -> "You've proven yourself at the lower levels. Now it's time to test yourself in the ${league.name}."
            4 -> "We've been impressed with your work. Come help us push for promotion in the ${league.name}."
            else -> "We see great potential in you. Join our project in the ${league.name}."
        }
    }

    // ============ OFFER RESPONSE HANDLING ============

    suspend fun acceptOffer(offerId: Int): Boolean {
        val offer = managerOffersDao.getById(offerId) ?: return false
        if (offer.status != "pending" || offer.isExpired) return false

        // Update offer status
        val updatedOffer = offer.copy(status = "accepted")
        managerOffersDao.update(updatedOffer)

        // Get current manager
        val manager = managersDao.getById(offer.managerId) ?: return false

        // If manager is currently employed, they leave their current club
        if (manager.isEmployed) {
            managersDao.update(manager.leaveClub())
        }

        // Sign with new club
        val updatedManager = manager.signContract(
            teamId = offer.offeringTeamId,
            salary = offer.offeredSalary,
            contractYears = offer.contractYears
        )
        managersDao.update(updatedManager)

        // Reject all other pending offers for this manager
        val otherOffers = managerOffersDao.getPendingOffersByManager(manager.id)
            .firstOrNull() ?: emptyList()

        otherOffers.forEach { otherOffer ->
            if (otherOffer.id != offerId) {
                managerOffersDao.update(otherOffer.copy(status = "rejected"))
            }
        }

        return true
    }

    suspend fun rejectOffer(offerId: Int): Boolean {
        val offer = managerOffersDao.getById(offerId) ?: return false
        if (offer.status != "pending") return false

        val updatedOffer = offer.copy(status = "rejected")
        managerOffersDao.update(updatedOffer)
        return true
    }

    // ============ QUERIES ============

    fun getOffersByManager(managerId: Int): Flow<List<ManagerOffersEntity>> =
        managerOffersDao.getOffersByManager(managerId)

    fun getPendingOffersByManager(managerId: Int): Flow<List<ManagerOffersEntity>> =
        managerOffersDao.getPendingOffersByManager(managerId)

    fun getOffersByLeagueLevel(level: Int): Flow<List<ManagerOffersEntity>> =
        managerOffersDao.getOffersByLeagueLevel(level)

    fun getPromotionOffers(): Flow<List<ManagerOffersEntity>> =
        managerOffersDao.getPromotionOffers()

    suspend fun getOfferWithDetails(offerId: Int): OfferWithDetails? =
        managerOffersDao.getOfferWithDetails(offerId)

    // ============ DASHBOARD ============

    suspend fun getManagerOffersDashboard(managerId: Int): ManagerOffersDashboard {
        val pendingOffers = managerOffersDao.getPendingOffersByManager(managerId)
            .firstOrNull() ?: emptyList()

        val acceptedOffers = managerOffersDao.getAcceptedOffersByManager(managerId)
            .firstOrNull() ?: emptyList()

        val rejectedOffers = managerOffersDao.getRejectedOffersByManager(managerId)
            .firstOrNull() ?: emptyList()

        val level5Offers = pendingOffers.filter { it.leagueLevel == 5 }
        val level4Offers = pendingOffers.filter { it.leagueLevel == 4 }
        val level3Offers = pendingOffers.filter { it.leagueLevel == 3 }
        val level2Offers = pendingOffers.filter { it.leagueLevel == 2 }
        val level1Offers = pendingOffers.filter { it.leagueLevel == 1 }

        return ManagerOffersDashboard(
            totalOffers = pendingOffers.size + acceptedOffers.size + rejectedOffers.size,
            pendingCount = pendingOffers.size,
            acceptedCount = acceptedOffers.size,
            rejectedCount = rejectedOffers.size,
            pendingOffers = pendingOffers,
            level5Offers = level5Offers.size,
            level4Offers = level4Offers.size,
            level3Offers = level3Offers.size,
            level2Offers = level2Offers.size,
            level1Offers = level1Offers.size,
            highestLevelOffer = pendingOffers.minByOrNull { it.leagueLevel }?.leagueLevel,
            bestSalaryOffer = pendingOffers.maxByOrNull { it.offeredSalary }
        )
    }
}

// ============ DATA CLASSES ============

data class ManagerOffersDashboard(
    val totalOffers: Int,
    val pendingCount: Int,
    val acceptedCount: Int,
    val rejectedCount: Int,
    val pendingOffers: List<ManagerOffersEntity>,
    val level5Offers: Int,
    val level4Offers: Int,
    val level3Offers: Int,
    val level2Offers: Int,
    val level1Offers: Int,
    val highestLevelOffer: Int?,
    val bestSalaryOffer: ManagerOffersEntity?
)