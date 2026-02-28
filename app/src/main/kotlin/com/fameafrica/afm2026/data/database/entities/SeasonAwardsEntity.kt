package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "season_awards",
    foreignKeys = [
        ForeignKey(
            entity = LeaguesEntity::class,
            parentColumns = ["name"],
            childColumns = ["league_name"],
            onDelete = ForeignKey.SET_NULL,
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
            entity = TeamsEntity::class,
            parentColumns = ["id"],
            childColumns = ["team_id"],
            onDelete = ForeignKey.CASCADE,
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
            entity = ManagersEntity::class,
            parentColumns = ["name"],
            childColumns = ["coach_name"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["season", "award_type"], unique = true),
        Index(value = ["season"]),
        Index(value = ["award_type"]),
        Index(value = ["player_id"]),
        Index(value = ["team_id"]),
        Index(value = ["league_name"])
    ]
)
data class SeasonAwardsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "season")
    val season: String,

    @ColumnInfo(name = "season_year")
    val seasonYear: Int,

    @ColumnInfo(name = "award_type")
    val awardType: String,  // PLAYER_OF_THE_SEASON, TOP_SCORER, BEST_ASSISTER, BEST_GOALKEEPER, BEST_DEFENDER, BEST_MIDFIELDER, BEST_FORWARD, YOUNG_PLAYER, COACH_OF_THE_SEASON, FAIR_PLAY, GOAL_OF_THE_SEASON

    @ColumnInfo(name = "award_category")
    val awardCategory: String,  // LEAGUE, CUP, CONTINENTAL, NATIONAL

    @ColumnInfo(name = "player_id")
    val playerId: Int? = null,

    @ColumnInfo(name = "player_name")
    val playerName: String? = null,

    @ColumnInfo(name = "team_id")
    val teamId: Int? = null,

    @ColumnInfo(name = "team_name")
    val teamName: String? = null,

    @ColumnInfo(name = "coach_name")
    val coachName: String? = null,

    @ColumnInfo(name = "manager_id")
    val managerId: Int? = null,

    @ColumnInfo(name = "league_name")
    val leagueName: String? = null,

    @ColumnInfo(name = "matches_played", defaultValue = "0")
    val matchesPlayed: Int = 0,

    @ColumnInfo(name = "goals", defaultValue = "0")
    val goals: Int? = 0,

    @ColumnInfo(name = "assists", defaultValue = "0")
    val assists: Int? = 0,

    @ColumnInfo(name = "clean_sheets")
    val cleanSheets: Int? = null,

    @ColumnInfo(name = "rating", defaultValue = "7.0")
    val rating: Double = 7.0,

    @ColumnInfo(name = "appearances")
    val appearances: Int? = null,

    @ColumnInfo(name = "yellow_cards")
    val yellowCards: Int? = null,

    @ColumnInfo(name = "red_cards")
    val redCards: Int? = null,

    @ColumnInfo(name = "prize_money")
    val prizeMoney: Int? = null,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "citation")
    val citation: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val awardDisplay: String
        get() = when (awardType) {
            "PLAYER_OF_THE_SEASON" -> "Player of the Season"
            "TOP_SCORER" -> "Top Scorer"
            "BEST_ASSISTER" -> "Best Assister"
            "BEST_GOALKEEPER" -> "Best Goalkeeper"
            "BEST_DEFENDER" -> "Best Defender"
            "BEST_MIDFIELDER" -> "Best Midfielder"
            "BEST_FORWARD" -> "Best Forward"
            "YOUNG_PLAYER" -> "Young Player of the Season"
            "COACH_OF_THE_SEASON" -> "Coach of the Season"
            "FAIR_PLAY" -> "Fair Play Award"
            "GOAL_OF_THE_SEASON" -> "Goal of the Season"
            else -> awardType.replace('_', ' ')
        }

    val prizeMoneyInMillions: Double
        get() = (prizeMoney ?: 0) / 1_000_000.0

    val recipient: String
        get() = playerName ?: coachName ?: teamName ?: "Unknown"
}

// ============ ENUMS ============

enum class AwardType(val value: String) {
    PLAYER_OF_THE_SEASON("PLAYER_OF_THE_SEASON"),
    TOP_SCORER("TOP_SCORER"),
    BEST_ASSISTER("BEST_ASSISTER"),
    BEST_GOALKEEPER("BEST_GOALKEEPER"),
    BEST_DEFENDER("BEST_DEFENDER"),
    BEST_MIDFIELDER("BEST_MIDFIELDER"),
    BEST_FORWARD("BEST_FORWARD"),
    YOUNG_PLAYER("YOUNG_PLAYER"),
    COACH_OF_THE_SEASON("COACH_OF_THE_SEASON"),
    FAIR_PLAY("FAIR_PLAY"),
    GOAL_OF_THE_SEASON("GOAL_OF_THE_SEASON")
}

enum class AwardCategory(val value: String) {
    LEAGUE("LEAGUE"),
    CUP("CUP"),
    CONTINENTAL("CONTINENTAL"),
    NATIONAL("NATIONAL")
}