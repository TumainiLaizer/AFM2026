package com.fameafrica.afm2026.ui.screen.transfers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.database.entities.TransferStatus
import com.fameafrica.afm2026.data.database.entities.TransferType
import com.fameafrica.afm2026.data.repository.TransfersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransfersViewModel @Inject constructor(
    private val transfersRepository: TransfersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransfersUiState())
    val uiState: StateFlow<TransfersUiState> = _uiState.asStateFlow()

    init {
        loadTransferData()
    }

    private fun loadTransferData() {
        viewModelScope.launch {
            // Simulate loading data from repository
            // In real implementation, this would flow from repository
            _uiState.update { state ->
                state.copy(
                    transferBudget = 45_000_000,
                    wageBudget = 500_000,
                    marketPlayers = generateMarketPlayers(),
                    incomingOffers = generateIncomingOffers(),
                    outgoingOffers = generateOutgoingOffers(),
                    completedTransfers = generateCompletedTransfers(),
                    transferRumours = generateRumours(),
                    isTransferWindowOpen = true,
                    windowDaysRemaining = 21,
                    windowType = "Summer Window",
                    foreignPlayerSlots = ForeignPlayerSlotsUiModel(
                        current = 4,
                        max = 7,
                        nextSeason = 8
                    )
                )
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun toggleSearch() {
        _uiState.update { it.copy(showSearch = !it.showSearch) }
    }

    fun toggleFilters() {
        _uiState.update { it.copy(showFilters = !it.showFilters) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun searchPlayers() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    searchResults = state.marketPlayers.filter {
                        it.name.contains(state.searchQuery, ignoreCase = true)
                    }
                )
            }
        }
    }

    fun searchPlayer(playerName: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = playerName,
                showSearch = true,
                searchResults = state.marketPlayers.filter {
                    it.name.contains(playerName, ignoreCase = true)
                }
            )
        }
    }

    fun setTransferType(type: String?) {
        _uiState.update { it.copy(selectedTransferType = type) }
    }

    fun setStatus(status: String?) {
        _uiState.update { it.copy(selectedStatus = status) }
    }

    fun setMinRating(rating: Int) {
        _uiState.update { it.copy(minRating = rating) }
    }

    fun setMaxFee(fee: Int) {
        _uiState.update { it.copy(maxFee = fee) }
    }

    fun initiateTransfer(playerId: Int) {
        // Navigate to transfer creation screen
    }

    fun addToShortlist(playerId: Int) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    marketPlayers = state.marketPlayers.map {
                        if (it.id == playerId) it.copy(onShortlist = !it.onShortlist) else it
                    }
                )
            }
        }
    }

    fun acceptTransfer(offerId: Int) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    incomingOffers = state.incomingOffers.map {
                        if (it.id == offerId) {
                            it.copy(status = TransferStatus.ACCEPTED.value)
                        } else it
                    }
                )
            }

            // Simulate processing
            delay(1000)

            _uiState.update { state ->
                state.copy(
                    incomingOffers = state.incomingOffers.filter { it.id != offerId },
                    completedTransfers = state.completedTransfers +
                            state.incomingOffers.first { it.id == offerId }.copy(
                                status = TransferStatus.COMPLETED.value,
                                completedDate = "2024-08-15"
                            )
                )
            }
        }
    }

    fun rejectTransfer(offerId: Int) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    incomingOffers = state.incomingOffers.map {
                        if (it.id == offerId) {
                            it.copy(status = TransferStatus.REJECTED.value)
                        } else it
                    }
                )
            }

            delay(1500)

            _uiState.update { state ->
                state.copy(
                    incomingOffers = state.incomingOffers.filter { it.id != offerId }
                )
            }
        }
    }

    fun counterOffer(offerId: Int) {
        // Navigate to negotiation screen
    }

    fun withdrawOffer(offerId: Int) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    outgoingOffers = state.outgoingOffers.filter { it.id != offerId }
                )
            }
        }
    }

    fun modifyOffer(offerId: Int) {
        // Navigate to offer modification screen
    }

    // Mock data generation
    private fun generateMarketPlayers(): List<TransferPlayerUiModel> {
        return listOf(
            TransferPlayerUiModel(1, "Victor Osimhen", 24, "ST", "NGA", "Napoli", 88, 80_000_000, 250_000, true, false, 94),
            TransferPlayerUiModel(2, "Mohammed Kudus", 23, "CAM", "GHA", "West Ham", 84, 45_000_000, 150_000, true, false, 87),
            TransferPlayerUiModel(3, "Achraf Hakimi", 25, "RB", "MAR", "PSG", 86, 60_000_000, 200_000, true, true, 91),
            TransferPlayerUiModel(4, "Riyad Mahrez", 32, "RW", "ALG", "Al Ahli", 85, 15_000_000, 300_000, true, false, 82),
            TransferPlayerUiModel(5, "Youssef En-Nesyri", 26, "ST", "MAR", "Sevilla", 82, 25_000_000, 90_000, true, false, 79),
            TransferPlayerUiModel(6, "Percy Tau", 29, "LW", "RSA", "Al Ahly", 78, 4_500_000, 60_000, false, false, 76),
            TransferPlayerUiModel(7, "Peter Shalulile", 30, "ST", "NAM", "Mamelodi Sundowns", 76, 2_500_000, 45_000, false, false, 73)
        )
    }

    private fun generateIncomingOffers(): List<TransferOfferUiModel> {
        return listOf(
            TransferOfferUiModel(
                id = 101,
                playerId = 5,
                playerName = "Youssef En-Nesyri",
                playerAge = 26,
                playerPosition = "ST",
                fromTeam = "Sevilla",
                toTeam = "Al Ahly",
                transferType = TransferType.BUY.value,
                status = TransferStatus.PENDING.value,
                fee = 22_000_000,
                wage = 120_000,
                contractLength = 4,
                isLoanToBuy = false,
                scoutRating = 79,
                isUrgent = true,
                expiryDate = "2024-08-18",
                loanBuyFee = null,
                completedDate = null
            ),
            TransferOfferUiModel(
                id = 102,
                playerId = 8,
                playerName = "Hakim Ziyech",
                playerAge = 31,
                playerPosition = "RW",
                fromTeam = "Galatasaray",
                toTeam = "Zamalek",
                transferType = TransferType.LOAN.value,
                status = TransferStatus.NEGOTIATING.value,
                fee = 0,
                wage = 80_000,
                contractLength = 1,
                isLoanToBuy = true,
                loanBuyFee = 5_000_000,
                scoutRating = 81,
                isUrgent = false,
                expiryDate = "2024-08-25"
            )
        )
    }

    private fun generateOutgoingOffers(): List<TransferOfferUiModel> {
        return listOf(
            TransferOfferUiModel(
                id = 201,
                playerId = 9,
                playerName = "Percy Tau",
                playerAge = 29,
                playerPosition = "LW",
                fromTeam = "Al Ahly",
                toTeam = "Kaizer Chiefs",
                transferType = TransferType.BUY.value,
                status = TransferStatus.PENDING.value,
                fee = 3_500_000,
                wage = 50_000,
                contractLength = 3,
                isLoanToBuy = false,
                scoutRating = 76,
                isUrgent = false,
                expiryDate = null,
                loanBuyFee = null,
                completedDate = null
            )
        )
    }

    private fun generateCompletedTransfers(): List<TransferOfferUiModel> {
        return listOf(
            TransferOfferUiModel(
                id = 301,
                playerId = 10,
                playerName = "Kahraba",
                playerAge = 30,
                playerPosition = "LW",
                fromTeam = "Al Ahly",
                toTeam = "Al Ittihad",
                transferType = TransferType.BUY.value,
                status = TransferStatus.COMPLETED.value,
                fee = 1_800_000,
                wage = 40_000,
                contractLength = 2,
                isLoanToBuy = false,
                completedDate = "2024-08-10",
                loanBuyFee = null,
                scoutRating = 80,
                isUrgent = false,
                expiryDate = null
            )
        )
    }

    private fun generateRumours(): List<TransferRumourUiModel> {
        return listOf(
            TransferRumourUiModel(
                playerName = "Victor Osimhen",
                headline = "Al Hilal prepare €70M bid for Nigerian striker",
                source = "African Football Insider",
                timeAgo = "2 hours ago",
                confidence = 75
            ),
            TransferRumourUiModel(
                playerName = "Achraf Hakimi",
                headline = "Moroccan star rejects move to Pyramids FC",
                source = "Le360 Sport",
                timeAgo = "5 hours ago",
                confidence = 90
            )
        )
    }
}

// ============ UI MODELS ============

data class TransfersUiState(
    val selectedTab: Int = 0,
    val showSearch: Boolean = false,
    val showFilters: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<TransferPlayerUiModel> = emptyList(),
    val selectedTransferType: String? = null,
    val selectedStatus: String? = null,
    val minRating: Int = 50,
    val maxFee: Int = 50_000_000,
    val transferBudget: Long = 0,
    val wageBudget: Long = 0,
    val marketPlayers: List<TransferPlayerUiModel> = emptyList(),
    val incomingOffers: List<TransferOfferUiModel> = emptyList(),
    val outgoingOffers: List<TransferOfferUiModel> = emptyList(),
    val completedTransfers: List<TransferOfferUiModel> = emptyList(),
    val transferRumours: List<TransferRumourUiModel> = emptyList(),
    val isTransferWindowOpen: Boolean = false,
    val windowDaysRemaining: Int = 0,
    val windowType: String = "",
    val foreignPlayerSlots: ForeignPlayerSlotsUiModel = ForeignPlayerSlotsUiModel()
)

data class TransferPlayerUiModel(
    val id: Int,
    val name: String,
    val age: Int,
    val position: String,
    val nationality: String,
    val club: String,
    val rating: Int,
    val value: Int,
    val wage: Int,
    val isForeign: Boolean,
    val onShortlist: Boolean,
    val scoutRating: Int
)

data class TransferOfferUiModel(
    val id: Int,
    val playerId: Int,
    val playerName: String,
    val playerAge: Int,
    val playerPosition: String,
    val fromTeam: String,
    val toTeam: String,
    val transferType: String,
    val status: String,
    val fee: Int,
    val wage: Int,
    val contractLength: Int,
    val isLoanToBuy: Boolean,
    val loanBuyFee: Int?,
    val scoutRating: Int = 0,
    val isUrgent: Boolean = false,
    val expiryDate: String? = null,
    val completedDate: String? = null
)

data class TransferRumourUiModel(
    val playerName: String,
    val headline: String,
    val source: String,
    val timeAgo: String,
    val confidence: Int
)

data class ForeignPlayerSlotsUiModel(
    val current: Int = 0,
    val max: Int = 0,
    val nextSeason: Int = 0
)