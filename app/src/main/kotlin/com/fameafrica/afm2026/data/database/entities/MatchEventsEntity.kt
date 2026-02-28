package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "match_events",
    foreignKeys = [
        ForeignKey(
            entity = FixturesEntity::class,
            parentColumns = ["id"],
            childColumns = ["match_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlayersEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlayersEntity::class,
            parentColumns = ["id"],
            childColumns = ["assist_player_id"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["team_name"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["match_id"]),
        Index(value = ["player_id"]),
        Index(value = ["assist_player_id"]),
        Index(value = ["team_name"]),
        Index(value = ["event_type"]),
        Index(value = ["minute"]),
        Index(value = ["match_id", "minute"]),
        Index(value = ["player_id", "event_type"]),
        Index(value = ["match_id", "event_type"])
    ]
)
data class MatchEventsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "event_id")
    val eventId: Int = 0,

    @ColumnInfo(name = "match_id")
    val matchId: Int,

    @ColumnInfo(name = "minute")
    val minute: Int,

    @ColumnInfo(name = "event_type")
    val eventType: String,  // GOAL, ASSIST, YELLOW_CARD, RED_CARD, SUBSTITUTION, PENALTY_SCORED, PENALTY_MISSED, OWN_GOAL, INJURY, VAR, etc.

    @ColumnInfo(name = "player_name")
    val playerName: String,

    @ColumnInfo(name = "player_id")
    val playerId: Int,

    @ColumnInfo(name = "assist_player_name")
    val assistPlayerName: String? = null,

    @ColumnInfo(name = "assist_player_id")
    val assistPlayerId: Int? = null,

    @ColumnInfo(name = "team_name")
    val teamName: String,

    @ColumnInfo(name = "opponent_team")
    val opponentTeam: String? = null,

    @ColumnInfo(name = "home_score")
    val homeScore: Int? = null,  // Score at time of event

    @ColumnInfo(name = "away_score")
    val awayScore: Int? = null,  // Score at time of event

    @ColumnInfo(name = "period")
    val period: String = "REGULAR",  // REGULAR, FIRST_HALF, SECOND_HALF, EXTRA_FIRST, EXTRA_SECOND, PENALTIES

    @ColumnInfo(name = "stoppage_time")
    val stoppageTime: Int? = null,  // Additional minutes for stoppage time goals

    @ColumnInfo(name = "substitution_in_player")
    val substitutionInPlayer: String? = null,  // Player coming on

    @ColumnInfo(name = "substitution_in_player_id")
    val substitutionInPlayerId: Int? = null,   // Player ID coming on

    @ColumnInfo(name = "substitution_out_player")
    val substitutionOutPlayer: String? = null, // Player going off

    @ColumnInfo(name = "substitution_out_player_id")
    val substitutionOutPlayerId: Int? = null,  // Player ID going off

    @ColumnInfo(name = "injury_minutes")
    val injuryMinutes: Int? = null,  // Estimated recovery time for injuries

    @ColumnInfo(name = "injury_type")
    val injuryType: String? = null,  // Minor, Moderate, Severe

    @ColumnInfo(name = "var_review")
    val varReview: Boolean = false,  // Was VAR used?

    @ColumnInfo(name = "var_overturned")
    val varOverturned: Boolean = false,  // Did VAR overturn the decision?

    @ColumnInfo(name = "penalty_saved")
    val penaltySaved: Boolean = false,  // Was penalty saved?

    @ColumnInfo(name = "penalty_post")
    val penaltyPost: Boolean = false,   // Hit the post/crossbar

    @ColumnInfo(name = "own_goal")
    val ownGoal: Boolean = false,       // Was it an own goal?

    @ColumnInfo(name = "goal_x")
    val goalX: Float? = null,  // X coordinate of goal (for heat maps)

    @ColumnInfo(name = "goal_y")
    val goalY: Float? = null,  // Y coordinate of goal (for heat maps)

    @ColumnInfo(name = "shot_type")
    val shotType: String? = null,  // HEADER, LEFT_FOOT, RIGHT_FOOT, FREE_KICK, PENALTY, VOLLEY, etc.

    @ColumnInfo(name = "shot_distance")
    val shotDistance: Int? = null,  // Distance in meters

    @ColumnInfo(name = "expected_goals")
    val expectedGoals: Double? = null,  // xG value (0.0-1.0)

    @ColumnInfo(name = "expected_assists")
    val expectedAssists: Double? = null,  // xA value (0.0-1.0)

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "description")
    val description: String? = null  // Custom event description
) {

    // ============ COMPUTED PROPERTIES ============

    val isGoal: Boolean
        get() = eventType == "GOAL" || eventType == "PENALTY_SCORED" || eventType == "OWN_GOAL"

    val isCard: Boolean
        get() = eventType == "YELLOW_CARD" || eventType == "RED_CARD"

    val isSubstitution: Boolean
        get() = eventType == "SUBSTITUTION"

    val isPenalty: Boolean
        get() = eventType == "PENALTY_SCORED" || eventType == "PENALTY_MISSED"

    val isInjury: Boolean
        get() = eventType == "INJURY"

    val isVar: Boolean
        get() = varReview

    val isFirstHalf: Boolean
        get() = minute <= 45 || period == "FIRST_HALF"

    val isSecondHalf: Boolean
        get() = (minute > 45 && minute <= 90) || period == "SECOND_HALF"

    val isExtraTime: Boolean
        get() = minute > 90 && period in listOf("EXTRA_FIRST", "EXTRA_SECOND")

    val isStoppageTime: Boolean
        get() = stoppageTime != null && stoppageTime > 0

    val displayMinute: String
        get() = if (stoppageTime != null && stoppageTime > 0) {
            "$minute+$stoppageTime"
        } else {
            minute.toString()
        }

    val eventIcon: String
        get() = when (eventType) {
            "GOAL" -> "⚽"
            "PENALTY_SCORED" -> "⚽ (P)"
            "PENALTY_MISSED" -> "❌ (P)"
            "ASSIST" -> "🎯"
            "FREEKICK_SCORED" -> "⚽ (FK)"
            "FREEKICK_MISSED" -> "❌ (FK)"
            "OWN_GOAL" -> "⚽ (OG)"
            "YELLOW_CARD" -> "🟨"
            "RED_CARD" -> "🟥"
            "SUBSTITUTION" -> "🔄"
            "INJURY" -> "🩹"
            "VAR" -> "📺"
            else -> "•"
        }

    val eventSummary: String
        get() = when (eventType) {
            "GOAL" -> "$displayMinute' $eventIcon ${playerName} scores"
            "ASSIST" -> "$displayMinute' 🎯 ${playerName} assists"
            "PENALTY_SCORED" -> "$displayMinute' ⚽ ${playerName} scores from the spot"
            "PENALTY_MISSED" -> "$displayMinute' ❌ ${playerName} misses penalty"
            "FREEKICK_SCORED" -> "$displayMinute' ⚽ What a free kick goal from ${playerName} !"
            "FREEKICK_MISSED" -> "$displayMinute' ❌ ${playerName} misses free kick"
            "OWN_GOAL" -> "$displayMinute' ⚽ ${playerName} (own goal)"
            "YELLOW_CARD" -> "$displayMinute' 🟨 ${playerName} booked"
            "RED_CARD" -> "$displayMinute' 🟥 ${playerName} sent off"
            "SUBSTITUTION" -> "$displayMinute' 🔄 ${substitutionInPlayer} replaces ${playerName}"
            "INJURY" -> "$displayMinute' 🩹 ${playerName} injured"
            "VAR" -> "$displayMinute' 📺 VAR check: ${description}"
            else -> "$displayMinute' $eventIcon ${playerName}"
        }
}

// Enum classes for type safety
enum class EventType(val value: String) {
    GOAL("GOAL"),
    ASSIST("ASSIST"),
    YELLOW_CARD("YELLOW_CARD"),
    RED_CARD("RED_CARD"),
    SUBSTITUTION("SUBSTITUTION"),
    PENALTY_SCORED("PENALTY_SCORED"),
    PENALTY_MISSED("PENALTY_MISSED"),
    OWN_GOAL("OWN_GOAL"),
    INJURY("INJURY"),
    VAR("VAR"),
    SHOT("SHOT"),
    SHOT_ON_TARGET("SHOT_ON_TARGET"),
    SHOT_OFF_TARGET("SHOT_OFF_TARGET"),
    CORNER("CORNER"),
    FOUL("FOUL"),
    OFFSIDE("OFFSIDE"),
    SAVE("SAVE")
}

enum class MatchPeriod(val value: String) {
    FIRST_HALF("FIRST_HALF"),
    SECOND_HALF("SECOND_HALF"),
    EXTRA_FIRST("EXTRA_FIRST"),
    EXTRA_SECOND("EXTRA_SECOND"),
    PENALTIES("PENALTIES"),
    REGULAR("REGULAR")
}

enum class ShotType(val value: String) {
    HEADER("HEADER"),
    LEFT_FOOT("LEFT_FOOT"),
    RIGHT_FOOT("RIGHT_FOOT"),
    FREE_KICK("FREE_KICK"),
    PENALTY("PENALTY"),
    VOLLEY("VOLLEY"),
    HALF_VOLLEY("HALF_VOLLEY"),
    LONG_RANGE("LONG_RANGE"),
    CLOSE_RANGE("CLOSE_RANGE")
}

enum class InjurySeverity(val value: String) {
    MINOR("MINOR"),
    MODERATE("MODERATE"),
    SEVERE("SEVERE"),
    CAREER_ENDING("CAREER_ENDING")
}