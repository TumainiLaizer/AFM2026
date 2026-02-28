package com.fameafrica.afm2026.data.database.dao

import androidx.room.*
import com.fameafrica.afm2026.data.database.entities.FanReactionsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FanReactionsDao {

    // ============ BASIC CRUD ============

    @Query("SELECT * FROM fan_reactions ORDER BY timestamp DESC")
    fun getAll(): Flow<List<FanReactionsEntity>>

    @Query("SELECT * FROM fan_reactions WHERE id = :id")
    suspend fun getById(id: Int): FanReactionsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reaction: FanReactionsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reactions: List<FanReactionsEntity>)

    @Update
    suspend fun update(reaction: FanReactionsEntity)

    @Delete
    suspend fun delete(reaction: FanReactionsEntity)

    @Query("DELETE FROM fan_reactions WHERE timestamp < :cutoffDate")
    suspend fun deleteOldReactions(cutoffDate: String)

    @Query("DELETE FROM fan_reactions")
    suspend fun deleteAll()

    // ============ TEAM-BASED QUERIES ============

    @Query("SELECT * FROM fan_reactions WHERE team_name = :teamName ORDER BY timestamp DESC")
    fun getReactionsByTeam(teamName: String): Flow<List<FanReactionsEntity>>

    @Query("SELECT * FROM fan_reactions WHERE team_name = :teamName AND sentiment = :sentiment ORDER BY timestamp DESC")
    fun getReactionsByTeamAndSentiment(teamName: String, sentiment: String): Flow<List<FanReactionsEntity>>

    @Query("SELECT COUNT(*) FROM fan_reactions WHERE team_name = :teamName AND sentiment = 'Positive'")
    suspend fun getPositiveReactionCount(teamName: String): Int

    @Query("SELECT COUNT(*) FROM fan_reactions WHERE team_name = :teamName AND sentiment = 'Negative'")
    suspend fun getNegativeReactionCount(teamName: String): Int

    // ============ SENTIMENT QUERIES ============

    @Query("SELECT * FROM fan_reactions WHERE sentiment = :sentiment ORDER BY timestamp DESC")
    fun getReactionsBySentiment(sentiment: String): Flow<List<FanReactionsEntity>>

    @Query("""
        SELECT 
            sentiment,
            COUNT(*) as count
        FROM fan_reactions 
        GROUP BY sentiment
    """)
    fun getSentimentDistribution(): Flow<List<SentimentDistribution>>

    // ============ STATISTICS QUERIES ============

    @Query("""
        SELECT 
            team_name,
            COUNT(*) as reaction_count,
            COUNT(CASE WHEN sentiment = 'Positive' THEN 1 END) as positive_count,
            COUNT(CASE WHEN sentiment = 'Negative' THEN 1 END) as negative_count,
            COUNT(CASE WHEN sentiment = 'Neutral' THEN 1 END) as neutral_count
        FROM fan_reactions 
        GROUP BY team_name
        ORDER BY reaction_count DESC
    """)
    fun getTeamReactionStatistics(): Flow<List<TeamReactionStatistics>>

    @Query("""
        SELECT 
            reaction,
            COUNT(*) as count
        FROM fan_reactions 
        GROUP BY reaction
        ORDER BY count DESC
    """)
    fun getReactionTypeDistribution(): Flow<List<ReactionTypeDistribution>>
}

// ============ DATA CLASSES ============

data class SentimentDistribution(
    @ColumnInfo(name = "sentiment")
    val sentiment: String,

    @ColumnInfo(name = "count")
    val count: Int
)

data class TeamReactionStatistics(
    @ColumnInfo(name = "team_name")
    val teamName: String,

    @ColumnInfo(name = "reaction_count")
    val reactionCount: Int,

    @ColumnInfo(name = "positive_count")
    val positiveCount: Int,

    @ColumnInfo(name = "negative_count")
    val negativeCount: Int,

    @ColumnInfo(name = "neutral_count")
    val neutralCount: Int
)

data class ReactionTypeDistribution(
    @ColumnInfo(name = "reaction")
    val reaction: String,

    @ColumnInfo(name = "count")
    val count: Int
)