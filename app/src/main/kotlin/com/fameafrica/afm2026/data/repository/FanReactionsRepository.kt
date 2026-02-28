package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.FanReactionsDao
import com.fameafrica.afm2026.data.database.dao.ReactionTypeDistribution
import com.fameafrica.afm2026.data.database.dao.SentimentDistribution
import com.fameafrica.afm2026.data.database.dao.TeamReactionStatistics
import com.fameafrica.afm2026.data.database.entities.FanReactionsEntity
import com.fameafrica.afm2026.data.database.entities.FanSentiment
import com.fameafrica.afm2026.data.database.entities.FanReactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FanReactionsRepository @Inject constructor(
    private val fanReactionsDao: FanReactionsDao
) {

    // ============ BASIC CRUD ============

    fun getAllReactions(): Flow<List<FanReactionsEntity>> = fanReactionsDao.getAll()

    suspend fun getReactionById(id: Int): FanReactionsEntity? = fanReactionsDao.getById(id)

    suspend fun insertReaction(reaction: FanReactionsEntity) = fanReactionsDao.insert(reaction)

    suspend fun deleteReaction(reaction: FanReactionsEntity) = fanReactionsDao.delete(reaction)

    // ============ TEAM-BASED ============

    fun getReactionsByTeam(teamName: String): Flow<List<FanReactionsEntity>> =
        fanReactionsDao.getReactionsByTeam(teamName)

    fun getReactionsByTeamAndSentiment(teamName: String, sentiment: String): Flow<List<FanReactionsEntity>> =
        fanReactionsDao.getReactionsByTeamAndSentiment(teamName, sentiment)

    suspend fun getPositiveReactionCount(teamName: String): Int =
        fanReactionsDao.getPositiveReactionCount(teamName)

    suspend fun getNegativeReactionCount(teamName: String): Int =
        fanReactionsDao.getNegativeReactionCount(teamName)

    // ============ REACTION CREATION ============

    suspend fun addPositiveReaction(
        teamName: String,
        reaction: String = FanReactionType.CHEER.value
    ): FanReactionsEntity {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val reactionEntity = FanReactionsEntity(
            teamName = teamName,
            reaction = reaction,
            sentiment = FanSentiment.POSITIVE.value,
            timestamp = timestamp
        )

        fanReactionsDao.insert(reactionEntity)
        return reactionEntity
    }

    suspend fun addNegativeReaction(
        teamName: String,
        reaction: String = FanReactionType.PROTEST.value
    ): FanReactionsEntity {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val reactionEntity = FanReactionsEntity(
            teamName = teamName,
            reaction = reaction,
            sentiment = FanSentiment.NEGATIVE.value,
            timestamp = timestamp
        )

        fanReactionsDao.insert(reactionEntity)
        return reactionEntity
    }

    suspend fun addNeutralReaction(
        teamName: String,
        reaction: String = FanReactionType.CHANT.value
    ): FanReactionsEntity {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val reactionEntity = FanReactionsEntity(
            teamName = teamName,
            reaction = reaction,
            sentiment = FanSentiment.NEUTRAL.value,
            timestamp = timestamp
        )

        fanReactionsDao.insert(reactionEntity)
        return reactionEntity
    }

    // ============ REACTION GENERATION ============

    suspend fun generateReactionFromResult(
        teamName: String,
        isWin: Boolean,
        isDraw: Boolean,
        isLoss: Boolean,
        isUpset: Boolean = false
    ): FanReactionsEntity {
        return when {
            isWin && isUpset -> addPositiveReaction(teamName, "Euphoric Celebration")
            isWin -> addPositiveReaction(teamName, FanReactionType.CHEER.value)
            isDraw -> addNeutralReaction(teamName, FanReactionType.CHANT.value)
            isLoss && isUpset -> addNegativeReaction(teamName, "Angry Protest")
            isLoss -> addNegativeReaction(teamName, FanReactionType.PROTEST.value)
            else -> addNeutralReaction(teamName)
        }
    }

    suspend fun generateReactionFromBoardDecision(
        teamName: String,
        isPositive: Boolean
    ): FanReactionsEntity {
        return if (isPositive) {
            addPositiveReaction(teamName, "Board Approval")
        } else {
            addNegativeReaction(teamName, "Board Disapproval")
        }
    }

    // ============ STATISTICS ============

    fun getSentimentDistribution(): Flow<List<SentimentDistribution>> =
        fanReactionsDao.getSentimentDistribution()

    fun getTeamReactionStatistics(): Flow<List<TeamReactionStatistics>> =
        fanReactionsDao.getTeamReactionStatistics()

    fun getReactionTypeDistribution(): Flow<List<ReactionTypeDistribution>> =
        fanReactionsDao.getReactionTypeDistribution()

    // ============ CLEANUP ============

    suspend fun deleteOldReactions(daysToKeep: Int = 30) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysToKeep)
        val cutoffDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        fanReactionsDao.deleteOldReactions(cutoffDate)
    }

    // ============ DASHBOARD ============

    suspend fun getFanReactionsDashboard(teamName: String): FanReactionsDashboard {
        val allReactions = fanReactionsDao.getReactionsByTeam(teamName).firstOrNull() ?: emptyList()
        val positive = allReactions.count { it.sentiment == FanSentiment.POSITIVE.value }
        val negative = allReactions.count { it.sentiment == FanSentiment.NEGATIVE.value }
        val neutral = allReactions.count { it.sentiment == FanSentiment.NEUTRAL.value }

        val recentReactions = allReactions.take(10)

        val sentimentScore = if (allReactions.isNotEmpty()) {
            ((positive - negative).toDouble() / allReactions.size * 100)
        } else 0.0

        return FanReactionsDashboard(
            teamName = teamName,
            totalReactions = allReactions.size,
            positiveReactions = positive,
            negativeReactions = negative,
            neutralReactions = neutral,
            sentimentScore = sentimentScore,
            recentReactions = recentReactions
        )
    }
}

// ============ DATA CLASSES ============

data class FanReactionsDashboard(
    val teamName: String,
    val totalReactions: Int,
    val positiveReactions: Int,
    val negativeReactions: Int,
    val neutralReactions: Int,
    val sentimentScore: Double,
    val recentReactions: List<FanReactionsEntity>
)