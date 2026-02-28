package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "scout_assignments",
    foreignKeys = [
        ForeignKey(
            entity = StaffEntity::class,
            parentColumns = ["id"],
            childColumns = ["scout_id"],
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
        Index(value = ["scout_id"]),
        Index(value = ["player_id"]),
        Index(value = ["assigned_date"]),
        Index(value = ["completion_date"]),
        Index(value = ["report_status"]),
        Index(value = ["priority"]),
        Index(value = ["scout_id", "player_id"], unique = true)
    ]
)
data class ScoutAssignmentsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "scout_id")
    val scoutId: Int,

    @ColumnInfo(name = "scout_name")
    val scoutName: String,

    @ColumnInfo(name = "player_id")
    val playerId: Int,

    @ColumnInfo(name = "player_name")
    val playerName: String,

    @ColumnInfo(name = "assigned_date")
    val assignedDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "completion_date")
    val completionDate: Long? = null,

    @ColumnInfo(name = "report_status")
    val reportStatus: String = "In Progress",  // In Progress, Completed, Failed

    @ColumnInfo(name = "priority")
    val priority: String = "Normal",  // Low, Normal, High, Urgent

    @ColumnInfo(name = "scouting_focus")
    val scoutingFocus: String? = null,  // Technical, Tactical, Physical, Mental, All

    @ColumnInfo(name = "assignment_notes")
    val assignmentNotes: String? = null,

    @ColumnInfo(name = "scout_report")
    val scoutReport: String? = null,

    @ColumnInfo(name = "scout_rating")
    val scoutRating: Int? = null,  // 1-100

    @ColumnInfo(name = "estimated_value")
    val estimatedValue: Int? = null,

    @ColumnInfo(name = "potential_rating")
    val potentialRating: Int? = null,

    @ColumnInfo(name = "strengths")
    val strengths: String? = null,  // JSON array of strengths

    @ColumnInfo(name = "weaknesses")
    val weaknesses: String? = null,  // JSON array of weaknesses

    @ColumnInfo(name = "verdict")
    val verdict: String? = null,  // Recommended, Not Recommended, Watch
) {

    // ============ COMPUTED PROPERTIES ============

    val isInProgress: Boolean
        get() = reportStatus == "In Progress"

    val isCompleted: Boolean
        get() = reportStatus == "Completed"

    val isFailed: Boolean
        get() = reportStatus == "Failed"

    val isHighPriority: Boolean
        get() = priority == "High" || priority == "Urgent"

    val assignmentDuration: Long?
        get() = if (completionDate != null) completionDate - assignedDate else null

    val assignmentDays: Int?
        get() = assignmentDuration?.let { (it / (1000 * 60 * 60 * 24)).toInt() }

    val summary: String
        get() = "$playerName - $reportStatus (Priority: $priority)"
}

// ============ ENUMS ============

enum class ScoutReportStatus(val value: String) {
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    FAILED("Failed")
}

enum class ScoutPriority(val value: String) {
    LOW("Low"),
    NORMAL("Normal"),
    HIGH("High"),
    URGENT("Urgent")
}

enum class ScoutingFocus(val value: String) {
    TECHNICAL("Technical"),
    TACTICAL("Tactical"),
    PHYSICAL("Physical"),
    MENTAL("Mental"),
    ALL("All")
}

enum class ScoutVerdict(val value: String) {
    RECOMMENDED("Recommended"),
    NOT_RECOMMENDED("Not Recommended"),
    WATCH("Watch")
}