package com.fameafrica.afm2026.ui.screen.club

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.database.entities.FinancesEntity
import com.fameafrica.afm2026.data.database.entities.LeaguesEntity
import com.fameafrica.afm2026.data.database.entities.SeasonHistoryEntity
import com.fameafrica.afm2026.data.repository.*
import com.fameafrica.afm2026.ui.theme.FameColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// ============ UI MODELS ============

data class ClubUiState(
    val isLoading: Boolean = true,
    val clubName: String = "",
    val reputationLevel: String = "Local",
    val clubInfo: ClubInfoUiModel? = null,
    val finances: FinancialUiModel? = null,
    val infrastructure: InfrastructureUiModel? = null,
    val revenueBreakdown: List<RevenueItemUiModel> = emptyList(),
    val sponsors: List<SponsorUiModel> = emptyList(),
    val activeUpgrades: List<UpgradeUiModel> = emptyList(),
    val recentHistory: List<HistoryUiModel> = emptyList(),
    val legends: List<LegendUiModel> = emptyList(),
    val quickStats: QuickStatsUiModel? = null
)

data class ClubInfoUiModel(
    val id: Int,
    val name: String,
    val league: String,
    val stadium: String,
    val stadiumCapacity: Int,
    val logoUrl: String?,
    val reputationLevel: String,
    val reputation: Int
)

data class FinancialUiModel(
    val revenue: Long,
    val revenueChange: Double,
    val expenses: Long,
    val expensesChange: Double,
    val profit: Long,
    val profitChange: Double,
    val budget: Long,
    val budgetUsed: Float
)

data class InfrastructureUiModel(
    val stadiumLevel: Int,
    val stadiumCapacity: Int,
    val trainingLevel: Int,
    val trainingEfficiency: Int,
    val youthLevel: Int,
    val youthTalent: Int,
    val medicalLevel: Int,
    val injuryRecovery: Int
)

data class RevenueItemUiModel(
    val label: String,
    val amount: Long,
    val percentage: Int,
    val color: Color
)

data class SponsorUiModel(
    val id: Int,
    val name: String,
    val type: String,
    val value: Long,
    val logoUrl: String?
)

data class UpgradeUiModel(
    val id: Int,
    val name: String,
    val type: String,
    val currentLevel: Int,
    val targetLevel: Int,
    val progress: Int,
    val remainingDays: String
)

data class HistoryUiModel(
    val id: Int,
    val title: String,
    val season: String,
    val achievement: String,
    val type: String
)

data class LegendUiModel(
    val id: Int,
    val name: String,
    val era: String,
    val titles: Int
)

data class QuickStatsUiModel(
    val leaguePosition: Int,
    val overallStars: Int,
    val fanLoyalty: Int,
    val seasons: Int
)

// Game Context for game date tracking
data class GameContextState(
    val gameStateId: Int = 0,
    val gameDate: GameDate? = null,
    val week: Int = 1,
    val season: String = "2024/25",
    val saveName: String = "",
    val lastPlayed: String? = null
)

data class GameDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val weekOfSeason: Int = 0
) {
    fun toDisplayString(): String = String.format("%d-%02d-%02d", year, month, day)
}

@HiltViewModel
class ClubViewModel @Inject constructor(
    private val teamsRepository: TeamsRepository,
    private val financesRepository: FinancesRepository,
    private val infrastructureRepository: InfrastructureUpgradesRepository,
    private val sponsorsRepository: SponsorsRepository,
    private val seasonHistoryRepository: SeasonHistoryRepository,
    private val clubLegendsRepository: ClubLegendsRepository,
    private val gameStateRepository: GameStatesRepository,
    private val leagueStandingsRepository: LeagueStandingsRepository,
    private val leaguesRepository: LeaguesRepository

) : ViewModel() {

    private val _uiState = MutableStateFlow(ClubUiState(isLoading = true))
    val uiState: StateFlow<ClubUiState> = _uiState

    private val _gameContext = MutableStateFlow(GameContextState())
    val gameContext: StateFlow<GameContextState> = _gameContext

    init {
        loadClubData()
    }

    private fun loadClubData() {
        viewModelScope.launch {
            // Get current game state
            val validSaves = gameStateRepository.getValidSaveGames().firstOrNull()
            val currentGameState = validSaves?.maxByOrNull { it.lastPlayed ?: "" }

            if (currentGameState == null) {
                _uiState.value = ClubUiState(isLoading = false)
                return@launch
            }

            val teamId = currentGameState.teamId
            val teamName = currentGameState.teamName
            val season = currentGameState.season
            val week = currentGameState.week

            // Update game context
            _gameContext.value = GameContextState(
                gameStateId = currentGameState.id,
                week = week,
                season = season,
                saveName = currentGameState.name,
                lastPlayed = currentGameState.lastPlayed
            )

            // Get team details
            val team = teamsRepository.getTeamById(teamId)

            if (team == null) {
                _uiState.value = ClubUiState(isLoading = false)
                return@launch
            }

            // Get finances
            val finances = financesRepository.getTeamFinances(teamId, season)
            val previousSeasonFinances = financesRepository.getTeamFinances(teamId, calculatePreviousSeason(season))

            // Get infrastructure upgrades
            val upgrades = infrastructureRepository.getTeamUpgrades(teamName).firstOrNull() ?: emptyList()
            val completedUpgrades = upgrades.filter { it.status == "Completed" }

            // Calculate infrastructure levels
            val stadiumLevel = infrastructureRepository.getCurrentUpgradeLevel(teamName, "STADIUM")
            val trainingLevel = infrastructureRepository.getCurrentUpgradeLevel(teamName, "TRAINING_FACILITY")
            val youthLevel = infrastructureRepository.getCurrentUpgradeLevel(teamName, "YOUTH_ACADEMY")
            val medicalLevel = infrastructureRepository.getCurrentUpgradeLevel(teamName, "MEDICAL_CENTER")

            // Get sponsors
            val sponsors = sponsorsRepository.getTeamSponsors(teamName).firstOrNull() ?: emptyList()

            // Get season history
            val history = seasonHistoryRepository.getTeamHistory(teamName).firstOrNull() ?: emptyList()

            // Get club legends
            val legends = clubLegendsRepository.getLegendsByClub(teamName).firstOrNull() ?: emptyList()

            // Get league position
            val leaguePosition = getLeaguePosition(team.league, season, teamName)

            // Build UI models
            val clubInfo = ClubInfoUiModel(
                id = team.id,
                name = team.name,
                league = team.league,
                stadium = team.homeStadium,
                stadiumCapacity = team.stadiumCapacity,
                logoUrl = team.logoPath,
                reputationLevel = getReputationLevel(team.reputation),
                reputation = team.reputation
            )

            val financialModel = finances?.let {
                FinancialUiModel(
                    revenue = it.revenue,
                    revenueChange = calculateYearOverYearChange(
                        it.revenue,
                        previousSeasonFinances?.revenue
                    ),
                    expenses = it.expenses,
                    expensesChange = calculateYearOverYearChange(
                        it.expenses,
                        previousSeasonFinances?.expenses
                    ),
                    profit = it.profitLoss,
                    profitChange = calculateYearOverYearChange(
                        it.profitLoss,
                        previousSeasonFinances?.profitLoss
                    ),
                    budget = it.budget,
                    budgetUsed = if (it.budget > 0)
                        (it.transferSpending.toFloat() / it.budget.toFloat()).coerceIn(0f, 1f)
                    else 0f
                )
            }

            val infrastructureModel = InfrastructureUiModel(
                stadiumLevel = stadiumLevel,
                stadiumCapacity = team.stadiumCapacity,
                trainingLevel = trainingLevel,
                trainingEfficiency = 70 + (trainingLevel * 5),
                youthLevel = youthLevel,
                youthTalent = 50 + (youthLevel * 3),
                medicalLevel = medicalLevel,
                injuryRecovery = 60 + (medicalLevel * 4)
            )

            val revenueBreakdown = calculateRevenueBreakdown(finances)

            val sponsorModels = sponsors.map { sponsor ->
                SponsorUiModel(
                    id = sponsor.id,
                    name = sponsor.name,
                    type = sponsor.sponsorType,
                    value = sponsor.sponsorshipValue,
                    logoUrl = sponsor.logo
                )
            }

            val activeUpgrades = upgrades
                .filter { it.status == "In Progress" || it.status == "Pending" }
                .map { upgrade ->
                    UpgradeUiModel(
                        id = upgrade.id,
                        name = formatUpgradeName(upgrade.upgradeType),
                        type = upgrade.upgradeType,
                        currentLevel = upgrade.upgradeLevel,
                        targetLevel = upgrade.targetLevel,
                        progress = calculateProgress(upgrade.startDate, upgrade.completionDate),
                        remainingDays = calculateRemainingDays(upgrade.completionDate)
                    )
                }

            val historyModels = history.take(5).map { season ->
                HistoryUiModel(
                    id = season.id,
                    title = season.leagueName ?: "Season ${season.season}",
                    season = season.season,
                    achievement = formatAchievement(season, leaguesRepository.getLeagueByName(season.leagueName ?: "")),
                    type = if (season.trophiesWon > 0) "Trophy" else "League"
                )
            }

            val legendModels = legends.map { legend ->
                LegendUiModel(
                    id = legend.id,
                    name = legend.playerName,
                    era = "${legend.yearsPlayed} years",
                    titles = legend.majorTitlesWon
                )
            }

            val quickStats = QuickStatsUiModel(
                leaguePosition = leaguePosition,
                overallStars = (team.reputation / 20).coerceIn(1, 5),
                fanLoyalty = team.fanLoyalty,
                seasons = history.size
            )

            _uiState.value = ClubUiState(
                isLoading = false,
                clubName = team.name,
                reputationLevel = getReputationLevel(team.reputation),
                clubInfo = clubInfo,
                finances = financialModel,
                infrastructure = infrastructureModel,
                revenueBreakdown = revenueBreakdown,
                sponsors = sponsorModels,
                activeUpgrades = activeUpgrades,
                recentHistory = historyModels,
                legends = legendModels,
                quickStats = quickStats
            )
        }
    }

    private suspend fun getLeaguePosition(leagueName: String, season: String, teamName: String): Int {
        if (leagueName.isBlank()) return 0

        val seasonYear = try {
            season.split("/").first().toInt()
        } catch (e: Exception) {
            2024
        }

        val standings = leagueStandingsRepository.getStandingsByPosition(leagueName, seasonYear)
            .firstOrNull() ?: emptyList()

        return standings.indexOfFirst { it.teamName == teamName } + 1
    }

    private fun getReputationLevel(reputation: Int): String {
        return when {
            reputation >= 85 -> "African Legend"
            reputation >= 70 -> "Continental"
            reputation >= 50 -> "National"
            else -> "Local"
        }
    }

    private fun calculateRevenueBreakdown(finances: FinancesEntity?): List<RevenueItemUiModel> {
        if (finances == null) return emptyList()

        val items = mutableListOf<RevenueItemUiModel>()
        val totalRevenue = finances.revenue
        if (totalRevenue == 0L) return emptyList()

        val revenueSources = listOf(
            Triple("Sponsorship", finances.sponsorshipRevenue, FameColors.ChampionsGold),
            Triple("Broadcasting", finances.broadcastingRevenue, FameColors.PitchGreen),
            Triple("Matchday", finances.matchdayRevenue, FameColors.AfroSunOrange),
            Triple("Merchandise", finances.merchandiseRevenue, FameColors.AfricanLegendEmerald),
            Triple("Prize Money", finances.prizeMoney + finances.continentalPrizeMoney, FameColors.BaobabBrown),
            Triple("Player Sales", finances.playerSales, FameColors.KenteRed),
            Triple("Membership", finances.membershipRevenue, FameColors.NationalSilver)
        )

        revenueSources.forEach { (label, amount, color) ->
            if (amount > 0) {
                val percentage = ((amount.toDouble() / totalRevenue) * 100).toInt()
                items.add(
                    RevenueItemUiModel(
                        label = label,
                        amount = amount,
                        percentage = percentage,
                        color = color
                    )
                )
            }
        }

        return items.sortedByDescending { it.amount }
    }

    private fun calculateYearOverYearChange(current: Long, previous: Long?): Double {
        if (previous == null || previous == 0L) return 0.0
        return ((current - previous).toDouble() / previous) * 100
    }

    private fun calculatePreviousSeason(currentSeason: String): String {
        val parts = currentSeason.split("/")
        return if (parts.size == 2) {
            val startYear = parts[0].toInt() - 1
            val endYear = (startYear + 1).toString().takeLast(2)
            "$startYear/$endYear"
        } else {
            currentSeason
        }
    }

    private fun formatUpgradeName(type: String): String {
        return when (type) {
            "STADIUM" -> "Stadium Expansion"
            "TRAINING_FACILITY" -> "Training Facility Upgrade"
            "YOUTH_ACADEMY" -> "Youth Academy Upgrade"
            "MEDICAL_CENTER" -> "Medical Center Upgrade"
            "SCOUTING_NETWORK" -> "Scouting Network Expansion"
            else -> type.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
        }
    }

    private fun calculateRemainingDays(completionDate: String): String {
        if (completionDate.isBlank()) return "TBD"

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val end = format.parse(completionDate)?.time ?: return "TBD"
            val now = Date().time
            val diffDays = (end - now) / (1000 * 60 * 60 * 24)

            when {
                diffDays <= 0 -> "Complete"
                diffDays == 1L -> "1 day"
                diffDays < 7 -> "$diffDays days"
                diffDays < 30 -> "${diffDays / 7} weeks"
                else -> "${diffDays / 30} months"
            }
        } catch (e: Exception) {
            "TBD"
        }
    }

    private fun calculateProgress(startDate: String, completionDate: String): Int {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val start = format.parse(startDate)?.time ?: return 0
            val end = format.parse(completionDate)?.time ?: return 0
            val now = Date().time

            if (now >= end) return 100
            if (now <= start) return 0

            val totalDuration = end - start
            val elapsed = now - start

            ((elapsed.toDouble() / totalDuration) * 100).toInt().coerceIn(0, 100)
        } catch (e: Exception) {
            0
        }
    }

    private suspend fun formatAchievement(season: SeasonHistoryEntity, league: LeaguesEntity?): String {
        // If no league info, use default formatting
        if (league == null) {
            return formatAchievementWithoutLeague(season)
        }

        val leagueSize = getLeagueSize(league)
        val position = season.position ?: 0

        return when {
            // Trophy wins take priority
            season.trophiesWon > 0 -> {
                when {
                    season.trophiesWon > 1 -> "Multiple Trophies"
                    season.cupTitles > 0 -> "Cup Winners"
                    else -> "League Champions"
                }
            }

            // League position based achievements
            position == 1 -> "League Champions"
            position == 2 -> "Runners Up"
            position == 3 -> "3rd Place"

            // CAF Competition qualifications (African context)
            position <= getChampionsLeagueSpots(league) -> "CAF Champions League"
            position <= getConfederationCupSpots(league) -> "CAF Confederation Cup"

            // Promotion/Relegation based on league level
            isPromotionPosition(position, league) -> "Promoted"
            isPromotionPlayoffPosition(position, league) -> "Promotion Playoffs"
            isRelegationPosition(position, league) -> "Relegated"
            isRelegationPlayoffPosition(position, league) -> "Relegation Playoffs"

            // Generic positions based on league size
            position <= leagueSize / 3 -> "Top Third"
            position <= leagueSize / 2 -> "Top Half"
            position >= leagueSize - 3 -> "Relegation Battle"

            // Default
            else -> "${getOrdinal(position)} Place"
        }
    }

    /**
     * Fallback formatting without league info
     */
    private fun formatAchievementWithoutLeague(season: SeasonHistoryEntity): String {
        val position = season.position ?: 0
        return when {
            season.trophiesWon > 0 -> "Champions"
            position == 1 -> "League Champions"
            position == 2 -> "Runners Up"
            position == 3 -> "3rd Place"
            position == 4 -> "Top Four"
            position in 5..8 -> "Top Half"
            position in 9..12 -> "Mid Table"
            position >= 13 -> "Relegation Battle"
            else -> "${getOrdinal(position)} Place"
        }
    }

    /**
     * Get league size based on league entity
     */
    private suspend fun getLeagueSize(league: LeaguesEntity): Int {
        // You might want to add a teams count field to LeaguesEntity
        // For now, return typical sizes based on league name/level
        return when {
            league.name.contains("Tanzania Premier League") -> 16
            league.name.contains("Nigerian Professional") -> 20
            league.name.contains("Ghana Premier") -> 18
            league.name.contains("South African Premier") -> 16
            league.name.contains("Egyptian Premier") -> 18
            league.name.contains("Moroccan Botola") -> 16
            league.name.contains("Algerian Ligue 1") -> 16
            league.name.contains("Tunisian Ligue") -> 14
            league.level == 1 -> 16  // Default top division
            league.level == 2 -> 16  // Default second division
            else -> 14
        }
    }

    /**
     * Get number of CAF Champions League spots
     */
    private fun getChampionsLeagueSpots(league: LeaguesEntity): Int {
        // Top leagues get more spots
        return when {
            league.name.contains("Egyptian") -> 2
            league.name.contains("South African") -> 2
            league.name.contains("Moroccan") -> 2
            league.name.contains("Tunisian") -> 2
            league.name.contains("Nigerian") -> 2
            else -> 1
        }
    }

    /**
     * Get number of CAF Confederation Cup spots
     */
    private fun getConfederationCupSpots(league: LeaguesEntity): Int {
        val champSpots = getChampionsLeagueSpots(league)
        return champSpots + 2  // Confederation Cup spots are usually 2 more than CL spots
    }

    /**
     * Check if position is promotion spot
     */
    private fun isPromotionPosition(position: Int, league: LeaguesEntity): Boolean {
        // Only non-top divisions can promote
        if (league.level == 1) return false

        // Usually top 1-2 get automatic promotion
        return position <= league.promotionSpots
    }

    /**
     * Check if position is promotion playoff spot
     */
    private fun isPromotionPlayoffPosition(position: Int, league: LeaguesEntity): Boolean {
        // Only non-top divisions have playoffs
        if (league.level == 1 || league.playoffSpots == 0) return false

        // Positions after automatic promotion spots go to playoffs
        val autoPromotionSpots = league.promotionSpots
        return position in (autoPromotionSpots + 1)..(autoPromotionSpots + league.playoffSpots)
    }

    /**
     * Check if position is relegation spot
     */
    private suspend fun isRelegationPosition(position: Int, league: LeaguesEntity): Boolean {
        val leagueSize = getLeagueSize(league)
        // Bottom teams get relegated
        return position >= leagueSize - league.relegationSpots + 1
    }

    /**
     * Check if position is relegation playoff spot
     */
    private suspend fun isRelegationPlayoffPosition(position: Int, league: LeaguesEntity): Boolean {
        val leagueSize = getLeagueSize(league)
        // Positions just above relegation go to playoffs
        val relegationStart = leagueSize - league.relegationSpots + 1
        return position == relegationStart - 1
    }

    /**
     * Get ordinal string for a number (1st, 2nd, 3rd, 4th, etc.)
     */
    private fun getOrdinal(position: Int): String {
        return when {
            position % 100 in 11..13 -> "${position}th"
            position % 10 == 1 -> "${position}st"
            position % 10 == 2 -> "${position}nd"
            position % 10 == 3 -> "${position}rd"
            else -> "${position}th"
        }
    }

    fun refreshData() {
        loadClubData()
    }

    fun handleSponsorClick(sponsorId: Int) {
        // Navigate to sponsor details
    }

    fun handleUpgradeClick(upgradeId: Int) {
        // Navigate to upgrade details
    }

    fun handleLegendClick(legendId: Int) {
        // Navigate to legend details
    }
}