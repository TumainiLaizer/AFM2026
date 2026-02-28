package com.fameafrica.afm2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.domain.manager.GameManager
import com.fameafrica.afm2026.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameManager: GameManager,
    private val notificationsRepository: NotificationsRepository,
    private val newsRepository: NewsRepository,
    private val transfersRepository: TransfersRepository,
    private val objectivesRepository: ObjectivesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState

    private val _unreadNotifications = MutableStateFlow(0)
    val unreadNotifications: StateFlow<Int> = _unreadNotifications

    private val _breakingNews = MutableStateFlow<List<NewsEntity>>(emptyList())
    val breakingNews: StateFlow<List<NewsEntity>> = _breakingNews

    init {
        observeGameState()
        observeNotifications()
        observeNews()
    }

    private fun observeGameState() {
        viewModelScope.launch {
            gameManager.gameState.collect { state ->
                _uiState.value = _uiState.value.copy(
                    gameState = state
                )
            }
        }
    }

    private fun observeNotifications() {
        viewModelScope.launch {
            notificationsRepository.getUnreadCount().collect { count ->
                _unreadNotifications.value = count
            }
        }
    }

    private fun observeNews() {
        viewModelScope.launch {
            newsRepository.getTopNews(3).collect { news ->
                _breakingNews.value = news
            }
        }
    }

    fun processNextWeek() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true)

            // Process match
            gameManager.processNextMatch()

            // Process transfers
            checkTransferUpdates()

            // Check objectives
            checkObjectiveProgress()

            // Check board satisfaction
            checkBoardStatus()

            // Auto-save
            autoSaveGame()

            _uiState.value = _uiState.value.copy(isProcessing = false)
        }
    }

    private suspend fun checkTransferUpdates() {
        val currentState = gameManager.gameState.value
        if (currentState !is GameManager.GameState.Active) return

        // Check for incoming transfer offers
        val offers = transfersRepository.getIncomingTransfers(currentState.context.teamName)
            .firstOrNull()?.filter { it.transferStatus == "Pending" }

        offers?.forEach { offer ->
            notificationsRepository.insertNotification(
                NotificationFactory.createTransferOffer(
                    transfer = offer,
                    playerName = offer.playerName,
                    offeringTeam = offer.targetTeam,
                    fee = offer.transferFee
                )
            )
        }
    }

    private suspend fun checkObjectiveProgress() {
        val currentState = gameManager.gameState.value
        if (currentState !is GameManager.GameState.Active) return

        val objectives = objectivesRepository.getPendingObjectivesByTeam(currentState.context.teamName)
            .firstOrNull() ?: emptyList()

        objectives.forEach { objective ->
            // Check progress and update
            objectivesRepository.checkObjectiveCompletion(objective.id)
        }
    }

    private suspend fun checkBoardStatus() {
        val currentState = gameManager.gameState.value
        if (currentState !is GameManager.GameState.Active) return

        val boardEval = boardEvaluationRepository.getEvaluationByManagerName(
            currentState.context.managerId.toString()
        )

        boardEval?.let { eval ->
            if (eval.status == "Critical") {
                notificationsRepository.insertNotification(
                    NotificationFactory.createBoardEvaluationNotification(
                        evaluation = eval,
                        status = eval.status
                    )
                )
            }
        }
    }

    private fun autoSaveGame() {
        viewModelScope.launch {
            // Auto-save logic
        }
    }

    fun markNotificationsAsRead() {
        viewModelScope.launch {
            notificationsRepository.markAllAsRead()
        }
    }

    data class GameUiState(
        val gameState: GameManager.GameState = GameManager.GameState.Loading,
        val isProcessing: Boolean = false,
        val showTransferAlert: Boolean = false,
        val showInjuryAlert: Boolean = false,
        val showBoardAlert: Boolean = false
    )
}