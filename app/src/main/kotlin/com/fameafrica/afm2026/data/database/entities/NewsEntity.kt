package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(
    tableName = "news",
    foreignKeys = [
        ForeignKey(
            entity = JournalistsEntity::class,
            parentColumns = ["name"],
            childColumns = ["journalist_name"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["category"]),
        Index(value = ["journalist_name"]),
        Index(value = ["timestamp"]),
        Index(value = ["is_top_news"]),
        Index(value = ["related_team"]),
        Index(value = ["related_player"]),
        Index(value = ["related_manager"])
    ]
)
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "headline")
    val headline: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "category")
    val category: String,  // MATCH, TRANSFER, INJURY, INTERVIEW, PRESS_CONFERENCE, BOARD, FANS, RUMOR, AWARD

    @ColumnInfo(name = "journalist_name")
    val journalistName: String?,

    @ColumnInfo(name = "journalist_logo")
    val journalistLogo: String?,

    @ColumnInfo(name = "timestamp")
    val timestamp: String,

    @ColumnInfo(name = "is_top_news", defaultValue = "1")
    val isTopNews: Int = 1,

    @ColumnInfo(name = "related_team")
    val relatedTeam: String? = null,

    @ColumnInfo(name = "related_player")
    val relatedPlayer: String? = null,

    @ColumnInfo(name = "related_manager")
    val relatedManager: String? = null,

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    @ColumnInfo(name = "views")
    val views: Int = 0,

    @ColumnInfo(name = "likes")
    val likes: Int = 0,

    @ColumnInfo(name = "comments")
    val comments: Int = 0
) {

    // ============ COMPUTED PROPERTIES ============

    val formattedTimestamp: String
        get() {
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = dateFormat.parse(timestamp)
                val now = Date()
                val diff = now.time - date.time

                when {
                    diff < 60000 -> "Just now"
                    diff < 3600000 -> "${diff / 60000} minutes ago"
                    diff < 86400000 -> "${diff / 3600000} hours ago"
                    else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
                }
            } catch (e: Exception) {
                timestamp
            }
        }

    val categoryColor: String
        get() = when (category) {
            "TRANSFER" -> "Blue"
            "MATCH" -> "Green"
            "INJURY" -> "Red"
            "INTERVIEW" -> "Purple"
            "PRESS_CONFERENCE" -> "Orange"
            "BOARD" -> "Brown"
            "FANS" -> "Yellow"
            "RUMOR" -> "Pink"
            "AWARD" -> "Gold"
            else -> "Gray"
        }
}

// ============ ENUMS ============

enum class NewsCategory(val value: String) {
    MATCH("MATCH"),
    TRANSFER("TRANSFER"),
    INJURY("INJURY"),
    INTERVIEW("INTERVIEW"),
    PRESS_CONFERENCE("PRESS_CONFERENCE"),
    BOARD("BOARD"),
    FANS("FANS"),
    RUMOR("RUMOR"),
    AWARD("AWARD"),
    ANNOUNCEMENT("ANNOUNCEMENT")
}