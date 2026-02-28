package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.NewsCategoryDistribution
import com.fameafrica.afm2026.data.database.dao.NewsDao
import com.fameafrica.afm2026.data.database.entities.NewsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val newsDao: NewsDao
) {

    // ============ BASIC CRUD ============

    fun getAllNews(): Flow<List<NewsEntity>> = newsDao.getAll()

    suspend fun getNewsById(id: Int): NewsEntity? = newsDao.getById(id)

    suspend fun insertNews(news: NewsEntity) = newsDao.insert(news)

    suspend fun updateNews(news: NewsEntity) = newsDao.update(news)

    suspend fun deleteNews(news: NewsEntity) = newsDao.delete(news)

    // ============ CATEGORY-BASED ============

    fun getNewsByCategory(category: String): Flow<List<NewsEntity>> =
        newsDao.getByCategory(category)

    fun getTransferNews(): Flow<List<NewsEntity>> =
        newsDao.getByCategory("TRANSFER")

    fun getMatchNews(): Flow<List<NewsEntity>> =
        newsDao.getByCategory("MATCH")

    fun getInterviewNews(): Flow<List<NewsEntity>> =
        newsDao.getByCategory("INTERVIEW")

    // ============ TOP NEWS ============

    fun getTopNews(limit: Int = 5): Flow<List<NewsEntity>> =
        newsDao.getTopNews(limit)

    // ============ RELATED ENTITIES ============

    fun getNewsByTeam(teamName: String): Flow<List<NewsEntity>> =
        newsDao.getNewsByTeam(teamName)

    fun getNewsByPlayer(playerName: String): Flow<List<NewsEntity>> =
        newsDao.getNewsByPlayer(playerName)

    fun getNewsByManager(managerName: String): Flow<List<NewsEntity>> =
        newsDao.getNewsByManager(managerName)

    // ============ NEWS CREATION ============

    suspend fun createNewsArticle(
        headline: String,
        content: String,
        category: String,
        journalistName: String? = null,
        journalistLogo: String? = null,
        relatedTeam: String? = null,
        relatedPlayer: String? = null,
        relatedManager: String? = null,
        imageUrl: String? = null,
        isTopNews: Boolean = false
    ): NewsEntity {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())

        val news = NewsEntity(
            headline = headline,
            content = content,
            category = category,
            journalistName = journalistName,
            journalistLogo = journalistLogo,
            timestamp = timestamp,
            isTopNews = if (isTopNews) 1 else 0,
            relatedTeam = relatedTeam,
            relatedPlayer = relatedPlayer,
            relatedManager = relatedManager,
            imageUrl = imageUrl
        )

        newsDao.insert(news)
        return news
    }

    suspend fun createTransferRumor(
        playerName: String,
        fromTeam: String,
        toTeam: String,
        fee: Int? = null,
        journalistName: String? = null
    ): NewsEntity {
        val headline = if (fee != null) {
            "EXCLUSIVE: $playerName set for $toTeam move in ${fee / 1_000_000}M deal"
        } else {
            "RUMOR: $playerName linked with move to $toTeam"
        }

        val content = buildString {
            appendLine("Sources indicate that $playerName could be on his way to $toTeam.")
            if (fee != null) {
                appendLine("The deal is reportedly worth ${fee / 1_000_000}M.")
            }
            appendLine("${fromTeam} may be willing to negotiate if the right offer comes in.")
        }

        return createNewsArticle(
            headline = headline,
            content = content,
            category = "TRANSFER",
            journalistName = journalistName,
            relatedPlayer = playerName,
            relatedTeam = fromTeam,
            isTopNews = fee != null && fee >= 10_000_000
        )
    }

    suspend fun createMatchReport(
        homeTeam: String,
        awayTeam: String,
        homeScore: Int,
        awayScore: Int,
        isUpset: Boolean = false,
        journalistName: String? = null
    ): NewsEntity {
        val winner = if (homeScore > awayScore) homeTeam else awayTeam
        val headline = if (isUpset) {
            "MAJOR UPSET: $homeTeam $homeScore-$awayScore $awayTeam - Fans in shock!"
        } else {
            "$homeTeam $homeScore-$awayScore $awayTeam: ${winner} claims victory"
        }

        val content = "$homeTeam hosted $awayTeam in a thrilling encounter that ended $homeScore-$awayScore."

        return createNewsArticle(
            headline = headline,
            content = content,
            category = "MATCH",
            journalistName = journalistName,
            relatedTeam = homeTeam,
            isTopNews = isUpset
        )
    }

    // ============ STATISTICS ============

    fun getNewsCategoryDistribution(): Flow<List<NewsCategoryDistribution>> =
        newsDao.getNewsCategoryDistribution()

    // ============ CLEANUP ============

    suspend fun deleteOldNews(daysToKeep: Int = 30) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysToKeep)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cutoffDate = dateFormat.format(calendar.time)
        newsDao.deleteOldNews(cutoffDate)
    }

    // ============ DASHBOARD ============

    suspend fun getNewsDashboard(): NewsDashboard {
        val allNews = newsDao.getAll().firstOrNull() ?: emptyList()
        val topNews = newsDao.getTopNews(5).firstOrNull() ?: emptyList()
        val transferNews = allNews.filter { it.category == "TRANSFER" }
        val matchNews = allNews.filter { it.category == "MATCH" }
        val interviewNews = allNews.filter { it.category == "INTERVIEW" }

        return NewsDashboard(
            totalArticles = allNews.size,
            topNews = topNews,
            transferNews = transferNews.take(5),
            matchNews = matchNews.take(5),
            interviewNews = interviewNews.take(5),
            categoryDistribution = getNewsCategoryDistribution().firstOrNull() ?: emptyList()
        )
    }
}

// ============ DATA CLASSES ============

data class NewsDashboard(
    val totalArticles: Int,
    val topNews: List<NewsEntity>,
    val transferNews: List<NewsEntity>,
    val matchNews: List<NewsEntity>,
    val interviewNews: List<NewsEntity>,
    val categoryDistribution: List<NewsCategoryDistribution>
)