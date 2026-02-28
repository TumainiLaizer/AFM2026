package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "cup_brackets",
    foreignKeys = [
        ForeignKey(
            entity = CupsEntity::class,
            parentColumns = ["name"],
            childColumns = ["cupName"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["teamName"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["opponentName"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FixturesEntity::class,
            parentColumns = ["id"],
            childColumns = ["fixtureId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["cupName"]),
        Index(value = ["season"]),
        Index(value = ["round"]),
        Index(value = ["teamName"]),
        Index(value = ["fixtureId"]),
        Index(value = ["cupName", "season", "round"]),
        Index(value = ["cupName", "season", "teamName"]),
        Index(value = ["bracket_position"]),
        Index(value = ["parent_bracket_id"]),
        Index(value = ["is_walkover"])
    ]
)
data class CupBracketsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "cupName")
    val cupName: String?,

    @ColumnInfo(name = "season")
    val season: Int,

    @ColumnInfo(name = "round")
    val round: String?,

    @ColumnInfo(name = "round_number")
    val roundNumber: Int,  // 1 = First Round, 2 = Second Round, etc.

    @ColumnInfo(name = "bracket_position")
    val bracketPosition: Int,  // Position in the bracket (1-64, etc.)

    @ColumnInfo(name = "teamName")
    val teamName: String?,

    @ColumnInfo(name = "opponentName")
    val opponentName: String?,

    @ColumnInfo(name = "result")
    val result: String?,

    @ColumnInfo(name = "home_score")
    val homeScore: Int? = null,

    @ColumnInfo(name = "away_score")
    val awayScore: Int? = null,

    @ColumnInfo(name = "penalty_score")
    val penaltyScore: String? = null,  // e.g., "4-3" for penalty shootouts

    @ColumnInfo(name = "aggregate_score")
    val aggregateScore: String? = null,  // e.g., "3-2" for two-legged ties

    @ColumnInfo(name = "fixtureId")
    val fixtureId: Int,

    @ColumnInfo(name = "first_leg_fixture_id")
    val firstLegFixtureId: Int? = null,

    @ColumnInfo(name = "second_leg_fixture_id")
    val secondLegFixtureId: Int? = null,

    @ColumnInfo(name = "is_two_legged")
    val isTwoLegged: Boolean = false,

    @ColumnInfo(name = "winner")
    val winner: String? = null,

    @ColumnInfo(name = "loser")
    val loser: String? = null,

    @ColumnInfo(name = "next_bracket_id")
    val nextBracketId: Int? = null,  // ID of bracket in next round

    @ColumnInfo(name = "parent_bracket_id")
    val parentBracketId: Int? = null,  // For tracking progression

    @ColumnInfo(name = "is_walkover")
    val isWalkover: Boolean = false,

    @ColumnInfo(name = "walkover_reason")
    val walkoverReason: String? = null,

    @ColumnInfo(name = "match_date")
    val matchDate: String? = null,

    @ColumnInfo(name = "stadium")
    val stadium: String? = null,

    @ColumnInfo(name = "attendance")
    val attendance: Int? = null,

    @ColumnInfo(name = "legacyTag")
    val legacyTag: String? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val isCompleted: Boolean
        get() = result != null && (result == "WIN" || result == "LOSS" || result?.contains("-") == true)

    val isScheduled: Boolean
        get() = !isCompleted && teamName != null && opponentName != null

    val isBye: Boolean
        get() = opponentName == null || opponentName == "BYE"

    val displayResult: String
        get() = when {
            isWalkover -> "WO"
            penaltyScore != null -> "$result (${penaltyScore} pens)"
            aggregateScore != null -> "$aggregateScore agg"
            else -> result ?: "TBD"
        }

    val roundDisplay: String
        get() = when (round) {
            "PRELIMINARY" -> "Preliminary Round"
            "FIRST" -> "First Round"
            "SECOND" -> "Second Round"
            "THIRD" -> "Third Round"
            "FOURTH" -> "Fourth Round"
            "GROUP" -> "Group Stage"
            "ROUND_64" -> "Round of 64"
            "ROUND_32" -> "Round of 32"
            "ROUND_16" -> "Round of 16"
            "QUARTER" -> "Quarter-Finals"
            "QUARTER_FINAL" -> "Quarter-Finals"
            "SEMI" -> "Semi-Finals"
            "SEMI_FINAL" -> "Semi-Finals"
            "FINAL" -> "Final"
            else -> round ?: "Unknown"
        }

    val bracketPath: String
        get() = "$cupName - $roundDisplay #$bracketPosition"

    val matchSummary: String
        get() = when {
            isBye -> "$teamName receives a bye"
            isCompleted && winner != null -> "$winner defeated $loser"
            isScheduled -> "$teamName vs $opponentName"
            else -> "Match TBD"
        }
}

// ============ ENUMS ============

enum class CupRound(val value: String, val order: Int) {
    PRELIMINARY("PRELIMINARY", 0),
    FIRST("FIRST", 1),
    SECOND("SECOND", 2),
    THIRD("THIRD", 3),
    FOURTH("FOURTH", 4),
    GROUP("GROUP", 5),
    ROUND_64("ROUND_64", 6),
    ROUND_32("ROUND_32", 7),
    ROUND_16("ROUND_16", 8),
    QUARTER_FINAL("QUARTER_FINAL", 9),
    SEMI_FINAL("SEMI_FINAL", 10),
    FINAL("FINAL", 11)
}

enum class MatchResult(val value: String) {
    WIN("WIN"),
    LOSS("LOSS"),
    DRAW("DRAW"),
    ADVANCE("ADVANCE"),
    ELIMINATED("ELIMINATED")
}