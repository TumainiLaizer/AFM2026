package com.fameafrica.afm2026.ui.screen.squad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.repository.*
import com.fameafrica.afm2026.utils.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

data class SquadUiState(
    val isLoading: Boolean = true,
    val selectedTab: String = "ALL",
    val players: List<PlayerUiModel> = emptyList(),
    val filteredPlayers: List<PlayerUiModel> = emptyList(),
    val searchQuery: String = "",
    val sortBy: SortOption = SortOption.RATING,
    val sortAscending: Boolean = false,
    val squadStats: SquadStatsUiModel = SquadStatsUiModel(),
    val teamName: String = "",
    val formation: String = "4-4-2"
)

data class PlayerUiModel(
    val id: Int,
    val name: String,
    val age: Int,
    val position: String,
    val positionCategory: String,
    val rating: Int,
    val potential: Int,
    val form: Int,
    val morale: Int,
    val nationality: String,
    val nationalityFlag: String?,
    val shirtNumber: Int,
    val marketValue: Int,
    val contractExpiry: String,
    val isInjured: Boolean,
    val injuryStatus: String?,
    val isSuspended: Boolean,
    val isCaptain: Boolean,
    val isViceCaptain: Boolean,
    val goals: Int,
    val assists: Int,
    val appearances: Int,
    val yellowCards: Int,
    val redCards: Int
)

data class SquadStatsUiModel(
    val totalPlayers: Int = 0,
    val averageRating: Double = 0.0,
    val averageAge: Double = 0.0,
    val averageHeight: Double = 0.0,
    val totalMarketValue: Int = 0,
    val injuredCount: Int = 0,
    val suspendedCount: Int = 0,
    val goalkeepers: Int = 0,
    val defenders: Int = 0,
    val midfielders: Int = 0,
    val forwards: Int = 0
)

enum class SortOption {
    RATING,
    NAME,
    AGE,
    VALUE,
    FORM,
    GOALS
}

@HiltViewModel
class SquadViewModel @Inject constructor(
    private val playersRepository: PlayersRepository,
    private val teamsRepository: TeamsRepository,
    private val gameStateRepository: GameStatesRepository,
    private val playerContractsRepository: PlayerContractsRepository,
    private val playerTrainingRepository: PlayerTrainingRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SquadUiState(isLoading = true))
    val uiState: StateFlow<SquadUiState> = _uiState

    private var allPlayers: List<PlayerUiModel> = emptyList()

    init {
        loadSquadData()
    }

    private fun loadSquadData() {
        viewModelScope.launch {
            // Get current team
            val gameState = gameStateRepository.getValidSaveGames().firstOrNull()?.firstOrNull()
            val teamId = gameState?.teamId ?: 0
            val teamName = gameState?.teamName ?: ""

            // Get team details for formation
            val team = teamsRepository.getTeamById(teamId)

            // Get all players for the team
            val players = playersRepository.getPlayersByTeamId(teamId).firstOrNull() ?: emptyList()

            // Convert to UI models
            val playerModels = players.map { player ->
                val contract = playerContractsRepository.getContractByPlayerName(player.name)

                PlayerUiModel(
                    id = player.id,
                    name = player.name,
                    age = player.age,
                    position = player.position,
                    positionCategory = player.positionCategory,
                    rating = player.rating,
                    potential = player.potential,
                    form = player.currentForm,
                    morale = player.morale,
                    nationality = player.nationality,
                    nationalityFlag = "flags/${player.nationality}.png", // Will be loaded by coil
                    shirtNumber = player.shirtNumber,
                    marketValue = player.marketValue,
                    contractExpiry = contract?.contractEndDate ?: player.contractExpiry,
                    isInjured = player.injuryStatus != "HEALTHY",
                    injuryStatus = player.injuryStatus,
                    isSuspended = player.suspended,
                    isCaptain = player.isCaptain,
                    isViceCaptain = player.isViceCaptain,
                    goals = player.goals,
                    assists = player.assists,
                    appearances = player.matches,
                    yellowCards = player.yellowCards,
                    redCards = player.redCards
                )
            }.sortedByDescending { it.rating }

            allPlayers = playerModels

            // Calculate squad stats
            val stats = SquadStatsUiModel(
                totalPlayers = playerModels.size,
                averageRating = playerModels.map { it.rating }.average(),
                averageAge = playerModels.map { it.age }.average(),
                averageHeight = playerModels.map { it.height }.average(),
                totalMarketValue = playerModels.sumOf { it.marketValue },
                injuredCount = playerModels.count { it.isInjured },
                suspendedCount = playerModels.count { it.isSuspended },
                goalkeepers = playerModels.count { it.position == "GK" },
                defenders = playerModels.count { it.positionCategory == "DEFENDER" },
                midfielders = playerModels.count { it.positionCategory == "MIDFIELDER" },
                forwards = playerModels.count { it.positionCategory == "FORWARD" }
            )

            _uiState.value = SquadUiState(
                isLoading = false,
                players = playerModels,
                filteredPlayers = playerModels,
                squadStats = stats,
                teamName = teamName,
                formation = team?.formation ?: "4-4-2"
            )
        }
    }

    fun selectTab(tab: String) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        filterPlayers()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterPlayers()
    }

    fun updateSortOption(option: SortOption) {
        val current = _uiState.value
        val newAscending = if (current.sortBy == option) !current.sortAscending else when (option) {
            SortOption.NAME -> true
            SortOption.AGE -> true
            else -> false
        }

        _uiState.value = current.copy(
            sortBy = option,
            sortAscending = newAscending
        )
        sortPlayers()
    }

    fun filterPlayers() {
        val state = _uiState.value
        val filtered = allPlayers.filter { player ->
            val matchesTab = when (state.selectedTab) {
                "ALL" -> true
                "GK" -> player.position == "GK"
                "DEF" -> player.positionCategory == "DEFENDER"
                "MID" -> player.positionCategory == "MIDFIELDER"
                "FWD" -> player.positionCategory == "FORWARD"
                "INJURED" -> player.isInjured
                "SUSPENDED" -> player.isSuspended
                else -> true
            }

            val matchesSearch = if (state.searchQuery.isBlank()) {
                true
            } else {
                player.name.contains(state.searchQuery, ignoreCase = true) ||
                        player.nationality.contains(state.searchQuery, ignoreCase = true)
            }

            matchesTab && matchesSearch
        }

        _uiState.value = state.copy(filteredPlayers = filtered)
        sortPlayers()
    }

    private fun sortPlayers() {
        val state = _uiState.value
        val sorted = state.filteredPlayers.sortedWith { a, b ->
            val comparison = when (state.sortBy) {
                SortOption.RATING -> a.rating.compareTo(b.rating)
                SortOption.NAME -> a.name.compareTo(b.name)
                SortOption.AGE -> a.age.compareTo(b.age)
                SortOption.VALUE -> a.marketValue.compareTo(b.marketValue)
                SortOption.FORM -> a.form.compareTo(b.form)
                SortOption.GOALS -> a.goals.compareTo(b.goals)
            }

            if (state.sortAscending) comparison else -comparison
        }

        _uiState.value = state.copy(filteredPlayers = sorted)
    }

    fun getPlayerById(playerId: Int): PlayerUiModel? {
        return allPlayers.find { it.id == playerId }
    }
}