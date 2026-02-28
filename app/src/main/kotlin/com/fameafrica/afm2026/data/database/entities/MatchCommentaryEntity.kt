package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "match_commentary",
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
            onDelete = ForeignKey.SET_NULL,
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
            entity = ManagersEntity::class,
            parentColumns = ["id"],
            childColumns = ["manager_id"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RefereesEntity::class,
            parentColumns = ["referee_id"],
            childColumns = ["referee_id"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["team_name"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MatchEventsEntity::class,
            parentColumns = ["event_id"],
            childColumns = ["event_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["match_id", "minute"]),
        Index(value = ["match_id"]),
        Index(value = ["minute"]),
        Index(value = ["event_id"]),
        Index(value = ["commentary_type"]),
        Index(value = ["importance"]),
        Index(value = ["player_id"]),
        Index(value = ["assist_player_id"]),
        Index(value = ["team_name"]),
        Index(value = ["period"]),
        Index(value = ["is_controversial"]),
        Index(value = ["crowd_noise_level"])
    ]
)
data class MatchCommentaryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "match_id")
    val matchId: Int,

    @ColumnInfo(name = "event_id")
    val eventId: Int? = null,  // Link to specific match event

    @ColumnInfo(name = "minute")
    val minute: Int,

    @ColumnInfo(name = "stoppage_time")
    val stoppageTime: Int? = null,

    @ColumnInfo(name = "period")
    val period: String = "REGULAR",  // REGULAR, FIRST_HALF, SECOND_HALF, EXTRA_FIRST, EXTRA_SECOND, PENALTIES

    @ColumnInfo(name = "commentary_text")
    val commentaryText: String,

    @ColumnInfo(name = "commentary_type")
    val commentaryType: String,  // GOAL, CARD, SUBSTITUTION, INJURY, VAR, CONTROVERSY, FAN_REACTION, CELEBRATION, DRAMA, PENALTY, FREEKICK, SHOT, SAVE, CORNER, FOUL, OFFSIDE

    @ColumnInfo(name = "importance")
    val importance: Int = 1,  // 1-5, 5 being most important (goals, red cards, etc.)

    @ColumnInfo(name = "player_id")
    val playerId: Int? = null,

    @ColumnInfo(name = "player_name")
    val playerName: String? = null,

    @ColumnInfo(name = "assist_player_id")
    val assistPlayerId: Int? = null,

    @ColumnInfo(name = "assist_player_name")
    val assistPlayerName: String? = null,

    @ColumnInfo(name = "team_name")
    val teamName: String? = null,

    @ColumnInfo(name = "opponent_team")
    val opponentTeam: String? = null,

    @ColumnInfo(name = "manager_id")
    val managerId: Int? = null,

    @ColumnInfo(name = "manager_name")
    val managerName: String? = null,

    @ColumnInfo(name = "referee_id")
    val refereeId: Int? = null,

    @ColumnInfo(name = "referee_name")
    val refereeName: String? = null,

    @ColumnInfo(name = "current_score")
    val currentScore: String? = null,  // e.g., "2-1"

    @ColumnInfo(name = "home_score")
    val homeScore: Int? = null,

    @ColumnInfo(name = "away_score")
    val awayScore: Int? = null,

    @ColumnInfo(name = "is_controversial")
    val isControversial: Boolean = false,

    @ColumnInfo(name = "var_review")
    val varReview: Boolean = false,

    @ColumnInfo(name = "var_overturned")
    val varOverturned: Boolean = false,

    @ColumnInfo(name = "penalty_saved")
    val penaltySaved: Boolean = false,

    @ColumnInfo(name = "penalty_post")
    val penaltyPost: Boolean = false,

    @ColumnInfo(name = "own_goal")
    val ownGoal: Boolean = false,

    @ColumnInfo(name = "shot_type")
    val shotType: String? = null,  // HEADER, LEFT_FOOT, RIGHT_FOOT, FREE_KICK, PENALTY, VOLLEY

    @ColumnInfo(name = "shot_distance")
    val shotDistance: Int? = null,  // Distance in meters

    @ColumnInfo(name = "expected_goals")
    val expectedGoals: Double? = null,  // xG value

    @ColumnInfo(name = "substitution_in_player")
    val substitutionInPlayer: String? = null,

    @ColumnInfo(name = "substitution_in_player_id")
    val substitutionInPlayerId: Int? = null,

    @ColumnInfo(name = "substitution_out_player")
    val substitutionOutPlayer: String? = null,

    @ColumnInfo(name = "substitution_out_player_id")
    val substitutionOutPlayerId: Int? = null,

    @ColumnInfo(name = "injury_type")
    val injuryType: String? = null,  // MINOR, MODERATE, SEVERE

    @ColumnInfo(name = "injury_minutes")
    val injuryMinutes: Int? = null,  // Estimated recovery time

    @ColumnInfo(name = "fan_reaction")
    val fanReaction: String? = null,  // CHEERING, WHISTLING, PROTEST, CHANTING, OLE, WAVING, FLARES

    @ColumnInfo(name = "crowd_noise_level")
    val crowdNoiseLevel: Int = 5,  // 1-10

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "description")
    val description: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val displayMinute: String
        get() = if (stoppageTime != null && stoppageTime > 0) {
            "$minute+$stoppageTime"
        } else {
            minute.toString()
        }

    val formattedCommentary: String
        get() = "$displayMinute' [$period] - $commentaryText"

    val importanceLevel: String
        get() = when (importance) {
            5 -> "🔴🔴🔴 CRITICAL"
            4 -> "🔴🔴 HIGH"
            3 -> "🟡 MEDIUM"
            2 -> "⚪ LOW"
            else -> "⚪ MINOR"
        }

    val commentaryIcon: String
        get() = when (commentaryType) {
            "GOAL" -> "⚽"
            "PENALTY" -> "⚽ (P)"
            "FREEKICK" -> "⚽ (FK)"
            "OWN_GOAL" -> "⚽ (OG)"
            "CARD" -> if (commentaryText.contains("red", ignoreCase = true) || commentaryText.contains("RED", ignoreCase = true)) "🟥" else "🟨"
            "SUBSTITUTION" -> "🔄"
            "INJURY" -> "🩹"
            "VAR" -> "📺"
            "CONTROVERSY" -> "🔥"
            "FAN_REACTION" -> "📢"
            "CELEBRATION" -> "🎉"
            "DRAMA" -> "🎭"
            "SHOT" -> "⚡"
            "SAVE" -> "🧤"
            "CORNER" -> "⛳"
            "FOUL" -> "🚫"
            "OFFSIDE" -> "🚩"
            else -> "📝"
        }

    val crowdNoiseDescription: String
        get() = when (crowdNoiseLevel) {
            1 -> "Pin drop silence"
            2 -> "Quiet murmuring"
            3 -> "Normal crowd noise"
            4 -> "Lively atmosphere"
            5 -> "Vocal support"
            6 -> "Loud cheering"
            7 -> "Very loud"
            8 -> "Deafening roar"
            9 -> "Ear-splitting"
            10 -> "Volcanic eruption"
            else -> "Unknown"
        }
}

// ============ ENUMS ============

enum class CommentaryType(val value: String) {
    GOAL("GOAL"),
    PENALTY("PENALTY"),
    FREEKICK("FREEKICK"),
    OWN_GOAL("OWN_GOAL"),
    CARD("CARD"),
    SUBSTITUTION("SUBSTITUTION"),
    INJURY("INJURY"),
    VAR("VAR"),
    CONTROVERSY("CONTROVERSY"),
    FAN_REACTION("FAN_REACTION"),
    CELEBRATION("CELEBRATION"),
    DRAMA("DRAMA"),
    SHOT("SHOT"),
    SAVE("SAVE"),
    CORNER("CORNER"),
    FOUL("FOUL"),
    OFFSIDE("OFFSIDE"),
    TACTICAL("TACTICAL"),
    STATISTIC("STATISTIC"),
    MANAGER("MANAGER"),
    REFEREE("REFEREE")
}

enum class FanReaction(val value: String) {
    CHEERING("CHEERING"),
    WHISTLING("WHISTLING"),
    PROTEST("PROTEST"),
    CHANTING("CHANTING"),
    OLE("OLE"),
    WAVING("WAVING"),
    FLARES("FLARES"),
    SILENCE("SILENCE"),
    APPLAUSE("APPLAUSE"),
    BOOING("BOOING")
}