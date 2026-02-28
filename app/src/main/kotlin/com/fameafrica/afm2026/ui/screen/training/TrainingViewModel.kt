package com.fameafrica.afm2026.ui.screen.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrainingSessionUiModel(
    val id: Int,
    val drillName: String,
    val playersInvolved: Int,
    val focus: String,
    val progress: Int,
    val startTime: String
)

data class DrillUiModel(
    val id: Int,
    val name: String,
    val category: String,
    val focus: String,
    val duration: Int,
    val attributes: String,
    val difficulty: String,
    val injuryRisk: Int
)

data class TrainingUiState(
    val isLoading: Boolean = true,
    val overallProgress: Int = 0,
    val averageMorale: Int = 0,
    val injuryRisk: Int = 0,
    val fitnessLevel: Int = 0,
    val selectedCategory: String = "ALL",
    val currentSession: TrainingSessionUiModel? = null,
    val drills: List<DrillUiModel> = emptyList()
)

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val playerTrainingRepository: PlayerTrainingRepository,
    private val playersRepository: PlayersRepository,
    private val gameStateRepository: GameStatesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingUiState(isLoading = true))
    val uiState: StateFlow<TrainingUiState> = _uiState

    private val allDrills = listOf(
        DrillUiModel(1, "Passing Drills", "TECHNICAL", "Accuracy", 45, "Passing, Vision", "Easy", 5),
        DrillUiModel(2, "Shooting Practice", "TECHNICAL", "Finishing", 60, "Finishing, Power", "Medium", 8),
        DrillUiModel(3, "Dribbling Course", "TECHNICAL", "Ball Control", 40, "Dribbling, Agility", "Medium", 10),
        DrillUiModel(4, "Tactical Formation", "TACTICAL", "Positioning", 90, "Positioning, Decisions", "Hard", 3),
        DrillUiModel(5, "Set Pieces", "TACTICAL", "Dead Balls", 60, "Crossing, Heading", "Medium", 4),
        DrillUiModel(6, "Sprint Training", "PHYSICAL", "Speed", 30, "Pace, Acceleration", "Hard", 15),
        DrillUiModel(7, "Endurance Run", "PHYSICAL", "Stamina", 120, "Stamina, Work Rate", "Hard", 12),
        DrillUiModel(8, "Strength Gym", "PHYSICAL", "Power", 60, "Strength, Aggression", "Medium", 8),
        DrillUiModel(9, "Visualization", "MENTAL", "Focus", 30, "Composure, Decisions", "Easy", 1),
        DrillUiModel(10, "Leadership Talk", "MENTAL", "Confidence", 45, "Leadership, Motivation", "Easy", 0),
        DrillUiModel(11, "Reflex Training", "GOALKEEPING", "Reactions", 40, "Reflexes, Handling", "Medium", 7),
        DrillUiModel(12, "Cross Collection", "GOALKEEPING", "Aerial", 50, "Aerial, Command", "Medium", 6)
    )

    init {
        loadTrainingData()
    }

    private fun loadTrainingData() {
        viewModelScope.launch {
            val gameState = gameStateRepository.getValidSaveGames().firstOrNull()?.firstOrNull()
            val teamId = gameState?.teamId ?: 0

            val players = playersRepository.getPlayersByTeamId(teamId).firstOrNull() ?: emptyList()

            val avgMorale = if (players.isNotEmpty()) players.map { it.morale }.average().toInt() else 0
            val avgFitness = 75 // Placeholder

            _uiState.value = TrainingUiState(
                isLoading = false,
                overallProgress = 65,
                averageMorale = avgMorale,
                injuryRisk = 12,
                fitnessLevel = avgFitness,
                drills = allDrills
            )
        }
    }

    fun selectCategory(category: String) {
        val filtered = if (category == "ALL") {
            allDrills
        } else {
            allDrills.filter { it.category == category }
        }

        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            drills = filtered
        )
    }

    fun startDrill(drillId: Int) {
        val drill = allDrills.find { it.id == drillId }

        drill?.let {
            val session = TrainingSessionUiModel(
                id = drillId,
                drillName = it.name,
                playersInvolved = 11,
                focus = it.focus,
                progress = 0,
                startTime = "Just now"
            )

            _uiState.value = _uiState.value.copy(currentSession = session)
        }
    }

    fun completeDrill() {
        // Update player progress
        _uiState.value = _uiState.value.copy(
            currentSession = null,
            overallProgress = (_uiState.value.overallProgress + 5).coerceAtMost(100)
        )
    }

    fun cancelDrill() {
        _uiState.value = _uiState.value.copy(currentSession = null)
    }
}