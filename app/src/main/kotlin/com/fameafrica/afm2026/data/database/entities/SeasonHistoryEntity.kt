package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "season_history",
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
        Index(value = ["team_name", "season"], unique = true),
        Index(value = ["season"]),
        Index(value = ["team_name"]),
        Index(value = ["position"]),
        Index(value = ["trophies_won"])
    ]
)
data class SeasonHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "season")
    val season: String,

    @ColumnInfo(name = "team_name")
    val teamName: String,

    @ColumnInfo(name = "team_id")
    val teamId: Int,

    @ColumnInfo(name = "league_name")
    val leagueName: String? = null,

    @ColumnInfo(name = "position")
    val position: Int? = null,

    @ColumnInfo(name = "points")
    val points: Int? = null,

    @ColumnInfo(name = "wins")
    val wins: Int? = null,

    @ColumnInfo(name = "draws")
    val draws: Int? = null,

    @ColumnInfo(name = "losses")
    val losses: Int? = null,

    @ColumnInfo(name = "goals_for")
    val goalsFor: Int? = null,

    @ColumnInfo(name = "goals_against")
    val goalsAgainst: Int? = null,

    @ColumnInfo(name = "goal_difference")
    val goalDifference: Int? = null,

    @ColumnInfo(name = "trophies_won")
    val trophiesWon: Int = 0,

    @ColumnInfo(name = "league_titles")
    val leagueTitles: Int = 0,

    @ColumnInfo(name = "cup_titles")
    val cupTitles: Int = 0,

    @ColumnInfo(name = "continental_titles")
    val continentalTitles: Int = 0,

    @ColumnInfo(name = "promoted")
    val promoted: Boolean = false,

    @ColumnInfo(name = "relegated")
    val relegated: Boolean = false,

    @ColumnInfo(name = "qualified_for_continental")
    val qualifiedForContinental: Boolean = false,

    @ColumnInfo(name = "average_attendance")
    val averageAttendance: Int? = null,

    @ColumnInfo(name = "top_scorer")
    val topScorer: String? = null,

    @ColumnInfo(name = "top_scorer_goals")
    val topScorerGoals: Int? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val winPercentage: Double
        get() = if ((wins?.plus(draws ?: 0)?.plus(losses ?: 0) ?: 0) > 0) {
            (wins?.toDouble() ?: 0.0) / (wins?.plus(draws ?: 0)?.plus(losses ?: 0) ?: 1) * 100
        } else 0.0

    val isChampion: Boolean
        get() = position == 1

    val isPromoted: Boolean
        get() = promoted

    val isRelegated: Boolean
        get() = relegated

    val seasonDisplay: String
        get() = "$season Season"

    val summary: String
        get() = buildString {
            append("$teamName - $season: ")
            if (position != null) append("${position}th place")
            if (trophiesWon > 0) append(", $trophiesWon trophy(ies)")
        }
}

// ============ ENUMS ============

enum class SeasonOutcome(val value: String) {
    CHAMPION("Champion"),
    PROMOTED("Promoted"),
    RELEGATED("Relegated"),
    QUALIFIED_CONTINENTAL("Qualified for Continental"),
    MID_TABLE("Mid-table"),
    STRUGGLING("Struggling")
}