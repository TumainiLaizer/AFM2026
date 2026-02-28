package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "community_shield",
    foreignKeys = [
        ForeignKey(
            entity = LeaguesEntity::class,
            parentColumns = ["name"],
            childColumns = ["league_name"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["league_winner"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["league_runner_up"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["league_third"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["league_fourth"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["league_name"]),
        Index(value = ["season"]),
        Index(value = ["league_winner"]),
        Index(value = ["league_runner_up"]),
        Index(value = ["match_date"]),
        Index(value = ["is_played"]),
        Index(value = ["league_name", "season"], unique = true)
    ]
)
data class CommunityShieldEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "league_name")
    val leagueName: String,

    @ColumnInfo(name = "season")
    val season: String,  // e.g., "2024/25"

    @ColumnInfo(name = "match_date")
    val matchDate: String,

    @ColumnInfo(name = "league_winner")
    val leagueWinner: String?,

    @ColumnInfo(name = "league_runner_up")
    val leagueRunnerUp: String?,

    @ColumnInfo(name = "league_third")
    val leagueThird: String?,

    @ColumnInfo(name = "league_fourth")
    val leagueFourth: String?,

    @ColumnInfo(name = "participants_format")
    val participantsFormat: String,  // "CHAMPION_VS_RUNNER_UP", "TOP_FOUR", "CHAMPION_VS_CUP_WINNER"

    @ColumnInfo(name = "fixture_id")
    val fixtureId: Int? = null,

    @ColumnInfo(name = "home_team")
    val homeTeam: String? = null,

    @ColumnInfo(name = "away_team")
    val awayTeam: String? = null,

    @ColumnInfo(name = "home_score")
    val homeScore: Int? = null,

    @ColumnInfo(name = "away_score")
    val awayScore: Int? = null,

    @ColumnInfo(name = "winner")
    val winner: String? = null,

    @ColumnInfo(name = "result")
    val result: String? = null,

    @ColumnInfo(name = "is_played")
    val isPlayed: Boolean = false,

    @ColumnInfo(name = "prize_money", defaultValue = "10000")
    val prizeMoney: Int = 10000,

    @ColumnInfo(name = "stadium")
    val stadium: String? = null,

    @ColumnInfo(name = "attendance")
    val attendance: Int? = null,

    @ColumnInfo(name = "logo")
    val logo: String? = null,

    @ColumnInfo(name = "tv_channel")
    val tvChannel: String? = "Azam Sports TV",

    @ColumnInfo(name = "notes")
    val notes: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val displayName: String
        get() = when (participantsFormat) {
            "CHAMPION_VS_RUNNER_UP" -> "${leagueName} Community Shield"
            "TOP_FOUR" -> "${leagueName} Super Cup"
            "CHAMPION_VS_CUP_WINNER" -> "${leagueName} Charity Shield"
            else -> "${leagueName} Shield"
        }

    val matchDisplay: String
        get() = if (isPlayed && winner != null) {
            "$winner won the ${displayName}"
        } else if (homeTeam != null && awayTeam != null) {
            "$homeTeam vs $awayTeam"
        } else {
            "TBD"
        }

    val scoreline: String
        get() = if (homeScore != null && awayScore != null) {
            "$homeScore - $awayScore"
        } else {
            "Not Played"
        }

    val isChampionVsRunnerUp: Boolean
        get() = participantsFormat == "CHAMPION_VS_RUNNER_UP"

    val isTopFour: Boolean
        get() = participantsFormat == "TOP_FOUR"

    val isChampionVsCupWinner: Boolean
        get() = participantsFormat == "CHAMPION_VS_CUP_WINNER"
}

// ============ ENUMS ============

enum class ShieldFormat(val value: String) {
    CHAMPION_VS_RUNNER_UP("CHAMPION_VS_RUNNER_UP"),
    TOP_FOUR("TOP_FOUR"),
    CHAMPION_VS_CUP_WINNER("CHAMPION_VS_CUP_WINNER")
}

enum class ShieldStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED
}