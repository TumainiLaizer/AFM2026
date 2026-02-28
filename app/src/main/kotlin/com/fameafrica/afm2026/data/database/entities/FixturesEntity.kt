package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "fixtures",
    foreignKeys = [
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["home_team"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["away_team"],
            onDelete = ForeignKey.CASCADE,
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
            entity = LeaguesEntity::class,
            parentColumns = ["name"],
            childColumns = ["league"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CupsEntity::class,
            parentColumns = ["name"],
            childColumns = ["cup_name"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["match_date"]),
        Index(value = ["home_team", "away_team", "match_date"], unique = true),
        Index(value = ["referee_id"]),
        Index(value = ["league"]),
        Index(value = ["cup_name"]),
        Index(value = ["match_status"]),
        Index(value = ["season"]),
        Index(value = ["match_type"])
    ]
)
data class FixturesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "match_date")
    val matchDate: String,  // Format: YYYY-MM-DD HH:MM

    @ColumnInfo(name = "home_team")
    val homeTeam: String,

    @ColumnInfo(name = "away_team")
    val awayTeam: String,

    @ColumnInfo(name = "home_score", defaultValue = "0")
    val homeScore: Int = 0,

    @ColumnInfo(name = "away_score", defaultValue = "0")
    val awayScore: Int = 0,

    @ColumnInfo(name = "weather_conditions", defaultValue = "Clear")
    val weatherConditions: String = "Clear",

    @ColumnInfo(name = "stadium", defaultValue = "FAME Africa Stadium")
    val stadium: String = "FAME Africa Stadium",

    @ColumnInfo(name = "referee_id")  // FK to referees table
    val refereeId: Int? = null,

    @ColumnInfo(name = "tv_channel", defaultValue = "Azam Sports TV")
    val tvChannel: String = "Azam Sports TV",

    @ColumnInfo(name = "match_type", defaultValue = "League")
    val matchType: String = "League",  // League, Cup, Friendly, Preseason Tour, Playoff, International

    @ColumnInfo(name = "postseason", defaultValue = "0")
    val postseason: Int = 0,  // 0 = regular season, 1 = playoffs, 2 = finals

    @ColumnInfo(name = "rescheduled_date")
    val rescheduledDate: String? = null,

    @ColumnInfo(name = "season", defaultValue = "2024/25")
    val season: String = "2024/25",

    @ColumnInfo(name = "league")  // FK to leagues table
    val league: String? = null,

    @ColumnInfo(name = "cup_name")  // FK to cups table
    val cupName: String? = null,

    @ColumnInfo(name = "match_status")
    val matchStatus: String? = null,  // SCHEDULED, LIVE, COMPLETED, POSTPONED, CANCELLED

    @ColumnInfo(name = "position")
    val position: Int = 0,  // Game week / round number

    @ColumnInfo(name = "round", defaultValue = "Round 1")
    val round: String = "Round 1",

    @ColumnInfo(name = "timeZone", defaultValue = "Africa/Dar es Salaam")
    val timeZone: String = "Africa/Dar es Salaam",

    @ColumnInfo(name = "badgeTag", defaultValue = "None")
    val badgeTag: String = "None"
) {

    // ============ COMPUTED PROPERTIES ============

    val isCompleted: Boolean
        get() = matchStatus == "COMPLETED"

    val isLive: Boolean
        get() = matchStatus == "LIVE"

    val isScheduled: Boolean
        get() = matchStatus == "SCHEDULED"

    val isPostponed: Boolean
        get() = matchStatus == "POSTPONED"

    val isCancelled: Boolean
        get() = matchStatus == "CANCELLED"

    val isLeagueMatch: Boolean
        get() = matchType == "League" && league != null

    val isCupMatch: Boolean
        get() = matchType == "Cup" && cupName != null

    val isInternational: Boolean
        get() = matchType == "International"

    val isFriendly: Boolean
        get() = matchType == "Friendly"

    val isPlayoff: Boolean
        get() = matchType == "Playoff" || postseason > 0

    val winner: String?
        get() = when {
            !isCompleted -> null
            homeScore > awayScore -> homeTeam
            awayScore > homeScore -> awayTeam
            else -> "Draw"
        }

    val loser: String?
        get() = when {
            !isCompleted -> null
            homeScore > awayScore -> awayTeam
            awayScore > homeScore -> homeTeam
            else -> null
        }

    val result: String
        get() = when {
            !isCompleted -> "Not Played"
            homeScore > awayScore -> "$homeTeam Win"
            awayScore > homeScore -> "$awayTeam Win"
            else -> "Draw"
        }

    val scoreline: String
        get() = "$homeTeam $homeScore - $awayScore $awayTeam"

    // ============ BUSINESS METHODS ============

    fun updateScore(home: Int, away: Int): FixturesEntity {
        return this.copy(
            homeScore = home,
            awayScore = away,
            matchStatus = "COMPLETED"
        )
    }

    fun postpone(newDate: String): FixturesEntity {
        return this.copy(
            matchStatus = "POSTPONED",
            rescheduledDate = newDate
        )
    }

    fun reschedule(newDate: String): FixturesEntity {
        return this.copy(
            matchDate = newDate,
            matchStatus = "SCHEDULED",
            rescheduledDate = null
        )
    }

    fun cancel(): FixturesEntity {
        return this.copy(
            matchStatus = "CANCELLED"
        )
    }

    fun start(): FixturesEntity {
        return this.copy(
            matchStatus = "LIVE"
        )
    }

    fun getHomeTeamId(): Int = 0  // Will be populated via JOIN
    fun getAwayTeamId(): Int = 0  // Will be populated via JOIN
}