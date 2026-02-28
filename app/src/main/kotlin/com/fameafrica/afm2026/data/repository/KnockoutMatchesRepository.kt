package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.CupKnockoutStats
import com.fameafrica.afm2026.data.database.dao.CupTopPerformer
import com.fameafrica.afm2026.data.database.dao.KnockoutMatchWithDetails
import com.fameafrica.afm2026.data.database.dao.KnockoutMatchWithLogos
import com.fameafrica.afm2026.data.database.dao.KnockoutMatchesDao
import com.fameafrica.afm2026.data.database.dao.RoundStats
import com.fameafrica.afm2026.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KnockoutMatchesRepository @Inject constructor(
    private val knockoutMatchesDao: KnockoutMatchesDao,
    private val cupsRepository: CupsRepository,
    private val teamsRepository: TeamsRepository,
    private val fixturesRepository: FixturesRepository,
    private val cupBracketsRepository: CupBracketsRepository,
    private val refereesRepository: RefereesRepository
) {

    // ============ BASIC CRUD ============

    fun getAllMatches(): Flow<List<KnockoutMatchesEntity>> = knockoutMatchesDao.getAll()

    suspend fun getMatchById(id: Int): KnockoutMatchesEntity? = knockoutMatchesDao.getById(id)

    suspend fun getMatchByFixtureId(fixtureId: Int): KnockoutMatchesEntity? =
        knockoutMatchesDao.getByFixtureId(fixtureId)

    suspend fun insertMatch(match: KnockoutMatchesEntity) = knockoutMatchesDao.insert(match)

    suspend fun insertAllMatches(matches: List<KnockoutMatchesEntity>) = knockoutMatchesDao.insertAll(matches)

    suspend fun updateMatch(match: KnockoutMatchesEntity) = knockoutMatchesDao.update(match)

    suspend fun deleteMatch(match: KnockoutMatchesEntity) = knockoutMatchesDao.delete(match)

    suspend fun deleteByCupAndSeason(cupName: String, season: String) =
        knockoutMatchesDao.deleteByCupAndSeason(cupName, season)

    // ============ CUP-BASED ============

    fun getMatchesByCupAndSeason(cupName: String, season: String): Flow<List<KnockoutMatchesEntity>> =
        knockoutMatchesDao.getMatchesByCupAndSeason(cupName, season)

    fun getMatchesByRound(cupName: String, season: String, round: String): Flow<List<KnockoutMatchesEntity>> =
        knockoutMatchesDao.getMatchesByRound(cupName, season, round)

    suspend fun getFinalMatch(cupName: String, season: String): KnockoutMatchesEntity? =
        knockoutMatchesDao.getFinalMatch(cupName, season)

    fun getSemiFinals(cupName: String, season: String): Flow<List<KnockoutMatchesEntity>> =
        knockoutMatchesDao.getSemiFinals(cupName, season)

    fun getQuarterFinals(cupName: String, season: String): Flow<List<KnockoutMatchesEntity>> =
        knockoutMatchesDao.getQuarterFinals(cupName, season)

    // ============ KNOCKOUT BRACKET GENERATION ============

    /**
     * Generate knockout bracket for a cup
     */
    suspend fun generateKnockoutBracket(
        cupName: String,
        season: String,
        qualifiedTeams: List<String>,
        startDate: String,
        isTwoLegged: Boolean = false
    ): List<KnockoutMatchesEntity> {

        // Delete existing matches
        knockoutMatchesDao.deleteByCupAndSeason(cupName, season)

        val matches = mutableListOf<KnockoutMatchesEntity>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        calendar.time = dateFormat.parse(startDate) ?: Calendar.getInstance().time

        val numTeams = qualifiedTeams.size
        val rounds = calculateRounds(numTeams)

        // Create first round matches
        var matchNumber = 1
        for (i in qualifiedTeams.indices step 2) {
            if (i + 1 < qualifiedTeams.size) {
                val match = KnockoutMatchesEntity(
                    cupName = cupName,
                    season = season,
                    round = getRoundName(1, rounds),
                    roundNumber = 1,
                    matchNumber = matchNumber++,
                    homeTeam = qualifiedTeams[i],
                    awayTeam = qualifiedTeams[i + 1],
                    matchDate = dateFormat.format(calendar.time),
                    matchResult = "DRAW", // Default until played
                    stadium = getTeamStadium(qualifiedTeams[i]),
                    isTwoLegged = isTwoLegged,
                    isPlayed = false
                )
                matches.add(match)
                calendar.add(Calendar.DAY_OF_YEAR, 3)
            }
        }

        // Create subsequent round placeholders
        var teamsInRound = matches.size
        var roundNum = 2

        while (teamsInRound > 1) {
            val matchesInRound = teamsInRound / 2

            for (i in 0 until matchesInRound) {
                val match = KnockoutMatchesEntity(
                    cupName = cupName,
                    season = season,
                    round = getRoundName(roundNum, rounds),
                    roundNumber = roundNum,
                    matchNumber = i + 1,
                    homeTeam = "TBD",
                    awayTeam = "TBD",
                    matchDate = dateFormat.format(calendar.time),
                    matchResult = "DRAW",
                    stadium = null,
                    isTwoLegged = isTwoLegged && roundNum < rounds,
                    isPlayed = false
                )
                matches.add(match)
                calendar.add(Calendar.DAY_OF_YEAR, 3)
            }

            teamsInRound = matchesInRound
            roundNum++
        }

        // Link matches to next round
        linkMatchesToNextRound(matches)

        knockoutMatchesDao.insertAll(matches)
        return matches
    }

    private fun calculateRounds(numTeams: Int): Int {
        return when {
            numTeams <= 2 -> 1
            numTeams <= 4 -> 2
            numTeams <= 8 -> 3
            numTeams <= 16 -> 4
            numTeams <= 32 -> 5
            numTeams <= 64 -> 6
            else -> 7
        }
    }

    private fun getRoundName(roundNum: Int, totalRounds: Int): String {
        return when {
            roundNum == totalRounds -> "Final"
            roundNum == totalRounds - 1 -> "Semi-final"
            roundNum == totalRounds - 2 -> "Quarter-final"
            roundNum == totalRounds - 3 -> "Round of 16"
            roundNum == totalRounds - 4 -> "Round of 32"
            roundNum == totalRounds - 5 -> "Round of 64"
            else -> "Round $roundNum"
        }
    }

    private fun linkMatchesToNextRound(matches: MutableList<KnockoutMatchesEntity>) {
        val matchesByRound = matches.groupBy { it.roundNumber }

        // Iterate through the sorted round numbers, excluding the last one
        for (round in matchesByRound.keys.sorted().dropLast(1)) {
            val currentRoundMatches = matchesByRound[round] ?: continue
            val nextRoundMatches = matchesByRound[round + 1] ?: continue

            for ((index, match) in currentRoundMatches.withIndex()) {
                val nextMatchIndex = index / 2
                if (nextMatchIndex < nextRoundMatches.size) {
                    val updatedMatch = match.copy(
                        nextMatchId = nextRoundMatches[nextMatchIndex].id
                    )
                    matches[matches.indexOf(match)] = updatedMatch
                }
            }
        }
    }

    private suspend fun getTeamStadium(teamName: String): String? {
        return teamsRepository.getTeamByName(teamName)?.homeStadium
    }

    // ============ TWO-LEGGED TIE GENERATION ============

    /**
     * Generate a two-legged tie
     */
    suspend fun generateTwoLeggedTie(
        cupName: String,
        season: String,
        round: String,
        roundNumber: Int,
        matchNumber: Int,
        homeTeam: String,
        awayTeam: String,
        firstLegDate: String,
        secondLegDate: String
    ): Pair<KnockoutMatchesEntity, KnockoutMatchesEntity> {

        // First leg
        val firstLeg = KnockoutMatchesEntity(
            cupName = cupName,
            season = season,
            round = round,
            roundNumber = roundNumber,
            matchNumber = matchNumber,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            matchDate = firstLegDate,
            matchResult = "DRAW",
            stadium = getTeamStadium(homeTeam),
            isTwoLegged = true,
            leg = MatchLeg.FIRST.value,
            isPlayed = false
        )

        knockoutMatchesDao.insert(firstLeg)

        // Second leg
        val secondLeg = KnockoutMatchesEntity(
            cupName = cupName,
            season = season,
            round = round,
            roundNumber = roundNumber,
            matchNumber = matchNumber,
            homeTeam = awayTeam,
            awayTeam = homeTeam,
            matchDate = secondLegDate,
            matchResult = "DRAW",
            stadium = getTeamStadium(awayTeam),
            isTwoLegged = true,
            leg = MatchLeg.SECOND.value,
            firstLegId = firstLeg.id,
            isPlayed = false
        )

        knockoutMatchesDao.insert(secondLeg)

        // Update first leg with second leg ID
        val updatedFirstLeg = firstLeg.copy(secondLegId = secondLeg.id)
        knockoutMatchesDao.update(updatedFirstLeg)

        return Pair(updatedFirstLeg, secondLeg)
    }

    // ============ MATCH RESULT UPDATES ============

    /**
     * Update single leg match result
     */
    suspend fun updateMatchResult(
        matchId: Int,
        homeScore: Int,
        awayScore: Int,
        attendance: Int? = null,
        refereeId: Int? = null,
        weatherConditions: String = "Clear"
    ): KnockoutMatchesEntity? {

        val match = knockoutMatchesDao.getById(matchId) ?: return null

        val winner = when {
            homeScore > awayScore -> match.homeTeam
            awayScore > homeScore -> match.awayTeam
            else -> null
        }

        val loser = when {
            homeScore > awayScore -> match.awayTeam
            awayScore > homeScore -> match.homeTeam
            else -> null
        }

        val matchResult = when {
            homeScore > awayScore -> MatchResultType.HOME_WIN.value
            awayScore > homeScore -> MatchResultType.AWAY_WIN.value
            else -> MatchResultType.DRAW.value
        }

        val updatedMatch = match.copy(
            homeScore = homeScore,
            awayScore = awayScore,
            winner = winner,
            loser = loser,
            matchResult = matchResult,
            attendance = attendance ?: match.attendance,
            refereeId = refereeId ?: match.refereeId,
            weatherConditions = weatherConditions,
            isPlayed = true
        )

        knockoutMatchesDao.update(updatedMatch)

        // Update next round match with winner
        match.nextMatchId?.let { nextMatchId ->
            updateNextRoundMatch(nextMatchId, winner ?: return@let)
        }

        return updatedMatch
    }

    /**
     * Update two-legged tie result
     */
    suspend fun updateTwoLeggedTieResult(
        firstLegId: Int,
        secondLegId: Int,
        firstLegHomeScore: Int,
        firstLegAwayScore: Int,
        secondLegHomeScore: Int,
        secondLegAwayScore: Int,
        homePenaltyScore: Int? = null,
        awayPenaltyScore: Int? = null,
        attendance: Int? = null,
        refereeId: Int? = null
    ): Pair<KnockoutMatchesEntity, KnockoutMatchesEntity>? {

        val firstLeg = knockoutMatchesDao.getById(firstLegId) ?: return null
        val secondLeg = knockoutMatchesDao.getById(secondLegId) ?: return null

        // Update first leg
        val updatedFirstLeg = firstLeg.copy(
            homeScore = firstLegHomeScore,
            awayScore = firstLegAwayScore,
            isPlayed = true,
            attendance = attendance ?: firstLeg.attendance,
            refereeId = refereeId ?: firstLeg.refereeId
        )
        knockoutMatchesDao.update(updatedFirstLeg)

        // Calculate aggregate scores
        val aggregateHome = firstLegHomeScore + secondLegAwayScore // Home team in first leg is away in second
        val aggregateAway = firstLegAwayScore + secondLegHomeScore // Away team in first leg is home in second

        // Determine winner
        val winner = when {
            aggregateHome > aggregateAway -> firstLeg.homeTeam
            aggregateAway > aggregateHome -> firstLeg.awayTeam
            homePenaltyScore != null && awayPenaltyScore != null -> {
                if (homePenaltyScore > awayPenaltyScore) firstLeg.homeTeam else firstLeg.awayTeam
            }
            else -> null // Extra time would be handled separately
        }

        val matchResult = when {
            aggregateHome > aggregateAway -> MatchResultType.AGGREGATE_HOME.value
            aggregateAway > aggregateHome -> MatchResultType.AGGREGATE_AWAY.value
            homePenaltyScore != null && awayPenaltyScore != null -> {
                if (homePenaltyScore > awayPenaltyScore)
                    MatchResultType.HOME_WIN_PENS.value
                else
                    MatchResultType.AWAY_WIN_PENS.value
            }
            else -> MatchResultType.DRAW.value
        }

        // Update second leg
        val updatedSecondLeg = secondLeg.copy(
            homeScore = secondLegHomeScore,
            awayScore = secondLegAwayScore,
            homePenaltyScore = homePenaltyScore,
            awayPenaltyScore = awayPenaltyScore,
            aggregateHomeScore = aggregateHome,
            aggregateAwayScore = aggregateAway,
            winner = winner,
            loser = if (winner == firstLeg.homeTeam) firstLeg.awayTeam else firstLeg.homeTeam,
            matchResult = matchResult,
            isPlayed = true,
            attendance = attendance ?: secondLeg.attendance,
            refereeId = refereeId ?: secondLeg.refereeId
        )
        knockoutMatchesDao.update(updatedSecondLeg)

        // Update next round match with winner
        secondLeg.nextMatchId?.let { nextMatchId ->
            updateNextRoundMatch(nextMatchId, winner ?: return@let)
        }

        return Pair(updatedFirstLeg, updatedSecondLeg)
    }

    /**
     * Update penalty shootout result
     */
    suspend fun updatePenaltyShootoutResult(
        matchId: Int,
        homePenaltyScore: Int,
        awayPenaltyScore: Int
    ): KnockoutMatchesEntity? {

        val match = knockoutMatchesDao.getById(matchId) ?: return null

        val winner = if (homePenaltyScore > awayPenaltyScore) match.homeTeam else match.awayTeam
        val loser = if (winner == match.homeTeam) match.awayTeam else match.homeTeam

        val matchResult = if (winner == match.homeTeam)
            MatchResultType.HOME_WIN_PENS.value
        else
            MatchResultType.AWAY_WIN_PENS.value

        val updatedMatch = match.copy(
            homePenaltyScore = homePenaltyScore,
            awayPenaltyScore = awayPenaltyScore,
            winner = winner,
            loser = loser,
            matchResult = matchResult,
            isPlayed = true
        )

        knockoutMatchesDao.update(updatedMatch)

        // Update next round match with winner
        match.nextMatchId?.let { nextMatchId ->
            updateNextRoundMatch(nextMatchId, winner)
        }

        return updatedMatch
    }

    private suspend fun updateNextRoundMatch(nextMatchId: Int, winner: String) {
        val nextMatch = knockoutMatchesDao.getById(nextMatchId) ?: return

        val updatedNextMatch = if (nextMatch.homeTeam == "TBD") {
            nextMatch.copy(homeTeam = winner)
        } else if (nextMatch.awayTeam == "TBD") {
            nextMatch.copy(awayTeam = winner)
        } else {
            nextMatch
        }

        if (updatedNextMatch != nextMatch) {
            knockoutMatchesDao.update(updatedNextMatch)
        }
    }

    // ============ FIXTURE LINKING ============

    /**
     * Link a fixture to a knockout match
     */
    suspend fun linkFixtureToKnockoutMatch(
        knockoutMatchId: Int,
        fixtureId: Int
    ): KnockoutMatchesEntity? {

        val match = knockoutMatchesDao.getById(knockoutMatchId) ?: return null

        val updatedMatch = match.copy(fixtureId = fixtureId)
        knockoutMatchesDao.update(updatedMatch)

        return updatedMatch
    }

    /**
     * Create fixtures for all unplayed knockout matches in a round
     */
    suspend fun createFixturesForRound(
        cupName: String,
        season: String,
        round: String
    ): List<Int> {

        val matches = knockoutMatchesDao.getMatchesByRound(cupName, season, round)
            .firstOrNull() ?: return emptyList()

        val fixtureIds = mutableListOf<Int>()

        for (match in matches) {
            if (!match.isPlayed && match.homeTeam != "TBD" && match.awayTeam != "TBD") {
                val fixture = fixturesRepository.createKnockoutFixture(
                    homeTeam = match.homeTeam,
                    awayTeam = match.awayTeam,
                    matchDate = match.matchDate,
                    season = season,
                    cupName = cupName,
                    round = match.round,
                    stadium = match.stadium ?: getTeamStadium(match.homeTeam) ?: "FAME Africa Stadium"
                )

                if (fixture != null) {
                    linkFixtureToKnockoutMatch(match.id, fixture.id)
                    fixtureIds.add(fixture.id)
                }
            }
        }

        return fixtureIds
    }

    // ============ BRACKET LINKING ============

    /**
     * Link a cup bracket entry to a knockout match
     */
    suspend fun linkBracketToKnockoutMatch(
        knockoutMatchId: Int,
        bracketId: Int
    ): KnockoutMatchesEntity? {

        val match = knockoutMatchesDao.getById(knockoutMatchId) ?: return null

        val updatedMatch = match.copy(bracketId = bracketId)
        knockoutMatchesDao.update(updatedMatch)

        return updatedMatch
    }

    // ============ STATISTICS ============

    fun getCupKnockoutStatistics(season: String): Flow<List<CupKnockoutStats>> =
        knockoutMatchesDao.getCupKnockoutStatistics(season)

    fun getRoundStatistics(cupName: String, season: String): Flow<List<RoundStats>> =
        knockoutMatchesDao.getRoundStatistics(cupName, season)

    fun getTopPerformers(cupName: String, limit: Int): Flow<List<CupTopPerformer>> =
        knockoutMatchesDao.getTopPerformers(cupName, limit)

    // ============ JOIN QUERIES ============

    suspend fun getKnockoutMatchWithDetails(matchId: Int): KnockoutMatchWithDetails? =
        knockoutMatchesDao.getKnockoutMatchWithDetails(matchId)

    fun getKnockoutBracketWithLogos(cupName: String, season: String): Flow<List<KnockoutMatchWithLogos>> =
        knockoutMatchesDao.getKnockoutBracketWithLogos(cupName, season)

    // ============ DASHBOARD ============

    suspend fun getCupKnockoutDashboard(cupName: String, season: String): CupKnockoutDashboard {
        val allMatches = knockoutMatchesDao.getMatchesByCupAndSeason(cupName, season)
            .firstOrNull() ?: emptyList()

        val played = allMatches.filter { it.isPlayed }
        val upcoming = allMatches.filter { !it.isPlayed }

        val totalGoals = played.sumOf { it.homeScore + it.awayScore }
        val averageGoals = if (played.isNotEmpty()) totalGoals.toDouble() / played.size else 0.0

        val highestScoring = played.maxByOrNull { it.homeScore + it.awayScore }
        val biggestWin = played.maxByOrNull {
            Math.abs((it.homeScore - it.awayScore))
        }

        val rounds = allMatches.groupBy { it.round }
            .map { (round, matches) ->
                RoundSummary(
                    round = round,
                    matchCount = matches.size,
                    playedCount = matches.count { it.isPlayed }
                )
            }
            .sortedBy { it.roundNumber(allMatches) }

        return CupKnockoutDashboard(
            cupName = cupName,
            season = season,
            totalMatches = allMatches.size,
            playedMatches = played.size,
            upcomingMatches = upcoming.size,
            totalGoals = totalGoals,
            averageGoalsPerMatch = averageGoals,
            highestScoringMatch = highestScoring,
            biggestWin = biggestWin,
            rounds = rounds,
            upcomingMatchesList = upcoming.sortedBy { it.matchDate }.take(5),
            recentResults = played.sortedByDescending { it.matchDate }.take(5)
        )
    }
}

// ============ HELPER FUNCTIONS ============

fun RoundSummary.roundNumber(allMatches: List<KnockoutMatchesEntity>): Int {
    return allMatches.firstOrNull { it.round == round }?.roundNumber ?: 0
}

// ============ DATA CLASSES ============

data class RoundSummary(
    val round: String,
    val matchCount: Int,
    val playedCount: Int
)

data class CupKnockoutDashboard(
    val cupName: String,
    val season: String,
    val totalMatches: Int,
    val playedMatches: Int,
    val upcomingMatches: Int,
    val totalGoals: Int,
    val averageGoalsPerMatch: Double,
    val highestScoringMatch: KnockoutMatchesEntity?,
    val biggestWin: KnockoutMatchesEntity?,
    val rounds: List<RoundSummary>,
    val upcomingMatchesList: List<KnockoutMatchesEntity>,
    val recentResults: List<KnockoutMatchesEntity>
)