package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.FinancesDao
import com.fameafrica.afm2026.data.database.dao.TeamsDao
import com.fameafrica.afm2026.data.database.dao.LeaguesDao
import com.fameafrica.afm2026.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FinancesRepository @Inject constructor(
    private val financesDao: FinancesDao,
    private val teamsDao: TeamsDao,
    private val leaguesDao: LeaguesDao
) {

    // ============ BASIC CRUD ============

    fun getAllFinances(): Flow<List<FinancesEntity>> = financesDao.getAll()

    suspend fun getFinancesById(id: Int): FinancesEntity? = financesDao.getById(id)

    suspend fun getTeamFinances(teamId: Int, season: String): FinancesEntity? =
        financesDao.getByTeamAndSeason(teamId, season)

    suspend fun getTeamFinancesHistory(teamId: Int): Flow<List<FinancesEntity>> =
        financesDao.getTeamFinances(teamId)

    suspend fun insertFinances(finances: FinancesEntity) = financesDao.insert(finances)

    suspend fun updateFinances(finances: FinancesEntity) = financesDao.update(finances)

    // ============ INITIALIZE FINANCES FOR NEW SEASON ============

    /**
     * Initialize finances for all teams at the start of a new season
     * Based on realistic African football economics
     */
    suspend fun initializeSeasonFinances(season: String) {
        val teams = teamsDao.getAll().firstOrNull() ?: return

        for (team in teams) {
            // Check if finances already exist for this team/season
            val existing = financesDao.getByTeamAndSeason(team.id, season)
            if (existing != null) continue

            val league = leaguesDao.getLeagueByName(team.league)
            val financialTier = determineFinancialTier(team, league)

            val baseRevenue = calculateBaseRevenue(team, league, financialTier)
            val baseBudget = calculateBaseBudget(baseRevenue, team.reputation)

            val finances = FinancesEntity(
                teamId = team.id,
                teamName = team.name,
                season = season,
                revenue = baseRevenue,
                budget = baseBudget,
                bankBalance = baseBudget / 2, // Start with half budget in bank
                financialTier = financialTier.value,
                sponsorshipRevenue = calculateSponsorshipRevenue(team, league, financialTier),
                broadcastingRevenue = calculateBroadcastingRevenue(league, financialTier),
                matchdayRevenue = calculateMatchdayRevenue(team, league, financialTier),
                merchandiseRevenue = calculateMerchandiseRevenue(team, financialTier),
                membershipRevenue = calculateMembershipRevenue(team, financialTier),
                wageBill = calculateWageBill(team, league, financialTier),
                staffWages = calculateStaffWages(team, financialTier),
                operationalCosts = calculateOperationalCosts(financialTier),
                lastUpdated = getCurrentDate()
            )

            // Calculate total expenses and profit/loss
            val updatedFinances = finances.copy(
                expenses = finances.wageBill + finances.staffWages + finances.operationalCosts,
                profitLoss = finances.revenue - (finances.wageBill + finances.staffWages + finances.operationalCosts)
            )

            financesDao.insert(updatedFinances)
        }
    }

    private fun determineFinancialTier(team: TeamsEntity, league: LeaguesEntity?): FinancialTier {
        return when {
            // North African Giants (Egypt, Morocco, Algeria, Tunisia)
            team.name.contains("Al Ahly") || team.name.contains("Zamalek SC") ||
                    team.name.contains("Pyramids FC") || team.name.contains("Wydad Athletic Club") ||
                    team.name.contains("Espérance") || team.name.contains("RSB Berkane") ||
                    team.name.contains("Mamelodi Sundowns") -> FinancialTier.RICH

            // Top clubs in major leagues
            (team.reputation >= 75 && league?.level == 1) ||
                    team.name.contains("Young Africans") || team.name.contains("Simba SC") ||
                    team.name.contains("Kaizer Chiefs") || team.name.contains("Orlando Pirates") ||
                    team.name.contains("Enyimba FC") || team.name.contains("TP Mazembe") ||
                    team.name.contains("AS Vita Club") -> FinancialTier.UPPER_MIDDLE

            // Most top division clubs
            league?.level == 1 && team.reputation >= 50 -> FinancialTier.MIDDLE

            // Lower division or lower reputation clubs
            league?.level == 2 || (league?.level == 1 && team.reputation < 50) -> FinancialTier.LOWER

            // Everything else
            else -> FinancialTier.POOR
        }
    }

    private fun calculateBaseRevenue(team: TeamsEntity, league: LeaguesEntity?, tier: FinancialTier): Long {
        return when (tier) {
            FinancialTier.RICH -> Random.nextLong(50_000_000, 150_000_000)  // $50M-$150M
            FinancialTier.UPPER_MIDDLE -> Random.nextLong(20_000_000, 50_000_000)  // $20M-$50M
            FinancialTier.MIDDLE -> Random.nextLong(8_000_000, 20_000_000)   // $8M-$20M
            FinancialTier.LOWER -> Random.nextLong(2_000_000, 8_000_000)    // $2M-$8M
            FinancialTier.POOR -> Random.nextLong(500_000, 2_000_000)       // $0.5M-$2M
        }
    }

    private fun calculateBaseBudget(revenue: Long, reputation: Int): Long {
        // Budget is typically 60-80% of revenue
        val budgetPercentage = 0.6 + (reputation / 500.0)
        return (revenue * budgetPercentage).toLong()
    }

    private fun calculateSponsorshipRevenue(team: TeamsEntity, league: LeaguesEntity?, tier: FinancialTier): Long {
        return when (tier) {
            FinancialTier.RICH -> Random.nextLong(10_000_000, 30_000_000)
            FinancialTier.UPPER_MIDDLE -> Random.nextLong(5_000_000, 12_000_000)
            FinancialTier.MIDDLE -> Random.nextLong(2_000_000, 6_000_000)
            FinancialTier.LOWER -> Random.nextLong(500_000, 2_000_000)
            FinancialTier.POOR -> Random.nextLong(100_000, 500_000)
        }
    }

    private fun calculateBroadcastingRevenue(league: LeaguesEntity?, tier: FinancialTier): Long {
        return when (tier) {
            FinancialTier.RICH -> Random.nextLong(8_000_000, 20_000_000)
            FinancialTier.UPPER_MIDDLE -> Random.nextLong(3_000_000, 8_000_000)
            FinancialTier.MIDDLE -> Random.nextLong(1_000_000, 3_000_000)
            FinancialTier.LOWER -> Random.nextLong(300_000, 1_000_000)
            FinancialTier.POOR -> Random.nextLong(50_000, 300_000)
        }
    }

    private fun calculateMatchdayRevenue(team: TeamsEntity, league: LeaguesEntity?, tier: FinancialTier): Long {
        val stadiumCapacity = team.stadiumCapacity
        val fanLoyalty = team.fanLoyalty
        val averageTicketPrice = when (tier) {
            FinancialTier.RICH -> 50  // $50 average ticket
            FinancialTier.UPPER_MIDDLE -> 30
            FinancialTier.MIDDLE -> 20
            FinancialTier.LOWER -> 10
            FinancialTier.POOR -> 5
        }

        // Estimate matches per season (league + cups)
        val homeMatches = 19 // 19 league home games + cup matches

        val averageAttendance = (stadiumCapacity * fanLoyalty / 100.0).toInt()

        return (averageAttendance * averageTicketPrice * homeMatches).toLong()
    }

    private fun calculateMerchandiseRevenue(team: TeamsEntity, tier: FinancialTier): Long {
        val fanLoyalty = team.fanLoyalty
        return when (tier) {
            FinancialTier.RICH -> (fanLoyalty * 200_000).toLong()
            FinancialTier.UPPER_MIDDLE -> (fanLoyalty * 100_000).toLong()
            FinancialTier.MIDDLE -> (fanLoyalty * 50_000).toLong()
            FinancialTier.LOWER -> (fanLoyalty * 20_000).toLong()
            FinancialTier.POOR -> (fanLoyalty * 5_000).toLong()
        }
    }

    private fun calculateMembershipRevenue(team: TeamsEntity, tier: FinancialTier): Long {
        val fanLoyalty = team.fanLoyalty
        return when (tier) {
            FinancialTier.RICH -> (fanLoyalty * 50_000).toLong()
            FinancialTier.UPPER_MIDDLE -> (fanLoyalty * 25_000).toLong()
            FinancialTier.MIDDLE -> (fanLoyalty * 10_000).toLong()
            FinancialTier.LOWER -> (fanLoyalty * 5_000).toLong()
            FinancialTier.POOR -> (fanLoyalty * 1_000).toLong()
        }
    }

    private fun calculateWageBill(team: TeamsEntity, league: LeaguesEntity?, tier: FinancialTier): Long {
        // Wage bill is typically 40-60% of revenue
        val squadSize = 25
        val averageWage = when (tier) {
            FinancialTier.RICH -> Random.nextLong(500_000, 2_500_000)  // $500k-$2.5M per player
            FinancialTier.UPPER_MIDDLE -> Random.nextLong(200_000, 500_000)
            FinancialTier.MIDDLE -> Random.nextLong(80_000, 200_000)
            FinancialTier.LOWER -> Random.nextLong(30_000, 80_000)
            FinancialTier.POOR -> Random.nextLong(10_000, 30_000)
        }

        return averageWage * squadSize
    }

    private fun calculateStaffWages(team: TeamsEntity, tier: FinancialTier): Long {
        val staffSize = when (tier) {
            FinancialTier.RICH -> 30
            FinancialTier.UPPER_MIDDLE -> 20
            FinancialTier.MIDDLE -> 15
            FinancialTier.LOWER -> 10
            FinancialTier.POOR -> 5
        }

        val averageStaffWage = when (tier) {
            FinancialTier.RICH -> 100_000
            FinancialTier.UPPER_MIDDLE -> 60_000
            FinancialTier.MIDDLE -> 35_000
            FinancialTier.LOWER -> 20_000
            FinancialTier.POOR -> 10_000
        }

        return (averageStaffWage * staffSize).toLong()
    }

    private fun calculateOperationalCosts(tier: FinancialTier): Long {
        return when (tier) {
            FinancialTier.RICH -> Random.nextLong(5_000_000, 10_000_000)
            FinancialTier.UPPER_MIDDLE -> Random.nextLong(2_000_000, 5_000_000)
            FinancialTier.MIDDLE -> Random.nextLong(1_000_000, 2_000_000)
            FinancialTier.LOWER -> Random.nextLong(300_000, 1_000_000)
            FinancialTier.POOR -> Random.nextLong(100_000, 300_000)
        }
    }

    // ============ REVENUE UPDATES ============

    suspend fun addSponsorshipRevenue(teamId: Int, season: String, amount: Long) {
        financesDao.addSponsorshipRevenue(teamId, season, amount)
        updateFinancialTier(teamId, season)
    }

    suspend fun addBroadcastingRevenue(teamId: Int, season: String, amount: Long) {
        financesDao.addBroadcastingRevenue(teamId, season, amount)
        updateFinancialTier(teamId, season)
    }

    suspend fun addMatchdayRevenue(teamId: Int, season: String, amount: Long) {
        financesDao.addMatchdayRevenue(teamId, season, amount)
        updateFinancialTier(teamId, season)
    }

    suspend fun addPrizeMoney(teamId: Int, season: String, amount: Long) {
        financesDao.addPrizeMoney(teamId, season, amount)
        updateFinancialTier(teamId, season)
    }

    suspend fun addContinentalPrizeMoney(teamId: Int, season: String, amount: Long) {
        financesDao.addContinentalPrizeMoney(teamId, season, amount)
        updateFinancialTier(teamId, season)
    }

    suspend fun addPlayerSale(teamId: Int, season: String, amount: Long) {
        financesDao.addPlayerSale(teamId, season, amount)
        updateFinancialTier(teamId, season)
    }

    // ============ EXPENSE UPDATES ============

    suspend fun addWages(teamId: Int, season: String, amount: Long) {
        financesDao.addWages(teamId, season, amount)
        updateFinancialTier(teamId, season)
    }

    suspend fun addStaffWages(teamId: Int, season: String, amount: Long) {
        financesDao.addStaffWages(teamId, season, amount)
        updateFinancialTier(teamId, season)
    }

    suspend fun addTransferSpending(teamId: Int, season: String, amount: Long) {
        financesDao.addTransferSpending(teamId, season, amount)
        updateFinancialTier(teamId, season)
    }

    suspend fun addInfrastructureCost(teamId: Int, season: String, amount: Long) {
        financesDao.addInfrastructureCost(teamId, season, amount)
        updateFinancialTier(teamId, season)
    }

    // ============ BUDGET MANAGEMENT ============

    suspend fun updateTransferBudget(teamId: Int, season: String, newBudget: Long) {
        financesDao.updateBudget(teamId, season, newBudget)
    }

    suspend fun canAffordTransfer(teamId: Int, season: String, fee: Long, wages: Long): Boolean {
        val finances = financesDao.getByTeamAndSeason(teamId, season) ?: return false
        return finances.budget >= fee && (finances.bankBalance >= fee + (wages * 12))
    }

    // ============ FINANCIAL TIER UPDATE ============

    private suspend fun updateFinancialTier(teamId: Int, season: String) {
        val finances = financesDao.getByTeamAndSeason(teamId, season) ?: return

        val newTier = when {
            finances.revenue >= 50_000_000 -> FinancialTier.RICH
            finances.revenue >= 20_000_000 -> FinancialTier.UPPER_MIDDLE
            finances.revenue >= 8_000_000 -> FinancialTier.MIDDLE
            finances.revenue >= 2_000_000 -> FinancialTier.LOWER
            else -> FinancialTier.POOR
        }

        financesDao.update(finances.copy(financialTier = newTier.value))
    }

    // ============ END OF SEASON PROCESSING ============

    /**
     * Process end of season financial updates
     * - Calculate profit/loss
     * - Roll over balance to next season
     * - Update financial tiers
     */
    suspend fun processEndOfSeason(oldSeason: String, newSeason: String) {
        val teams = teamsDao.getAll().firstOrNull() ?: return

        for (team in teams) {
            val oldFinances = financesDao.getByTeamAndSeason(team.id, oldSeason) ?: continue

            // Calculate final profit/loss
            val finalProfitLoss = oldFinances.revenue - oldFinances.expenses

            // Update old season with final figures
            val updatedOld = oldFinances.copy(
                profitLoss = finalProfitLoss,
                bankBalance = oldFinances.bankBalance + finalProfitLoss
            )
            financesDao.update(updatedOld)

            // Create new season finances with rollover
            val league = leaguesDao.getLeagueByName(team.league)
            val tier = determineFinancialTier(team, league)

            val newFinances = FinancesEntity(
                teamId = team.id,
                teamName = team.name,
                season = newSeason,
                revenue = calculateBaseRevenue(team, league, tier),
                bankBalance = (updatedOld.bankBalance * 0.8).toLong(), // 80% carried over
                financialTier = tier.value,
                sponsorshipRevenue = calculateSponsorshipRevenue(team, league, tier),
                broadcastingRevenue = calculateBroadcastingRevenue(league, tier),
                matchdayRevenue = calculateMatchdayRevenue(team, league, tier),
                merchandiseRevenue = calculateMerchandiseRevenue(team, tier),
                membershipRevenue = calculateMembershipRevenue(team, tier),
                lastUpdated = getCurrentDate()
            )

            // Calculate budget
            val budget = calculateBaseBudget(newFinances.revenue, team.reputation)

            val finalNew = newFinances.copy(
                budget = budget,
                wageBill = calculateWageBill(team, league, tier),
                staffWages = calculateStaffWages(team, tier),
                operationalCosts = calculateOperationalCosts(tier)
            )

            financesDao.insert(finalNew)
        }
    }

    // ============ UTILITY ============

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // ============ QUERIES ============

    fun getRichestTeams(season: String, limit: Int = 10): Flow<List<FinancesEntity>> =
        financesDao.getRichestTeams(season, limit)

    fun getHighestWageBills(season: String, limit: Int = 10): Flow<List<FinancesEntity>> =
        financesDao.getHighestWageBills(season, limit)

    fun getMostProfitableTeams(season: String, limit: Int = 10): Flow<List<FinancesEntity>> =
        financesDao.getMostProfitableTeams(season, limit)

    fun getTeamsInDebt(season: String): Flow<List<FinancesEntity>> =
        financesDao.getTeamsInDebt(season)

    // ============ DASHBOARD ============

    suspend fun getTeamFinanceDashboard(teamId: Int, season: String): TeamFinanceDashboard {
        val finances = financesDao.getByTeamAndSeason(teamId, season)
            ?: return TeamFinanceDashboard.empty()

        val revenueBreakdown = mapOf(
            "Sponsorship" to finances.sponsorshipRevenue,
            "Broadcasting" to finances.broadcastingRevenue,
            "Matchday" to finances.matchdayRevenue,
            "Merchandise" to finances.merchandiseRevenue,
            "Prize Money" to (finances.prizeMoney + finances.continentalPrizeMoney),
            "Player Sales" to finances.playerSales,
            "Membership" to finances.membershipRevenue
        )

        val expenseBreakdown = mapOf(
            "Player Wages" to finances.wageBill,
            "Staff Wages" to finances.staffWages,
            "Transfer Spending" to finances.transferSpending,
            "Infrastructure" to finances.infrastructureCosts,
            "Operational" to finances.operationalCosts
        )

        return TeamFinanceDashboard(
            teamId = teamId,
            teamName = finances.teamName,
            season = season,
            revenue = finances.revenue,
            expenses = finances.expenses,
            profitLoss = finances.profitLoss,
            budget = finances.budget,
            bankBalance = finances.bankBalance,
            financialTier = finances.financialTier ?: "Unknown",
            financialHealth = finances.financialHealth,
            revenueBreakdown = revenueBreakdown.filter { it.value > 0 },
            expenseBreakdown = expenseBreakdown.filter { it.value > 0 },
            isProfitable = finances.isProfitable
        )
    }
}

// ============ DATA CLASSES ============

data class TeamFinanceDashboard(
    val teamId: Int,
    val teamName: String,
    val season: String,
    val revenue: Long,
    val expenses: Long,
    val profitLoss: Long,
    val budget: Long,
    val bankBalance: Long,
    val financialTier: String,
    val financialHealth: String,
    val revenueBreakdown: Map<String, Long>,
    val expenseBreakdown: Map<String, Long>,
    val isProfitable: Boolean
) {
    companion object {
        fun empty(): TeamFinanceDashboard = TeamFinanceDashboard(
            teamId = 0,
            teamName = "",
            season = "",
            revenue = 0,
            expenses = 0,
            profitLoss = 0,
            budget = 0,
            bankBalance = 0,
            financialTier = "",
            financialHealth = "",
            revenueBreakdown = emptyMap(),
            expenseBreakdown = emptyMap(),
            isProfitable = false
        )
    }
}