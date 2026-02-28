package com.fameafrica.afm2026.ui.screen.league

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeagueStandingUiModel(
    val id: Int,
    val position: Int,
    val name: String,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val gf: Int,
    val ga: Int,
    val gd: Int,
    val points: Int,
    val form: String
)

data class LeagueTableUiState(
    val isLoading: Boolean = true,
    val season: String = "2024/25",
    val standings: List<LeagueStandingUiModel> = emptyList(),
    val userTeamId: Int? = null
)

@HiltViewModel
class LeagueTableViewModel @Inject constructor(
    private val leagueStandingsRepository: LeagueStandingsRepository,
    private val teamsRepository: TeamsRepository,
    private val gameStateRepository: GameStatesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeagueTableUiState(isLoading = true))
    val uiState: StateFlow<LeagueTableUiState> = _uiState

    fun loadLeagueTable(leagueName: String) {
        viewModelScope.launch {
            val gameState = gameStateRepository.getValidSaveGames().firstOrNull()?.firstOrNull()
            val season = gameState?.season ?: "2024/25"
            val seasonYear = season.split("/").first().toInt()

            val standings = leagueStandingsRepository.getStandings(leagueName, seasonYear)
                .firstOrNull() ?: emptyList()

            val userTeamId = gameState?.teamId

            val uiModels = standings.map { standing ->
                LeagueStandingUiModel(
                    id = standing.id,
                    position = standing.position,
                    name = standing.teamName,
                    played = standing.matchesPlayed,
                    wins = standing.wins,
                    draws = standing.draws,
                    losses = standing.losses,
                    gf = standing.goalsScored,
                    ga = standing.goalsConceded,
                    gd = standing.goalDifference,
                    points = standing.points,
                    form = standing.form ?: "-----"
                )
            }

            _uiState.value = LeagueTableUiState(
                isLoading = false,
                season = season,
                standings = uiModels,
                userTeamId = userTeamId
            )
        }
    }
}