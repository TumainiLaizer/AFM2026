package com.fameafrica.afm2026.data.database.dao

import androidx.room.*
import com.fameafrica.afm2026.data.database.entities.FixturesResultsEntity
import com.fameafrica.afm2026.data.database.entities.TeamsEntity
import com.fameafrica.afm2026.data.database.entities.RefereesEntity
import com.fameafrica.afm2026.data.database.entities.PlayersEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FixturesResultsDao {

    // ============ BASIC CRUD ============

    @Query("SELECT * FROM fixtures_results ORDER BY match_date DESC")
    fun getAll(): Flow<List<FixturesResultsEntity>>

    @Query("SELECT * FROM fixtures_results WHERE fixture_id = :fixtureId")
    suspend fun getByFixtureId(fixtureId: Int): FixturesResultsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: FixturesResultsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<FixturesResultsEntity>)

    @Update
    suspend fun update(result: FixturesResultsEntity)

    @Delete
    suspend fun delete(result: FixturesResultsEntity)

    @Query("DELETE FROM fixtures_results")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM fixtures_results")
    suspend fun getCount(): Int

    // ============ DATE-BASED QUERIES ============

    @Query("SELECT * FROM fixtures_results WHERE date(match_date) = date(:date) ORDER BY match_date")
    fun getResultsByDate(date: String): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE date(match_date) BETWEEN date(:startDate) AND date(:endDate)
        ORDER BY match_date
    """)
    fun getResultsBetween(startDate: String, endDate: String): Flow<List<FixturesResultsEntity>>

    @Query("SELECT * FROM fixtures_results WHERE strftime('%Y', match_date) = :year")
    fun getResultsByYear(year: String): Flow<List<FixturesResultsEntity>>

    @Query("SELECT * FROM fixtures_results WHERE strftime('%m', match_date) = :month AND strftime('%Y', match_date) = :year")
    fun getResultsByMonth(month: String, year: String): Flow<List<FixturesResultsEntity>>

    // ============ TEAM-BASED QUERIES ============

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE home_team = :teamName OR away_team = :teamName 
        ORDER BY match_date DESC
    """)
    fun getResultsByTeam(teamName: String): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE (home_team = :team1 AND away_team = :team2) 
        OR (home_team = :team2 AND away_team = :team1)
        ORDER BY match_date DESC
    """)
    fun getHeadToHead(team1: String, team2: String): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE home_team = :teamName 
        ORDER BY match_date DESC
    """)
    fun getHomeResults(teamName: String): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE away_team = :teamName 
        ORDER BY match_date DESC
    """)
    fun getAwayResults(teamName: String): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE (home_team = :teamName AND home_score > away_score)
        OR (away_team = :teamName AND away_score > home_score)
        ORDER BY match_date DESC
    """)
    fun getWinsByTeam(teamName: String): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE (home_team = :teamName AND home_score < away_score)
        OR (away_team = :teamName AND away_score < home_score)
        ORDER BY match_date DESC
    """)
    fun getLossesByTeam(teamName: String): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE (home_team = :teamName OR away_team = :teamName)
        AND home_score = away_score
        AND home_penalty_score IS NULL
        ORDER BY match_date DESC
    """)
    fun getDrawsByTeam(teamName: String): Flow<List<FixturesResultsEntity>>

    // ============ LEAGUE-BASED QUERIES ============

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE league_name = :leagueName 
        AND season = :season
        ORDER BY match_date
    """)
    fun getLeagueResults(leagueName: String, season: String): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE league_name = :leagueName 
        AND season = :season
        AND home_team = :teamName OR away_team = :teamName
        ORDER BY match_date
    """)
    fun getTeamLeagueResults(leagueName: String, season: String, teamName: String): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT SUM(home_score + away_score) 
        FROM fixtures_results 
        WHERE league_name = :leagueName 
        AND season = :season
    """)
    suspend fun getTotalGoalsInLeague(leagueName: String, season: String): Int?

    @Query("""
        SELECT AVG(home_score + away_score) 
        FROM fixtures_results 
        WHERE league_name = :leagueName 
        AND season = :season
    """)
    suspend fun getAverageGoalsInLeague(leagueName: String, season: String): Double?

    // ============ CUP-BASED QUERIES ============

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE cup_name = :cupName 
        AND season = :season
        ORDER BY match_date
    """)
    fun getCupResults(cupName: String, season: String): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE cup_name = :cupName 
        AND season = :season
        AND cup_round = :round
        ORDER BY match_date
    """)
    fun getCupRoundResults(cupName: String, season: String, round: String): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE cup_name = :cupName 
        AND season = :season
        AND home_penalty_score IS NOT NULL
    """)
    fun getPenaltyShootouts(cupName: String, season: String): Flow<List<FixturesResultsEntity>>

    // ============ REFEREE-BASED QUERIES ============

    @Query("SELECT * FROM fixtures_results WHERE referee_id = :refereeId ORDER BY match_date DESC")
    fun getResultsByReferee(refereeId: Int): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT 
            AVG(yellow_cards_home + yellow_cards_away) as avg_yellow,
            AVG(red_cards_home + red_cards_away) as avg_red,
            COUNT(*) as matches
        FROM fixtures_results 
        WHERE referee_id = :refereeId
    """)
    suspend fun getRefereeCardStats(refereeId: Int): RefereeCardStats?

    // ============ PLAYER-BASED QUERIES ============

    @Query("SELECT * FROM fixtures_results WHERE man_of_match = :playerName ORDER BY match_date DESC")
    fun getResultsByManOfMatch(playerName: String): Flow<List<FixturesResultsEntity>>

    // ============ UPSET/TRENDING QUERIES ============

    @Query("SELECT * FROM fixtures_results WHERE is_upset = 1 ORDER BY upset_factor DESC LIMIT :limit")
    fun getBiggestUpsets(limit: Int = 10): Flow<List<FixturesResultsEntity>>

    @Query("SELECT * FROM fixtures_results WHERE home_score + away_score >= 5 ORDER BY home_score + away_score DESC LIMIT :limit")
    fun getHighestScoringGames(limit: Int = 10): Flow<List<FixturesResultsEntity>>

    @Query("SELECT * FROM fixtures_results WHERE home_score >= 5 OR away_score >= 5 ORDER BY (home_score + away_score) DESC LIMIT :limit")
    fun getThrashingResults(limit: Int = 10): Flow<List<FixturesResultsEntity>>

    @Query("""
        SELECT * FROM fixtures_results 
        WHERE (home_halftime_score < away_halftime_score AND home_score > away_score)
        OR (away_halftime_score < home_halftime_score AND away_score > home_score)
        ORDER BY (home_score + away_score) DESC
        LIMIT :limit
    """)
    fun getComebackVictories(limit: Int = 10): Flow<List<FixturesResultsEntity>>

    @Query("SELECT * FROM fixtures_results WHERE home_score = 0 AND away_score = 0 ORDER BY match_date DESC LIMIT :limit")
    fun getGoallessDraws(limit: Int = 10): Flow<List<FixturesResultsEntity>>

    // ============ STATISTICS QUERIES ============

    @Query("""
        SELECT 
            COUNT(*) as total_matches,
            AVG(home_score) as avg_home_goals,
            AVG(away_score) as avg_away_goals,
            AVG(home_score + away_score) as avg_total_goals,
            SUM(CASE WHEN home_score > away_score THEN 1 ELSE 0 END) as home_wins,
            SUM(CASE WHEN away_score > home_score THEN 1 ELSE 0 END) as away_wins,
            SUM(CASE WHEN home_score = away_score AND home_penalty_score IS NULL THEN 1 ELSE 0 END) as draws,
            AVG(attendance) as avg_attendance,
            SUM(yellow_cards_home + yellow_cards_away) as total_yellows,
            SUM(red_cards_home + red_cards_away) as total_reds
        FROM fixtures_results 
        WHERE season = :season
    """)
    suspend fun getSeasonStatistics(season: String): SeasonStatistics?

    @Query("""
        SELECT 
            strftime('%m', match_date) as month,
            COUNT(*) as matches,
            AVG(home_score + away_score) as avg_goals
        FROM fixtures_results 
        WHERE strftime('%Y', match_date) = :year
        GROUP BY month
        ORDER BY month
    """)
    fun getMonthlyStats(year: String): Flow<List<MonthlyStatistics>>

    // ============ SEASON-BASED QUERIES ============

    @Query("SELECT DISTINCT season FROM fixtures_results ORDER BY season DESC")
    fun getSeasons(): Flow<List<String>>

    @Query("SELECT * FROM fixtures_results WHERE season = :season ORDER BY match_date DESC")
    fun getResultsBySeason(season: String): Flow<List<FixturesResultsEntity>>

    // ============ COMPETITION-BASED QUERIES ============

    @Query("SELECT * FROM fixtures_results WHERE match_type = :matchType ORDER BY match_date DESC")
    fun getResultsByMatchType(matchType: String): Flow<List<FixturesResultsEntity>>

    // ============ JOIN QUERIES ============

    @Query("""
        SELECT 
            fr.*,
            ht.logo_path as home_team_logo,
            ht.league as home_team_league,
            ht.elo_rating as home_team_current_elo,
            at.logo_path as away_team_logo,
            at.league as away_team_league,
            at.elo_rating as away_team_current_elo,
            r.name as referee_name,
            r.strictness as referee_strictness,
            r.bias as referee_bias,
            n.nationality as referee_nationality,
            p.name as man_of_match_player_name,
            p.position as man_of_match_position,
            p.rating as man_of_match_rating_value
        FROM fixtures_results fr
        LEFT JOIN teams ht ON fr.home_team = ht.name
        LEFT JOIN teams at ON fr.away_team = at.name
        LEFT JOIN referees r ON fr.referee_id = r.referee_id
        LEFT JOIN nationalities n ON r.nationality_id = n.id
        LEFT JOIN players p ON fr.man_of_match = p.name
        WHERE fr.fixture_id = :fixtureId
    """)
    suspend fun getCompleteResultDetails(fixtureId: Int): CompleteResultDetails?

    @Query("""
        SELECT 
            fr.*,
            l.name as league_name,
            l.level as league_level,
            l.country_id as league_country_id,
            c.name as cup_competition_name,
            c.type as cup_competition_type
        FROM fixtures_results fr
        LEFT JOIN leagues l ON fr.league_name = l.name
        LEFT JOIN cups c ON fr.cup_name = c.name
        WHERE fr.fixture_id = :fixtureId
    """)
    suspend fun getResultWithCompetition(fixtureId: Int): ResultWithCompetition?
}

// ============ DATA CLASSES FOR STATISTICS ============

data class RefereeCardStats(
    @ColumnInfo(name = "avg_yellow")
    val averageYellowCards: Double,

    @ColumnInfo(name = "avg_red")
    val averageRedCards: Double,

    @ColumnInfo(name = "matches")
    val matchesOfficiated: Int
)

data class SeasonStatistics(
    @ColumnInfo(name = "total_matches")
    val totalMatches: Int,

    @ColumnInfo(name = "avg_home_goals")
    val averageHomeGoals: Double,

    @ColumnInfo(name = "avg_away_goals")
    val averageAwayGoals: Double,

    @ColumnInfo(name = "avg_total_goals")
    val averageTotalGoals: Double,

    @ColumnInfo(name = "home_wins")
    val homeWins: Int,

    @ColumnInfo(name = "away_wins")
    val awayWins: Int,

    @ColumnInfo(name = "draws")
    val draws: Int,

    @ColumnInfo(name = "avg_attendance")
    val averageAttendance: Double,

    @ColumnInfo(name = "total_yellows")
    val totalYellowCards: Int,

    @ColumnInfo(name = "total_reds")
    val totalRedCards: Int
)

data class MonthlyStatistics(
    @ColumnInfo(name = "month")
    val month: String,

    @ColumnInfo(name = "matches")
    val matchesPlayed: Int,

    @ColumnInfo(name = "avg_goals")
    val averageGoals: Double
)

// ============ DATA CLASSES FOR JOIN QUERIES ============

data class CompleteResultDetails(
    @Embedded
    val result: FixturesResultsEntity,

    // Home Team
    @ColumnInfo(name = "home_team_logo")
    val homeTeamLogo: String?,

    @ColumnInfo(name = "home_team_league")
    val homeTeamLeague: String?,

    @ColumnInfo(name = "home_team_current_elo")
    val homeTeamCurrentElo: Int?,

    // Away Team
    @ColumnInfo(name = "away_team_logo")
    val awayTeamLogo: String?,

    @ColumnInfo(name = "away_team_league")
    val awayTeamLeague: String?,

    @ColumnInfo(name = "away_team_current_elo")
    val awayTeamCurrentElo: Int?,

    // Referee
    @ColumnInfo(name = "referee_name")
    val refereeName: String?,

    @ColumnInfo(name = "referee_strictness")
    val refereeStrictness: Int?,

    @ColumnInfo(name = "referee_bias")
    val refereeBias: Int?,

    @ColumnInfo(name = "referee_nationality")
    val refereeNationality: String?,

    // Man of the Match
    @ColumnInfo(name = "man_of_match_player_name")
    val manOfMatchPlayerName: String?,

    @ColumnInfo(name = "man_of_match_position")
    val manOfMatchPosition: String?,

    @ColumnInfo(name = "man_of_match_rating_value")
    val manOfMatchRatingValue: Int?
)

data class ResultWithCompetition(
    @Embedded
    val result: FixturesResultsEntity,

    @ColumnInfo(name = "league_name")
    val leagueCompetitionName: String?,

    @ColumnInfo(name = "league_level")
    val leagueLevel: Int?,

    @ColumnInfo(name = "league_country_id")
    val leagueCountryId: Int?,

    @ColumnInfo(name = "cup_competition_name")
    val cupCompetitionName: String?,

    @ColumnInfo(name = "cup_competition_type")
    val cupCompetitionType: String?
)