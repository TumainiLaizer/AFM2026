package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "player_loans",
    foreignKeys = [
        ForeignKey(
            entity = PlayersEntity::class,
            parentColumns = ["name"],
            childColumns = ["player_name"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["loaning_team"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["receiving_team"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["player_name"]),
        Index(value = ["loaning_team"]),
        Index(value = ["receiving_team"]),
        Index(value = ["status"]),
        Index(value = ["start_date"]),
        Index(value = ["end_date"]),
        Index(value = ["option_to_buy"]),
        Index(value = ["season"])
    ]
)
data class PlayerLoansEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "player_name")
    val playerName: String,

    @ColumnInfo(name = "player_id")
    val playerId: Int,

    @ColumnInfo(name = "loaning_team")
    val loaningTeam: String,  // Owner club

    @ColumnInfo(name = "loaning_team_id")
    val loaningTeamId: Int,

    @ColumnInfo(name = "receiving_team")
    val receivingTeam: String,  // Loanee club

    @ColumnInfo(name = "receiving_team_id")
    val receivingTeamId: Int,

    @ColumnInfo(name = "season")
    val season: String,

    @ColumnInfo(name = "start_date")
    val startDate: String,

    @ColumnInfo(name = "end_date")
    val endDate: String,

    @ColumnInfo(name = "duration", defaultValue = "6")
    val duration: Int = 6,  // In months

    @ColumnInfo(name = "loan_fee")
    val loanFee: Int? = null,

    @ColumnInfo(name = "wage_contribution")
    val wageContribution: Int = 100,  // Percentage (50-100)

    @ColumnInfo(name = "option_to_buy", defaultValue = "0")
    val optionToBuy: Boolean = false,

    @ColumnInfo(name = "buy_option_fee")
    val buyOptionFee: Int? = null,

    @ColumnInfo(name = "mandatory_buy")
    val mandatoryBuy: Boolean = false,

    @ColumnInfo(name = "mandatory_buy_fee")
    val mandatoryBuyFee: Int? = null,

    @ColumnInfo(name = "games_played")
    val gamesPlayed: Int = 0,

    @ColumnInfo(name = "goals_scored")
    val goalsScored: Int = 0,

    @ColumnInfo(name = "assists_made")
    val assistsMade: Int = 0,

    @ColumnInfo(name = "status", defaultValue = "Active")
    val status: String = "Active",  // Active, Completed, EarlyReturn, BuyOptionTriggered

    @ColumnInfo(name = "recall_option")
    val recallOption: Boolean = false,

    @ColumnInfo(name = "recall_date")
    val recallDate: String? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val isActive: Boolean
        get() = status == "Active"

    val isCompleted: Boolean
        get() = status == "Completed"

    val isOptionToBuy: Boolean
        get() = optionToBuy

    val isMandatoryBuy: Boolean
        get() = mandatoryBuy

    val loanFeeInMillions: Double
        get() = (loanFee ?: 0) / 1_000_000.0

    val buyOptionInMillions: Double
        get() = (buyOptionFee ?: 0) / 1_000_000.0

    val loanSummary: String
        get() = "$playerName: $loaningTeam → $receivingTeam (${duration} months)"

    val monthsRemaining: Int
        get() {
            // Calculate based on end_date
            return duration
        }
}

// ============ ENUMS ============

enum class LoanStatus(val value: String) {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    EARLY_RETURN("EarlyReturn"),
    BUY_OPTION_TRIGGERED("BuyOptionTriggered")
}