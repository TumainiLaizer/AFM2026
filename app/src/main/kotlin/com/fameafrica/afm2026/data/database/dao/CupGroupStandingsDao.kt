package com.fameafrica.afm2026.data.database.dao

import androidx.room.*
import com.fameafrica.afm2026.data.database.entities.CupGroupStandingsEntity
import com.fameafrica.afm2026.data.database.entities.TeamsEntity
import com.fameafrica.afm2026.data.database.entities.CupsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CupGroupStandingsDao {

    // ============ BASIC CRUD ============

    @Query("SELECT * FROM cup_group_standings")
    fun getAll(): Flow<List<CupGroupStandingsEntity>>

    @Query("SELECT * FROM cup_group_standings WHERE id = :id")
    suspend fun getById(id: Int): CupGroupStandingsEntity?

    @Query("SELECT * FROM cup_group_standings WHERE team_name = :teamName AND cup_name = :cupName AND season_year = :seasonYear")
    suspend fun getTeamStanding(teamName: String, cupName: String, seasonYear: Int): CupGroupStandingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(standing: CupGroupStandingsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(standings: List<CupGroupStandingsEntity>)

    @Update
    suspend fun update(standing: CupGroupStandingsEntity)

    @Delete
    suspend fun delete(standing: CupGroupStandingsEntity)

    @Query("DELETE FROM cup_group_standings WHERE cup_name = :cupName AND season_year = :seasonYear")
    suspend fun deleteByCupAndSeason(cupName: String, seasonYear: Int)

    @Query("DELETE FROM cup_group_standings")
    suspend fun deleteAll()

    // ============ GROUP STANDINGS QUERIES ============

    @Query("""
        SELECT * FROM cup_group_standings 
        WHERE cup_name = :cupName AND season_year = :seasonYear 
        ORDER BY points DESC, goal_difference DESC, goals_scored DESC
    """)
    fun getGroupStandings(cupName: String, seasonYear: Int): Flow<List<CupGroupStandingsEntity>>

    @Query("""
        SELECT * FROM cup_group_standings 
        WHERE cup_name = :cupName AND season_year = :seasonYear 
        ORDER BY position ASC
    """)
    fun getStandingsByPosition(cupName: String, seasonYear: Int): Flow<List<CupGroupStandingsEntity>>

    @Query("""
        SELECT * FROM cup_group_standings 
        WHERE cup_name = :cupName AND season_year = :seasonYear 
        AND position <= 2
        ORDER BY position ASC
    """)
    fun getQualifiedTeams(cupName: String, seasonYear: Int): Flow<List<CupGroupStandingsEntity>>

    @Query("""
        SELECT * FROM cup_group_standings 
        WHERE cup_name = :cupName AND season_year = :seasonYear 
        AND position = 1
    """)
    suspend fun getGroupWinner(cupName: String, seasonYear: Int): CupGroupStandingsEntity?

    @Query("""
        SELECT * FROM cup_group_standings 
        WHERE cup_name = :cupName AND season_year = :seasonYear 
        AND team_name = :teamName
    """)
    suspend fun getTeamPosition(cupName: String, seasonYear: Int, teamName: String): CupGroupStandingsEntity?

    // ============ TEAM HISTORY QUERIES ============

    @Query("""
        SELECT * FROM cup_group_standings 
        WHERE team_name = :teamName 
        ORDER BY season_year DESC
    """)
    fun getTeamCupHistory(teamName: String): Flow<List<CupGroupStandingsEntity>>

    @Query("""
        SELECT * FROM cup_group_standings 
        WHERE team_name = :teamName AND position = 1
        ORDER BY season_year DESC
    """)
    fun getTeamGroupWins(teamName: String): Flow<List<CupGroupStandingsEntity>>

    // ============ STATISTICS QUERIES ============

    @Query("""
        SELECT 
            AVG(points) as avg_points,
            AVG(goals_scored) as avg_goals_for,
            AVG(goals_conceded) as avg_goals_against
        FROM cup_group_standings 
        WHERE cup_name = :cupName AND season_year = :seasonYear
    """)
    suspend fun getCupGroupStatistics(cupName: String, seasonYear: Int): CupGroupStatistics?

    @Query("""
        SELECT 
            team_name,
            COUNT(CASE WHEN position = 1 THEN 1 END) as group_wins
        FROM cup_group_standings 
        WHERE cup_name = :cupName
        GROUP BY team_name
        ORDER BY group_wins DESC
    """)
    fun getMostGroupWins(cupName: String): Flow<List<GroupWinsStats>>

    // ============ JOIN QUERIES ============

    @Query("""
        SELECT 
            cgs.*,
            t.logo_path as team_logo,
            t.home_stadium as team_stadium,
            t.elo_rating as team_elo,
            c.type as cup_type,
            c.country as cup_country
        FROM cup_group_standings cgs
        LEFT JOIN teams t ON cgs.team_name = t.name
        LEFT JOIN cups c ON cgs.cup_name = c.name
        WHERE cgs.cup_name = :cupName AND cgs.season_year = :seasonYear
        ORDER BY cgs.position ASC
    """)
    fun getFullGroupStandings(cupName: String, seasonYear: Int): Flow<List<FullGroupStandingEntry>>
}

// ============ DATA CLASSES ============

data class CupGroupStatistics(
    @ColumnInfo(name = "avg_points")
    val averagePoints: Double,

    @ColumnInfo(name = "avg_goals_for")
    val averageGoalsFor: Double,

    @ColumnInfo(name = "avg_goals_against")
    val averageGoalsAgainst: Double
)

data class GroupWinsStats(
    @ColumnInfo(name = "team_name")
    val teamName: String,

    @ColumnInfo(name = "group_wins")
    val groupWins: Int
)

data class FullGroupStandingEntry(
    @Embedded
    val standing: CupGroupStandingsEntity,

    @ColumnInfo(name = "team_logo")
    val teamLogo: String?,

    @ColumnInfo(name = "team_stadium")
    val teamStadium: String?,

    @ColumnInfo(name = "team_elo")
    val teamElo: Int?,

    @ColumnInfo(name = "cup_type")
    val cupType: String?,

    @ColumnInfo(name = "cup_country")
    val cupCountry: String?
)