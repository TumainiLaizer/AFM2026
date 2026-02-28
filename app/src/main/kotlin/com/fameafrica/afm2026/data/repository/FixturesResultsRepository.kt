package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.CompleteResultDetails
import com.fameafrica.afm2026.data.database.dao.FixturesResultsDao
import com.fameafrica.afm2026.data.database.dao.MonthlyStatistics
import com.fameafrica.afm2026.data.database.dao.RefereeCardStats
import com.fameafrica.afm2026.data.database.dao.ResultWithCompetition
import com.fameafrica.afm2026.data.database.dao.SeasonStatistics
import com.fameafrica.afm2026.data.database.entities.FixturesResultsEntity
import com.fameafrica.afm2026.data.database.entities.FixturesEntity
import com.fameafrica.afm2026.data.database.entities.MatchEventsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FixturesResultsRepository @Inject constructor(
    private val fixturesResultsDao: FixturesResultsDao,
    private val fixturesRepository: FixturesRepository,
    private val teamsRepository: TeamsRepository,
    private val playersRepository: PlayersRepository,
    private val refereesRepository: RefereesRepository,
    private val leagueStandingsRepository: LeagueStandingsRepository,
    private val cupGroupStandingsRepository: CupGroupStandingsRepository,
    private val matchEventsRepository: MatchEventsRepository
) {

    // ============ BASIC CRUD ============

    fun getAllResults(): Flow<List<FixturesResultsEntity>> = fixturesResultsDao.getAll()

    suspend fun getResultByFixtureId(fixtureId: Int): FixturesResultsEntity? =
        fixturesResultsDao.getByFixtureId(fixtureId)

    suspend fun insertResult(result: FixturesResultsEntity) =
        fixturesResultsDao.insert(result)

    suspend fun insertAllResults(results: List<FixturesResultsEntity>) =
        fixturesResultsDao.insertAll(results)

    suspend fun updateResult(result: FixturesResultsEntity) =
        fixturesResultsDao.update(result)

    suspend fun deleteResult(result: FixturesResultsEntity) =
        fixturesResultsDao.delete(result)

    suspend fun getResultsCount(): Int = fixturesResultsDao.getCount()

    // ============ DATE-BASED ============

    fun getResultsByDate(date: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getResultsByDate(date)

    fun getResultsBetween(startDate: String, endDate: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getResultsBetween(startDate, endDate)

    fun getResultsByYear(year: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getResultsByYear(year)

    fun getResultsByMonth(month: String, year: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getResultsByMonth(month, year)

    /**
     * Get today's results
     */
    fun getTodaysResults(): Flow<List<FixturesResultsEntity>> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return fixturesResultsDao.getResultsByDate(today)
    }

    // ============ TEAM-BASED ============

    fun getResultsByTeam(teamName: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getResultsByTeam(teamName)

    fun getHeadToHead(team1: String, team2: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getHeadToHead(team1, team2)

    fun getHomeResults(teamName: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getHomeResults(teamName)

    fun getAwayResults(teamName: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getAwayResults(teamName)

    fun getWinsByTeam(teamName: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getWinsByTeam(teamName)

    fun getLossesByTeam(teamName: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getLossesByTeam(teamName)

    fun getDrawsByTeam(teamName: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getDrawsByTeam(teamName)

    /**
     * Get team's form from results (last 5 matches)
     */
    suspend fun getTeamForm(teamName: String, limit: Int = 5): TeamForm {
        val results = fixturesResultsDao.getResultsByTeam(teamName)
            .firstOrNull()?.take(limit) ?: emptyList()

        val form = results.joinToString("") { result ->
            when {
                result.homeTeam == teamName && result.homeTeamWin -> "W"
                result.awayTeam == teamName && result.awayTeamWin -> "W"
                result.isDraw -> "D"
                else -> "L"
            }
        }

        val goalsFor = results.sumOf { result ->
            if (result.homeTeam == teamName) result.homeScore else result.awayScore
        }

        val goalsAgainst = results.sumOf { result ->
            if (result.homeTeam == teamName) result.awayScore else result.homeScore
        }

        val wins = results.count { result ->
            (result.homeTeam == teamName && result.homeTeamWin) ||
                    (result.awayTeam == teamName && result.awayTeamWin)
        }

        val draws = results.count { it.isDraw }
        val losses = results.size - wins - draws

        return TeamForm(
            formString = form,
            played = results.size,
            wins = wins,
            draws = draws,
            losses = losses,
            goalsFor = goalsFor,
            goalsAgainst = goalsAgainst,
            goalDifference = goalsFor - goalsAgainst,
            points = wins * 3 + draws
        )
    }

    /**
     * Get head-to-head record summary
     */
    suspend fun getHeadToHeadRecord(team1: String, team2: String): HeadToHeadRecord {
        val matches = fixturesResultsDao.getHeadToHead(team1, team2)
            .firstOrNull() ?: emptyList()

        val team1Wins = matches.count { result ->
            (result.homeTeam == team1 && result.homeTeamWin) ||
                    (result.awayTeam == team1 && result.awayTeamWin)
        }

        val team2Wins = matches.count { result ->
            (result.homeTeam == team2 && result.homeTeamWin) ||
                    (result.awayTeam == team2 && result.awayTeamWin)
        }

        val draws = matches.count { it.isDraw }

        val team1Goals = matches.sumOf { result ->
            if (result.homeTeam == team1) result.homeScore else result.awayScore
        }

        val team2Goals = matches.sumOf { result ->
            if (result.homeTeam == team2) result.homeScore else result.awayScore
        }

        return HeadToHeadRecord(
            totalMatches = matches.size,
            team1Wins = team1Wins,
            team2Wins = team2Wins,
            draws = draws,
            team1Goals = team1Goals,
            team2Goals = team2Goals,
            team1RecentForm = matches.take(5).joinToString("") { result ->
                if ((result.homeTeam == team1 && result.homeTeamWin) ||
                    (result.awayTeam == team1 && result.awayTeamWin)) "W"
                else if (result.isDraw) "D"
                else "L"
            }
        )
    }

    // ============ LEAGUE-BASED ============

    fun getLeagueResults(leagueName: String, season: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getLeagueResults(leagueName, season)

    fun getTeamLeagueResults(leagueName: String, season: String, teamName: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getTeamLeagueResults(leagueName, season, teamName)

    suspend fun getTotalGoalsInLeague(leagueName: String, season: String): Int? =
        fixturesResultsDao.getTotalGoalsInLeague(leagueName, season)

    suspend fun getAverageGoalsInLeague(leagueName: String, season: String): Double? =
        fixturesResultsDao.getAverageGoalsInLeague(leagueName, season)

    // ============ CUP-BASED ============

    fun getCupResults(cupName: String, season: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getCupResults(cupName, season)

    fun getCupRoundResults(cupName: String, season: String, round: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getCupRoundResults(cupName, season, round)

    fun getPenaltyShootouts(cupName: String, season: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getPenaltyShootouts(cupName, season)

    // ============ REFEREE-BASED ============

    fun getResultsByReferee(refereeId: Int): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getResultsByReferee(refereeId)

    suspend fun getRefereeCardStats(refereeId: Int): RefereeCardStats? =
        fixturesResultsDao.getRefereeCardStats(refereeId)

    // ============ UPSET/TRENDING ============

    fun getBiggestUpsets(limit: Int = 10): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getBiggestUpsets(limit)

    fun getHighestScoringGames(limit: Int = 10): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getHighestScoringGames(limit)

    fun getThrashingResults(limit: Int = 10): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getThrashingResults(limit)

    fun getComebackVictories(limit: Int = 10): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getComebackVictories(limit)

    fun getGoallessDraws(limit: Int = 10): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getGoallessDraws(limit)

    // ============ STATISTICS ============

    suspend fun getSeasonStatistics(season: String): SeasonStatistics? =
        fixturesResultsDao.getSeasonStatistics(season)

    fun getMonthlyStats(year: String): Flow<List<MonthlyStatistics>> =
        fixturesResultsDao.getMonthlyStats(year)

    fun getSeasons(): Flow<List<String>> = fixturesResultsDao.getSeasons()

    fun getResultsBySeason(season: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getResultsBySeason(season)

    fun getResultsByMatchType(matchType: String): Flow<List<FixturesResultsEntity>> =
        fixturesResultsDao.getResultsByMatchType(matchType)

    // ============ JOIN QUERIES ============

    suspend fun getCompleteResultDetails(fixtureId: Int): CompleteResultDetails? =
        fixturesResultsDao.getCompleteResultDetails(fixtureId)

    suspend fun getResultWithCompetition(fixtureId: Int): ResultWithCompetition? =
        fixturesResultsDao.getResultWithCompetition(fixtureId)

    // ============ MATCH PROCESSING ============

    /**
     * Complete match processing pipeline
     * 1. Create result from fixture and events
     * 2. Calculate ELO changes
     * 3. Update team stats
     * 4. Update league standings
     * 5. Update cup standings
     * 6. Update referee stats
     * 7. Update player stats
     */
    suspend fun processMatchResult(
        fixture: FixturesEntity,
        events: List<MatchEventsEntity>
    ): FixturesResultsEntity {

        // Calculate match statistics from events
        val stats = calculateMatchStatistics(events, fixture)

        // Get team ELO ratings
        val homeTeam = teamsRepository.getTeamByName(fixture.homeTeam)
        val awayTeam = teamsRepository.getTeamByName(fixture.awayTeam)

        // Create result entity
        var result = FixturesResultsEntity(
            fixtureId = fixture.id,
            matchDate = fixture.matchDate,
            homeTeam = fixture.homeTeam,
            awayTeam = fixture.awayTeam,
            homeScore = fixture.homeScore,
            awayScore = fixture.awayScore,
            homeHalftimeScore = stats.homeHalftime,
            awayHalftimeScore = stats.awayHalftime,
            homePenaltyScore = stats.homePenalties,
            awayPenaltyScore = stats.awayPenalties,
            possessionHome = stats.possessionHome,
            possessionAway = stats.possessionAway,
            shotsHome = stats.shotsHome,
            shotsAway = stats.shotsAway,
            shotsOnTargetHome = stats.shotsOnTargetHome,
            shotsOnTargetAway = stats.shotsOnTargetAway,
            cornersHome = stats.cornersHome,
            cornersAway = stats.cornersAway,
            foulsHome = stats.foulsHome,
            foulsAway = stats.foulsAway,
            yellowCardsHome = stats.yellowCardsHome,
            yellowCardsAway = stats.yellowCardsAway,
            redCardsHome = stats.redCardsHome,
            redCardsAway = stats.redCardsAway,
            offsidesHome = stats.offsidesHome,
            offsidesAway = stats.offsidesAway,
            refereeId = fixture.refereeId,
            attendance = stats.attendance ?: 0,
            weatherConditions = fixture.weatherConditions,
            stadium = fixture.stadium,
            matchType = fixture.matchType,
            leagueName = fixture.league,
            cupName = fixture.cupName,
            cupRound = if (fixture.isCupMatch) fixture.round else null,
            season = fixture.season,
            homeTeamElo = homeTeam?.eloRating ?: 1500,
            awayTeamElo = awayTeam?.eloRating ?: 1500,
            manOfMatch = stats.manOfMatch,
            manOfMatchTeam = stats.manOfMatchTeam,
            manOfMatchRating = stats.manOfMatchRating
        )

        // Calculate ELO changes and upset factor
        result = result.withCalculatedElo()

        // Save result
        insertResult(result)

        // Update related entities
        updateRelatedEntities(result, fixture, stats)

        return result
    }

    /**
     * Calculate match statistics from events
     */
    private suspend fun calculateMatchStatistics(
        events: List<MatchEventsEntity>,
        fixture: FixturesEntity
    ): MatchStatistics {
        val stats = MatchStatistics()

        var homeHalftime = 0
        var awayHalftime = 0

        for (event in events) {
            when (event.eventType.uppercase()) {
                "GOAL" -> {
                    if (event.teamName == fixture.homeTeam) {
                        stats.shotsOnTargetHome++
                        if (event.minute <= 45) homeHalftime++
                    } else {
                        stats.shotsOnTargetAway++
                        if (event.minute <= 45) awayHalftime++
                    }
                }
                "SHOT" -> {
                    if (event.teamName == fixture.homeTeam) stats.shotsHome++
                    else stats.shotsAway++
                }
                "SHOT_ON_TARGET" -> {
                    if (event.teamName == fixture.homeTeam) stats.shotsOnTargetHome++
                    else stats.shotsOnTargetAway++
                }
                "CORNER" -> {
                    if (event.teamName == fixture.homeTeam) stats.cornersHome++
                    else stats.cornersAway++
                }
                "FOUL" -> {
                    if (event.teamName == fixture.homeTeam) stats.foulsHome++
                    else stats.foulsAway++
                }
                "YELLOW_CARD" -> {
                    if (event.teamName == fixture.homeTeam) stats.yellowCardsHome++
                    else stats.yellowCardsAway++
                }
                "RED_CARD" -> {
                    if (event.teamName == fixture.homeTeam) stats.redCardsHome++
                    else stats.redCardsAway++
                }
                "OFFSIDE" -> {
                    if (event.teamName == fixture.homeTeam) stats.offsidesHome++
                    else stats.offsidesAway++
                }
                "PENALTY_SCORED" -> {
                    if (event.teamName == fixture.homeTeam) {
                        stats.homePenalties = (stats.homePenalties ?: 0) + 1
                    } else {
                        stats.awayPenalties = (stats.awayPenalties ?: 0) + 1
                    }
                }
                "MAN_OF_MATCH" -> {
                    stats.manOfMatch = event.playerName
                    stats.manOfMatchTeam = event.teamName
                    // You might want to get player rating here
                }
            }
        }

        stats.homeHalftime = homeHalftime
        stats.awayHalftime = awayHalftime

        // Set possession (simplified - in real game would be more complex)
        val totalShots = stats.shotsHome + stats.shotsAway
        if (totalShots > 0) {
            stats.possessionHome = (stats.shotsHome * 100 / totalShots).coerceIn(30, 70)
            stats.possessionAway = 100 - stats.possessionHome
        }

        return stats
    }

    /**
     * Update all related entities after match
     */
    private suspend fun updateRelatedEntities(
        result: FixturesResultsEntity,
        fixture: FixturesEntity,
        stats: MatchStatistics
    ) {
        // 1. Update team stats
        teamsRepository.updateTeamAfterMatch(result)

        // 2. Update league standings
        if (fixture.league != null) {
            leagueStandingsRepository.updateStandingsAfterMatch(result)
        }

        // 3. Update cup standings
        if (fixture.cupName != null) {
            if (fixture.matchType == "Cup" && fixture.round?.contains("Group") == true) {
                cupGroupStandingsRepository.updateGroupStandingsAfterMatch(result)
            }
        }

        // 4. Update referee stats
        fixture.refereeId?.let { refereeId ->
            refereesRepository.processMatchPerformance(
                refereeId = refereeId,
                yellowCards = result.yellowCardsHome + result.yellowCardsAway,
                redCards = result.redCardsHome + result.redCardsAway,
                matchImportance = when {
                    result.isUpset -> 1.5
                    result.isPenaltyShootout -> 1.3
                    result.matchType == "Final" -> 2.0
                    else -> 1.0
                }
            )
        }

        // 5. Update player stats via match events (handled by MatchEventsRepository)
        // This is already done when events are recorded

        // 6. Update team ELO ratings
        teamsRepository.updateTeamElo(
            teamName = result.homeTeam,
            newElo = result.homeTeamElo + result.eloChangeHome
        )
        teamsRepository.updateTeamElo(
            teamName = result.awayTeam,
            newElo = result.awayTeamElo + result.eloChangeAway
        )
    }

    // ============ DASHBOARD ============

    /**
     * Get comprehensive results dashboard
     */
    suspend fun getResultsDashboard(): ResultsDashboard {
        val recentResults = getRecentFixtures(10).firstOrNull() ?: emptyList()
        val biggestUpsets = getBiggestUpsets(5).firstOrNull() ?: emptyList()
        val highestScoring = getHighestScoringGames(5).firstOrNull() ?: emptyList()

        return ResultsDashboard(
            totalMatches = getResultsCount(),
            recentResults = recentResults,
            biggestUpsets = biggestUpsets,
            highestScoringGames = highestScoring,
            homeWinPercentage = calculateHomeWinPercentage(),
            averageGoalsPerGame = calculateAverageGoalsPerGame()
        )
    }

    private suspend fun calculateHomeWinPercentage(): Double {
        val allResults = fixturesResultsDao.getAll().firstOrNull() ?: emptyList()
        if (allResults.isEmpty()) return 0.0

        val homeWins = allResults.count { it.homeTeamWin }
        return (homeWins.toDouble() / allResults.size) * 100
    }

    private suspend fun calculateAverageGoalsPerGame(): Double {
        val allResults = fixturesResultsDao.getAll().firstOrNull() ?: emptyList()
        if (allResults.isEmpty()) return 0.0

        val totalGoals = allResults.sumOf { it.totalGoals }
        return totalGoals.toDouble() / allResults.size
    }

    // Helper method to get recent fixtures
    private fun getRecentFixtures(limit: Int): Flow<List<FixturesResultsEntity>> {
        return fixturesResultsDao.getAll().map { list ->
            list.sortedByDescending { it.matchDate }.take(limit)
        }
    }
}

// ============ DATA CLASSES ============

data class MatchStatistics(
    var homeHalftime: Int = 0,
    var awayHalftime: Int = 0,
    var homePenalties: Int? = null,
    var awayPenalties: Int? = null,
    var possessionHome: Int = 50,
    var possessionAway: Int = 50,
    var shotsHome: Int = 0,
    var shotsAway: Int = 0,
    var shotsOnTargetHome: Int = 0,
    var shotsOnTargetAway: Int = 0,
    var cornersHome: Int = 0,
    var cornersAway: Int = 0,
    var foulsHome: Int = 0,
    var foulsAway: Int = 0,
    var yellowCardsHome: Int = 0,
    var yellowCardsAway: Int = 0,
    var redCardsHome: Int = 0,
    var redCardsAway: Int = 0,
    var offsidesHome: Int = 0,
    var offsidesAway: Int = 0,
    var attendance: Int? = null,
    var manOfMatch: String? = null,
    var manOfMatchTeam: String? = null,
    var manOfMatchRating: Double? = null
)

//data class TeamForm(
    //val formString: String,
    //val played: Int,
    //val wins: Int,
    //val draws: Int,
    //val losses: Int,
    //val goalsFor: Int,
    //val goalsAgainst: Int,
    //val goalDifference: Int,
    //val points: Int
//)

//data class HeadToHeadRecord(
    //val totalMatches: Int,
    //val team1Wins: Int,
    //val team2Wins: Int,
    //val draws: Int,
    //val team1Goals: Int,
    //val team2Goals: Int,
    //val team1RecentForm: String
//)

data class ResultsDashboard(
    val totalMatches: Int,
    val recentResults: List<FixturesResultsEntity>,
    val biggestUpsets: List<FixturesResultsEntity>,
    val highestScoringGames: List<FixturesResultsEntity>,
    val homeWinPercentage: Double,
    val averageGoalsPerGame: Double
)