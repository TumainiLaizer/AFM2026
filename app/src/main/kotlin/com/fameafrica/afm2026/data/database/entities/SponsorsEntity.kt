package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Calendar

@Entity(
    tableName = "sponsors",
    foreignKeys = [
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["team_name"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["team_name"]),
        Index(value = ["sponsor_type"]),
        Index(value = ["sponsorship_value"]),
        Index(value = ["is_active"]),
        Index(value = ["can_fund_transfers"])
    ]
)
data class SponsorsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "logo")
    val logo: String? = null,

    @ColumnInfo(name = "sponsor_type")
    val sponsorType: String,  // TITLE_SPONSOR, BROADCAST_PARTNER, BETTING_PARTNER, OFFICIAL_BEVERAGE, CLUB_SPONSOR, TRANSFER_FUNDER, PLACEHOLDER

    @ColumnInfo(name = "sponsorship_value")
    val sponsorshipValue: Long = 50_000_000,  // Annual sponsorship value

    @ColumnInfo(name = "contract_duration", defaultValue = "3")
    val contractDuration: Int = 3,  // In years

    @ColumnInfo(name = "performance_bonus", defaultValue = "10000000")
    val performanceBonus: Long = 10_000_000,

    @ColumnInfo(name = "transfer_funding_limit")
    val transferFundingLimit: Long? = null,  // Maximum amount sponsor will fund for transfers

    @ColumnInfo(name = "transfer_funding_used")
    val transferFundingUsed: Long = 0,  // Amount already used this season

    @ColumnInfo(name = "funding_per_season")
    val fundingPerSeason: Long? = null,  // Annual transfer funding budget

    @ColumnInfo(name = "requires_objectives_met")
    val requiresObjectivesMet: Boolean = false,  // Whether sponsor requires objectives to be met before funding

    @ColumnInfo(name = "team_name")
    val teamName: String? = null,  // Null for title sponsors not tied to specific team

    @ColumnInfo(name = "contract_start_date")
    val contractStartDate: String? = null,

    @ColumnInfo(name = "contract_end_date")
    val contractEndDate: String? = null,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "can_fund_transfers")
    val canFundTransfers: Boolean = false,  // Whether this sponsor can fund transfer requests

    @ColumnInfo(name = "min_performance_rating")
    val minPerformanceRating: Int? = null,  // Minimum team performance rating required

    @ColumnInfo(name = "max_foreign_players")
    val maxForeignPlayers: Int? = null,  // Maximum foreign players they'll fund

    @ColumnInfo(name = "preferred_player_nationalities")
    val preferredPlayerNationalities: String? = null,  // JSON array or comma-separated list

    @ColumnInfo(name = "notes")
    val notes: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val sponsorshipValueInMillions: Double
        get() = sponsorshipValue / 1_000_000.0

    val performanceBonusInMillions: Double
        get() = performanceBonus / 1_000_000.0

    val transferFundingLimitInMillions: Double?
        get() = transferFundingLimit?.let { it / 1_000_000.0 }

    val remainingTransferFunding: Long?
        get() = transferFundingLimit?.minus(transferFundingUsed)

    val isTitleSponsor: Boolean
        get() = sponsorType == "TITLE_SPONSOR"

    val isClubSponsor: Boolean
        get() = sponsorType == "CLUB_SPONSOR"

    val isBroadcastPartner: Boolean
        get() = sponsorType == "BROADCAST_PARTNER"

    val isBettingPartner: Boolean
        get() = sponsorType == "BETTING_PARTNER"

    val isPlaceholder: Boolean
        get() = sponsorType == "PLACEHOLDER"

    val isTransferFunder: Boolean
        get() = canFundTransfers

    val contractRemainingYears: Int
        get() {
            if (contractEndDate == null) return 0
            return try {
                val endYear = contractEndDate.split("-").first().toInt()
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                (endYear - currentYear).coerceAtLeast(0)
            } catch (e: Exception) {
                0
            }
        }
}

// ============ ENUMS ============

enum class SponsorType(val value: String) {
    TITLE_SPONSOR("TITLE_SPONSOR"),
    BROADCAST_PARTNER("BROADCAST_PARTNER"),
    BETTING_PARTNER("BETTING_PARTNER"),
    OFFICIAL_BEVERAGE("OFFICIAL_BEVERAGE"),
    CLUB_SPONSOR("CLUB_SPONSOR"),
    TRANSFER_FUNDER("TRANSFER_FUNDER"),
    TOURISM_PARTNER("TOURISM_PARTNER"),
    TELECOM_PARTNER("TELECOM_PARTNER"),
    INSURANCE_PARTNER("INSURANCE_PARTNER"),
    REAL_ESTATE_PARTNER("REAL_ESTATE_PARTNER"),
    PLACEHOLDER("PLACEHOLDER")
}

enum class SponsorStatus {
    ACTIVE,
    EXPIRED,
    PENDING_RENEWAL,
    TERMINATED
}