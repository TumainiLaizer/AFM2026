package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "finances",
    foreignKeys = [
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["id"],
            childColumns = ["team_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["team_id"], unique = true),
        Index(value = ["season"]),
        Index(value = ["financial_tier"])
    ]
)
data class FinancesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "team_id")
    val teamId: Int,

    @ColumnInfo(name = "team_name")
    val teamName: String,

    @ColumnInfo(name = "season")
    val season: String,

    // ============ CORE FINANCIALS ============
    @ColumnInfo(name = "revenue", defaultValue = "0")
    val revenue: Long = 0,

    @ColumnInfo(name = "expenses", defaultValue = "0")
    val expenses: Long = 0,

    @ColumnInfo(name = "budget", defaultValue = "0")
    val budget: Long = 0,  // Available transfer/wage budget

    @ColumnInfo(name = "profit_loss", defaultValue = "0")
    val profitLoss: Long = 0,

    @ColumnInfo(name = "bank_balance", defaultValue = "0")
    val bankBalance: Long = 0,

    // ============ REVENUE STREAMS ============
    @ColumnInfo(name = "sponsorship_revenue", defaultValue = "0")
    val sponsorshipRevenue: Long = 0,

    @ColumnInfo(name = "broadcasting_revenue", defaultValue = "0")
    val broadcastingRevenue: Long = 0,

    @ColumnInfo(name = "matchday_revenue", defaultValue = "0")
    val matchdayRevenue: Long = 0,  // Tickets, hospitality

    @ColumnInfo(name = "merchandise_revenue", defaultValue = "0")
    val merchandiseRevenue: Long = 0,

    @ColumnInfo(name = "prize_money", defaultValue = "0")
    val prizeMoney: Long = 0,  // League and cup prizes

    @ColumnInfo(name = "continental_prize_money", defaultValue = "0")
    val continentalPrizeMoney: Long = 0,  // CAF competitions

    @ColumnInfo(name = "player_sales", defaultValue = "0")
    val playerSales: Long = 0,  // Transfer income

    @ColumnInfo(name = "loan_income", defaultValue = "0")
    val loanIncome: Long = 0,  // Loan fees received

    @ColumnInfo(name = "membership_revenue", defaultValue = "0")
    val membershipRevenue: Long = 0,  // Fan memberships

    @ColumnInfo(name = "other_revenue", defaultValue = "0")
    val otherRevenue: Long = 0,

    // ============ EXPENSES ============
    @ColumnInfo(name = "wage_bill", defaultValue = "0")
    val wageBill: Long = 0,  // Total player salaries

    @ColumnInfo(name = "staff_wages", defaultValue = "0")
    val staffWages: Long = 0,  // Manager and staff salaries

    @ColumnInfo(name = "transfer_spending", defaultValue = "0")
    val transferSpending: Long = 0,  // Player purchases

    @ColumnInfo(name = "loan_fees", defaultValue = "0")
    val loanFees: Long = 0,  // Loan fees paid

    @ColumnInfo(name = "agent_fees", defaultValue = "0")
    val agentFees: Long = 0,

    @ColumnInfo(name = "infrastructure_costs", defaultValue = "0")
    val infrastructureCosts: Long = 0,  // Stadium maintenance, upgrades

    @ColumnInfo(name = "youth_academy_costs", defaultValue = "0")
    val youthAcademyCosts: Long = 0,

    @ColumnInfo(name = "travel_costs", defaultValue = "0")
    val travelCosts: Long = 0,  // Away matches, continental travel

    @ColumnInfo(name = "operational_costs", defaultValue = "0")
    val operationalCosts: Long = 0,  // Day-to-day running

    @ColumnInfo(name = "taxes", defaultValue = "0")
    val taxes: Long = 0,

    @ColumnInfo(name = "other_expenses", defaultValue = "0")
    val otherExpenses: Long = 0,

    // ============ FINANCIAL METRICS ============
    @ColumnInfo(name = "financial_tier")
    val financialTier: String? = null,  // RICH, UPPER_MIDDLE, MIDDLE, LOWER, POOR

    @ColumnInfo(name = "debt", defaultValue = "0")
    val debt: Long = 0,

    @ColumnInfo(name = "credit_rating")
    val creditRating: Int? = null,  // 0-100

    @ColumnInfo(name = "last_updated")
    val lastUpdated: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val revenueInMillions: Double
        get() = revenue / 1_000_000.0

    val expensesInMillions: Double
        get() = expenses / 1_000_000.0

    val profitLossInMillions: Double
        get() = profitLoss / 1_000_000.0

    val budgetInMillions: Double
        get() = budget / 1_000_000.0

    val isProfitable: Boolean
        get() = profitLoss > 0

    val isInDebt: Boolean
        get() = debt > 0

    val financialHealth: String
        get() = when {
            bankBalance > 50_000_000 && debt == 0L -> "Excellent"
            bankBalance > 20_000_000 && debt < 5_000_000 -> "Good"
            bankBalance > 5_000_000 && debt < 10_000_000 -> "Stable"
            bankBalance > 0 && debt < 20_000_000 -> "Fair"
            else -> "Concerning"
        }
}

// ============ ENUMS ============

enum class FinancialTier(val value: String, val minRevenue: Long) {
    RICH("Rich", 50_000_000),           // $50M+ (Al Ahly, Sundowns)
    UPPER_MIDDLE("Upper Middle", 20_000_000), // $20-50M (Yanga, Esperance)
    MIDDLE("Middle", 8_000_000),         // $8-20M (Most top division)
    LOWER("Lower", 2_000_000),           // $2-8M (Smaller top division)
    POOR("Poor", 0)                       // <$2M (Lower divisions)
}

enum class RevenueSource(val value: String) {
    SPONSORSHIP("Sponsorship"),
    BROADCASTING("Broadcasting"),
    MATCHDAY("Matchday"),
    MERCHANDISE("Merchandise"),
    PRIZE_MONEY("Prize Money"),
    CONTINENTAL("Continental"),
    PLAYER_SALES("Player Sales"),
    MEMBERSHIP("Membership")
}