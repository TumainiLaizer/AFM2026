package com.fameafrica.afm2026.data.database.dao

import androidx.room.*
import com.fameafrica.afm2026.data.database.entities.PreseasonScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreseasonScheduleDao {

    // ============ BASIC CRUD ============

    @Query("SELECT * FROM preseason_schedule ORDER BY match_date ASC")
    fun getAll(): Flow<List<PreseasonScheduleEntity>>

    @Query("SELECT * FROM preseason_schedule WHERE id = :id")
    suspend fun getById(id: Int): PreseasonScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(match: PreseasonScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(matches: List<PreseasonScheduleEntity>)

    @Update
    suspend fun update(match: PreseasonScheduleEntity)

    @Delete
    suspend fun delete(match: PreseasonScheduleEntity)

    @Query("DELETE FROM preseason_schedule WHERE season = :season")
    suspend fun deleteBySeason(season: String)

    // ============ TEAM-BASED QUERIES ============

    @Query("SELECT * FROM preseason_schedule WHERE team_name = :teamName AND season = :season ORDER BY match_date ASC")
    fun getTeamPreseasonSchedule(teamName: String, season: String): Flow<List<PreseasonScheduleEntity>>

    @Query("SELECT * FROM preseason_schedule WHERE team_name = :teamName AND season = :season AND status = 'Scheduled' ORDER BY match_date ASC")
    fun getUpcomingPreseasonMatches(teamName: String, season: String): Flow<List<PreseasonScheduleEntity>>

    @Query("SELECT * FROM preseason_schedule WHERE team_name = :teamName AND season = :season AND status = 'Completed' ORDER BY match_date DESC")
    fun getCompletedPreseasonMatches(teamName: String, season: String): Flow<List<PreseasonScheduleEntity>>

    // ============ USER TEAM QUERIES ============

    @Query("SELECT * FROM preseason_schedule WHERE is_user_team = 1 AND season = :season ORDER BY match_date ASC")
    fun getUserPreseasonSchedule(season: String): Flow<List<PreseasonScheduleEntity>>

    // ============ OPPONENT QUERIES ============

    @Query("SELECT DISTINCT opponent FROM preseason_schedule WHERE team_name = :teamName AND season = :season")
    fun getPreseasonOpponents(teamName: String, season: String): Flow<List<String>>

    // ============ SEASON QUERIES ============

    @Query("SELECT DISTINCT season FROM preseason_schedule ORDER BY season DESC")
    fun getSeasons(): Flow<List<String>>

    // ============ STATISTICS QUERIES ============

    @Query("""
        SELECT 
            COUNT(*) as total_matches,
            COUNT(CASE WHEN home_score > opponent_score THEN 1 END) as wins,
            COUNT(CASE WHEN home_score < opponent_score THEN 1 END) as losses,
            COUNT(CASE WHEN home_score = opponent_score THEN 1 END) as draws
        FROM preseason_schedule 
        WHERE team_name = :teamName AND season = :season AND status = 'Completed'
    """)
    suspend fun getPreseasonStats(teamName: String, season: String): PreseasonStats?
}

// ============ DATA CLASSES ============

data class PreseasonStats(
    @ColumnInfo(name = "total_matches")
    val totalMatches: Int,

    @ColumnInfo(name = "wins")
    val wins: Int,

    @ColumnInfo(name = "losses")
    val losses: Int,

    @ColumnInfo(name = "draws")
    val draws: Int
)