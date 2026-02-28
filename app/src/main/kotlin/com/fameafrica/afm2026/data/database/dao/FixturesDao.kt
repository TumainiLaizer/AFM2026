package com.fameafrica.afm2026.data.database.dao

import androidx.room.*
import com.fameafrica.afm2026.data.database.entities.FixturesEntity
import com.fameafrica.afm2026.data.database.entities.TeamsEntity
import com.fameafrica.afm2026.data.database.entities.RefereesEntity
import com.fameafrica.afm2026.data.database.entities.LeaguesEntity
import com.fameafrica.afm2026.data.database.entities.CupsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FixturesDao {

    // ============ BASIC CRUD ============

    @Query("SELECT * FROM fixtures ORDER BY match_date DESC")
    fun getAll(): Flow<List<FixturesEntity>>

    @Query("SELECT * FROM fixtures WHERE id = :id")
    suspend fun getById(id: Int): FixturesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fixture: FixturesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fixtures: List<FixturesEntity>)

    @Update
    suspend fun update(fixture: FixturesEntity)

    @Delete
    suspend fun delete(fixture: FixturesEntity)

    @Query("DELETE FROM fixtures")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM fixtures")
    suspend fun getCount(): Int

    // ============ DATE-BASED QUERIES ============

    /**
     * Get fixtures for a specific date
     */
    @Query("SELECT * FROM fixtures WHERE date(match_date) = date(:date) ORDER BY match_date")
    fun getFixturesByDate(date: String): Flow<List<FixturesEntity>>

    /**
     * Get upcoming fixtures from current date
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE date(match_date) >= date('now') 
        AND match_status IN ('SCHEDULED', 'LIVE')
        ORDER BY match_date ASC
    """)
    fun getUpcomingFixtures(): Flow<List<FixturesEntity>>

    /**
     * Get upcoming fixtures with limit
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE date(match_date) >= date('now') 
        AND match_status IN ('SCHEDULED', 'LIVE')
        ORDER BY match_date ASC
        LIMIT :limit
    """)
    fun getUpcomingFixturesLimit(limit: Int): Flow<List<FixturesEntity>>

    /**
     * Get recent completed fixtures
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE date(match_date) < date('now') 
        AND match_status = 'COMPLETED'
        ORDER BY match_date DESC
        LIMIT :limit
    """)
    fun getRecentFixtures(limit: Int): Flow<List<FixturesEntity>>

    /**
     * Get fixtures between two dates
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE date(match_date) BETWEEN date(:startDate) AND date(:endDate)
        ORDER BY match_date
    """)
    fun getFixturesBetween(startDate: String, endDate: String): Flow<List<FixturesEntity>>

    // ============ TEAM-BASED QUERIES ============

    /**
     * Get all fixtures for a team (home and away)
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE home_team = :teamName OR away_team = :teamName 
        ORDER BY match_date DESC
    """)
    fun getFixturesByTeam(teamName: String): Flow<List<FixturesEntity>>

    /**
     * Get upcoming fixtures for a team
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE (home_team = :teamName OR away_team = :teamName)
        AND date(match_date) >= date('now')
        AND match_status IN ('SCHEDULED', 'LIVE')
        ORDER BY match_date ASC
    """)
    fun getUpcomingFixturesByTeam(teamName: String): Flow<List<FixturesEntity>>

    /**
     * Get recent results for a team
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE (home_team = :teamName OR away_team = :teamName)
        AND date(match_date) < date('now')
        AND match_status = 'COMPLETED'
        ORDER BY match_date DESC
        LIMIT :limit
    """)
    fun getRecentResultsByTeam(teamName: String, limit: Int = 5): Flow<List<FixturesEntity>>

    /**
     * Get head-to-head fixtures between two teams
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE (home_team = :team1 AND away_team = :team2) 
        OR (home_team = :team2 AND away_team = :team1)
        ORDER BY match_date DESC
    """)
    fun getHeadToHead(team1: String, team2: String): Flow<List<FixturesEntity>>

    // ============ LEAGUE-BASED QUERIES ============

    /**
     * Get all fixtures for a league in a season
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE league = :leagueName 
        AND season = :season
        ORDER BY position, match_date
    """)
    fun getLeagueFixtures(leagueName: String, season: String): Flow<List<FixturesEntity>>

    /**
     * Get upcoming league fixtures
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE league = :leagueName 
        AND season = :season
        AND date(match_date) >= date('now')
        AND match_status = 'SCHEDULED'
        ORDER BY match_date ASC
    """)
    fun getUpcomingLeagueFixtures(leagueName: String, season: String): Flow<List<FixturesEntity>>

    /**
     * Get completed league fixtures
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE league = :leagueName 
        AND season = :season
        AND match_status = 'COMPLETED'
        ORDER BY position, match_date DESC
    """)
    fun getCompletedLeagueFixtures(leagueName: String, season: String): Flow<List<FixturesEntity>>

    /**
     * Get fixture by league and round
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE league = :leagueName 
        AND season = :season
        AND position = :gameWeek
        ORDER BY match_date
    """)
    fun getLeagueFixturesByRound(leagueName: String, season: String, gameWeek: Int): Flow<List<FixturesEntity>>

    // ============ CUP-BASED QUERIES ============

    /**
     * Get all fixtures for a cup in a season
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE cup_name = :cupName 
        AND season = :season
        ORDER BY 
            CASE 
                WHEN round = 'Final' THEN 1
                WHEN round = 'Semi-final' THEN 2
                WHEN round = 'Quarter-final' THEN 3
                ELSE 4
            END,
            match_date
    """)
    fun getCupFixtures(cupName: String, season: String): Flow<List<FixturesEntity>>

    /**
     * Get cup fixtures by round
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE cup_name = :cupName 
        AND season = :season
        AND round = :round
        ORDER BY match_date
    """)
    fun getCupFixturesByRound(cupName: String, season: String, round: String): Flow<List<FixturesEntity>>

    // ============ REFEREE-BASED QUERIES ============

    /**
     * Get fixtures officiated by a referee
     */
    @Query("SELECT * FROM fixtures WHERE referee_id = :refereeId ORDER BY match_date DESC")
    fun getFixturesByReferee(refereeId: Int): Flow<List<FixturesEntity>>

    /**
     * Get upcoming fixtures for a referee
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE referee_id = :refereeId
        AND date(match_date) >= date('now')
        AND match_status = 'SCHEDULED'
        ORDER BY match_date ASC
    """)
    fun getUpcomingFixturesByReferee(refereeId: Int): Flow<List<FixturesEntity>>

    // ============ STATUS-BASED QUERIES ============

    @Query("SELECT * FROM fixtures WHERE match_status = :status ORDER BY match_date")
    fun getFixturesByStatus(status: String): Flow<List<FixturesEntity>>

    @Query("SELECT * FROM fixtures WHERE match_status = 'LIVE'")
    fun getLiveFixtures(): Flow<List<FixturesEntity>>

    @Query("SELECT * FROM fixtures WHERE match_status = 'SCHEDULED' AND date(match_date) = date('now')")
    fun getTodaysFixtures(): Flow<List<FixturesEntity>>

    // ============ SEASON-BASED QUERIES ============

    @Query("SELECT DISTINCT season FROM fixtures ORDER BY season DESC")
    fun getSeasons(): Flow<List<String>>

    @Query("SELECT * FROM fixtures WHERE season = :season ORDER BY match_date")
    fun getFixturesBySeason(season: String): Flow<List<FixturesEntity>>

    // ============ TYPE-BASED QUERIES ============

    @Query("SELECT * FROM fixtures WHERE match_type = :matchType ORDER BY match_date DESC")
    fun getFixturesByType(matchType: String): Flow<List<FixturesEntity>>

    // ============ STATISTICS QUERIES ============

    /**
     * Get team's form (last 5 results)
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE (home_team = :teamName OR away_team = :teamName)
        AND match_status = 'COMPLETED'
        ORDER BY match_date DESC
        LIMIT 5
    """)
    suspend fun getTeamRecentForm(teamName: String): List<FixturesEntity>

    /**
     * Get team's home record
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE home_team = :teamName
        AND match_status = 'COMPLETED'
        ORDER BY match_date DESC
    """)
    fun getTeamHomeFixtures(teamName: String): Flow<List<FixturesEntity>>

    /**
     * Get team's away record
     */
    @Query("""
        SELECT * FROM fixtures 
        WHERE away_team = :teamName
        AND match_status = 'COMPLETED'
        ORDER BY match_date DESC
    """)
    fun getTeamAwayFixtures(teamName: String): Flow<List<FixturesEntity>>

    /**
     * Get total goals scored in a league season
     */
    @Query("""
        SELECT SUM(home_score + away_score) 
        FROM fixtures 
        WHERE league = :leagueName 
        AND season = :season
        AND match_status = 'COMPLETED'
    """)
    suspend fun getTotalGoalsInLeague(leagueName: String, season: String): Int?

    /**
     * Get average goals per game in a league season
     */
    @Query("""
        SELECT AVG(home_score + away_score) 
        FROM fixtures 
        WHERE league = :leagueName 
        AND season = :season
        AND match_status = 'COMPLETED'
    """)
    suspend fun getAverageGoalsPerGame(leagueName: String, season: String): Double?

    // ============ JOIN QUERIES ============

    /**
     * Get fixture with full team details
     */
    @Query("""
        SELECT 
            f.*,
            ht.name as home_team_name,
            ht.logo_path as home_team_logo,
            ht.league as home_team_league,
            at.name as away_team_name,
            at.logo_path as away_team_logo,
            at.league as away_team_league
        FROM fixtures f
        LEFT JOIN teams ht ON f.home_team = ht.name
        LEFT JOIN teams at ON f.away_team = at.name
        WHERE f.id = :fixtureId
    """)
    suspend fun getFixtureWithTeams(fixtureId: Int): FixtureWithTeams?

    /**
     * Get fixture with referee details
     */
    @Query("""
        SELECT 
            f.*,
            r.name as referee_name,
            r.strictness as referee_strictness,
            r.bias as referee_bias,
            r.rating as referee_rating,
            n.nationality as referee_nationality,
            n.fifa_code as referee_fifa_code
        FROM fixtures f
        LEFT JOIN referees r ON f.referee_id = r.referee_id
        LEFT JOIN nationalities n ON r.nationality_id = n.id
        WHERE f.id = :fixtureId
    """)
    suspend fun getFixtureWithReferee(fixtureId: Int): FixtureWithReferee?

    /**
     * Get fixture with competition details
     */
    @Query("""
        SELECT 
            f.*,
            l.name as league_name,
            l.level as league_level,
            l.country_id as league_country_id,
            c.name as cup_name,
            c.type as cup_type,
            c.country_id as cup_country_id
        FROM fixtures f
        LEFT JOIN leagues l ON f.league = l.name
        LEFT JOIN cups c ON f.cup_name = c.name
        WHERE f.id = :fixtureId
    """)
    suspend fun getFixtureWithCompetition(fixtureId: Int): FixtureWithCompetition?

    /**
     * Get complete fixture details (all joins)
     */
    @Query("""
        SELECT 
            f.*,
            ht.name as home_team_name,
            ht.logo_path as home_team_logo,
            ht.league as home_team_league,
            ht.elo_rating as home_team_elo,
            at.name as away_team_name,
            at.logo_path as away_team_logo,
            at.league as away_team_league,
            at.elo_rating as away_team_elo,
            r.name as referee_name,
            r.strictness as referee_strictness,
            r.bias as referee_bias,
            r.rating as referee_rating,
            n.nationality as referee_nationality,
            l.name as league_name,
            l.level as league_level,
            c.name as cup_competition_name,
            c.type as cup_competition_type
        FROM fixtures f
        LEFT JOIN teams ht ON f.home_team = ht.name
        LEFT JOIN teams at ON f.away_team = at.name
        LEFT JOIN referees r ON f.referee_id = r.referee_id
        LEFT JOIN nationalities n ON r.nationality_id = n.id
        LEFT JOIN leagues l ON f.league = l.name
        LEFT JOIN cups c ON f.cup_name = c.name
        WHERE f.id = :fixtureId
    """)
    suspend fun getCompleteFixtureDetails(fixtureId: Int): CompleteFixtureDetails?
}

// ============ DATA CLASSES FOR JOIN QUERIES ============

data class FixtureWithTeams(
    @Embedded
    val fixture: FixturesEntity,

    @ColumnInfo(name = "home_team_name")
    val homeTeamName: String,

    @ColumnInfo(name = "home_team_logo")
    val homeTeamLogo: String?,

    @ColumnInfo(name = "home_team_league")
    val homeTeamLeague: String?,

    @ColumnInfo(name = "away_team_name")
    val awayTeamName: String,

    @ColumnInfo(name = "away_team_logo")
    val awayTeamLogo: String?,

    @ColumnInfo(name = "away_team_league")
    val awayTeamLeague: String?
)

data class FixtureWithReferee(
    @Embedded
    val fixture: FixturesEntity,

    @ColumnInfo(name = "referee_name")
    val refereeName: String?,

    @ColumnInfo(name = "referee_strictness")
    val refereeStrictness: Int?,

    @ColumnInfo(name = "referee_bias")
    val refereeBias: Int?,

    @ColumnInfo(name = "referee_rating")
    val refereeRating: Int?,

    @ColumnInfo(name = "referee_nationality")
    val refereeNationality: String?,

    @ColumnInfo(name = "referee_fifa_code")
    val refereeFifaCode: String?
)

data class FixtureWithCompetition(
    @Embedded
    val fixture: FixturesEntity,

    @ColumnInfo(name = "league_name")
    val leagueName: String?,

    @ColumnInfo(name = "league_level")
    val leagueLevel: Int?,

    @ColumnInfo(name = "league_country_id")
    val leagueCountryId: Int?,

    @ColumnInfo(name = "cup_name")
    val cupCompetitionName: String?,

    @ColumnInfo(name = "cup_type")
    val cupCompetitionType: String?,

    @ColumnInfo(name = "cup_country_id")
    val cupCountryId: Int?
)

data class CompleteFixtureDetails(
    @Embedded
    val fixture: FixturesEntity,

    // Home Team
    @ColumnInfo(name = "home_team_name")
    val homeTeamName: String,

    @ColumnInfo(name = "home_team_logo")
    val homeTeamLogo: String?,

    @ColumnInfo(name = "home_team_league")
    val homeTeamLeague: String?,

    @ColumnInfo(name = "home_team_elo")
    val homeTeamElo: Int?,

    // Away Team
    @ColumnInfo(name = "away_team_name")
    val awayTeamName: String,

    @ColumnInfo(name = "away_team_logo")
    val awayTeamLogo: String?,

    @ColumnInfo(name = "away_team_league")
    val awayTeamLeague: String?,

    @ColumnInfo(name = "away_team_elo")
    val awayTeamElo: Int?,

    // Referee
    @ColumnInfo(name = "referee_name")
    val refereeName: String?,

    @ColumnInfo(name = "referee_strictness")
    val refereeStrictness: Int?,

    @ColumnInfo(name = "referee_bias")
    val refereeBias: Int?,

    @ColumnInfo(name = "referee_rating")
    val refereeRating: Int?,

    @ColumnInfo(name = "referee_nationality")
    val refereeNationality: String?,

    // Competition
    @ColumnInfo(name = "league_name")
    val leagueCompetitionName: String?,

    @ColumnInfo(name = "league_level")
    val leagueLevel: Int?,

    @ColumnInfo(name = "cup_competition_name")
    val cupCompetitionName: String?,

    @ColumnInfo(name = "cup_competition_type")
    val cupCompetitionType: String?
)