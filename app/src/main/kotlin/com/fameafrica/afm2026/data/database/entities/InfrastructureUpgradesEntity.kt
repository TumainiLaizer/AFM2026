package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "infrastructure_upgrades",
    foreignKeys = [
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["team_name"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["team_name"]),
        Index(value = ["upgrade_type"]),
        Index(value = ["status"]),
        Index(value = ["completion_date"]),
        Index(value = ["upgrade_level"])
    ]
)
data class InfrastructureUpgradesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "team_name")
    val teamName: String,

    @ColumnInfo(name = "team_id")
    val teamId: Int,

    @ColumnInfo(name = "upgrade_type")
    val upgradeType: String,  // STADIUM, TRAINING_FACILITY, YOUTH_ACADEMY, MEDICAL_CENTER, FAN_ZONE

    @ColumnInfo(name = "upgrade_level")
    val upgradeLevel: Int,  // Current level (1-5)

    @ColumnInfo(name = "target_level")
    val targetLevel: Int,  // Target level after upgrade

    @ColumnInfo(name = "cost")
    val cost: Long,

    @ColumnInfo(name = "status", defaultValue = "Pending")
    val status: String,  // Pending, In Progress, Completed, Cancelled

    @ColumnInfo(name = "start_date")
    val startDate: String,

    @ColumnInfo(name = "completion_date")
    val completionDate: String,

    @ColumnInfo(name = "actual_completion_date")
    val actualCompletionDate: String? = null,

    @ColumnInfo(name = "benefit_description")
    val benefitDescription: String? = null,

    @ColumnInfo(name = "capacity_increase")
    val capacityIncrease: Int? = null,  // For stadium upgrades

    @ColumnInfo(name = "training_efficiency_increase")
    val trainingEfficiencyIncrease: Int? = null,  // For training facilities

    @ColumnInfo(name = "youth_talent_increase")
    val youthTalentIncrease: Int? = null,  // For youth academy

    @ColumnInfo(name = "injury_recovery_boost")
    val injuryRecoveryBoost: Int? = null,  // For medical center

    @ColumnInfo(name = "fan_capacity_increase")
    val fanCapacityIncrease: Int? = null,  // For fan zone

    @ColumnInfo(name = "notes")
    val notes: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val isPending: Boolean
        get() = status == "Pending"

    val isInProgress: Boolean
        get() = status == "In Progress"

    val isCompleted: Boolean
        get() = status == "Completed"

    val isCancelled: Boolean
        get() = status == "Cancelled"

    val costInMillions: Double
        get() = cost / 1_000_000.0

    val upgradeName: String
        get() = when (upgradeType) {
            "STADIUM" -> "Stadium Expansion"
            "TRAINING_FACILITY" -> "Training Facility Upgrade"
            "YOUTH_ACADEMY" -> "Youth Academy Improvement"
            "MEDICAL_CENTER" -> "Medical Center Upgrade"
            "FAN_ZONE" -> "Fan Zone Expansion"
            else -> upgradeType
        }
}

// ============ ENUMS ============

enum class UpgradeType(val value: String) {
    STADIUM("STADIUM"),
    TRAINING_FACILITY("TRAINING_FACILITY"),
    YOUTH_ACADEMY("YOUTH_ACADEMY"),
    MEDICAL_CENTER("MEDICAL_CENTER"),
    FAN_ZONE("FAN_ZONE")
}

enum class UpgradeStatus(val value: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class UpgradeLevel(val level: Int, val multiplier: Double) {
    BASIC(1, 1.0),
    IMPROVED(2, 1.5),
    ADVANCED(3, 2.0),
    ELITE(4, 2.5),
    WORLD_CLASS(5, 3.0)
}