package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "manager_offers_for_retired_players",
    foreignKeys = [
        ForeignKey(
            entity = PlayersEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["offered_team"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["player_id"]),
        Index(value = ["offered_team"]),
        Index(value = ["status"]),
        Index(value = ["role_type"]),
        Index(value = ["offer_date"])
    ]
)
data class ManagerOffersForRetiredPlayersEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "player_id")
    val playerId: Int,

    @ColumnInfo(name = "player_name")
    val playerName: String,

    @ColumnInfo(name = "offered_team")
    val offeredTeam: String,

    @ColumnInfo(name = "offered_team_id")
    val offeredTeamId: Int,

    @ColumnInfo(name = "league_name")
    val leagueName: String,

    @ColumnInfo(name = "league_level")
    val leagueLevel: Int,

    @ColumnInfo(name = "offered_salary", defaultValue = "500000")
    val offeredSalary: Int = 500000,

    @ColumnInfo(name = "contract_years")
    val contractYears: Int = 2,

    @ColumnInfo(name = "status", defaultValue = "Pending")
    val status: String = "Pending",

    @ColumnInfo(name = "role_type")
    val roleType: String,  // PLAYER_COACH, ASSISTANT_MANAGER, SPORTING_DIRECTOR, SCOUT, etc.

    @ColumnInfo(name = "role_description")
    val roleDescription: String? = null,

    @ColumnInfo(name = "offer_date")
    val offerDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "expiry_date")
    val expiryDate: Long,

    @ColumnInfo(name = "message")
    val message: String? = null,

    @ColumnInfo(name = "logo")
    val logo: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiryDate

    val isPending: Boolean
        get() = status == "Pending" && !isExpired

    val isAccepted: Boolean
        get() = status == "Accepted"

    val isRejected: Boolean
        get() = status == "Rejected"

    val salaryInMillions: Double
        get() = offeredSalary / 1_000_000.0

    val daysRemaining: Int
        get() = ((expiryDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
}

// ============ ENUMS ============

enum class RetiredPlayerRoleType(val value: String) {
    PLAYER_COACH("PLAYER_COACH"),
    ASSISTANT_MANAGER("ASSISTANT_MANAGER"),
    SPORTING_DIRECTOR("SPORTING_DIRECTOR"),
    TECHNICAL_DIRECTOR("TECHNICAL_DIRECTOR"),
    YOUTH_COACH("YOUTH_COACH"),
    GOALKEEPER_COACH("GOALKEEPER_COACH"),
    FITNESS_COACH("FITNESS_COACH"),
    SCOUT("SCOUT"),
    CHIEF_SCOUT("CHIEF_SCOUT"),
    PHYSIOTHERAPIST("PHYSIOTHERAPIST"),
    CLUB_MEDIA_OFFICER("CLUB_MEDIA_OFFICER"),
    ACADEMY_MANAGER("ACADEMY_MANAGER")
}