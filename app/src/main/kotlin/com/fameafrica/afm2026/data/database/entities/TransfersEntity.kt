package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "transfers",
    foreignKeys = [
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["current_team"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["target_team"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlayersEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["player_name"]),
        Index(value = ["player_id"]),
        Index(value = ["current_team"]),
        Index(value = ["target_team"]),
        Index(value = ["transfer_status"]),
        Index(value = ["transfer_type"]),
        Index(value = ["timestamp"]),
        Index(value = ["scout_rating"]),
        Index(value = ["is_loan_to_buy"]),
        Index(value = ["window_id"])
    ]
)
data class TransfersEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "player_id")
    val playerId: Int,

    @ColumnInfo(name = "player_name")
    val playerName: String,

    @ColumnInfo(name = "current_team")
    val currentTeam: String,

    @ColumnInfo(name = "target_team")
    val targetTeam: String,

    @ColumnInfo(name = "transfer_fee")
    val transferFee: Int,

    @ColumnInfo(name = "contract_length", defaultValue = "3")
    val contractLength: Int = 3,

    @ColumnInfo(name = "monthly_wage")
    val monthlyWage: Int,

    @ColumnInfo(name = "transfer_type")
    val transferType: String,  // Buy, Loan, Free

    @ColumnInfo(name = "transfer_status")
    val transferStatus: String = "Pending",  // Pending, Negotiating, Accepted, Rejected, Completed, Cancelled

    @ColumnInfo(name = "rumours")
    val rumours: String? = null,

    @ColumnInfo(name = "scout_rating", defaultValue = "70")
    val scoutRating: Int = 70,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "window_id")
    val windowId: Int? = null,

    @ColumnInfo(name = "is_loan_to_buy")
    val isLoanToBuy: Boolean = false,

    @ColumnInfo(name = "loan_buy_fee")
    val loanBuyFee: Int? = null,

    @ColumnInfo(name = "agent_fee")
    val agentFee: Int? = null,

    @ColumnInfo(name = "sell_on_percentage")
    val sellOnPercentage: Int? = null,  // Percentage of future sale

    @ColumnInfo(name = "signing_bonus")
    val signingBonus: Int? = null,

    @ColumnInfo(name = "relegation_release_clause")
    val relegationReleaseClause: Boolean = false,

    @ColumnInfo(name = "minimum_fee_release_clause")
    val minimumFeeReleaseClause: Int? = null,

    @ColumnInfo(name = "completed_date")
    val completedDate: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val isPending: Boolean
        get() = transferStatus == "Pending"

    val isNegotiating: Boolean
        get() = transferStatus == "Negotiating"

    val isAccepted: Boolean
        get() = transferStatus == "Accepted"

    val isRejected: Boolean
        get() = transferStatus == "Rejected"

    val isCompleted: Boolean
        get() = transferStatus == "Completed"

    val isCancelled: Boolean
        get() = transferStatus == "Cancelled"

    val isBuyTransfer: Boolean
        get() = transferType == "Buy"

    val isLoanTransfer: Boolean
        get() = transferType == "Loan"

    val isFreeTransfer: Boolean
        get() = transferType == "Free"

    val transferFeeInMillions: Double
        get() = transferFee / 1_000_000.0

    val monthlyWageInMillions: Double
        get() = monthlyWage / 1_000_000.0

    val annualWage: Int
        get() = monthlyWage * 12

    val totalPackageValue: Int
        get() = transferFee + (monthlyWage * 12 * contractLength)

    val transferValueRating: String
        get() = when {
            transferFee >= 50_000_000 -> "Record Breaking"
            transferFee >= 20_000_000 -> "Big Money"
            transferFee >= 10_000_000 -> "Significant"
            transferFee >= 5_000_000 -> "Moderate"
            transferFee >= 1_000_000 -> "Low"
            else -> "Minimal"
        }

    val displaySummary: String
        get() = buildString {
            append("$playerName: ")
            when (transferType) {
                "Buy" -> append("${transferFeeInMillions}M transfer")
                "Loan" -> append("Loan move${if (isLoanToBuy) " with option to buy" else ""}")
                "Free" -> append("Free transfer")
            }
        }
}
