package com.fameafrica.afm2026.ui.screen.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.repository.NotificationsRepository
import com.fameafrica.afm2026.data.database.entities.NotificationPriority
import com.fameafrica.afm2026.ui.theme.FameColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiModel(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val icon: String,
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val isRead: Boolean,
    val type: String,
    val priority: Int
)

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val selectedTab: Int = 0,
    val notifications: List<NotificationUiModel> = emptyList(),
    val unreadCount: Int = 0
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState(isLoading = true))
    val uiState: StateFlow<NotificationsUiState> = _uiState

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            val allNotifications = notificationsRepository.getAllNotifications()
                .firstOrNull() ?: emptyList()

            val unreadCount = notificationsRepository.getUnreadCount()

            val uiModels = allNotifications.map { notification ->
                val backgroundColor = when (notification.priority) {
                    5 -> FameColors.KenteRed
                    4 -> FameColors.AfroSunOrange
                    3 -> FameColors.ChampionsGold
                    2 -> FameColors.PitchGreen
                    else -> FameColors.MutedParchment
                }

                NotificationUiModel(
                    id = notification.id,
                    title = notification.title,
                    message = notification.message ?: "",
                    time = notification.formattedTime,
                    icon = notification.notificationIcon,
                    backgroundColor = backgroundColor,
                    isRead = notification.isRead,
                    type = notification.notificationType,
                    priority = notification.priority
                )
            }

            _uiState.value = NotificationsUiState(
                isLoading = false,
                notifications = uiModels,
                unreadCount = unreadCount
            )
        }
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
        filterNotifications()
    }

    private fun filterNotifications() {
        viewModelScope.launch {
            val allNotifications = notificationsRepository.getAllNotifications()
                .firstOrNull() ?: emptyList()

            val filtered = when (_uiState.value.selectedTab) {
                0 -> allNotifications
                1 -> allNotifications.filter { it.notificationType == "MATCH" }
                2 -> allNotifications.filter { it.notificationType == "TRANSFER" }
                3 -> allNotifications.filter { it.notificationType == "INJURY" }
                4 -> allNotifications.filter { it.notificationType == "BOARD" }
                5 -> allNotifications.filter { it.notificationType == "SYSTEM" }
                else -> allNotifications
            }

            val uiModels = filtered.map { notification ->
                val backgroundColor = when (notification.priority) {
                    5 -> FameColors.KenteRed
                    4 -> FameColors.AfroSunOrange
                    3 -> FameColors.ChampionsGold
                    2 -> FameColors.PitchGreen
                    else -> FameColors.MutedParchment
                }

                NotificationUiModel(
                    id = notification.id,
                    title = notification.title,
                    message = notification.message ?: "",
                    time = notification.formattedTime,
                    icon = notification.notificationIcon,
                    backgroundColor = backgroundColor,
                    isRead = notification.isRead,
                    type = notification.notificationType,
                    priority = notification.priority
                )
            }

            _uiState.value = _uiState.value.copy(notifications = uiModels)
        }
    }

    fun markAsRead(id: Int) {
        viewModelScope.launch {
            notificationsRepository.markAsRead(id)
            loadNotifications()
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationsRepository.markAllAsRead()
            loadNotifications()
        }
    }

    fun dismissNotification(id: Int) {
        viewModelScope.launch {
            notificationsRepository.deleteNotificationById(id)
            loadNotifications()
        }
    }
}