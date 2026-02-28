package com.fameafrica.afm2026.ui.screen.tactics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TacticsUiState(
    val isLoading: Boolean = true,
    val formations: List<String> = listOf(
        "4-4-2", "4-3-3", "4-2-3-1", "3-5-2",
        "4-1-4-1", "4-4-2 Diamond", "3-4-3", "5-3-2"
    ),
    val selectedFormation: String = "4-4-2",
    val selectedStyle: String = "Balanced",
    val defensiveThreshold: Int = 50,
    val attackingThreshold: Int = 50,
    val tempo: Int = 50,
    val width: Int = 50,
    val depth: Int = 50,
    val pressIntensity: Int = 50,
    val passingDirectness: Int = 50,
    val creativity: Int = 50
)

@HiltViewModel
class TacticsViewModel @Inject constructor(
    private val tacticsRepository: TacticsRepository,
    private val gameStateRepository: GameStatesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TacticsUiState(isLoading = true))
    val uiState: StateFlow<TacticsUiState> = _uiState

    init {
        loadTactics()
    }

    private fun loadTactics() {
        viewModelScope.launch {
            val gameState = gameStateRepository.getValidSaveGames().firstOrNull()?.firstOrNull()
            val teamName = gameState?.teamName ?: ""

            val tactics = tacticsRepository.getTacticsByTeam(teamName)

            if (tactics != null) {
                _uiState.value = TacticsUiState(
                    isLoading = false,
                    selectedFormation = tactics.formation,
                    selectedStyle = tactics.playstyle,
                    defensiveThreshold = tactics.defensiveThreshold,
                    attackingThreshold = tactics.attackingThreshold,
                    tempo = tactics.tempo,
                    width = tactics.width,
                    depth = tactics.depth,
                    pressIntensity = tactics.pressIntensity,
                    passingDirectness = tactics.passingDirectness,
                    creativity = tactics.creativity
                )
            } else {
                _uiState.value = TacticsUiState(isLoading = false)
            }
        }
    }

    fun selectFormation(formation: String) {
        _uiState.value = _uiState.value.copy(selectedFormation = formation)
    }

    fun updateStyle(style: String) {
        _uiState.value = _uiState.value.copy(selectedStyle = style)

        // Update thresholds based on style
        when (style) {
            "Possession" -> _uiState.value = _uiState.value.copy(
                defensiveThreshold = 40,
                attackingThreshold = 60,
                tempo = 40,
                pressIntensity = 60,
                passingDirectness = 30,
                creativity = 70
            )
            "Attacking" -> _uiState.value = _uiState.value.copy(
                defensiveThreshold = 30,
                attackingThreshold = 80,
                tempo = 70,
                pressIntensity = 70,
                passingDirectness = 60,
                creativity = 70
            )
            "Balanced" -> _uiState.value = _uiState.value.copy(
                defensiveThreshold = 50,
                attackingThreshold = 50,
                tempo = 50,
                pressIntensity = 50,
                passingDirectness = 50,
                creativity = 50
            )
            "Counter" -> _uiState.value = _uiState.value.copy(
                defensiveThreshold = 60,
                attackingThreshold = 50,
                tempo = 80,
                pressIntensity = 40,
                passingDirectness = 80,
                creativity = 30
            )
            "Defensive" -> _uiState.value = _uiState.value.copy(
                defensiveThreshold = 80,
                attackingThreshold = 30,
                tempo = 30,
                pressIntensity = 30,
                passingDirectness = 30,
                creativity = 20
            )
            "Pressing" -> _uiState.value = _uiState.value.copy(
                defensiveThreshold = 60,
                attackingThreshold = 70,
                tempo = 80,
                pressIntensity = 90,
                passingDirectness = 50,
                creativity = 50
            )
        }
    }

    fun updateSlider(slider: String, value: Int) {
        _uiState.value = when (slider) {
            "defensive" -> _uiState.value.copy(defensiveThreshold = value)
            "attacking" -> _uiState.value.copy(attackingThreshold = value)
            "tempo" -> _uiState.value.copy(tempo = value)
            "width" -> _uiState.value.copy(width = value)
            "depth" -> _uiState.value.copy(depth = value)
            "press" -> _uiState.value.copy(pressIntensity = value)
            "passing" -> _uiState.value.copy(passingDirectness = value)
            "creativity" -> _uiState.value.copy(creativity = value)
            else -> _uiState.value
        }
    }

    fun saveTactics() {
        viewModelScope.launch {
            val gameState = gameStateRepository.getValidSaveGames().firstOrNull()?.firstOrNull()
            val teamName = gameState?.teamName ?: ""

            val existing = tacticsRepository.getTacticsByTeam(teamName)

            if (existing != null) {
                tacticsRepository.customizeTactics(
                    teamName = teamName,
                    formation = _uiState.value.selectedFormation,
                    playstyle = _uiState.value.selectedStyle,
                    defensiveThreshold = _uiState.value.defensiveThreshold,
                    attackingThreshold = _uiState.value.attackingThreshold,
                    tempo = _uiState.value.tempo,
                    width = _uiState.value.width,
                    depth = _uiState.value.depth,
                    pressIntensity = _uiState.value.pressIntensity,
                    passingDirectness = _uiState.value.passingDirectness,
                    creativity = _uiState.value.creativity
                )
            }
        }
    }

    fun resetTactics() {
        _uiState.value = _uiState.value.copy(
            selectedFormation = "4-4-2",
            selectedStyle = "Balanced",
            defensiveThreshold = 50,
            attackingThreshold = 50,
            tempo = 50,
            width = 50,
            depth = 50,
            pressIntensity = 50,
            passingDirectness = 50,
            creativity = 50
        )
    }
}