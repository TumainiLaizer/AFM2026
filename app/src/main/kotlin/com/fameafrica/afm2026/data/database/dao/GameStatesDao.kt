package com.fameafrica.afm2026.data.database.dao

import androidx.room.*
import com.fameafrica.afm2026.data.database.entities.GameStatesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameStatesDao {

    // ============ BASIC CRUD ============

    @Query("SELECT * FROM game_states ORDER BY last_played DESC")
    fun getAll(): Flow<List<GameStatesEntity>>

    @Query("SELECT * FROM game_states WHERE id = :id")
    suspend fun getById(id: Int): GameStatesEntity?

    @Query("SELECT * FROM game_states WHERE manager_id = :managerId")
    suspend fun getByManagerId(managerId: Int): GameStatesEntity?

    @Query("SELECT * FROM game_states WHERE is_valid = 1 ORDER BY last_played DESC")
    fun getValidSaveGames(): Flow<List<GameStatesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gameState: GameStatesEntity)

    @Update
    suspend fun update(gameState: GameStatesEntity)

    @Delete
    suspend fun delete(gameState: GameStatesEntity)

    @Query("DELETE FROM game_states WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM game_states")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM game_states WHERE is_valid = 1")
    suspend fun getValidSaveCount(): Int

    // ============ TEAM-BASED QUERIES ============

    @Query("SELECT * FROM game_states WHERE team_id = :teamId ORDER BY last_played DESC")
    fun getSaveGamesForTeam(teamId: Int): Flow<List<GameStatesEntity>>

    @Query("SELECT * FROM game_states WHERE team_name = :teamName ORDER BY last_played DESC")
    fun getSaveGamesForTeamName(teamName: String): Flow<List<GameStatesEntity>>

    // ============ SEASON-BASED QUERIES ============

    @Query("SELECT * FROM game_states WHERE season = :season ORDER BY last_played DESC")
    fun getSaveGamesForSeason(season: String): Flow<List<GameStatesEntity>>

    @Query("SELECT DISTINCT season FROM game_states ORDER BY season DESC")
    fun getSeasonsWithSaves(): Flow<List<String>>

    // ============ VALIDITY MANAGEMENT ============

    @Query("UPDATE game_states SET is_valid = 0 WHERE id = :id")
    suspend fun invalidateSave(id: Int)

    @Query("UPDATE game_states SET is_valid = 1 WHERE id = :id")
    suspend fun validateSave(id: Int)

    @Query("UPDATE game_states SET last_played = :lastPlayed WHERE id = :id")
    suspend fun updateLastPlayed(id: Int, lastPlayed: String)

    // ============ SEARCH ============

    @Query("SELECT * FROM game_states WHERE name LIKE '%' || :searchQuery || '%' ORDER BY last_played DESC")
    fun searchSaveGames(searchQuery: String): Flow<List<GameStatesEntity>>

    // ============ STATISTICS ============

    @Query("SELECT COUNT(*) FROM game_states")
    suspend fun getTotalSaveCount(): Int

    @Query("SELECT AVG(week) FROM game_states WHERE is_valid = 1")
    suspend fun getAverageWeek(): Double?

    @Query("SELECT MAX(week) FROM game_states WHERE is_valid = 1")
    suspend fun getMaxWeek(): Int?
    fun insertAll(saves: List<GameStatesEntity>)
}

// ============ DATA CLASSES ============

data class SaveGameSummary(
    val totalSaves: Int,
    val validSaves: Int,
    val latestSave: GameStatesEntity?,
    val oldestSave: GameStatesEntity?,
    val averageWeek: Double
)