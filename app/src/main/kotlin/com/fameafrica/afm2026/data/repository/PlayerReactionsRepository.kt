package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.PlayerReactionDistribution
import com.fameafrica.afm2026.data.database.dao.PlayerReactionsDao
import com.fameafrica.afm2026.data.database.dao.PlayerReactivityStats
import com.fameafrica.afm2026.data.database.entities.PlayerReactionsEntity
import com.fameafrica.afm2026.data.database.entities.PlayerReactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerReactionsRepository @Inject constructor(
    private val playerReactionsDao: PlayerReactionsDao
) {

    // ============ BASIC CRUD ============

    fun getAllReactions(): Flow<List<PlayerReactionsEntity>> = playerReactionsDao.getAll()

    suspend fun getReactionById(id: Int): PlayerReactionsEntity? = playerReactionsDao.getById(id)

    suspend fun insertReaction(reaction: PlayerReactionsEntity) = playerReactionsDao.insert(reaction)

    suspend fun deleteReaction(reaction: PlayerReactionsEntity) = playerReactionsDao.delete(reaction)

    // ============ PLAYER-BASED ============

    fun getReactionsByPlayer(playerName: String): Flow<List<PlayerReactionsEntity>> =
        playerReactionsDao.getReactionsByPlayer(playerName)

    suspend fun getPositiveReactionCount(playerName: String): Int =
        playerReactionsDao.getPositiveReactionCount(playerName)

    suspend fun getNegativeReactionCount(playerName: String): Int =
        playerReactionsDao.getNegativeReactionCount(playerName)

    // ============ REACTION CREATION ============

    suspend fun addPlayerReaction(
        playerName: String,
        reactionType: String,
        reactionText: String
    ): PlayerReactionsEntity {
        val reaction = PlayerReactionsEntity(
            playerName = playerName,
            reactionType = reactionType,
            reactionText = reactionText
        )
        playerReactionsDao.insert(reaction)
        return reaction
    }

    suspend fun addHappyReaction(playerName: String, context: String): PlayerReactionsEntity {
        val text = when (context) {
            "GOAL" -> "I'm thrilled to score for the team!"
            "WIN" -> "Great team performance today!"
            "CONTRACT" -> "Happy to commit my future to the club!"
            "AWARD" -> "Honored to receive this recognition!"
            else -> "I'm feeling happy with how things are going!"
        }
        return addPlayerReaction(playerName, PlayerReactionType.HAPPY.value, text)
    }

    suspend fun addExcitedReaction(playerName: String, context: String): PlayerReactionsEntity {
        val text = when (context) {
            "NEW_SIGNING" -> "Can't wait to get started with my new teammates!"
            "DERBY" -> "This is what I live for! Bring on the derby!"
            "FINAL" -> "We're in the final! Let's bring the trophy home!"
            else -> "I'm excited about what's ahead for us!"
        }
        return addPlayerReaction(playerName, PlayerReactionType.EXCITED.value, text)
    }

    suspend fun addAngryReaction(playerName: String, context: String): PlayerReactionsEntity {
        val text = when (context) {
            "RED_CARD" -> "That decision was absolutely ridiculous!"
            "LOSS" -> "We should have won that game. Unacceptable."
            "BENCHED" -> "I need to prove myself and get back in the starting XI."
            else -> "I'm not happy with how things are going."
        }
        return addPlayerReaction(playerName, PlayerReactionType.ANGRY.value, text)
    }

    suspend fun addFrustratedReaction(playerName: String, context: String): PlayerReactionsEntity {
        val text = when (context) {
            "INJURY" -> "So frustrating to be sidelined when I just found my form."
            "MISSED_CHANCE" -> "Should have scored that. I know I'm better than this."
            "TRANSFER_BLOCKED" -> "I was hoping for a move, but the club rejected the offer."
            else -> "I feel frustrated with my recent performances."
        }
        return addPlayerReaction(playerName, PlayerReactionType.FRUSTRATED.value, text)
    }

    suspend fun addDisappointedReaction(playerName: String, context: String): PlayerReactionsEntity {
        val text = when (context) {
            "RELEGATION" -> "We let the fans down. This hurts."
            "ELIMINATION" -> "Out of the cup. We should have gone further."
            "POOR_FORM" -> "I know I can do better. Need to work harder."
            else -> "Disappointed with the result today."
        }
        return addPlayerReaction(playerName, PlayerReactionType.DISAPPOINTED.value, text)
    }

    suspend fun addProudReaction(playerName: String, context: String): PlayerReactionsEntity {
        val text = when (context) {
            "DEBUT" -> "Dream come true to make my debut for this club!"
            "CAPTAINCY" -> "Honored to lead this team. I won't let you down."
            "TROPHY" -> "Champions! So proud of every single player in this squad."
            "RECORD" -> "To break a club record is something I'll never forget."
            else -> "So proud of what we've achieved together."
        }
        return addPlayerReaction(playerName, PlayerReactionType.PROUD.value, text)
    }

    suspend fun addThoughtfulReaction(playerName: String, context: String): PlayerReactionsEntity {
        val text = when (context) {
            "FUTURE" -> "I'm focused on the next game. That's all that matters."
            "TACTICS" -> "We're trying a new system and it's starting to click."
            "YOUNG_PLAYER" -> "I remember being in his position. Happy to help him develop."
            else -> "We need to analyze what went wrong and improve."
        }
        return addPlayerReaction(playerName, PlayerReactionType.THOUGHTFUL.value, text)
    }

    // ============ STATISTICS ============

    fun getReactionTypeDistribution(): Flow<List<PlayerReactionDistribution>> =
        playerReactionsDao.getReactionTypeDistribution()

    fun getMostReactivePlayers(limit: Int): Flow<List<PlayerReactivityStats>> =
        playerReactionsDao.getMostReactivePlayers(limit)

    // ============ DASHBOARD ============

    suspend fun getPlayerReactionsDashboard(playerName: String): PlayerReactionsDashboard {
        val reactions = playerReactionsDao.getReactionsByPlayer(playerName).firstOrNull() ?: emptyList()
        val positive = reactions.count { it.isPositive }
        val negative = reactions.count { it.isNegative }
        val neutral = reactions.count { it.isNeutral }

        val recentReactions = reactions.take(10)

        val sentimentScore = if (reactions.isNotEmpty()) {
            ((positive - negative).toDouble() / reactions.size * 100)
        } else 0.0

        return PlayerReactionsDashboard(
            playerName = playerName,
            totalReactions = reactions.size,
            positiveReactions = positive,
            negativeReactions = negative,
            neutralReactions = neutral,
            sentimentScore = sentimentScore,
            recentReactions = recentReactions
        )
    }
}

// ============ DATA CLASSES ============

data class PlayerReactionsDashboard(
    val playerName: String,
    val totalReactions: Int,
    val positiveReactions: Int,
    val negativeReactions: Int,
    val neutralReactions: Int,
    val sentimentScore: Double,
    val recentReactions: List<PlayerReactionsEntity>
)