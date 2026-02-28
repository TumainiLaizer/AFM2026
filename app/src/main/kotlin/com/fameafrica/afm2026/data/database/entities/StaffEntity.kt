package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "staff",
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
        Index(value = ["role"]),
        Index(value = ["specialization"]),
        Index(value = ["impact_rating"]),
        Index(value = ["experience_level"]),
        Index(value = ["previous_player"]),
        Index(value = ["staff_type"])
    ]
)
data class StaffEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name", defaultValue = "Tumaini Chacha")
    val name: String = "Tumaini Chacha",

    @ColumnInfo(name = "role")
    val role: String,  // ASSISTANT_MANAGER, COACH, SCOUT, PHYSIO, etc.

    @ColumnInfo(name = "staff_type")
    val staffType: String,  // COACHING, MEDICAL, SCOUTING, ADMIN

    @ColumnInfo(name = "team_name")
    val teamName: String,

    @ColumnInfo(name = "specialization", defaultValue = "General")
    val specialization: String,

    @ColumnInfo(name = "impact_rating", defaultValue = "70")
    val impactRating: Int = 70,  // 0-100 effectiveness in role

    @ColumnInfo(name = "salary", defaultValue = "1200000")
    val salary: Int = 1200000,

    @ColumnInfo(name = "experience_level", defaultValue = "0")
    val experienceLevel: Int = 0,  // Years of experience

    @ColumnInfo(name = "face_image")
    val faceImage: String? = null,

    @ColumnInfo(name = "previous_player", defaultValue = "NULL")
    val previousPlayer: String? = null,  // Player name if this staff was a player

    @ColumnInfo(name = "nationality")
    val nationality: String? = null,

    @ColumnInfo(name = "age")
    val age: Int? = null,

    @ColumnInfo(name = "contract_end_date")
    val contractEndDate: String? = null,

    @ColumnInfo(name = "is_head_of_department")
    val isHeadOfDepartment: Boolean = false,

    @ColumnInfo(name = "mentoring_ability")
    val mentoringAbility: Int = 50,  // Ability to develop young players

    @ColumnInfo(name = "loyalty")
    val loyalty: Int = 50,  // Likelihood to stay at club

    @ColumnInfo(name = "adaptability")
    val adaptability: Int = 50,  // Ability to work in new environments
) {

    // ============ COMPUTED PROPERTIES ============

    val impactLevel: String
        get() = when {
            impactRating >= 90 -> "World Class"
            impactRating >= 80 -> "Elite"
            impactRating >= 70 -> "Very Good"
            impactRating >= 60 -> "Good"
            impactRating >= 50 -> "Decent"
            else -> "Average"
        }

    val experienceLevelDescription: String
        get() = when {
            experienceLevel >= 20 -> "Legendary"
            experienceLevel >= 15 -> "Veteran"
            experienceLevel >= 10 -> "Experienced"
            experienceLevel >= 5 -> "Established"
            else -> "Developing"
        }

    val isCoach: Boolean
        get() = staffType == "COACHING"

    val isScout: Boolean
        get() = staffType == "SCOUTING"

    val isMedical: Boolean
        get() = staffType == "MEDICAL"

    val isAdmin: Boolean
        get() = staffType == "ADMIN"

    val isFormerPlayer: Boolean
        get() = previousPlayer != null && previousPlayer != "NULL"

    val salaryInMillions: Double
        get() = salary / 1_000_000.0

    val roleDisplay: String
        get() = role.split('_').joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }

    val specializationDisplay: String
        get() = specialization.split('_').joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
}

// ============ ENUMS ============

enum class StaffRole(val value: String, val staffType: String) {
    // Coaching Staff
    ASSISTANT_MANAGER("ASSISTANT_MANAGER", "COACHING"),
    FIRST_TEAM_COACH("FIRST_TEAM_COACH", "COACHING"),
    GOALKEEPER_COACH("GOALKEEPER_COACH", "COACHING"),
    FITNESS_COACH("FITNESS_COACH", "COACHING"),
    YOUTH_COACH("YOUTH_COACH", "COACHING"),
    TECHNICAL_COACH("TECHNICAL_COACH", "COACHING"),
    SET_PIECE_COACH("SET_PIECE_COACH", "COACHING"),
    ATTACKING_COACH("ATTACKING_COACH", "COACHING"),
    DEFENSIVE_COACH("DEFENSIVE_COACH", "COACHING"),

    // Scouting Staff
    CHIEF_SCOUT("CHIEF_SCOUT", "SCOUTING"),
    SCOUT("SCOUT", "SCOUTING"),
    REGIONAL_SCOUT("REGIONAL_SCOUT", "SCOUTING"),
    YOUTH_SCOUT("YOUTH_SCOUT", "SCOUTING"),
    DATA_ANALYST("DATA_ANALYST", "SCOUTING"),

    // Medical Staff
    HEAD_PHYSIO("HEAD_PHYSIO", "MEDICAL"),
    PHYSIOTHERAPIST("PHYSIOTHERAPIST", "MEDICAL"),
    SPORTS_SCIENTIST("SPORTS_SCIENTIST", "MEDICAL"),
    NUTRITIONIST("NUTRITIONIST", "MEDICAL"),
    DOCTOR("DOCTOR", "MEDICAL"),
    MASSAGE_THERAPIST("MASSAGE_THERAPIST", "MEDICAL"),

    // Administrative Staff
    SPORTING_DIRECTOR("SPORTING_DIRECTOR", "ADMIN"),
    TECHNICAL_DIRECTOR("TECHNICAL_DIRECTOR", "ADMIN"),
    ACADEMY_DIRECTOR("ACADEMY_DIRECTOR", "ADMIN"),
    HEAD_OF_YOUTH("HEAD_OF_YOUTH", "ADMIN"),
    CLUB_SECRETARY("CLUB_SECRETARY", "ADMIN"),
    MEDIA_OFFICER("MEDIA_OFFICER", "ADMIN"),
    KIT_MANAGER("KIT_MANAGER", "ADMIN")
}

enum class Specialization(val value: String) {
    // General
    GENERAL("General"),

    // Coaching Specializations
    ATTACKING("Attacking"),
    DEFENSIVE("Defensive"),
    MIDFIELD("Midfield"),
    GOALKEEPING("Goalkeeping"),
    FITNESS("Fitness"),
    YOUTH_DEVELOPMENT("Youth Development"),
    SET_PIECES("Set Pieces"),
    TACTICAL("Tactical"),
    TECHNICAL("Technical"),

    // Scouting Specializations
    DOMESTIC("Domestic"),
    INTERNATIONAL("International"),
    YOUTH("Youth"),
    OPPOSITION("Opposition Analysis"),
    ADVANCED_ANALYTICS("Advanced Analytics"),

    // Medical Specializations
    INJURY_PREVENTION("Injury Prevention"),
    REHABILITATION("Rehabilitation"),
    SPORTS_SCIENCE("Sports Science"),
    NUTRITION("Nutrition"),

    // Administrative Specializations
    TRANSFERS("Transfers"),
    CONTRACTS("Contracts"),
    FINANCE("Finance"),
    MEDIA("Media"),
    OPERATIONS("Operations")
}