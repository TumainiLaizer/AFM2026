package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.FixturesDao
import com.fameafrica.afm2026.data.database.entities.FixturesEntity
import com.fameafrica.afm2026.data.database.entities.LeaguesEntity
import com.fameafrica.afm2026.data.database.entities.CupsEntity
import com.fameafrica.afm2026.data.database.entities.TeamsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FixturesRepository @Inject constructor(
    private val fixturesDao: FixturesDao,
    private val teamsRepository: TeamsRepository,
    private val leaguesRepository: LeaguesRepository,
    private val cupsRepository: CupsRepository,
    private val refereesRepository: RefereesRepository
) {

    // ============ BASIC CRUD ============

    fun getAllFixtures(): Flow<List<FixturesEntity>> = fixturesDao.getAll()

    suspend fun getFixtureById(id: Int): FixturesEntity? = fixturesDao.getById(id)

    suspend fun insertFixture(fixture: FixturesEntity) = fixturesDao.insert(fixture)

    suspend fun insertAllFixtures(fixtures: List<FixturesEntity>) = fixturesDao.insertAll(fixtures)

    suspend fun updateFixture(fixture: FixturesEntity) = fixturesDao.update(fixture)

    suspend fun deleteFixture(fixture: FixturesEntity) = fixturesDao.delete(fixture)

    suspend fun deleteAllFixtures() = fixturesDao.deleteAll()

    suspend fun getFixtureCount(): Int = fixturesDao.getCount()

    // ============ DATE-BASED QUERIES ============

    fun getFixturesByDate(date: String): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesByDate(date)

    fun getUpcomingFixtures(): Flow<List<FixturesEntity>> =
        fixturesDao.getUpcomingFixtures()

    fun getUpcomingFixturesLimit(limit: Int): Flow<List<FixturesEntity>> =
        fixturesDao.getUpcomingFixturesLimit(limit)

    fun getRecentFixtures(limit: Int): Flow<List<FixturesEntity>> =
        fixturesDao.getRecentFixtures(limit)

    fun getFixturesBetween(startDate: String, endDate: String): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesBetween(startDate, endDate)

    /**
     * Get today's fixtures
     */
    fun getTodaysFixtures(): Flow<List<FixturesEntity>> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return fixturesDao.getFixturesByDate(today)
    }

    /**
     * Get this week's fixtures
     */
    fun getThisWeeksFixtures(): Flow<List<FixturesEntity>> {
        val calendar = Calendar.getInstance()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val nextWeek = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        return fixturesDao.getFixturesBetween(today, nextWeek)
    }

    /**
     * Get next match for a team
     */
    suspend fun getNextMatchForTeam(teamName: String): FixturesEntity? {
        return fixturesDao.getUpcomingFixturesByTeam(teamName)
            .firstOrNull()
            ?.firstOrNull()
    }

    /**
     * Get last match for a team
     */
    suspend fun getLastMatchForTeam(teamName: String): FixturesEntity? {
        return fixturesDao.getRecentResultsByTeam(teamName, 1)
            .firstOrNull()
            ?.firstOrNull()
    }

    // ============ TEAM-BASED QUERIES ============

    fun getFixturesByTeam(teamName: String): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesByTeam(teamName)

    fun getUpcomingFixturesByTeam(teamName: String): Flow<List<FixturesEntity>> =
        fixturesDao.getUpcomingFixturesByTeam(teamName)

    fun getRecentResultsByTeam(teamName: String, limit: Int = 5): Flow<List<FixturesEntity>> =
        fixturesDao.getRecentResultsByTeam(teamName, limit)

    fun getHeadToHead(team1: String, team2: String): Flow<List<FixturesEntity>> =
        fixturesDao.getHeadToHead(team1, team2)

    /**
     * Get team's form string (e.g., "WDLWW") from fixtures
     */
    suspend fun getTeamFormString(teamName: String, limit: Int = 5): String {
        val recentFixtures = fixturesDao.getTeamRecentForm(teamName)
        return recentFixtures.take(limit).joinToString("") { fixture ->
            when {
                !fixture.isCompleted -> ""
                fixture.winner == teamName -> "W"
                fixture.winner == "Draw" -> "D"
                else -> "L"
            }
        }
    }

    /**
     * Get team's home record
     */
    suspend fun getTeamHomeRecord(teamName: String): TeamRecord {
        val homeFixtures = fixturesDao.getTeamHomeFixtures(teamName)
            .firstOrNull() ?: emptyList()

        val completed = homeFixtures.filter { it.isCompleted }
        val wins = completed.count { it.winner == teamName }
        val draws = completed.count { it.winner == "Draw" }
        val losses = completed.count { it.winner != teamName && it.winner != "Draw" }
        val goalsFor = completed.sumOf { it.homeScore }
        val goalsAgainst = completed.sumOf { it.awayScore }

        return TeamRecord(
            played = completed.size,
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
     * Get team's away record
     */
    suspend fun getTeamAwayRecord(teamName: String): TeamRecord {
        val awayFixtures = fixturesDao.getTeamAwayFixtures(teamName)
            .firstOrNull() ?: emptyList()

        val completed = awayFixtures.filter { it.isCompleted }
        val wins = completed.count { it.winner == teamName }
        val draws = completed.count { it.winner == "Draw" }
        val losses = completed.count { it.winner != teamName && it.winner != "Draw" }
        val goalsFor = completed.sumOf { it.awayScore }
        val goalsAgainst = completed.sumOf { it.homeScore }

        return TeamRecord(
            played = completed.size,
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
     * Get team's overall record
     */
    suspend fun getTeamOverallRecord(teamName: String): TeamRecord {
        val home = getTeamHomeRecord(teamName)
        val away = getTeamAwayRecord(teamName)

        return TeamRecord(
            played = home.played + away.played,
            wins = home.wins + away.wins,
            draws = home.draws + away.draws,
            losses = home.losses + away.losses,
            goalsFor = home.goalsFor + away.goalsFor,
            goalsAgainst = home.goalsAgainst + away.goalsAgainst,
            goalDifference = (home.goalsFor + away.goalsFor) - (home.goalsAgainst + away.goalsAgainst),
            points = home.points + away.points
        )
    }

    /**
     * Get team's record against specific opponent
     */
    suspend fun getTeamRecordVsOpponent(teamName: String, opponentName: String): HeadToHeadRecord {
        val h2h = getHeadToHead(teamName, opponentName).firstOrNull() ?: emptyList()
        val completed = h2h.filter { it.isCompleted }

        val wins = completed.count { it.winner == teamName }
        val losses = completed.count { it.winner == opponentName }
        val draws = completed.count { it.winner == "Draw" }
        val goalsFor = completed.sumOf {
            if (it.homeTeam == teamName) it.homeScore else it.awayScore
        }
        val goalsAgainst = completed.sumOf {
            if (it.homeTeam == teamName) it.awayScore else it.homeScore
        }

        return HeadToHeadRecord(
            totalMatches = completed.size,
            team1Wins = wins,
            team2Wins = losses,
            draws = draws,
            team1Goals = goalsFor,
            team2Goals = goalsAgainst,
            team1RecentForm = completed.take(5).joinToString("") { fixture ->
                when {
                    fixture.winner == teamName -> "W"
                    fixture.winner == "Draw" -> "D"
                    else -> "L"
                }
            }
        )
    }

    // ============ LEAGUE-BASED QUERIES ============

    fun getLeagueFixtures(leagueName: String, season: String): Flow<List<FixturesEntity>> =
        fixturesDao.getLeagueFixtures(leagueName, season)

    fun getUpcomingLeagueFixtures(leagueName: String, season: String): Flow<List<FixturesEntity>> =
        fixturesDao.getUpcomingLeagueFixtures(leagueName, season)

    fun getCompletedLeagueFixtures(leagueName: String, season: String): Flow<List<FixturesEntity>> =
        fixturesDao.getCompletedLeagueFixtures(leagueName, season)

    fun getLeagueFixturesByRound(leagueName: String, season: String, gameWeek: Int): Flow<List<FixturesEntity>> =
        fixturesDao.getLeagueFixturesByRound(leagueName, season, gameWeek)

    /**
     * Get current game week for a league
     */
    suspend fun getCurrentGameWeek(leagueName: String, season: String): Int {
        val completedFixtures = getCompletedLeagueFixtures(leagueName, season)
            .firstOrNull() ?: emptyList()

        val maxPosition = completedFixtures.maxOfOrNull { it.position } ?: 0
        return maxPosition + 1
    }

    /**
     * Get remaining fixtures count for a league
     */
    suspend fun getRemainingFixturesCount(leagueName: String, season: String): Int {
        val allFixtures = getLeagueFixtures(leagueName, season).firstOrNull() ?: emptyList()
        val completedFixtures = allFixtures.count { it.isCompleted }
        return allFixtures.size - completedFixtures
    }

    // ============ CUP-BASED QUERIES ============

    fun getCupFixtures(cupName: String, season: String): Flow<List<FixturesEntity>> =
        fixturesDao.getCupFixtures(cupName, season)

    fun getCupFixturesByRound(cupName: String, season: String, round: String): Flow<List<FixturesEntity>> =
        fixturesDao.getCupFixturesByRound(cupName, season, round)

    /**
     * Create a cup fixture
     */
    suspend fun createCupFixture(
        homeTeam: String,
        awayTeam: String,
        matchDate: String,
        season: String,
        cupName: String,
        round: String
    ): FixturesEntity? {

        val fixture = FixturesEntity(
            matchDate = matchDate,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            stadium = getTeamStadium(homeTeam) ?: "FAME Africa Stadium",
            matchType = "Cup",
            season = season,
            cupName = cupName,
            round = round,
            matchStatus = "SCHEDULED"
        )

        insertFixture(fixture)
        return fixture
    }

    /**
     * Get current round for a cup competition
     */
    suspend fun getCurrentCupRound(cupName: String, season: String): String? {
        val fixtures = getCupFixtures(cupName, season).firstOrNull() ?: emptyList()
        val completedRounds = fixtures
            .filter { it.isCompleted }
            .map { it.round }
            .distinct()

        val allRounds = fixtures.map { it.round }.distinct()

        return allRounds.firstOrNull { round ->
            round !in completedRounds
        }
    }

    /**
     * Create a knockout fixture
     */
    suspend fun createKnockoutFixture(
        homeTeam: String,
        awayTeam: String,
        matchDate: String,
        season: String,
        cupName: String,
        round: String,
        stadium: String
    ): FixturesEntity? {

        val fixture = FixturesEntity(
            matchDate = matchDate,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            stadium = stadium,
            matchType = "Cup",
            season = season,
            cupName = cupName,
            round = round,
            matchStatus = "SCHEDULED"
        )

        insertFixture(fixture)
        return fixture
    }

    // ============ REFEREE-BASED QUERIES ============

    fun getFixturesByReferee(refereeId: Int): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesByReferee(refereeId)

    fun getUpcomingFixturesByReferee(refereeId: Int): Flow<List<FixturesEntity>> =
        fixturesDao.getUpcomingFixturesByReferee(refereeId)

    /**
     * Get referee's next assignment
     */
    suspend fun getRefereeNextAssignment(refereeId: Int): FixturesEntity? {
        return fixturesDao.getUpcomingFixturesByReferee(refereeId)
            .firstOrNull()
            ?.firstOrNull()
    }

    // ============ STATUS-BASED QUERIES ============

    fun getFixturesByStatus(status: String): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesByStatus(status)

    fun getLiveFixtures(): Flow<List<FixturesEntity>> =
        fixturesDao.getLiveFixtures()

    fun getTodaysFixturesList(): Flow<List<FixturesEntity>> =
        fixturesDao.getTodaysFixtures()

    /**
     * Get postponed fixtures
     */
    fun getPostponedFixtures(): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesByStatus("POSTPONED")

    /**
     * Get cancelled fixtures
     */
    fun getCancelledFixtures(): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesByStatus("CANCELLED")

    // ============ SEASON-BASED QUERIES ============

    fun getSeasons(): Flow<List<String>> = fixturesDao.getSeasons()

    fun getFixturesBySeason(season: String): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesBySeason(season)

    /**
     * Get current active season
     */
    suspend fun getCurrentSeason(): String {
        val seasons = getSeasons().firstOrNull() ?: return "2024/25"
        return if (seasons.isNotEmpty()) seasons.first() else "2024/25"
    }

    // ============ TYPE-BASED QUERIES ============

    fun getFixturesByType(matchType: String): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesByType(matchType)

    fun getLeagueMatches(): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesByType("League")

    fun getCupMatches(): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesByType("Cup")

    fun getFriendlyMatches(): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesByType("Friendly")

    fun getInternationalMatches(): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesByType("International")

    fun getPlayoffMatches(): Flow<List<FixturesEntity>> =
        fixturesDao.getFixturesByType("Playoff")

    // ============ STATISTICS QUERIES ============

    /**
     * Get team's form (last 5 results)
     */
    suspend fun getTeamForm(teamName: String): TeamForm {
        val recentFixtures = fixturesDao.getTeamRecentForm(teamName)

        val formString = recentFixtures.take(5).joinToString("") { fixture ->
            when {
                !fixture.isCompleted -> ""
                fixture.winner == teamName -> "W"
                fixture.winner == "Draw" -> "D"
                else -> "L"
            }
        }

        val wins = recentFixtures.count { it.winner == teamName }
        val draws = recentFixtures.count { it.winner == "Draw" }
        val losses = recentFixtures.count {
            it.isCompleted && it.winner != teamName && it.winner != "Draw"
        }
        val goalsFor = recentFixtures.sumOf {
            if (it.homeTeam == teamName) it.homeScore else it.awayScore
        }
        val goalsAgainst = recentFixtures.sumOf {
            if (it.homeTeam == teamName) it.awayScore else it.homeScore
        }

        return TeamForm(
            formString = formString,
            played = recentFixtures.size,
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
     * Get league statistics
     */
    suspend fun getLeagueStatistics(leagueName: String, season: String): LeagueStatistics {
        val fixtures = getLeagueFixtures(leagueName, season).firstOrNull() ?: emptyList()
        val completed = fixtures.filter { it.isCompleted }

        val totalGoals = completed.sumOf { it.homeScore + it.awayScore }
        val homeWins = completed.count { it.homeScore > it.awayScore }
        val awayWins = completed.count { it.awayScore > it.homeScore }
        val draws = completed.count { it.homeScore == it.awayScore }

        return LeagueStatistics(
            totalMatches = completed.size,
            totalGoals = totalGoals,
            averageGoalsPerGame = if (completed.isNotEmpty()) totalGoals.toDouble() / completed.size else 0.0,
            homeWins = homeWins,
            awayWins = awayWins,
            draws = draws,
            homeWinPercentage = if (completed.isNotEmpty()) (homeWins.toDouble() / completed.size * 100) else 0.0,
            awayWinPercentage = if (completed.isNotEmpty()) (awayWins.toDouble() / completed.size * 100) else 0.0,
            drawPercentage = if (completed.isNotEmpty()) (draws.toDouble() / completed.size * 100) else 0.0
        )
    }

    /**
     * Get team's season progress
     */
    suspend fun getTeamSeasonProgress(teamName: String, season: String): TeamSeasonProgress {
        val allFixtures = getFixturesByTeam(teamName).firstOrNull() ?: emptyList()
        val seasonFixtures = allFixtures.filter { it.season == season }
        val completed = seasonFixtures.filter { it.isCompleted }

        val homeFixtures = seasonFixtures.filter { it.homeTeam == teamName }
        val awayFixtures = seasonFixtures.filter { it.awayTeam == teamName }

        return TeamSeasonProgress(
            totalFixtures = seasonFixtures.size,
            played = completed.size,
            remaining = seasonFixtures.size - completed.size,
            homePlayed = homeFixtures.count { it.isCompleted },
            homeRemaining = homeFixtures.size - homeFixtures.count { it.isCompleted },
            awayPlayed = awayFixtures.count { it.isCompleted },
            awayRemaining = awayFixtures.size - awayFixtures.count { it.isCompleted }
        )
    }

    /**
     * Create a Community Shield fixture with country-specific TV channel
     */
    suspend fun createShieldFixture(
        homeTeam: String,
        awayTeam: String,
        matchDate: String,
        season: String,
        competition: String,
        stadium: String,
        tvChannel: String
    ): FixturesEntity? {

        val fixture = FixturesEntity(
            matchDate = matchDate,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            stadium = stadium,
            matchType = "Community Shield",
            season = season,
            round = "Final",
            matchStatus = "SCHEDULED",
            tvChannel = tvChannel,
            weatherConditions = "Clear",
            timeZone = "Africa/Dar es Salaam"
        )

        insertFixture(fixture)
        return fixture
    }

    // ============ FIXTURE GENERATION ============

    /**
     * Generate league fixtures for a season
     * Handles both even and odd numbers of teams
     * For odd numbers, one team gets a bye each round
     */
    suspend fun generateLeagueFixtures(
        league: LeaguesEntity,
        season: String,
        teams: List<TeamsEntity>,
        startDate: String,
        daysBetweenRounds: Int = 7
    ): List<FixturesEntity> {
        val fixtures = mutableListOf<FixturesEntity>()
        val teamNames = teams.map { it.name }.toMutableList()
        val numTeams = teamNames.size
        val isOdd = numTeams % 2 != 0

        // For odd number of teams, add a dummy "BYE" team
        if (isOdd) {
            teamNames.add("BYE")
        }

        val effectiveNumTeams = teamNames.size // Now even
        val numRounds = (effectiveNumTeams - 1) * 2 // Double round-robin
        val matchesPerRound = effectiveNumTeams / 2

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        calendar.time = dateFormat.parse(startDate) ?: Calendar.getInstance().time

        for (round in 0 until numRounds) {
            val isFirstHalf = round < effectiveNumTeams - 1
            val roundNumber = round + 1

            for (match in 0 until matchesPerRound) {
                val homeIndex: Int
                val awayIndex: Int

                if (isFirstHalf) {
                    homeIndex = (round + match) % (effectiveNumTeams - 1)
                    awayIndex = (effectiveNumTeams - 1 - match + round) % (effectiveNumTeams - 1)

                    if (match == 0) {
                        // Last team vs first team special case
                        val homeTeamName = teamNames.last()
                        val awayTeamName = teamNames[round % (effectiveNumTeams - 1)]

                        // Skip if it's a BYE
                        if (homeTeamName != "BYE" && awayTeamName != "BYE") {
                            val fixture = createFixture(
                                homeTeam = homeTeamName,
                                awayTeam = awayTeamName,
                                matchDate = dateFormat.format(calendar.time),
                                season = season,
                                league = league,
                                roundNumber = roundNumber
                            )
                            fixtures.add(fixture)
                        }
                        calendar.add(Calendar.HOUR_OF_DAY, 2)
                        continue
                    }
                } else {
                    val firstHalfRound = round - (effectiveNumTeams - 1)
                    homeIndex = (effectiveNumTeams - 1 - firstHalfRound + match) % (effectiveNumTeams - 1)
                    awayIndex = (firstHalfRound + match) % (effectiveNumTeams - 1)

                    if (match == 0) {
                        val homeTeamName = teamNames[firstHalfRound % (effectiveNumTeams - 1)]
                        val awayTeamName = teamNames.last()

                        // Skip if it's a BYE
                        if (homeTeamName != "BYE" && awayTeamName != "BYE") {
                            val fixture = createFixture(
                                homeTeam = homeTeamName,
                                awayTeam = awayTeamName,
                                matchDate = dateFormat.format(calendar.time),
                                season = season,
                                league = league,
                                roundNumber = roundNumber
                            )
                            fixtures.add(fixture)
                        }
                        calendar.add(Calendar.HOUR_OF_DAY, 2)
                        continue
                    }
                }

                val homeTeamName = teamNames[homeIndex]
                val awayTeamName = teamNames[awayIndex]

                // Skip if either team is BYE
                if (homeTeamName != "BYE" && awayTeamName != "BYE") {
                    val fixture = createFixture(
                        homeTeam = homeTeamName,
                        awayTeam = awayTeamName,
                        matchDate = dateFormat.format(calendar.time),
                        season = season,
                        league = league,
                        roundNumber = roundNumber
                    )
                    fixtures.add(fixture)
                }

                calendar.add(Calendar.HOUR_OF_DAY, 2)
            }

            // Move to next round
            calendar.add(Calendar.DAY_OF_YEAR, daysBetweenRounds)
            calendar.set(Calendar.HOUR_OF_DAY, 15)
            calendar.set(Calendar.MINUTE, 0)
        }

        insertAllFixtures(fixtures)
        return fixtures
    }

    /**
     * Helper function to create a fixture
     */
    private suspend fun createFixture(
        homeTeam: String,
        awayTeam: String,
        matchDate: String,
        season: String,
        league: LeaguesEntity,
        roundNumber: Int
    ): FixturesEntity {
        return FixturesEntity(
            matchDate = matchDate,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            stadium = getTeamStadium(homeTeam) ?: "FAME Africa Stadium",
            matchType = "League",
            season = season,
            league = league.name,
            position = roundNumber,
            round = "Matchday $roundNumber",
            matchStatus = "SCHEDULED"
        )
    }

    /**
     * Alternative method for leagues with odd number of teams
     * Uses a different algorithm that naturally handles byes
     */
    suspend fun generateLeagueFixturesWithByes(
        league: LeaguesEntity,
        season: String,
        teams: List<TeamsEntity>,
        startDate: String,
        daysBetweenRounds: Int = 7
    ): List<FixturesEntity> {
        val fixtures = mutableListOf<FixturesEntity>()
        val teamNames = teams.map { it.name }.toMutableList()
        val numTeams = teamNames.size
        val isOdd = numTeams % 2 != 0

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        calendar.time = dateFormat.parse(startDate) ?: Calendar.getInstance().time

        if (isOdd) {
            // For odd number of teams, each round one team rests
            val rounds = numTeams // Number of rounds in first half
            val totalRounds = rounds * 2 // Double round-robin

            for (round in 0 until totalRounds) {
                val roundNumber = round + 1
                val isFirstHalf = round < rounds

                // Determine which team has a bye this round
                val byeTeamIndex = if (isFirstHalf) {
                    round % numTeams
                } else {
                    (round - rounds) % numTeams
                }

                for (i in 0 until numTeams) {
                    for (j in i + 1 until numTeams) {
                        // Skip if either team is the bye team
                        if (i == byeTeamIndex || j == byeTeamIndex) continue

                        // Determine home and away based on round
                        val homeIsFirst = if (isFirstHalf) {
                            (i + j + round) % 2 == 0
                        } else {
                            (i + j + round) % 2 != 0
                        }

                        val homeTeam = if (homeIsFirst) teamNames[i] else teamNames[j]
                        val awayTeam = if (homeIsFirst) teamNames[j] else teamNames[i]

                        val fixture = FixturesEntity(
                            matchDate = dateFormat.format(calendar.time),
                            homeTeam = homeTeam,
                            awayTeam = awayTeam,
                            stadium = getTeamStadium(homeTeam) ?: "FAME Africa Stadium",
                            matchType = "League",
                            season = season,
                            league = league.name,
                            position = roundNumber,
                            round = "Matchday $roundNumber",
                            matchStatus = "SCHEDULED"
                        )
                        fixtures.add(fixture)
                        calendar.add(Calendar.HOUR_OF_DAY, 2)
                    }
                }

                calendar.add(Calendar.DAY_OF_YEAR, daysBetweenRounds)
                calendar.set(Calendar.HOUR_OF_DAY, 15)
                calendar.set(Calendar.MINUTE, 0)
            }
        } else {
            // Use standard double round-robin for even numbers
            return generateLeagueFixtures(league, season, teams, startDate, daysBetweenRounds)
        }

        insertAllFixtures(fixtures)
        return fixtures
    }

    /**
     * Generate cup knockout fixtures
     */
    suspend fun generateCupKnockoutFixtures(
        cup: CupsEntity,
        season: String,
        round: String,
        matchPairs: List<Pair<String, String>>,
        matchDate: String,
        isTwoLegged: Boolean = false
    ): List<FixturesEntity> {
        val fixtures = mutableListOf<FixturesEntity>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        var currentDate = dateFormat.parse(matchDate) ?: Calendar.getInstance().time

        for ((index, pair) in matchPairs.withIndex()) {
            val firstLeg = FixturesEntity(
                matchDate = dateFormat.format(currentDate),
                homeTeam = pair.first,
                awayTeam = pair.second,
                stadium = getTeamStadium(pair.first) ?: "FAME Africa Stadium",
                matchType = "Cup",
                season = season,
                cupName = cup.name,
                round = round,
                position = index + 1,
                matchStatus = "SCHEDULED"
            )
            fixtures.add(firstLeg)

            if (isTwoLegged) {
                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                calendar.add(Calendar.DAY_OF_YEAR, 7)

                val secondLeg = FixturesEntity(
                    matchDate = dateFormat.format(calendar.time),
                    homeTeam = pair.second,
                    awayTeam = pair.first,
                    stadium = getTeamStadium(pair.second) ?: "FAME Africa Stadium",
                    matchType = "Cup",
                    season = season,
                    cupName = cup.name,
                    round = round,
                    position = index + 1,
                    matchStatus = "SCHEDULED"
                )
                fixtures.add(secondLeg)

                currentDate = calendar.time
            }

            val cal = Calendar.getInstance()
            cal.time = currentDate
            cal.add(Calendar.HOUR_OF_DAY, 2)
            currentDate = cal.time
        }

        insertAllFixtures(fixtures)
        return fixtures
    }

    /**
     * Generate friendly matches
     */
    suspend fun generateFriendlyFixtures(
        homeTeam: String,
        awayTeam: String,
        matchDate: String,
        season: String
    ): FixturesEntity {
        val fixture = FixturesEntity(
            matchDate = matchDate,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            stadium = getTeamStadium(homeTeam) ?: "FAME Africa Stadium",
            matchType = "Friendly",
            season = season,
            matchStatus = "SCHEDULED"
        )

        insertFixture(fixture)
        return fixture
    }

    // ============ FIXTURE MANAGEMENT ============

    /**
     * Postpone a fixture
     */
    suspend fun postponeFixture(fixtureId: Int, newDate: String): FixturesEntity? {
        val fixture = getFixtureById(fixtureId) ?: return null
        val updatedFixture = fixture.postpone(newDate)
        updateFixture(updatedFixture)
        return updatedFixture
    }

    /**
     * Reschedule a postponed fixture
     */
    suspend fun rescheduleFixture(fixtureId: Int, newDate: String): FixturesEntity? {
        val fixture = getFixtureById(fixtureId) ?: return null
        val updatedFixture = fixture.reschedule(newDate)
        updateFixture(updatedFixture)
        return updatedFixture
    }

    /**
     * Cancel a fixture
     */
    suspend fun cancelFixture(fixtureId: Int): FixturesEntity? {
        val fixture = getFixtureById(fixtureId) ?: return null
        val updatedFixture = fixture.cancel()
        updateFixture(updatedFixture)
        return updatedFixture
    }

    /**
     * Start a fixture (set to LIVE)
     */
    suspend fun startFixture(fixtureId: Int): FixturesEntity? {
        val fixture = getFixtureById(fixtureId) ?: return null
        val updatedFixture = fixture.start()
        updateFixture(updatedFixture)
        return updatedFixture
    }

    /**
     * Complete a fixture with scores
     */
    suspend fun completeFixture(
        fixtureId: Int,
        homeScore: Int,
        awayScore: Int
    ): FixturesEntity? {
        val fixture = getFixtureById(fixtureId) ?: return null
        val updatedFixture = fixture.updateScore(homeScore, awayScore)
        updateFixture(updatedFixture)
        return updatedFixture
    }

    // ============ UTILITY METHODS ============

    /**
     * Get team's home stadium
     */
    private suspend fun getTeamStadium(teamName: String): String? {
        return teamsRepository.getTeamByName(teamName)?.homeStadium
    }

    /**
     * Validate fixture before insertion
     */
    suspend fun validateFixture(fixture: FixturesEntity): ValidationResult {
        // Check if teams exist
        val homeTeam = teamsRepository.getTeamByName(fixture.homeTeam)
        if (homeTeam == null) {
            return ValidationResult(false, "Home team does not exist")
        }

        val awayTeam = teamsRepository.getTeamByName(fixture.awayTeam)
        if (awayTeam == null) {
            return ValidationResult(false, "Away team does not exist")
        }

        // Check if home and away are different
        if (fixture.homeTeam == fixture.awayTeam) {
            return ValidationResult(false, "Home and away teams must be different")
        }

        // Validate league reference
        if (fixture.league != null) {
            val league = leaguesRepository.getLeagueByName(fixture.league)
            if (league == null) {
                return ValidationResult(false, "League does not exist")
            }
        }

        // Validate cup reference
        if (fixture.cupName != null) {
            val cup = cupsRepository.getCupByName(fixture.cupName)
            if (cup == null) {
                return ValidationResult(false, "Cup does not exist")
            }
        }

        // Validate referee
        if (fixture.refereeId != null) {
            val referee = refereesRepository.getRefereeById(fixture.refereeId)
            if (referee == null) {
                return ValidationResult(false, "Referee does not exist")
            }
        }

        return ValidationResult(true, "Fixture is valid")
    }

    /**
     * Get fixtures dashboard
     */
    suspend fun getFixturesDashboard(): FixturesDashboard {
        val upcomingFixtures = getUpcomingFixturesLimit(10).firstOrNull() ?: emptyList()
        val recentResults = getRecentFixtures(10).firstOrNull() ?: emptyList()
        val liveFixtures = getLiveFixtures().firstOrNull() ?: emptyList()
        val todaysFixtures = getTodaysFixturesList().firstOrNull() ?: emptyList()

        return FixturesDashboard(
            totalUpcoming = getUpcomingFixtures().firstOrNull()?.size ?: 0,
            upcomingFixtures = upcomingFixtures,
            recentResults = recentResults,
            liveFixtures = liveFixtures,
            todaysFixtures = todaysFixtures,
            postponedCount = getPostponedFixtures().firstOrNull()?.size ?: 0,
            cancelledCount = getCancelledFixtures().firstOrNull()?.size ?: 0
        )
    }
}

// ============ DATA CLASSES ============

data class TeamRecord(
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int,
    val points: Int
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

data class LeagueStatistics(
    val totalMatches: Int,
    val totalGoals: Int,
    val averageGoalsPerGame: Double,
    val homeWins: Int,
    val awayWins: Int,
    val draws: Int,
    val homeWinPercentage: Double,
    val awayWinPercentage: Double,
    val drawPercentage: Double
)

data class TeamSeasonProgress(
    val totalFixtures: Int,
    val played: Int,
    val remaining: Int,
    val homePlayed: Int,
    val homeRemaining: Int,
    val awayPlayed: Int,
    val awayRemaining: Int
)

//data class ValidationResult(
    //val isValid: Boolean,
    //val message: String
//)

data class FixturesDashboard(
    val totalUpcoming: Int,
    val upcomingFixtures: List<FixturesEntity>,
    val recentResults: List<FixturesEntity>,
    val liveFixtures: List<FixturesEntity>,
    val todaysFixtures: List<FixturesEntity>,
    val postponedCount: Int,
    val cancelledCount: Int
)