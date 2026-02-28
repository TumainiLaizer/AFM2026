package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.BracketWithCupDetails
import com.fameafrica.afm2026.data.database.dao.BracketWithDetails
import com.fameafrica.afm2026.data.database.dao.CupBracketsDao
import com.fameafrica.afm2026.data.database.dao.CupPerformerStats
import com.fameafrica.afm2026.data.database.dao.CupStatistics
import com.fameafrica.afm2026.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.emptyMap

@Singleton
class CupBracketsRepository @Inject constructor(
    private val cupBracketsDao: CupBracketsDao,
    private val cupsDao: CupsRepository,
    private val teamsDao: TeamsRepository,
    private val fixturesDao: FixturesRepository,
    private val fixturesRepository: FixturesRepository
) {

    // ============ BASIC CRUD ============

    fun getAllBrackets(): Flow<List<CupBracketsEntity>> = cupBracketsDao.getAll()

    suspend fun getBracketById(id: Int): CupBracketsEntity? = cupBracketsDao.getById(id)

    suspend fun getBracketByFixtureId(fixtureId: Int): CupBracketsEntity? =
        cupBracketsDao.getByFixtureId(fixtureId)

    suspend fun insertBracket(bracket: CupBracketsEntity) = cupBracketsDao.insert(bracket)

    suspend fun insertAllBrackets(brackets: List<CupBracketsEntity>) = cupBracketsDao.insertAll(brackets)

    suspend fun updateBracket(bracket: CupBracketsEntity) = cupBracketsDao.update(bracket)

    suspend fun deleteBracket(bracket: CupBracketsEntity) = cupBracketsDao.delete(bracket)

    suspend fun deleteByCupAndSeason(cupName: String, season: Int) =
        cupBracketsDao.deleteByCupAndSeason(cupName, season)

    // ============ CUP-BASED ============

    fun getBracketsByCupAndSeason(cupName: String, season: Int): Flow<List<CupBracketsEntity>> =
        cupBracketsDao.getBracketsByCupAndSeason(cupName, season)

    fun getBracketsByRound(cupName: String, season: Int, round: String): Flow<List<CupBracketsEntity>> =
        cupBracketsDao.getBracketsByRound(cupName, season, round)

    fun getTeamBrackets(cupName: String, season: Int, teamName: String): Flow<List<CupBracketsEntity>> =
        cupBracketsDao.getTeamBrackets(cupName, season, teamName)

    suspend fun getFinalBracket(cupName: String, season: Int): CupBracketsEntity? =
        cupBracketsDao.getFinalBracket(cupName, season)

    // ============ BRACKET GENERATION ============

    /**
     * Generate knockout bracket for a cup
     * @param cupName Name of the cup
     * @param season Season year
     * @param teamNames List of team names in the competition
     * @param startDate Start date for first round matches
     * @param isTwoLegged Whether ties are two-legged
     */
    suspend fun generateKnockoutBracket(
        cupName: String,
        season: Int,
        teamNames: List<String>,
        startDate: String,
        isTwoLegged: Boolean = false
    ): List<CupBracketsEntity> {

        // Delete existing brackets for this cup/season
        cupBracketsDao.deleteByCupAndSeason(cupName, season)

        val brackets = mutableListOf<CupBracketsEntity>()
        val numTeams = teamNames.size

        // Calculate number of rounds
        val rounds = calculateRounds(numTeams)
        var currentTeams = teamNames.toMutableList()
        var currentRound = 1
        var bracketPosition = 1
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        calendar.time = dateFormat.parse(startDate) ?: Calendar.getInstance().time

        // Handle byes if necessary
        val totalSlots = Math.pow(2.0, rounds.toDouble()).toInt()
        if (numTeams < totalSlots) {
            val byes = totalSlots - numTeams
            currentTeams = addByes(currentTeams, byes)
        }

        // Generate first round brackets
        for (i in currentTeams.indices step 2) {
            if (i + 1 < currentTeams.size) {
                val bracket = CupBracketsEntity(
                    cupName = cupName,
                    season = season,
                    round = getRoundName(1, rounds),
                    roundNumber = 1,
                    bracketPosition = bracketPosition++,
                    teamName = currentTeams[i],
                    opponentName = currentTeams[i + 1],
                    fixtureId = 0, // Will be updated when fixture is created
                    isTwoLegged = isTwoLegged,
                    matchDate = dateFormat.format(calendar.time),
                    id = TODO(),
                    result = TODO(),
                    homeScore = TODO(),
                    awayScore = TODO(),
                    penaltyScore = TODO(),
                    aggregateScore = TODO(),
                    firstLegFixtureId = TODO(),
                    secondLegFixtureId = TODO(),
                    winner = TODO(),
                    loser = TODO(),
                    nextBracketId = TODO(),
                    parentBracketId = TODO(),
                    isWalkover = TODO(),
                    walkoverReason = TODO(),
                    stadium = TODO(),
                    attendance = TODO(),
                    legacyTag = TODO(),
                    notes = TODO()
                )
                brackets.add(bracket)
                calendar.add(Calendar.DAY_OF_YEAR, 3) // Space out matches
            }
        }

        // Generate subsequent rounds (placeholders)
        var matchesInRound = brackets.size / 2
        var roundNum = 2

        while (matchesInRound >= 1) {
            val roundBrackets = mutableListOf<CupBracketsEntity>()

            for (i in 0 until matchesInRound) {
                val bracket = CupBracketsEntity(
                    cupName = cupName,
                    season = season,
                    round = getRoundName(roundNum, rounds),
                    roundNumber = roundNum,
                    bracketPosition = bracketPosition++,
                    teamName = null, // To be filled as winners progress
                    opponentName = null,
                    fixtureId = 0,
                    isTwoLegged = isTwoLegged && roundNum < rounds, // Final might be one leg
                    matchDate = dateFormat.format(calendar.time),
                    id = TODO(),
                    result = TODO(),
                    homeScore = TODO(),
                    awayScore = TODO(),
                    penaltyScore = TODO(),
                    aggregateScore = TODO(),
                    firstLegFixtureId = TODO(),
                    secondLegFixtureId = TODO(),
                    winner = TODO(),
                    loser = TODO(),
                    nextBracketId = TODO(),
                    parentBracketId = TODO(),
                    isWalkover = TODO(),
                    walkoverReason = TODO(),
                    stadium = TODO(),
                    attendance = TODO(),
                    legacyTag = TODO(),
                    notes = TODO()
                )
                roundBrackets.add(bracket)
                calendar.add(Calendar.DAY_OF_YEAR, 3)
            }

            brackets.addAll(roundBrackets)
            matchesInRound /= 2
            roundNum++
        }

        // Link brackets (set next_bracket_id)
        linkBrackets(brackets)

        cupBracketsDao.insertAll(brackets)
        return brackets
    }

    /**
     * Generate group stage + knockout bracket (e.g., CAF Champions League style)
     */
    suspend fun generateGroupPlusKnockoutBracket(
        cupName: String,
        season: Int,
        groups: Map<String, List<String>>, // Group name -> list of teams
        startDate: String
    ): List<CupBracketsEntity> {

        cupBracketsDao.deleteByCupAndSeason(cupName, season)

        val brackets = mutableListOf<CupBracketsEntity>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        calendar.time = dateFormat.parse(startDate) ?: Calendar.getInstance().time

        var bracketPosition = 1

        // Group stage matches will be handled by cup_group_standings
        // Brackets start from knockout stage

        // Calculate number of teams advancing (top 2 from each group)
        val advancingTeams = groups.size * 2
        val knockoutRounds = calculateRounds(advancingTeams)

        // Generate Round of 16 (if applicable)
        if (advancingTeams >= 16) {
            for (i in 0 until advancingTeams step 2) {
                val bracket = CupBracketsEntity(
                    cupName = cupName,
                    season = season,
                    round = "ROUND_16",
                    roundNumber = 1,
                    bracketPosition = bracketPosition++,
                    teamName = null, // Group winner placeholder
                    opponentName = null, // Group runner-up placeholder
                    fixtureId = 0,
                    isTwoLegged = true,
                    matchDate = dateFormat.format(calendar.time),
                    id = TODO(),
                    result = TODO(),
                    homeScore = TODO(),
                    awayScore = TODO(),
                    penaltyScore = TODO(),
                    aggregateScore = TODO(),
                    firstLegFixtureId = TODO(),
                    secondLegFixtureId = TODO(),
                    winner = TODO(),
                    loser = TODO(),
                    nextBracketId = TODO(),
                    parentBracketId = TODO(),
                    isWalkover = TODO(),
                    walkoverReason = TODO(),
                    stadium = TODO(),
                    attendance = TODO(),
                    legacyTag = TODO(),
                    notes = TODO()
                )
                brackets.add(bracket)
                calendar.add(Calendar.DAY_OF_YEAR, 7)
            }
        }

        // Quarter-finals
        val quarterFinalCount = advancingTeams / 4
        for (i in 0 until quarterFinalCount) {
            val bracket = CupBracketsEntity(
                cupName = cupName,
                season = season,
                round = "QUARTER_FINAL",
                roundNumber = 2,
                bracketPosition = bracketPosition++,
                teamName = null,
                opponentName = null,
                fixtureId = 0,
                isTwoLegged = true,
                matchDate = dateFormat.format(calendar.time),
                id = TODO(),
                result = TODO(),
                homeScore = TODO(),
                awayScore = TODO(),
                penaltyScore = TODO(),
                aggregateScore = TODO(),
                firstLegFixtureId = TODO(),
                secondLegFixtureId = TODO(),
                winner = TODO(),
                loser = TODO(),
                nextBracketId = TODO(),
                parentBracketId = TODO(),
                isWalkover = TODO(),
                walkoverReason = TODO(),
                stadium = TODO(),
                attendance = TODO(),
                legacyTag = TODO(),
                notes = TODO()
            )
            brackets.add(bracket)
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }

        // Semi-finals
        for (i in 0 until 2) {
            val bracket = CupBracketsEntity(
                cupName = cupName,
                season = season,
                round = "SEMI_FINAL",
                roundNumber = 3,
                bracketPosition = bracketPosition++,
                teamName = null,
                opponentName = null,
                fixtureId = 0,
                isTwoLegged = true,
                matchDate = dateFormat.format(calendar.time),
                id = TODO(),
                result = TODO(),
                homeScore = TODO(),
                awayScore = TODO(),
                penaltyScore = TODO(),
                aggregateScore = TODO(),
                firstLegFixtureId = TODO(),
                secondLegFixtureId = TODO(),
                winner = TODO(),
                loser = TODO(),
                nextBracketId = TODO(),
                parentBracketId = TODO(),
                isWalkover = TODO(),
                walkoverReason = TODO(),
                stadium = TODO(),
                attendance = TODO(),
                legacyTag = TODO(),
                notes = TODO()
            )
            brackets.add(bracket)
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }

        // Final
        val finalBracket = CupBracketsEntity(
            cupName = cupName,
            season = season,
            round = "FINAL",
            roundNumber = 4,
            bracketPosition = bracketPosition++,
            teamName = null,
            opponentName = null,
            fixtureId = 0,
            isTwoLegged = false,
            matchDate = dateFormat.format(calendar.time),
            id = TODO(),
            result = TODO(),
            homeScore = TODO(),
            awayScore = TODO(),
            penaltyScore = TODO(),
            aggregateScore = TODO(),
            firstLegFixtureId = TODO(),
            secondLegFixtureId = TODO(),
            winner = TODO(),
            loser = TODO(),
            nextBracketId = TODO(),
            parentBracketId = TODO(),
            isWalkover = TODO(),
            walkoverReason = TODO(),
            stadium = TODO(),
            attendance = TODO(),
            legacyTag = TODO(),
            notes = TODO()
        )
        brackets.add(finalBracket)

        // Link brackets
        linkBrackets(brackets)

        cupBracketsDao.insertAll(brackets)
        return brackets
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
        return when (roundNum) {
            1 -> "FIRST"
            2 -> "SECOND"
            3 -> "THIRD"
            4 -> "FOURTH"
            totalRounds -> "FINAL"
            totalRounds - 1 -> "SEMI_FINAL"
            totalRounds - 2 -> "QUARTER_FINAL"
            else -> "ROUND_${Math.pow(2.0, (totalRounds - roundNum + 1).toDouble()).toInt()}"
        }
    }

    private fun addByes(teams: MutableList<String>, byeCount: Int): MutableList<String> {
        val result = mutableListOf<String>()
        for (i in teams.indices) {
            result.add(teams[i])
            if (i < byeCount) {
                result.add("BYE")
            }
        }
        return result
    }

    private fun linkBrackets(brackets: MutableList<CupBracketsEntity>) {
        // Group brackets by round
        val bracketsByRound = brackets.groupBy { it.roundNumber }

        // Iterate through the sorted round numbers, excluding the last one
        for (round in bracketsByRound.keys.sorted().dropLast(1)) {
            val currentRoundBrackets = bracketsByRound[round] ?: continue
            val nextRoundBrackets = bracketsByRound[round + 1] ?: continue

            for ((index, bracket) in currentRoundBrackets.withIndex()) {
                val nextBracketIndex = index / 2
                if (nextBracketIndex < nextRoundBrackets.size) {
                    val updatedBracket = bracket.copy(
                        nextBracketId = nextRoundBrackets[nextBracketIndex].id
                    )
                    // Update in list (will be inserted later)
                    brackets[brackets.indexOf(bracket)] = updatedBracket
                }
            }
        }
    }

    // ============ MATCH RESULT UPDATES ============

    /**
     * Update bracket after a match is played
     */
    suspend fun updateBracketAfterMatch(
        fixtureId: Int,
        homeScore: Int,
        awayScore: Int,
        winner: String,
        loser: String,
        penaltyScore: String? = null,
        aggregateScore: String? = null
    ): CupBracketsEntity? {

        val bracket = cupBracketsDao.getByFixtureId(fixtureId) ?: return null

        // Determine result string
        val result = if (penaltyScore != null) {
            "${homeScore}-${awayScore} (${penaltyScore} pens)"
        } else if (aggregateScore != null) {
            "$aggregateScore agg"
        } else {
            "${homeScore}-${awayScore}"
        }

        // Update current bracket
        val updatedBracket = bracket.copy(
            result = result,
            homeScore = homeScore,
            awayScore = awayScore,
            penaltyScore = penaltyScore,
            aggregateScore = aggregateScore,
            winner = winner,
            loser = loser
        )

        cupBracketsDao.update(updatedBracket)

        // Propagate winner to next round bracket
        bracket.nextBracketId?.let { nextBracketId ->
            val nextBracket = cupBracketsDao.getById(nextBracketId)
            if (nextBracket != null) {
                val updatedNextBracket = if (nextBracket.teamName == null) {
                    nextBracket.copy(teamName = winner)
                } else if (nextBracket.opponentName == null) {
                    nextBracket.copy(opponentName = winner)
                } else {
                    nextBracket
                }
                cupBracketsDao.update(updatedNextBracket)
            }
        }

        return updatedBracket
    }

    /**
     * Process a walkover
     */
    suspend fun processWalkover(
        bracketId: Int,
        advancingTeam: String,
        reason: String
    ): CupBracketsEntity? {

        val bracket = cupBracketsDao.getById(bracketId) ?: return null

        val updatedBracket = bracket.copy(
            result = "WO",
            winner = advancingTeam,
            loser = if (advancingTeam == bracket.teamName) bracket.opponentName else bracket.teamName,
            isWalkover = true,
            walkoverReason = reason
        )

        cupBracketsDao.update(updatedBracket)

        // Propagate to next round
        bracket.nextBracketId?.let { nextBracketId ->
            val nextBracket = cupBracketsDao.getById(nextBracketId)
            if (nextBracket != null) {
                val updatedNextBracket = if (nextBracket.teamName == null) {
                    nextBracket.copy(teamName = advancingTeam)
                } else if (nextBracket.opponentName == null) {
                    nextBracket.copy(opponentName = advancingTeam)
                } else {
                    nextBracket
                }
                cupBracketsDao.update(updatedNextBracket)
            }
        }

        return updatedBracket
    }

    // ============ FIXTURE LINKING ============

    /**
     * Link a fixture to a bracket
     */
    suspend fun linkFixtureToBracket(bracketId: Int, fixtureId: Int): CupBracketsEntity? {
        val bracket = cupBracketsDao.getById(bracketId) ?: return null

        val updatedBracket = bracket.copy(fixtureId = fixtureId)
        cupBracketsDao.update(updatedBracket)

        return updatedBracket
    }

    /**
     * Create fixtures for all brackets in a round
     */
    suspend fun createFixturesForRound(
        cupName: String,
        season: Int,
        round: String
    ): List<Int> {

        val brackets = cupBracketsDao.getBracketsByRound(cupName, season, round)
            .firstOrNull() ?: return emptyList()

        val fixtureIds = mutableListOf<Int>()

        for (bracket in brackets) {
            if (bracket.teamName != null && bracket.opponentName != null && bracket.opponentName != "BYE") {
                // Create fixture
                val fixture = fixturesRepository.createCupFixture(
                    homeTeam = bracket.teamName,
                    awayTeam = bracket.opponentName,
                    matchDate = bracket.matchDate ?: "",
                    season = season.toString(),
                    cupName = cupName,
                    round = round
                )

                if (fixture != null) {
                    linkFixtureToBracket(bracket.id, fixture.id)
                    fixtureIds.add(fixture.id)
                }
            } else if (bracket.opponentName == "BYE") {
                // Auto-advance team
                processWalkover(bracket.id, bracket.teamName ?: "", "Bye")
            }
        }

        return fixtureIds
    }

    // ============ BRACKET VISUALIZATION ============

    /**
     * Get bracket tree for visualization
     */
    suspend fun getBracketTree(cupName: String, season: Int): BracketTree {
        val brackets = cupBracketsDao.getBracketsByCupAndSeason(cupName, season).firstOrNull()

        // If there are no brackets, return a completely empty tree
        if (brackets.isNullOrEmpty()) {
            return BracketTree(emptyList(),
                emptyMap<Int, List<CupBracketsEntity>>() as SortedMap<Int, List<CupBracketsEntity>>
            )
        }

        // Now that we know brackets is not null, proceed with creating the tree
        val rounds: SortedMap<Int, List<CupBracketsEntity>> = brackets.groupBy { it.roundNumber }.toSortedMap()

        val roundNames = rounds.map { (roundNum, bracketsInRound) ->
            bracketsInRound.firstOrNull()?.round ?: "Round $roundNum"
        }

        return BracketTree(roundNames, rounds)
    }

    // ============ STATISTICS ============

    fun getCupStatistics(season: Int): Flow<List<CupStatistics>> =
        cupBracketsDao.getCupStatistics(season)

    fun getTopCupPerformers(limit: Int): Flow<List<CupPerformerStats>> =
        cupBracketsDao.getTopCupPerformers(limit)

    // ============ JOIN QUERIES ============

    suspend fun getBracketWithDetails(bracketId: Int): BracketWithDetails? =
        cupBracketsDao.getBracketWithDetails(bracketId)

    fun getFullBracketWithDetails(cupName: String, season: Int): Flow<List<BracketWithCupDetails>> =
        cupBracketsDao.getFullBracketWithDetails(cupName, season)

    // ============ DASHBOARD ============

    suspend fun getCupBracketsDashboard(cupName: String, season: Int): CupBracketsDashboard {
        val brackets = cupBracketsDao.getBracketsByCupAndSeason(cupName, season)
            .firstOrNull() ?: emptyList()

        val completed = brackets.count { it.isCompleted }
        val pending = brackets.count { it.isScheduled }
        val byes = brackets.count { it.isBye }
        val walkovers = brackets.count { it.isWalkover }

        val currentRound = brackets
            .filter { !it.isCompleted && !it.isBye }
            .minByOrNull { it.roundNumber }?.round

        val winner = brackets.firstOrNull { it.round == "FINAL" && it.winner != null }?.winner

        return CupBracketsDashboard(
            cupName = cupName,
            season = season,
            totalBrackets = brackets.size,
            completedMatches = completed,
            pendingMatches = pending,
            byes = byes,
            walkovers = walkovers,
            currentRound = currentRound,
            winner = winner,
            brackets = brackets
        )
    }
}

// ============ DATA CLASSES ============

data class BracketTree(
    val roundNames: List<String>,
    val bracketsByRound: SortedMap<Int, List<CupBracketsEntity>>
)

data class CupBracketsDashboard(
    val cupName: String,
    val season: Int,
    val totalBrackets: Int,
    val completedMatches: Int,
    val pendingMatches: Int,
    val byes: Int,
    val walkovers: Int,
    val currentRound: String?,
    val winner: String?,
    val brackets: List<CupBracketsEntity>
)