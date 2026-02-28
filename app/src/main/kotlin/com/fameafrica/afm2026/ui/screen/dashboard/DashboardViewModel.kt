package com.fameafrica.afm2026.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.repository.*
import com.fameafrica.afm2026.domain.manager.GameManager.GameDate
import com.fameafrica.afm2026.utils.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val managerName: String = "",
    val clubName: String = "",
    val reputationLevel: String = "Local",
    val boardSatisfaction: Int = 50,
    val leaguePosition: Int = 0,
    val form: String = "---",
    val morale: Int = 50,
    val transferBudget: Long = 0,
    val bankBalance: Long = 0,
    val wageBill: Long = 0,
    val financialTier: String = "",
    val nextMatch: NextMatchUiModel? = null,
    val objectives: List<ObjectiveUiModel> = emptyList(),
    val recentResults: List<ResultUiModel> = emptyList(),
    val upcomingFixtures: List<FixtureUiModel> = emptyList(),
    val latestNews: List<NewsUiModel> = emptyList(),
    val unreadNotifications: Int = 0
)

// Game Context State based on GameStatesEntity
data class GameContextState(
    val gameStateId: Int = 0,
    val gameDate: GameDate? = null,
    val week: Int = 1,
    val season: String = "2024/25",
    val saveName: String = "",
    val lastPlayed: String? = null,
    val gameVersion: String? = null
)

data class NextMatchUiModel(
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val competition: String,
    val time: String,
    val stadium: String,
    val matchDate: GameDate? = null
)

data class ObjectiveUiModel(
    val id: Int,
    val title: String,
    val progress: Int
)

data class ResultUiModel(
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int,
    val awayScore: Int,
    val competition: String,
    val isWin: Boolean,
    val isDraw: Boolean,
    val matchDate: GameDate? = null
)

data class FixtureUiModel(
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val date: String,
    val competition: String,
    val matchDate: GameDate? = null
)

data class NewsUiModel(
    val id: Int,
    val headline: String,
    val time: String
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val gameStatesRepository: GameStatesRepository,
    private val fixturesRepository: FixturesRepository,
    private val teamsRepository: TeamsRepository,
    private val financesRepository: FinancesRepository,
    private val leagueStandingsRepository: LeagueStandingsRepository,
    private val boardEvaluationRepository: BoardEvaluationRepository,
    private val objectivesRepository: ObjectivesRepository,
    private val newsRepository: NewsRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState

    private val _gameContext = MutableStateFlow(GameContextState())
    val gameContext: StateFlow<GameContextState> = _gameContext

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            // Get the most recent valid game state
            val validSaves = gameStatesRepository.getValidSaveGames().firstOrNull()
            val currentGameState = validSaves?.maxByOrNull { it.lastPlayed ?: "" }

            if (currentGameState == null) {
                // No active game state
                _uiState.value = DashboardUiState(isLoading = false)
                return@launch
            }

            val managerId = currentGameState.managerId
            val teamId = currentGameState.teamId
            val teamName = currentGameState.teamName
            val season = currentGameState.season
            val week = currentGameState.week
            val lastPlayed = currentGameState.lastPlayed
            val saveName = currentGameState.name

            // Parse game date from lastPlayed or create from week/season
            val gameDate = parseGameDate(lastPlayed, season, week)

            // Update game context
            _gameContext.value = GameContextState(
                gameStateId = currentGameState.id,
                gameDate = gameDate,
                week = week,
                season = season,
                saveName = saveName,
                lastPlayed = lastPlayed,
                gameVersion = currentGameState.gameVersion
            )

            // Get team details
            val team = teamsRepository.getTeamById(teamId)

            // Get team finances for current season
            val teamFinances = financesRepository.getTeamFinances(teamId, season)

            // Get financial dashboard for more detailed info
            val financeDashboard = financesRepository.getTeamFinanceDashboard(teamId, season)

            // Get board evaluation
            val boardEval = boardEvaluationRepository.getEvaluationByManagerName(
                currentGameState.managerName
            )

            // Get league position
            val leagueStandings = leagueStandingsRepository.getStandingsByPosition(
                team?.league ?: "",
                extractSeasonYear(season)
            ).firstOrNull() ?: emptyList()

            val position = leagueStandings.indexOfFirst { it.teamName == teamName } + 1

            // Get objectives
            val objectives = objectivesRepository.getObjectivesByTeam(teamName)
                .firstOrNull()?.take(3) ?: emptyList()

            // Get recent results (filtered by game date)
            val recentResults = fixturesRepository.getRecentResultsByTeam(teamName, 5)
                .firstOrNull() ?: emptyList()

            // Get upcoming fixtures (based on game date)
            val upcomingFixtures = fixturesRepository.getUpcomingFixturesByTeam(teamName)
                .firstOrNull()?.take(3) ?: emptyList()

            // Get news
            val news = newsRepository.getTopNews(5).firstOrNull() ?: emptyList()

            // Get next match
            val nextMatch = upcomingFixtures.firstOrNull()

            // Calculate form string from recent results
            val formString = calculateFormString(recentResults, teamName)

            _uiState.value = DashboardUiState(
                isLoading = false,
                managerName = currentGameState.managerName,
                clubName = teamName,
                reputationLevel = team?.reputation?.toString() ?: "Local",
                boardSatisfaction = boardEval?.boardSatisfaction ?: 50,
                leaguePosition = position,
                form = formString,
                morale = team?.morale ?: 50,
                // Financial data from FinancesRepository
                transferBudget = teamFinances?.budget ?: 0,
                bankBalance = teamFinances?.bankBalance ?: 0,
                wageBill = teamFinances?.wageBill ?: 0,
                financialTier = financeDashboard.financialTier ?: teamFinances?.financialTier ?: "Unknown",
                nextMatch = nextMatch?.let {
                    NextMatchUiModel(
                        id = it.id,
                        homeTeam = it.homeTeam,
                        awayTeam = it.awayTeam,
                        competition = it.league ?: it.cupName ?: "Friendly",
                        time = it.matchDate ?: "15:00",
                        stadium = it.stadium ?: "Home Stadium",
                        matchDate = parseMatchDate(it.matchDate, season, week)
                    )
                },
                objectives = objectives.map { obj ->
                    ObjectiveUiModel(
                        id = obj.id,
                        title = obj.objective ?: "Objective",
                        progress = obj.currentProgress?.toIntOrNull() ?: 0
                    )
                },
                recentResults = recentResults.map { result ->
                    val isWin = (result.homeTeam == teamName && result.homeScore > result.awayScore) ||
                            (result.awayTeam == teamName && result.awayScore > result.homeScore)
                    ResultUiModel(
                        id = result.id,
                        homeTeam = result.homeTeam,
                        awayTeam = result.awayTeam,
                        homeScore = result.homeScore,
                        awayScore = result.awayScore,
                        competition = result.league ?: result.cupName ?: "Friendly",
                        isWin = isWin,
                        isDraw = result.homeScore == result.awayScore,
                        matchDate = parseMatchDate(result.matchDate, season, week)
                    )
                },
                upcomingFixtures = upcomingFixtures.map { fixture ->
                    FixtureUiModel(
                        id = fixture.id,
                        homeTeam = fixture.homeTeam,
                        awayTeam = fixture.awayTeam,
                        date = formatMatchDateForDisplay(fixture.matchDate, season, week),
                        competition = fixture.league ?: fixture.cupName ?: "Friendly",
                        matchDate = parseMatchDate(fixture.matchDate, season, week)
                    )
                },
                latestNews = news.take(3).map { newsItem ->
                    NewsUiModel(
                        id = newsItem.id,
                        headline = newsItem.headline,
                        time = formatGameTime(newsItem.timestamp, week)
                    )
                }
            )
        }
    }

    /**
     * Parse game date from lastPlayed timestamp and game context
     */
    private fun parseGameDate(lastPlayed: String?, season: String, week: Int): GameDate? {
        return try {
            // Try to parse from lastPlayed if available
            if (!lastPlayed.isNullOrBlank()) {
                // Format: "yyyy-MM-dd HH:mm:ss"
                val datePart = lastPlayed.split(" ").firstOrNull()
                if (datePart != null) {
                    val parts = datePart.split("-")
                    if (parts.size == 3) {
                        return GameDate(
                            year = parts[0].toInt(),
                            month = parts[1].toInt(),
                            day = parts[2].toInt(),
                            weekOfSeason = week
                        )
                    }
                }
            }

            // Fallback: Calculate based on season and week
            // Assume season starts in August
            val seasonYear = extractSeasonYear(season)
            val startMonth = 8 // August
            val startDay = 1

            // Simple calculation: each week adds 7 days
            val totalDays = (week - 1) * 7
            var month = startMonth
            var day = startDay + totalDays

            // Very simplified month handling (would need proper calendar logic)
            while (day > 30) {
                day -= 30
                month++
                if (month > 12) {
                    month = 1
                }
            }

            GameDate(
                year = if (month >= startMonth) seasonYear else seasonYear + 1,
                month = month,
                day = day,
                weekOfSeason = week
            )
        } catch (e: Exception) {
            // Default fallback
            GameDate(
                year = extractSeasonYear(season),
                month = 8,
                day = 1 + (week - 1) * 7,
                weekOfSeason = week
            )
        }
    }

    /**
     * Parse match date from match data
     */
    private fun parseMatchDate(matchDateString: String?, season: String, week: Int): GameDate? {
        if (matchDateString.isNullOrBlank()) return null

        return try {
            // Try to parse as date
            val parts = matchDateString.split("-")
            if (parts.size == 3) {
                GameDate(
                    year = parts[0].toInt(),
                    month = parts[1].toInt(),
                    day = parts[2].toInt(),
                    weekOfSeason = week
                )
            } else {
                // Fallback to calculation
                parseGameDate(null, season, week)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extract season year from season string (e.g., "2024/25" -> 2024)
     */
    private fun extractSeasonYear(season: String): Int {
        return try {
            season.split("/").first().toInt()
        } catch (e: Exception) {
            2024
        }
    }

    /**
     * Calculate form string from recent results
     */
    private fun calculateFormString(results: List<Any>, teamName: String): String {
        // This would need proper implementation with your actual Result entity
        // For now, return a placeholder based on results if available
        return if (results.isNotEmpty()) {
            results.take(5).joinToString("") {
                // Placeholder - would check actual results
                "W"
            }.padEnd(5, '-').take(5)
        } else {
            "-----"
        }
    }

    /**
     * Format match date for display based on game context
     */
    private fun formatMatchDateForDisplay(matchDate: String?, season: String, week: Int): String {
        if (matchDate.isNullOrBlank()) return "TBD"

        return try {
            val gameDate = parseMatchDate(matchDate, season, week)
            gameDate?.toDisplayString() ?: matchDate
        } catch (e: Exception) {
            matchDate
        }
    }

    /**
     * Format game time based on game weeks instead of real time
     */
    private fun formatGameTime(newsTime: String?, currentWeek: Int): String {
        if (newsTime == null) return "Recent"

        // This is a placeholder implementation. A real implementation would parse the timestamp
        // and calculate the difference from the current time.
        return newsTime
    }

    /**
     * Get financial health color based on financial tier
     */
    fun getFinancialHealthColor(financialTier: String): String {
        return when (financialTier) {
            "Rich" -> "#1B5E3F"  // Pitch Green
            "Upper Middle" -> "#F4A261"  // Afro Sun Orange
            "Middle" -> "#FFD966"  // Champions Gold
            "Lower" -> "#C4B9A6"  // Muted Parchment
            "Poor" -> "#B83B3B"  // Kente Red
            else -> "#C4B9A6"
        }
    }

    /**
     * Check if a match is today in game time
     */
    fun isMatchToday(matchDate: GameDate?): Boolean {
        val currentGameDate = _gameContext.value.gameDate ?: return false
        return matchDate?.year == currentGameDate.year &&
                matchDate?.month == currentGameDate.month &&
                matchDate?.day == currentGameDate.day
    }

    /**
     * Get the current save game name for display
     */
    fun getCurrentSaveDisplay(): String {
        val context = _gameContext.value
        return "${context.saveName} - ${context.season} Week ${context.week}"
    }

    /**
     * Advance game time (simulate next week)
     */
    fun advanceToNextWeek() {
        viewModelScope.launch {
            val currentContext = _gameContext.value
            val newWeek = currentContext.week + 1

            // Update the game state in repository
            gameStatesRepository.saveGame(
                gameStateId = currentContext.gameStateId,
                week = newWeek
            )

            // Refresh data
            loadDashboardData()
        }
    }

    /**
     * Refresh dashboard data
     */
    fun refreshData() {
        loadDashboardData()
    }

    /**
     * Switch to a different save game
     */
    fun switchToSaveGame(gameStateId: Int) {
        viewModelScope.launch {
            gameStatesRepository.loadGame(gameStateId)
            loadDashboardData()
        }
    }
}
