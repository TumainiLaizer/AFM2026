package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "manager_offers",
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
            childColumns = ["offering_team"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["manager_id"]),
        Index(value = ["offering_team"]),
        Index(value = ["status"]),
        Index(value = ["offer_type"]),
        Index(value = ["league_level"]),
        Index(value = ["offer_date"])
    ]
)
data class ManagerOffersEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "manager_id")
    val managerId: Int,

    @ColumnInfo(name = "manager_name")
    val managerName: String,

    @ColumnInfo(name = "offering_team")
    val offeringTeam: String,

    @ColumnInfo(name = "offering_team_id")
    val offeringTeamId: Int,

    @ColumnInfo(name = "league_name")
    val leagueName: String,

    @ColumnInfo(name = "league_level")
    val leagueLevel: Int,  // 1-5, where 1 is highest, 5 is lowest

    @ColumnInfo(name = "offered_salary")
    val offeredSalary: Int,

    @ColumnInfo(name = "contract_years")
    val contractYears: Int,

    @ColumnInfo(name = "transfer_fee")
    val transferFee: Int? = null,  // Compensation to current club

    @ColumnInfo(name = "logo")
    val logo: String? = null,

    @ColumnInfo(name = "status", defaultValue = "pending")
    val status: String = "pending",  // pending, accepted, rejected, expired, withdrawn

    @ColumnInfo(name = "offer_type")
    val offerType: String,  // HEAD_COACH, ASSISTANT_MANAGER, SPORTING_DIRECTOR, etc.

    @ColumnInfo(name = "offer_date")
    val offerDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "expiry_date")
    val expiryDate: Long,  // Offer expires after 7 days

    @ColumnInfo(name = "is_mid_season")
    val isMidSeason: Boolean = false,

    @ColumnInfo(name = "is_promotion")
    val isPromotion: Boolean = false,  // Moving to higher level league

    @ColumnInfo(name = "message")
    val message: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiryDate

    val isPending: Boolean
        get() = status == "pending" && !isExpired

    val isAccepted: Boolean
        get() = status == "accepted"

    val isRejected: Boolean
        get() = status == "rejected"

    val salaryInMillions: Double
        get() = offeredSalary / 1_000_000.0

    val transferFeeInMillions: Double
        get() = (transferFee ?: 0) / 1_000_000.0

    val leagueTier: String
        get() = when (leagueLevel) {
            1 -> "Elite"
            2 -> "Professional"
            3 -> "Championship"
            4 -> "League One"
            5 -> "League Two"
            else -> "Non-League"
        }

    val isStepUp: Boolean
        get() = leagueLevel <= 3  // Level 1-3 are considered step up from level 5 start

    val daysRemaining: Int
        get() = ((expiryDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
}

// ============ ENUMS ============

enum class OfferStatus(val value: String) {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    EXPIRED("expired"),
    WITHDRAWN("withdrawn")
}

enum class ManagerOfferType(val value: String) {
    HEAD_COACH("HEAD_COACH"),
    ASSISTANT_MANAGER("ASSISTANT_MANAGER"),
    SPORTING_DIRECTOR("SPORTING_DIRECTOR"),
    TECHNICAL_DIRECTOR("TECHNICAL_DIRECTOR"),
    YOUTH_COACH("YOUTH_COACH"),
    GOALKEEPER_COACH("GOALKEEPER_COACH"),
    FITNESS_COACH("FITNESS_COACH"),
    SCOUT("SCOUT"),
    PHYSIOTHERAPIST("PHYSIOTHERAPIST"),
    CLUB_MEDIA_OFFICER("CLUB_MEDIA_OFFICER"),
    ACADEMY_MANAGER("ACADEMY_MANAGER"),
    CHIEF_SCOUT("CHIEF_SCOUT")
}