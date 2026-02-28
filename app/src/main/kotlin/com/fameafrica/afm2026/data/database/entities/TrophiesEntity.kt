package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "trophies",
    foreignKeys = [
        ForeignKey(
            entity = ManagersEntity::class,
            parentColumns = ["id"],
            childColumns = ["manager_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["club_name"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SeasonAwardsEntity::class,
            parentColumns = ["id"],
            childColumns = ["season_award_id"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SeasonHistoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["season_history_id"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["manager_id"]),
        Index(value = ["club_name"]),
        Index(value = ["season"]),
        Index(value = ["trophy_name"]),
        Index(value = ["competition_level"]),
        Index(value = ["competition_type"]),
        Index(value = ["season_award_id"]),
        Index(value = ["season_history_id"]),
        Index(value = ["manager_id", "season"]),
        Index(value = ["club_name", "season"])
    ]
)
data class TrophiesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "manager_id")
    val managerId: Int,

    @ColumnInfo(name = "manager_name")
    val managerName: String? = null,

    @ColumnInfo(name = "club_name")
    val clubName: String,

    @ColumnInfo(name = "club_id")
    val clubId: Int? = null,

    @ColumnInfo(name = "trophy_name")
    val trophyName: String,

    @ColumnInfo(name = "trophy_type")
    val trophyType: String,  // LEAGUE_TITLE, CUP_TITLE, CONTINENTAL_TITLE, SUPER_CUP, AWARD

    @ColumnInfo(name = "competition_id")
    val competitionId: Int? = null,  // References leagues(id) or cups(id)

    @ColumnInfo(name = "competition_name")
    val competitionName: String? = null,

    @ColumnInfo(name = "competition_level")
    val competitionLevel: String = "Domestic",  // Domestic, Continental, International

    @ColumnInfo(name = "season")
    val season: String,

    @ColumnInfo(name = "season_year")
    val seasonYear: Int,

    @ColumnInfo(name = "match_played")
    val matchPlayed: String? = null,  // e.g., "2-1 vs Al Ahly"

    @ColumnInfo(name = "opponent")
    val opponent: String? = null,

    @ColumnInfo(name = "venue")
    val venue: String? = null,

    @ColumnInfo(name = "attendance")
    val attendance: Int? = null,

    @ColumnInfo(name = "win_type")
    val winType: String? = null,  // Regular, Penalties, Walkover

    @ColumnInfo(name = "icon_path")
    val iconPath: String? = null,

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    @ColumnInfo(name = "season_award_id")
    val seasonAwardId: Int? = null,  // Link to season_awards if this trophy is an individual award

    @ColumnInfo(name = "season_history_id")
    val seasonHistoryId: Int? = null,  // Link to season_history

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "date_won")
    val dateWon: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String = getCurrentDateTime()
) {

    // ============ COMPUTED PROPERTIES ============

    val isLeagueTitle: Boolean
        get() = trophyType == "LEAGUE_TITLE"

    val isCupTitle: Boolean
        get() = trophyType == "CUP_TITLE"

    val isContinentalTitle: Boolean
        get() = trophyType == "CONTINENTAL_TITLE"

    val isSuperCup: Boolean
        get() = trophyType == "SUPER_CUP"

    val isIndividualAward: Boolean
        get() = trophyType == "AWARD"

    val isDomestic: Boolean
        get() = competitionLevel == "Domestic"

    val isContinental: Boolean
        get() = competitionLevel == "Continental"

    val isInternational: Boolean
        get() = competitionLevel == "International"

    val trophyDisplay: String
        get() = "$trophyName - $season"

    val fullDescription: String
        get() = buildString {
            append("$trophyName")
            if (opponent != null) append(" vs $opponent")
            if (matchPlayed != null) append(" ($matchPlayed)")
            if (winType != null) append(" - $winType")
        }

    val icon: String
        get() = when {
            isLeagueTitle -> "🏆"
            isCupTitle -> "🏆"
            isContinentalTitle -> "🌍🏆"
            isSuperCup -> "🛡️"
            isIndividualAward -> "🏅"
            else -> "🏆"
        }

    companion object {
        private fun getCurrentDateTime(): String {
            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            return dateFormat.format(java.util.Date())
        }
    }
}

// ============ ENUMS ============

enum class TrophyType(val value: String) {
    LEAGUE_TITLE("LEAGUE_TITLE"),
    CUP_TITLE("CUP_TITLE"),
    CONTINENTAL_TITLE("CONTINENTAL_TITLE"),
    SUPER_CUP("SUPER_CUP"),
    AWARD("AWARD")
}

enum class CompetitionLevel(val value: String) {
    DOMESTIC("Domestic"),
    CONTINENTAL("Continental"),
    INTERNATIONAL("International")
}