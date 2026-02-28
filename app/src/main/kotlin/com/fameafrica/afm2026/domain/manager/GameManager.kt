package com.fameafrica.afm2026.domain.manager

import com.fameafrica.afm2026.data.database.entities.FixturesEntity
import com.fameafrica.afm2026.data.database.entities.FixturesResultsEntity
import com.fameafrica.afm2026.data.database.entities.ManagersEntity
import com.fameafrica.afm2026.data.database.entities.MatchEventsEntity
import com.fameafrica.afm2026.data.database.entities.RefereesEntity
import com.fameafrica.afm2026.data.database.entities.TeamsEntity
import com.fameafrica.afm2026.data.repository.*
import com.fameafrica.afm2026.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameManager @Inject constructor(
    private val gameStateRepository: GameStatesRepository,
    private val fixturesRepository: FixturesRepository,
    private val fixturesResultsRepository: FixturesResultsRepository,
    private val matchEventsRepository: MatchEventsRepository,
    private val matchCommentaryRepository: MatchCommentaryRepository,
    private val teamsRepository: TeamsRepository,
    private val managersRepository: ManagersRepository,
    private val refereesRepository: RefereesRepository,
    private val playersRepository: PlayersRepository,
    private val leagueStandingsRepository: LeagueStandingsRepository,
    private val cupGroupStandingsRepository: CupGroupStandingsRepository,
    private val transfersRepository: TransfersRepository,
    private val financesRepository: FinancesRepository,
    private val boardEvaluationRepository: BoardEvaluationRepository,
    private val fanExpectationsRepository: FanExpectationsRepository,
    private val fanReactionsRepository: FanReactionsRepository,
    private val newsRepository: NewsRepository,
    private val notificationsRepository: NotificationsRepository,
    private val seasonHistoryRepository: SeasonHistoryRepository,
    private val seasonAwardsRepository: SeasonAwardsRepository,
    private val trophiesRepository: TrophiesRepository,
    private val eloHistoryRepository: EloHistoryRepository
) {

    private val _gameState = MutableStateFlow<GameState>(GameState.Loading)
    val gameState: StateFlow<GameState> = _gameState

    private val gameScope = CoroutineScope(Dispatchers.IO)

    data class GameContext(
        val managerId: Int,
        val teamId: Int,
        val teamName: String,
        val season: String,
        val week: Int,
        val gameDate: GameDate
    )

    data class GameDate(
        val year: Int,
        val month: Int,
        val day: Int,
        val weekOfSeason: Int
    ) {
        fun toDisplayString(): String {
            val monthNames = listOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
            return "${day} ${monthNames[month-1]} ${year}"
        }
    }

    sealed class GameState {
        object Loading : GameState()
        data class Active(val context: GameContext) : GameState()
        data class Processing(val message: String) : GameState()
        object NoSave : GameState()
    }

    fun initializeGame(managerId: Int) {
        gameScope.launch {
            val gameState = gameStateRepository.getGameStateByManagerId(managerId)

            if (gameState == null) {
                _gameState.value = GameState.NoSave
                return@launch
            }

            val context = GameContext(
                managerId = gameState.managerId,
                teamId = gameState.teamId,
                teamName = gameState.teamName,
                season = gameState.season,
                week = gameState.week,
                gameDate = calculateGameDate(gameState.season, gameState.week)
            )

            _gameState.value = GameState.Active(context)
        }
    }

    fun processNextMatch() {
        gameScope.launch {
            val currentState = _gameState.value
            if (currentState !is GameState.Active) return@launch

            _gameState.value = GameState.Processing("Simulating next match...")

            // Get next fixture
            val nextFixture = fixturesRepository.getNextMatchForTeam(currentState.context.teamName)

            if (nextFixture != null) {
                // Simulate match
                val matchResult = simulateMatch(nextFixture)

                // Process match result
                processMatchResult(matchResult)

                // Update game week
                val newWeek = currentState.context.week + 1
                gameStateRepository.saveGame(
                    gameStateId = gameStateRepository.getGameStateByManagerId(currentState.context.managerId)?.id ?: 0,
                    week = newWeek
                )

                // Create updated context
                val newContext = currentState.context.copy(week = newWeek)
                _gameState.value = GameState.Active(newContext)

                // Send notification
                sendMatchResultNotification(matchResult)
            } else {
                // No more matches - end of season?
                _gameState.value = currentState
            }
        }
    }

    private suspend fun simulateMatch(fixture: FixturesEntity): MatchResult {
        // Get team strengths
        val homeTeam = teamsRepository.getTeamByName(fixture.homeTeam)
        val awayTeam = teamsRepository.getTeamByName(fixture.awayTeam)
        val homeManager = homeTeam?.managerId?.let { managersRepository.getManagerById(it) }
        val awayManager = awayTeam?.managerId?.let { managersRepository.getManagerById(it) }
        val referee = fixture.refereeId?.let { refereesRepository.getRefereeById(it) }

        val homeStrength = homeTeam?.eloRating ?: 1500
        val awayStrength = awayTeam?.eloRating ?: 1500

        // Calculate probabilities
        val homeAdvantage = 1.1
        val homeWinProb = (homeStrength.toDouble() / (homeStrength + awayStrength)) * homeAdvantage
        val awayWinProb = (awayStrength.toDouble() / (homeStrength + awayStrength)) / homeAdvantage
        val drawProb = 1.0 - (homeWinProb + awayWinProb).coerceIn(0.0, 1.0)

        // Generate result
        val random = Math.random()
        val homeScore: Int
        val awayScore: Int
        val result: String

        when {
            random < homeWinProb -> {
                homeScore = (1..4).random()
                awayScore = (0..homeScore-1).random()
                result = "HOME_WIN"
            }
            random < homeWinProb + awayWinProb -> {
                awayScore = (1..4).random()
                homeScore = (0..awayScore-1).random()
                result = "AWAY_WIN"
            }
            else -> {
                homeScore = (0..2).random()
                awayScore = homeScore
                result = "DRAW"
            }
        }

        // Generate match events
        val events = generateMatchEvents(fixture, homeScore, awayScore)

        return MatchResult(
            fixture = fixture,
            homeScore = homeScore,
            awayScore = awayScore,
            result = result,
            events = events,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            homeManager = homeManager,
            awayManager = awayManager,
            referee = referee
        )
    }

    private suspend fun generateMatchEvents(
        fixture: FixturesEntity,
        homeScore: Int,
        awayScore: Int
    ): List<MatchEventsEntity> {
        val events = mutableListOf<MatchEventsEntity>()

        // Generate goals
        repeat(homeScore) { i ->
            events.add(
                MatchEventsEntity(
                    matchId = fixture.id,
                    minute = (1..90).random(),
                    eventType = "GOAL",
                    playerName = "Player ${i+1}",
                    playerId = 1,
                    teamName = fixture.homeTeam,
                    homeScore = i + 1,
                    awayScore = awayScore
                )
            )
        }

        repeat(awayScore) { i ->
            events.add(
                MatchEventsEntity(
                    matchId = fixture.id,
                    minute = (1..90).random(),
                    eventType = "GOAL",
                    playerName = "Player ${i+1}",
                    playerId = 2,
                    teamName = fixture.awayTeam,
                    homeScore = homeScore,
                    awayScore = i + 1
                )
            )
        }

        // Generate cards (random)
        if ((1..10).random() > 7) {
            events.add(
                MatchEventsEntity(
                    matchId = fixture.id,
                    minute = (1..90).random(),
                    eventType = "YELLOW_CARD",
                    playerName = "Carded Player",
                    playerId = 3,
                    teamName = if ((1..2).random() == 1) fixture.homeTeam else fixture.awayTeam
                )
            )
        }

        return events
    }

    private suspend fun processMatchResult(matchResult: MatchResult) {
        val fixture = matchResult.fixture

        // 1. Update fixture with result
        fixturesRepository.completeFixture(
            fixtureId = fixture.id,
            homeScore = matchResult.homeScore,
            awayScore = matchResult.awayScore
        )

        // 2. Create match result entity
        val resultEntity = FixturesResultsEntity(
            fixtureId = fixture.id,
            matchDate = fixture.matchDate,
            homeTeam = fixture.homeTeam,
            awayTeam = fixture.awayTeam,
            homeScore = matchResult.homeScore,
            awayScore = matchResult.awayScore,
            matchType = fixture.matchType,
            season = fixture.season,
            leagueName = fixture.league,
            cupName = fixture.cupName,
            stadium = fixture.stadium
        )
        fixturesResultsRepository.insertResult(resultEntity)

        // 3. Insert match events
        matchEventsRepository.insertAllEvents(matchResult.events)

        // 4. Generate commentary
        matchCommentaryRepository.createCommentaryFromEvent(
            matchId = fixture.id,
            homeTeam = matchResult.homeTeam?.name ?: fixture.homeTeam,
            awayTeam = matchResult.awayTeam?.name ?: fixture.awayTeam,
            homeManager = matchResult.homeManager,
            awayManager = matchResult.awayManager,
            referee = matchResult.referee,
            event = matchResult.events
        )

        // 5. Update Elo ratings
        eloHistoryRepository.processMatchResult(resultEntity)

        // 6. Update league standings if league match
        if (fixture.league != null) {
            leagueStandingsRepository.updateStandingsAfterMatch(resultEntity)
        }

        // 7. Update cup standings if cup match
        if (fixture.cupName != null && fixture.round?.contains("Group") == true) {
            cupGroupStandingsRepository.updateGroupStandingsAfterMatch(resultEntity)
        }

        // 8. Update team morale
        updateTeamMorale(fixture, matchResult.result)

        // 9. Update player stats
        updatePlayerStats(matchResult.events)

        // 10. Update finances (ticket revenue, prize money)
        updateMatchFinances(fixture, matchResult)

        // 11. Update board satisfaction
        updateBoardSatisfaction(fixture, matchResult)

        // 12. Update fan expectations
        updateFanReactions(fixture, matchResult)

        // 13. Create news article
        createMatchNews(matchResult)
    }

    private suspend fun updateTeamMorale(fixture: FixturesEntity, result: String) {
        val homeTeam = teamsRepository.getTeamByName(fixture.homeTeam)
        val awayTeam = teamsRepository.getTeamByName(fixture.awayTeam)

        val homeChange = when {
            result == "HOME_WIN" -> 5
            result == "DRAW" -> 1
            else -> -3
        }

        val awayChange = when {
            result == "AWAY_WIN" -> 5
            result == "DRAW" -> 1
            else -> -3
        }

        homeTeam?.let { teamsRepository.updateTeamMorale(it.id, homeChange) }
        awayTeam?.let { teamsRepository.updateTeamMorale(it.id, awayChange) }
    }

    private suspend fun updatePlayerStats(events: List<MatchEventsEntity>) {
        events.forEach { event ->
            when (event.eventType) {
                "GOAL" -> {
                    playersRepository.incrementPlayerGoals(event.playerId)
                    event.assistPlayerId?.let { playersRepository.incrementPlayerAssists(it) }
                }
                "YELLOW_CARD" -> playersRepository.incrementPlayerYellowCards(event.playerId)
                "RED_CARD" -> playersRepository.incrementPlayerRedCards(event.playerId)
            }
        }
    }

    private suspend fun updateMatchFinances(fixture: FixturesEntity, matchResult: MatchResult) {
        val season = fixture.season
        val homeTeam = teamsRepository.getTeamByName(fixture.homeTeam)

        // Ticket revenue (simplified)
        homeTeam?.let { team ->
            val attendance = (team.stadiumCapacity * 0.8).toInt()
            val avgTicketPrice = when (fixture.matchType) {
                "Derby" -> 40
                "Cup" -> 30
                else -> 20
            }
            val revenue = attendance * avgTicketPrice.toLong()

            financesRepository.addMatchdayRevenue(team.id, season, revenue)
        }
    }

    private suspend fun updateBoardSatisfaction(fixture: FixturesEntity, matchResult: MatchResult) {
        val homeTeam = teamsRepository.getTeamByName(fixture.homeTeam)
        homeTeam?.managerId?.let { managerId ->
            val boardEval = boardEvaluationRepository.getEvaluationByManagerName(
                teamsRepository.getTeamById(homeTeam.id)?.name ?: ""
            )

            boardEval?.let { eval ->
                val change = when {
                    matchResult.result == "HOME_WIN" -> 5
                    matchResult.result == "DRAW" -> 0
                    else -> -5
                }
                boardEvaluationRepository.adjustBoardSatisfaction(eval.managerName, change)
                boardEvaluationRepository.evaluateBoardStatus(eval.managerName)
            }
        }
    }

    private suspend fun updateFanReactions(fixture: FixturesEntity, matchResult: MatchResult) {
        val homeTeam = fixture.homeTeam
        val isWin = matchResult.result == "HOME_WIN"
        val isDraw = matchResult.result == "DRAW"
        val isUpset = checkForUpset(fixture, matchResult)

        fanExpectationsRepository.adjustConfidenceLevel(
            homeTeam,
            when {
                isWin && isUpset -> 10
                isWin -> 5
                isDraw -> 0
                else -> -5
            }
        )

        // Create fan reaction
        val reactionText = when {
            isWin && isUpset -> "EUROPEAN CLUBS ARE WATCHING! The fans are going wild after this massive result!"
            isWin -> "The fans are celebrating! Three points in the bag!"
            isDraw -> "Mixed feelings in the stands... a point is a point."
            else -> "The fans are disappointed. They expect better from this team."
        }

        // Generate reaction for the home team's fans
        fanReactionsRepository.generateReactionFromResult(
            teamName = fixture.homeTeam,
            isWin = matchResult.result == "HOME_WIN",
            isDraw = matchResult.result == "DRAW",
            isLoss = matchResult.result == "AWAY_WIN",
            isUpset = isUpset
        )

        // Generate reaction for the away team's fans
        fanReactionsRepository.generateReactionFromResult(
            teamName = fixture.awayTeam,
            isWin = matchResult.result == "AWAY_WIN",
            isDraw = matchResult.result == "DRAW",
            isLoss = matchResult.result == "HOME_WIN",
            isUpset = isUpset
        )

    }

    private suspend fun checkForUpset(fixture: FixturesEntity, matchResult: MatchResult): Boolean {
        val homeElo = teamsRepository.getTeamByName(fixture.homeTeam)?.eloRating ?: 1500
        val awayElo = teamsRepository.getTeamByName(fixture.awayTeam)?.eloRating ?: 1500

        return (matchResult.result == "HOME_WIN" && homeElo < awayElo - 100) ||
                (matchResult.result == "AWAY_WIN" && awayElo < homeElo - 100)
    }

    private suspend fun createMatchNews(matchResult: MatchResult) {
        val fixture = matchResult.fixture
        val isUpset = checkForUpset(fixture, matchResult)

        newsRepository.createMatchReport(
            homeTeam = fixture.homeTeam,
            awayTeam = fixture.awayTeam,
            homeScore = matchResult.homeScore,
            awayScore = matchResult.awayScore,
            isUpset = isUpset,
            journalistName = if (isUpset) "Breaking News" else "Sports Desk"
        )
    }

    private suspend fun sendMatchResultNotification(matchResult: MatchResult) {
        val fixture = matchResult.fixture
        val managerId = teamsRepository.getTeamByName(fixture.homeTeam)?.managerId
            ?: teamsRepository.getTeamByName(fixture.awayTeam)?.managerId
            ?: return // Cannot determine manager to notify

        val title = "Match Result"
        val message = "${fixture.homeTeam} ${matchResult.homeScore} - ${matchResult.awayScore} ${fixture.awayTeam}"

        notificationsRepository.createNotification(
            managerId = managerId,
            title = title,
            message = message,
            type = "match_result",
            relatedId = fixture.id
        )
    }

    private fun calculateGameDate(season: String, week: Int): GameDate {
        val seasonYear = season.split("/").first().toInt()

        // Assume season starts in August
        val baseMonth = 8 // August
        val totalDays = week * 7

        var month = baseMonth
        var day = totalDays

        while (day > 30) {
            month++
            day -= 30
            if (month > 12) {
                month = 1
            }
        }

        return GameDate(
            year = if (month >= 8) seasonYear else seasonYear + 1,
            month = month,
            day = day,
            weekOfSeason = week
        )
    }

    data class MatchResult(
        val fixture: FixturesEntity,
        val homeScore: Int,
        val awayScore: Int,
        val result: String,
        val events: List<MatchEventsEntity>,    // Add these new properties
        val homeTeam: TeamsEntity?,
        val awayTeam: TeamsEntity?,
        val homeManager: ManagersEntity?,
        val awayManager: ManagersEntity?,
        val referee: RefereesEntity?
    )
}