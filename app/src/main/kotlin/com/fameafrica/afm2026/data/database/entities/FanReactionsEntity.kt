package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "fan_reactions",
    foreignKeys = [
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["team_name"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["team_name"]),
        Index(value = ["reaction"]),
        Index(value = ["sentiment"]),
        Index(value = ["timestamp"])
    ]
)
data class FanReactionsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "team_name")
    val teamName: String,

    @ColumnInfo(name = "reaction")
    val reaction: String,

    @ColumnInfo(name = "sentiment", defaultValue = "Neutral")
    val sentiment: String = "Neutral",

    @ColumnInfo(name = "timestamp")
    val timestamp: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val isPositive: Boolean
        get() = sentiment == "Positive"

    val isNegative: Boolean
        get() = sentiment == "Negative"

    val isNeutral: Boolean
        get() = sentiment == "Neutral"

    val reactionEmoji: String
        get() = when (reaction.lowercase()) {
            "cheer" -> "🎉"
            "applause" -> "👏"
            "chant" -> "📢"
            "protest" -> "📋"
            "boycott" -> "🚫"
            "walkout" -> "🚶"
            else -> "💬"
        }
}

// ============ ENUMS ============

enum class FanSentiment(val value: String) {
    POSITIVE("Positive"),
    NEUTRAL("Neutral"),
    NEGATIVE("Negative")
}

enum class FanReactionType(val value: String) {
    CHEER("Cheer"),
    APPLAUSE("Applause"),
    CHANT("Chant"),
    PROTEST("Protest"),
    BOYCOTT("Boycott"),
    WALKOUT("Walkout")
}