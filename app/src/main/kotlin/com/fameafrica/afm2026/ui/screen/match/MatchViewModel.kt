package com.fameafrica.afm2026.ui.screen.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MatchStatus {
    PRE_MATCH,
    LIVE,
    HALFTIME,
    FULL_TIME,
    PAUSED
}

data class MatchInfoUiModel(
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val homeLogo: String?,
    val awayLogo: String?,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val competition: String,
    val stadium: String,
    val kickoff: String,
    val homePosition: Int,
    val awayPosition: Int,
    val homeForm: String,
    val awayForm: String,
    val homeAvgGoals: Double,
    val awayAvgGoals: Double,
    val h2hHomeWins: Int,
    val h2hAwayWins: Int,
    val h2hDraws: Int,
    val homeFormation: String,
    val awayFormation: String
)

data class MatchEventUiModel(
    val id: Int,
    val minute: String,
    val player: String,
    val team: String,
    val homeTeam: String,
    val awayTeam: String,
    val type: String,
    val icon: String,
    val detail: String
)

data class CommentaryUiModel(
    val id: Int,
    val minute: String,
    val text: String,
    val type: String
)

data class MatchStatsUiModel(
    val homePossession: Int,
    val awayPossession: Int,
    val homeShots: Int,
    val awayShots: Int,
    val homeShotsOnTarget: Int,
    val awayShotsOnTarget: Int,
    val homeCorners: Int,
    val awayCorners: Int,
    val homeFouls: Int,
    val awayFouls: Int,
    val homeYellowCards: Int,
    val awayYellowCards: Int,
    val homeRedCards: Int,
    val awayRedCards: Int,
    val homeOffsides: Int,
    val awayOffsides: Int
)

data class MatchUiState(
    val isLoading: Boolean = true,
    val matchStatus: MatchStatus = MatchStatus.PRE_MATCH,
    val matchInfo: MatchInfoUiModel? = null,
    val events: List<MatchEventUiModel> = emptyList(),
    val commentary: List<CommentaryUiModel> = emptyList(),
    val stats: MatchStatsUiModel? = null,
    val possession: Float = 50f,
    val currentMinute: Int = 0,
    val matchSpeed: Int = 1
)

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val fixturesRepository: FixturesRepository,
    private val fixturesResultsRepository: FixturesResultsRepository,
    private val matchEventsRepository: MatchEventsRepository,
    private val matchCommentaryRepository: MatchCommentaryRepository,
    private val teamsRepository: TeamsRepository,
    private val leagueStandingsRepository: LeagueStandingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchUiState(isLoading = true))
    val uiState: StateFlow<MatchUiState> = _uiState

    private var matchSimulationJob: kotlinx.coroutines.Job? = null

    fun loadMatch(matchId: Int) {
        viewModelScope.launch {
            // Load match data from repositories
            val fixture = fixturesRepository.getFixtureById(matchId)

            if (fixture == null) {
                _uiState.value = MatchUiState(isLoading = false)
                return@launch
            }

            val homeTeam = teamsRepository.getTeamByName(fixture.homeTeam)
            val awayTeam = teamsRepository.getTeamByName(fixture.awayTeam)

            // Get league positions
            val season = fixture.season.split("/").first().toInt()
            val homeStanding = fixture.league?.let {
                leagueStandingsRepository.getTeamPosition(it, season, fixture.homeTeam)
            }
            val awayStanding = fixture.league?.let {
                leagueStandingsRepository.getTeamPosition(it, season, fixture.awayTeam)
            }

            // Get recent form
            val homeForm = fixturesRepository.getTeamFormString(fixture.homeTeam, 5)
            val awayForm = fixturesRepository.getTeamFormString(fixture.awayTeam, 5)

            // Get head to head
            val h2h = fixturesRepository.getHeadToHead(fixture.homeTeam, fixture.awayTeam)
                .firstOrNull() ?: emptyList()
            val homeWins = h2h.count { it.winner == fixture.homeTeam }
            val awayWins = h2h.count { it.winner == fixture.awayTeam }
            val draws = h2h.count { it.isDraw }

            val matchInfo = MatchInfoUiModel(
                id = matchId,
                homeTeam = fixture.homeTeam,
                awayTeam = fixture.awayTeam,
                homeLogo = homeTeam?.logoPath,
                awayLogo = awayTeam?.logoPath,
                competition = fixture.league ?: fixture.cupName ?: "Friendly",
                stadium = fixture.stadium,
                kickoff = fixture.matchDate.substring(11, 16),
                homePosition = homeStanding?.position ?: 0,
                awayPosition = awayStanding?.position ?: 0,
                homeForm = homeForm,
                awayForm = awayForm,
                homeAvgGoals = 1.5,
                awayAvgGoals = 1.3,
                h2hHomeWins = homeWins,
                h2hAwayWins = awayWins,
                h2hDraws = draws,
                homeFormation = "4-4-2",
                awayFormation = "4-3-3"
            )

            _uiState.value = MatchUiState(
                isLoading = false,
                matchInfo = matchInfo
            )
        }
    }

    fun startMatch() {
        _uiState.value = _uiState.value.copy(matchStatus = MatchStatus.LIVE)
        startMatchSimulation()
    }

    fun pauseMatch() {
        _uiState.value = _uiState.value.copy(matchStatus = MatchStatus.PAUSED)
        matchSimulationJob?.cancel()
    }

    fun resumeMatch() {
        _uiState.value = _uiState.value.copy(matchStatus = MatchStatus.LIVE)
        startMatchSimulation()
    }

    fun changeSpeed(speed: Int) {
        _uiState.value = _uiState.value.copy(matchSpeed = speed)
        // Restart simulation with new speed
        if (_uiState.value.matchStatus == MatchStatus.LIVE) {
            matchSimulationJob?.cancel()
            startMatchSimulation()
        }
    }

    fun startSecondHalf() {
        _uiState.value = _uiState.value.copy(matchStatus = MatchStatus.LIVE)
        startMatchSimulation()
    }

    fun highlightEvent(eventId: Int) {
        // Scroll to event in commentary
    }

    private fun startMatchSimulation() {
        matchSimulationJob = viewModelScope.launch {
            val speed = _uiState.value.matchSpeed
            val delayMs = when (speed) {
                1 -> 2000L
                2 -> 1000L
                3 -> 500L
                4 -> 250L
                else -> 2000L
            }

            // Simulate 90 minutes
            for (minute in 1..90) {
                _uiState.value = _uiState.value.copy(currentMinute = minute)

                // Random events
                if (minute % 5 == 0) {
                    generateRandomEvent(minute)
                }

                delay(delayMs)
            }

            // Match ended
            _uiState.value = _uiState.value.copy(matchStatus = MatchStatus.FULL_TIME)
        }
    }

    private fun generateRandomEvent(minute: Int) {
        val currentState = _uiState.value
        val match = currentState.matchInfo ?: return

        val event = when ((minute % 10)) {
            0 -> {
                // Goal
                val scorer = if (minute % 2 == 0) match.homeTeam else match.awayTeam
                val newHomeScore = if (scorer == match.homeTeam) match.homeScore + 1 else match.homeScore
                val newAwayScore = if (scorer == match.awayTeam) match.awayScore + 1 else match.awayScore

                _uiState.value = currentState.copy(
                    matchInfo = match.copy(
                        homeScore = newHomeScore,
                        awayScore = newAwayScore
                    )
                )

                MatchEventUiModel(
                    id = minute,
                    minute = "$minute'",
                    player = if (scorer == match.homeTeam) "J. Mwamba" else "A. Diallo",
                    team = scorer,
                    homeTeam = match.homeTeam,
                    awayTeam = match.awayTeam,
                    type = "GOAL",
                    icon = "⚽",
                    detail = "Great finish!"
                )
            }
            5 -> {
                // Yellow Card
                MatchEventUiModel(
                    id = minute,
                    minute = "$minute'",
                    player = if (minute % 2 == 0) "M. Juma" else "K. Osei",
                    team = if (minute % 2 == 0) match.homeTeam else match.awayTeam,
                    homeTeam = match.homeTeam,
                    awayTeam = match.awayTeam,
                    type = "YELLOW",
                    icon = "🟨",
                    detail = "Late challenge"
                )
            }
            else -> {
                // Shot
                MatchEventUiModel(
                    id = minute,
                    minute = "$minute'",
                    player = if (minute % 2 == 0) "S. Msuva" else "M. Kipre",
                    team = if (minute % 2 == 0) match.homeTeam else match.awayTeam,
                    homeTeam = match.homeTeam,
                    awayTeam = match.awayTeam,
                    type = "SHOT",
                    icon = "⚡",
                    detail = "Wide of the goal"
                )
            }
        }

        val events = currentState.events.toMutableList()
        events.add(event)

        // Add commentary
        val commentary = currentState.commentary.toMutableList()
        commentary.add(
            CommentaryUiModel(
                id = minute,
                minute = "$minute'",
                text = when (event.type) {
                    "GOAL" -> "GOOOAL! ${event.player} scores! The crowd erupts!"
                    "YELLOW" -> "Yellow card for ${event.player}. The referee is firm."
                    else -> "${event.player} shoots... but it's wide."
                },
                type = event.type
            )
        )

        // Update stats
        val stats = currentState.stats ?: MatchStatsUiModel(
            homePossession = 50,
            awayPossession = 50,
            homeShots = 0,
            awayShots = 0,
            homeShotsOnTarget = 0,
            awayShotsOnTarget = 0,
            homeCorners = 0,
            awayCorners = 0,
            homeFouls = 0,
            awayFouls = 0,
            homeYellowCards = 0,
            awayYellowCards = 0,
            homeRedCards = 0,
            awayRedCards = 0,
            homeOffsides = 0,
            awayOffsides = 0
        )

        val newStats = when (event.type) {
            "GOAL" -> stats.copy(
                homeShots = stats.homeShots + (if (event.team == match.homeTeam) 1 else 0),
                awayShots = stats.awayShots + (if (event.team == match.awayTeam) 1 else 0),
                homeShotsOnTarget = stats.homeShotsOnTarget + (if (event.team == match.homeTeam) 1 else 0),
                awayShotsOnTarget = stats.awayShotsOnTarget + (if (event.team == match.awayTeam) 1 else 0)
            )
            "YELLOW" -> stats.copy(
                homeYellowCards = stats.homeYellowCards + (if (event.team == match.homeTeam) 1 else 0),
                awayYellowCards = stats.awayYellowCards + (if (event.team == match.awayTeam) 1 else 0)
            )
            else -> stats.copy(
                homeShots = stats.homeShots + (if (event.team == match.homeTeam) 1 else 0),
                awayShots = stats.awayShots + (if (event.team == match.awayTeam) 1 else 0)
            )
        }

        // Update possession (oscillate between 40-60)
        val newPossession = 50 + (minute % 20 - 10)

        _uiState.value = currentState.copy(
            events = events,
            commentary = commentary,
            stats = newStats,
            possession = newPossession.toFloat()
        )
    }
}