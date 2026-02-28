package com.fameafrica.afm2026.data.database.dao

import androidx.room.*
import com.fameafrica.afm2026.data.database.entities.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    // ============ BASIC CRUD ============

    @Query("SELECT * FROM news ORDER BY timestamp DESC")
    fun getAll(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE id = :id")
    suspend fun getById(id: Int): NewsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(news: NewsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(newsList: List<NewsEntity>)

    @Update
    suspend fun update(news: NewsEntity)

    @Delete
    suspend fun delete(news: NewsEntity)

    @Query("DELETE FROM news WHERE timestamp < :cutoffDate")
    suspend fun deleteOldNews(cutoffDate: String)

    // ============ CATEGORY QUERIES ============

    @Query("SELECT * FROM news WHERE category = :category ORDER BY timestamp DESC")
    fun getByCategory(category: String): Flow<List<NewsEntity>>

    // ============ TOP NEWS QUERIES ============

    @Query("SELECT * FROM news WHERE is_top_news = 1 ORDER BY timestamp DESC LIMIT :limit")
    fun getTopNews(limit: Int): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE is_top_news = 1 AND category = :category ORDER BY timestamp DESC")
    fun getTopNewsByCategory(category: String): Flow<List<NewsEntity>>

    // ============ JOURNALIST QUERIES ============

    @Query("SELECT * FROM news WHERE journalist_name = :journalistName ORDER BY timestamp DESC")
    fun getByJournalist(journalistName: String): Flow<List<NewsEntity>>

    // ============ RELATED ENTITY QUERIES ============

    @Query("SELECT * FROM news WHERE related_team = :teamName ORDER BY timestamp DESC")
    fun getNewsByTeam(teamName: String): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE related_player = :playerName ORDER BY timestamp DESC")
    fun getNewsByPlayer(playerName: String): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE related_manager = :managerName ORDER BY timestamp DESC")
    fun getNewsByManager(managerName: String): Flow<List<NewsEntity>>

    // ============ SEARCH QUERIES ============

    @Query("SELECT * FROM news WHERE headline LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%' ORDER BY timestamp DESC")
    fun searchNews(searchQuery: String): Flow<List<NewsEntity>>

    // ============ STATISTICS QUERIES ============

    @Query("""
        SELECT 
            category,
            COUNT(*) as count
        FROM news 
        GROUP BY category
        ORDER BY count DESC
    """)
    fun getNewsCategoryDistribution(): Flow<List<NewsCategoryDistribution>>
}

// ============ DATA CLASSES ============

data class NewsCategoryDistribution(
    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "count")
    val count: Int
)