package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "player_reactions",
    foreignKeys = [
        ForeignKey(
            entity = PlayersEntity::class,
            parentColumns = ["name"],
            childColumns = ["player_name"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["player_name"]),
        Index(value = ["reaction_type"]),
        Index(value = ["player_name", "reaction_type"])
    ]
)
data class PlayerReactionsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "player_name")
    val playerName: String,

    @ColumnInfo(name = "reaction_type", defaultValue = "Neutral")
    val reactionType: String = "Neutral",

    @ColumnInfo(name = "reaction_text")
    val reactionText: String
) {

    // ============ COMPUTED PROPERTIES ============

    val isPositive: Boolean
        get() = reactionType == "Happy" || reactionType == "Excited" || reactionType == "Proud"

    val isNegative: Boolean
        get() = reactionType == "Angry" || reactionType == "Frustrated" || reactionType == "Disappointed" || reactionType == "Sad"

    val isNeutral: Boolean
        get() = reactionType == "Neutral"

    val reactionEmoji: String
        get() = when (reactionType) {
            "Happy" -> "😊"
            "Excited" -> "🎉"
            "Proud" -> "🏆"
            "Angry" -> "😠"
            "Frustrated" -> "😤"
            "Disappointed" -> "😞"
            "Sad" -> "😢"
            "Neutral" -> "😐"
            "Thoughtful" -> "🤔"
            else -> "💬"
        }
}

// ============ ENUMS ============

enum class PlayerReactionType(val value: String) {
    HAPPY("Happy"),
    EXCITED("Excited"),
    PROUD("Proud"),
    ANGRY("Angry"),
    FRUSTRATED("Frustrated"),
    DISAPPOINTED("Disappointed"),
    SAD("Sad"),
    NEUTRAL("Neutral"),
    THOUGHTFUL("Thoughtful");

    companion object {
        fun fromString(value: String): PlayerReactionType? {
            return values().find { it.value == value }
        }
    }
}