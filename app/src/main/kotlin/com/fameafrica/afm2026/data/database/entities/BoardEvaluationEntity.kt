package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "board_evaluation",
    foreignKeys = [
        ForeignKey(
            entity = ManagersEntity::class,
            parentColumns = ["name"],
            childColumns = ["manager_name"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["manager_name"], unique = true),
        Index(value = ["board_satisfaction"]),
        Index(value = ["status"])
    ]
)
data class BoardEvaluationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "manager_name")
    val managerName: String,

    @ColumnInfo(name = "board_satisfaction", defaultValue = "50")
    val boardSatisfaction: Int = 50,

    @ColumnInfo(name = "recent_results")
    val recentResults: String? = null,  // JSON string of last 5 results

    @ColumnInfo(name = "financial_status")
    val financialStatus: String? = null,  // Rich, Healthy, Stable, Breaking Even, In Debt

    @ColumnInfo(name = "status", defaultValue = "Safe")
    val status: String = "Safe"  // Safe, Under Review, On Thin Ice, Critical, Sacked
) {

    // ============ COMPUTED PROPERTIES ============

    val satisfactionLevel: String
        get() = when {
            boardSatisfaction >= 90 -> "Ecstatic"
            boardSatisfaction >= 75 -> "Very Happy"
            boardSatisfaction >= 60 -> "Satisfied"
            boardSatisfaction >= 45 -> "Neutral"
            boardSatisfaction >= 30 -> "Disappointed"
            boardSatisfaction >= 15 -> "Angry"
            else -> "Furious"
        }

    val isSafe: Boolean
        get() = status == "Safe"

    val isUnderReview: Boolean
        get() = status == "Under Review"

    val isOnThinIce: Boolean
        get() = status == "On Thin Ice"

    val isCritical: Boolean
        get() = status == "Critical"

    val isSacked: Boolean
        get() = status == "Sacked"

    val statusColor: String
        get() = when (status) {
            "Safe" -> "Green"
            "Under Review" -> "Yellow"
            "On Thin Ice" -> "Orange"
            "Critical" -> "Red"
            "Sacked" -> "Dark Red"
            else -> "Gray"
        }
}

// ============ ENUMS ============

