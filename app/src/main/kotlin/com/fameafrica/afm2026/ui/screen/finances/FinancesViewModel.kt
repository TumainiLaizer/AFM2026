package com.fameafrica.afm2026.ui.screen.finances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.repository.*
import com.fameafrica.afm2026.domain.manager.GameManager.GameDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

// ============ UI MODELS ============

data class FinancesUiState(
    val isLoading: Boolean = true,
    val financialSummary: FinancialSummaryUiModel? = null,
    val budget: Long = 0,
    val bankBalance: Long = 0,
    val wageBill: Long = 0,
    val financialTier: String = "Unknown",
    val financialHealth: String = "Unknown",
    val isProfitable: Boolean = false,
    val revenueBreakdown: Map<String, Long> = emptyMap(),
    val expenseBreakdown: Map<String, Long> = emptyMap(),
    val profitLossHistory: List<ProfitLossEntry> = emptyList(),
    val sponsors: List<SponsorUiModel> = emptyList(),
    val leagueAverageRevenue: Long = 0,
    val leagueHighestRevenue: Long = 0
)

data class FinancialSummaryUiModel(
    val revenue: Long,
    val expenses: Long,
    val profitLoss: Long,
    val bankBalance: Long,
    val isProfitable: Boolean
)

data class ProfitLossEntry(
    val label: String,
    val amount: Long
)

data class SponsorUiModel(
    val id: Int,
    val name: String,
    val type: String,
    val annualValue: Long,
    val yearsRemaining: Int
)

// Game Context State
data class GameContextState(
    val gameStateId: Int = 0,
    val gameDate: GameDate? = null,
    val week: Int = 1,
    val season: String = "2024/25",
    val saveName: String = "",
    val lastPlayed: String? = null,
    val gameVersion: String? = null
)

@HiltViewModel
class FinancesViewModel @Inject constructor(
    private val gameStatesRepository: GameStatesRepository,
    private val financesRepository: FinancesRepository,
    private val sponsorsRepository: SponsorsRepository,
    private val teamsRepository: TeamsRepository,
    private val leaguesRepository: LeaguesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinancesUiState(isLoading = true))
    val uiState: StateFlow<FinancesUiState> = _uiState

    private val _gameContext = MutableStateFlow(GameContextState())
    val gameContext: StateFlow<GameContextState> = _gameContext

    init {
        loadFinancesData()
    }

    private fun loadFinancesData() {
        viewModelScope.launch {
            // Get current game state
            val validSaves = gameStatesRepository.getValidSaveGames().firstOrNull()
            val currentGameState = validSaves?.maxByOrNull { it.lastPlayed ?: "" }

            if (currentGameState == null) {
                _uiState.value = FinancesUiState(isLoading = false)
                return@launch
            }

            val teamId = currentGameState.teamId
            val teamName = currentGameState.teamName
            val season = currentGameState.season

            // Update game context
            _gameContext.value = GameContextState(
                gameStateId = currentGameState.id,
                week = currentGameState.week,
                season = season,
                saveName = currentGameState.name,
                lastPlayed = currentGameState.lastPlayed,
                gameVersion = currentGameState.gameVersion
            )

            // Get team details
            val team = teamsRepository.getTeamById(teamId)

            // Get finances dashboard
            val financeDashboard = financesRepository.getTeamFinanceDashboard(teamId, season)

            // Get sponsors
            val sponsors = sponsorsRepository.getTeamSponsors(teamName).firstOrNull() ?: emptyList()

            // Get league averages for comparison
            val leagueName = team?.league ?: ""
            val league = leaguesRepository.getLeagueByName(leagueName)

            // Get all teams in same league for comparison
            val allTeams = teamsRepository.getAllTeams().firstOrNull() ?: emptyList()
            val leagueTeams = allTeams.filter { it.league == leagueName }

            var leagueTotalRevenue = 0L
            var leagueMaxRevenue = 0L

            for (leagueTeam in leagueTeams) {
                val teamFinance = financesRepository.getTeamFinances(leagueTeam.id, season)
                val teamRevenue = teamFinance?.revenue ?: 0
                leagueTotalRevenue += teamRevenue
                if (teamRevenue > leagueMaxRevenue) {
                    leagueMaxRevenue = teamRevenue
                }
            }

            val leagueAverageRevenue = if (leagueTeams.isNotEmpty()) {
                leagueTotalRevenue / leagueTeams.size
            } else 0

            // Generate profit/loss history (last 5 seasons)
            val profitLossHistory = generateProfitLossHistory(teamId, season)

            // Build UI models
            val financialSummary = FinancialSummaryUiModel(
                revenue = financeDashboard.revenue,
                expenses = financeDashboard.expenses,
                profitLoss = financeDashboard.profitLoss,
                bankBalance = financeDashboard.bankBalance,
                isProfitable = financeDashboard.isProfitable
            )

            val sponsorModels = sponsors.map { sponsor ->
                SponsorUiModel(
                    id = sponsor.id,
                    name = sponsor.name,
                    type = sponsor.sponsorType,
                    annualValue = sponsor.sponsorshipValue,
                    yearsRemaining = sponsor.contractRemainingYears
                )
            }

            _uiState.value = FinancesUiState(
                isLoading = false,
                financialSummary = financialSummary,
                budget = financeDashboard.budget,
                bankBalance = financeDashboard.bankBalance,
                wageBill = financeDashboard.expenseBreakdown["Player Wages"] ?: 0,
                financialTier = financeDashboard.financialTier,
                financialHealth = financeDashboard.financialHealth,
                isProfitable = financeDashboard.isProfitable,
                revenueBreakdown = financeDashboard.revenueBreakdown,
                expenseBreakdown = financeDashboard.expenseBreakdown,
                profitLossHistory = profitLossHistory,
                sponsors = sponsorModels,
                leagueAverageRevenue = leagueAverageRevenue,
                leagueHighestRevenue = leagueMaxRevenue
            )
        }
    }

    private suspend fun generateProfitLossHistory(teamId: Int, currentSeason: String): List<ProfitLossEntry> {
        val history = mutableListOf<ProfitLossEntry>()
        val seasons = mutableListOf<String>()

        // Generate last 5 seasons
        var season = currentSeason
        seasons.add(season)

        for (i in 1..4) {
            season = getPreviousSeason(season)
            seasons.add(0, season) // Add to beginning to maintain chronological order
        }

        seasons.forEachIndexed { index, season ->
            val finances = financesRepository.getTeamFinances(teamId, season)
            val profitLoss = finances?.profitLoss ?: 0
            history.add(
                ProfitLossEntry(
                    label = season.take(4), // Use year as label
                    amount = profitLoss
                )
            )
        }

        return history
    }

    private fun getPreviousSeason(season: String): String {
        val parts = season.split("/")
        return if (parts.size == 2) {
            val startYear = parts[0].toInt() - 1
            val endYear = (startYear + 1).toString().takeLast(2)
            "$startYear/$endYear"
        } else {
            season
        }
    }

    fun refreshData() {
        loadFinancesData()
    }

    fun requestBudgetIncrease() {
        viewModelScope.launch {
            val currentContext = _gameContext.value
            val currentState = _uiState.value

            // Get the actual team ID from game context (need to fetch from game state again or store it)
            val validSaves = gameStatesRepository.getValidSaveGames().firstOrNull()
            val currentGameState = validSaves?.maxByOrNull { it.lastPlayed ?: "" }
            val teamId = currentGameState?.teamId ?: return@launch
            val season = currentContext.season

            // Request 20% budget increase
            val newBudget = (currentState.budget * 1.2).toLong()
            financesRepository.updateTransferBudget(teamId, season, newBudget)

            // Refresh data
            loadFinancesData()
        }
    }

    fun renegotiateSponsor(sponsorId: Int) {
        viewModelScope.launch {
            // Get current team ID
            val validSaves = gameStatesRepository.getValidSaveGames().firstOrNull()
            val currentGameState = validSaves?.maxByOrNull { it.lastPlayed ?: "" }
            val teamId = currentGameState?.teamId ?: return@launch

            // Get sponsor details
            val sponsor = sponsorsRepository.getSponsorById(sponsorId) ?: return@launch

            // Calculate new value (10% increase)
            val newValue = (sponsor.sponsorshipValue * 1.1).toLong()

            // Update sponsor (this would need a method in SponsorsRepository)
            // sponsorsRepository.updateSponsorValue(sponsorId, newValue)

            // Add revenue
            financesRepository.addSponsorshipRevenue(teamId, _gameContext.value.season, newValue - sponsor.sponsorshipValue)

            // Refresh data
            loadFinancesData()
        }
    }
}

// Extension function for teams repository (if needed)
suspend fun TeamsRepository.getAllTeams(): kotlinx.coroutines.flow.Flow<List<com.fameafrica.afm2026.data.database.entities.TeamsEntity>> {
    // This would need to be implemented in your TeamsRepository
    // For now, returning empty flow as placeholder
    return kotlinx.coroutines.flow.flowOf(emptyList())
}