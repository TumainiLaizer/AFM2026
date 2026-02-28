package com.fameafrica.afm2026.data.database.dao

import androidx.room.*
import com.fameafrica.afm2026.data.database.entities.PlayerReactionsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerReactionsDao {

    // ============ BASIC CRUD ============

    @Query("SELECT * FROM player_reactions ORDER BY id DESC")
    fun getAll(): Flow<List<PlayerReactionsEntity>>

    @Query("SELECT * FROM player_reactions WHERE id = :id")
    suspend fun getById(id: Int): PlayerReactionsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reaction: PlayerReactionsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reactions: List<PlayerReactionsEntity>)

    @Update
    suspend fun update(reaction: PlayerReactionsEntity)

    @Delete
    suspend fun delete(reaction: PlayerReactionsEntity)

    @Query("DELETE FROM player_reactions")
    suspend fun deleteAll()

    // ============ PLAYER-BASED QUERIES ============

    @Query("SELECT * FROM player_reactions WHERE player_name = :playerName ORDER BY id DESC")
    fun getReactionsByPlayer(playerName: String): Flow<List<PlayerReactionsEntity>>

    @Query("SELECT * FROM player_reactions WHERE player_name = :playerName AND reaction_type = :reactionType ORDER BY id DESC")
    fun getReactionsByPlayerAndType(playerName: String, reactionType: String): Flow<List<PlayerReactionsEntity>>

    @Query("SELECT COUNT(*) FROM player_reactions WHERE player_name = :playerName AND reaction_type IN ('Happy', 'Excited', 'Proud')")
    suspend fun getPositiveReactionCount(playerName: String): Int

    @Query("SELECT COUNT(*) FROM player_reactions WHERE player_name = :playerName AND reaction_type IN ('Angry', 'Frustrated', 'Disappointed', 'Sad')")
    suspend fun getNegativeReactionCount(playerName: String): Int

    // ============ TYPE-BASED QUERIES ============

    @Query("SELECT * FROM player_reactions WHERE reaction_type = :reactionType ORDER BY id DESC")
    fun getReactionsByType(reactionType: String): Flow<List<PlayerReactionsEntity>>

    @Query("SELECT DISTINCT player_name FROM player_reactions WHERE reaction_type = :reactionType")
    fun getPlayersByReactionType(reactionType: String): Flow<List<String>>

    // ============ STATISTICS QUERIES ============

    @Query("""
        SELECT 
            reaction_type,
            COUNT(*) as count
        FROM player_reactions 
        GROUP BY reaction_type
        ORDER BY count DESC
    """)
    fun getReactionTypeDistribution(): Flow<List<PlayerReactionDistribution>>

    @Query("""
        SELECT 
            player_name,
            COUNT(*) as reaction_count,
            COUNT(CASE WHEN reaction_type IN ('Happy', 'Excited', 'Proud') THEN 1 END) as positive_count,
            COUNT(CASE WHEN reaction_type IN ('Angry', 'Frustrated', 'Disappointed', 'Sad') THEN 1 END) as negative_count
        FROM player_reactions 
        GROUP BY player_name
        ORDER BY reaction_count DESC
        LIMIT :limit
    """)
    fun getMostReactivePlayers(limit: Int): Flow<List<PlayerReactivityStats>>
}

// ============ DATA CLASSES ============

data class PlayerReactionDistribution(
    @ColumnInfo(name = "reaction_type")
    val reactionType: String,

    @ColumnInfo(name = "count")
    val count: Int
)

data class PlayerReactivityStats(
    @ColumnInfo(name = "player_name")
    val playerName: String,

    @ColumnInfo(name = "reaction_count")
    val reactionCount: Int,

    @ColumnInfo(name = "positive_count")
    val positiveCount: Int,

    @ColumnInfo(name = "negative_count")
    val negativeCount: Int
)