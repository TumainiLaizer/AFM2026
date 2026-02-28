package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "managers",
    foreignKeys = [
        ForeignKey(
            entity = NationalitiesEntity::class,
            parentColumns = ["nationality"],
            childColumns = ["nationality"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["id"],
            childColumns = ["team_id"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["name"]),
        Index(value = ["team_id"]),
        Index(value = ["nationality"]),
        Index(value = ["reputation"]),
        Index(value = ["reputation_level"]),
        Index(value = ["performance_rating"]),
        Index(value = ["age"]),
        Index(value = ["coaching_license"])
    ]
)
data class ManagersEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "team_id")
    val teamId: Int? = null,

    @ColumnInfo(name = "name", defaultValue = "Tumaini Joseph")
    val name: String = "Tumaini Joseph",

    @ColumnInfo(name = "nationality", defaultValue = "Tanzania")
    val nationality: String = "Tanzania",

    @ColumnInfo(name = "age", defaultValue = "30")
    val age: Int = 30,

    @ColumnInfo(name = "contract_end_date")
    val contractEndDate: Int? = null,

    @ColumnInfo(name = "salary")
    val salary: Int? = null,

    @ColumnInfo(name = "matches_managed", defaultValue = "0")
    val matchesManaged: Int = 0,

    @ColumnInfo(name = "wins", defaultValue = "0")
    val wins: Int = 0,

    @ColumnInfo(name = "losses", defaultValue = "0")
    val losses: Int = 0,

    @ColumnInfo(name = "draws", defaultValue = "0")
    val draws: Int = 0,

    @ColumnInfo(name = "trophies_won", defaultValue = "0")
    val trophiesWon: Int = 0,

    @ColumnInfo(name = "preferred_formation", defaultValue = "4-4-2")
    val preferredFormation: String = "4-4-2",

    @ColumnInfo(name = "style", defaultValue = "Balanced")
    val style: String = "Balanced",

    @ColumnInfo(name = "performance_rating", defaultValue = "0")
    val performanceRating: Int = 0,

    @ColumnInfo(name = "reputation", defaultValue = "50")
    val reputation: Int = 50,

    @ColumnInfo(name = "reputation_level", defaultValue = "Local")
    val reputationLevel: String = "Local",

    @ColumnInfo(name = "face_image")
    val faceImage: String? = null,

    @ColumnInfo(name = "assistant_manager")
    val assistantManager: String? = null,

    @ColumnInfo(name = "staff")
    val staff: String? = null,

    @ColumnInfo(name = "monthly_awards", defaultValue = "0")
    val monthlyAwards: Int = 0,

    @ColumnInfo(name = "yearly_awards", defaultValue = "0")
    val yearlyAwards: Int = 0,

    @ColumnInfo(name = "coach_of_the_month_titles", defaultValue = "0")
    val coachOfTheMonthTitles: Int = 0,

    @ColumnInfo(name = "young_coach_of_the_month_titles", defaultValue = "0")
    val youngCoachOfTheMonthTitles: Int = 0,

    @ColumnInfo(name = "coach_of_the_year_titles", defaultValue = "0")
    val coachOfTheYearTitles: Int = 0,

    @ColumnInfo(name = "african_coach_of_the_year_titles", defaultValue = "0")
    val africanCoachOfTheYearTitles: Int = 0,

    @ColumnInfo(name = "previous_club")
    val previousClub: String? = null,

    @ColumnInfo(name = "coaching_license")
    val coachingLicense: String? = null,  // NONE, NATIONAL_C, NATIONAL_B, NATIONAL_A, PRO, UEFA_PRO, CAF_PRO

    @ColumnInfo(name = "special_ability")
    val specialAbility: String? = null,  // TACTICAL_GENIUS, MOTIVATOR, YOUTH_DEVELOPER, DEFENSIVE_SPECIALIST, ATTACKING_SPECIALIST, SET_PIECE_GURU, etc.

    @ColumnInfo(name = "transfer_fee")
    val transferFee: Int? = null,  // Compensation fee if under contract

    @ColumnInfo(name = "favorite_tactics")
    val favoriteTactics: String? = null,  // JSON string of preferred tactics

    @ColumnInfo(name = "youth_development_focus")
    val youthDevelopmentFocus: Int? = null,  // 0-100

    @ColumnInfo(name = "media_handling")
    val mediaHandling: Int? = null,  // 0-100

    @ColumnInfo(name = "tactical_flexibility")
    val tacticalFlexibility: Int? = null,  // 0-100

    @ColumnInfo(name = "player_motivation")
    val playerMotivation: Int? = null,  // 0-100

    @ColumnInfo(name = "discipline_level")
    val disciplineLevel: Int? = null,  // 0-100

    @ColumnInfo(name = "adaptability")
    val adaptability: Int? = null  // 0-100
) {

    // ============ COMPUTED PROPERTIES ============

    val winPercentage: Double
        get() = if (matchesManaged > 0) (wins.toDouble() / matchesManaged * 100) else 0.0

    val drawPercentage: Double
        get() = if (matchesManaged > 0) (draws.toDouble() / matchesManaged * 100) else 0.0

    val lossPercentage: Double
        get() = if (matchesManaged > 0) (losses.toDouble() / matchesManaged * 100) else 0.0

    val isEmployed: Boolean
        get() = teamId != null

    val isAvailable: Boolean
        get() = teamId == null

    val experienceLevel: String
        get() = when {
            matchesManaged >= 500 -> "Legendary"
            matchesManaged >= 300 -> "Elite"
            matchesManaged >= 200 -> "Experienced"
            matchesManaged >= 100 -> "Established"
            matchesManaged >= 50 -> "Developing"
            else -> "Rookie"
        }

    val reputationDescription: String
        get() = when (reputationLevel) {
            "Local" -> "Local Coach"
            "National" -> "National Coach"
            "Continental" -> "Continental Coach"
            "World Class" -> "World Class Coach"
            else -> reputationLevel
        }

    val contractStatus: String
        get() = when {
            contractEndDate == null -> "Unemployed"
            contractEndDate == 0 -> "Rolling Contract"
            contractEndDate > 0 -> "Contract until ${contractEndDate}"
            else -> "Unknown"
        }

    val careerStage: String
        get() = when {
            age <= 35 -> "Young Manager"
            age <= 45 -> "Prime Years"
            age <= 55 -> "Experienced"
            age <= 65 -> "Veteran"
            else -> "Legend"
        }

    val licenseLevel: Int
        get() = when (coachingLicense) {
            "NONE" -> 0
            "NATIONAL_C" -> 1
            "NATIONAL_B" -> 2
            "NATIONAL_A" -> 3
            "PRO" -> 4
            "UEFA_PRO" -> 5
            "CAF_PRO" -> 5
            else -> 0
        }

    val overallRating: Int
        get() {
            val base = reputation
            val licenseBonus = licenseLevel * 2
            val experienceBonus = (matchesManaged / 100) * 2
            val trophyBonus = trophiesWon * 3
            val performanceBonus = performanceRating / 10

            return (base + licenseBonus + experienceBonus + trophyBonus + performanceBonus).coerceIn(0, 100)
        }

    // ============ BUSINESS METHODS ============

    fun updateAfterMatch(won: Boolean, drew: Boolean, lost: Boolean): ManagersEntity {
        val newMatches = matchesManaged + 1
        val newWins = wins + (if (won) 1 else 0)
        val newDraws = draws + (if (drew) 1 else 0)
        val newLosses = losses + (if (lost) 1 else 0)

        // Calculate performance rating (0-100)
        val last10WinRate = if (matchesManaged > 0) {
            (newWins - wins) * 100 / 10 // Simplified - in real app would track last 10
        } else 0

        val newPerformanceRating = when {
            won -> (performanceRating + 5).coerceIn(0, 100)
            drew -> (performanceRating + 1).coerceIn(0, 100)
            else -> (performanceRating - 3).coerceIn(0, 100)
        }

        return this.copy(
            matchesManaged = newMatches,
            wins = newWins,
            draws = newDraws,
            losses = newLosses,
            performanceRating = newPerformanceRating
        )
    }

    fun winTrophy(): ManagersEntity {
        val newTrophies = trophiesWon + 1

        // Reputation gain based on current level
        val reputationGain = when (reputationLevel) {
            "Local" -> 5
            "National" -> 4
            "Continental" -> 3
            "World Class" -> 2
            else -> 3
        }

        val newReputation = (reputation + reputationGain).coerceIn(0, 100)
        val newReputationLevel = determineReputationLevel(newReputation)

        return this.copy(
            trophiesWon = newTrophies,
            reputation = newReputation,
            reputationLevel = newReputationLevel
        )
    }

    fun signContract(teamId: Int, salary: Int, contractYears: Int): ManagersEntity {
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

        return this.copy(
            teamId = teamId,
            salary = salary,
            contractEndDate = currentYear + contractYears,
            previousClub = this.teamId?.let {
                "Previous club would be stored here"
            }
        )
    }

    fun leaveClub(): ManagersEntity {
        return this.copy(
            teamId = null,
            contractEndDate = null
        )
    }

    fun renewContract(newSalary: Int, additionalYears: Int): ManagersEntity {
        val currentEndYear = contractEndDate ?: java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

        return this.copy(
            salary = newSalary,
            contractEndDate = currentEndYear + additionalYears
        )
    }

    fun updateReputation(newReputation: Int): ManagersEntity {
        val clampedReputation = newReputation.coerceIn(0, 100)

        return this.copy(
            reputation = clampedReputation,
            reputationLevel = determineReputationLevel(clampedReputation)
        )
    }

    fun earnAward(awardType: String): ManagersEntity {
        return when (awardType) {
            "COACH_OF_THE_MONTH" -> this.copy(
                monthlyAwards = monthlyAwards + 1,
                coachOfTheMonthTitles = coachOfTheMonthTitles + 1,
                reputation = (reputation + 2).coerceIn(0, 100),
                reputationLevel = determineReputationLevel(reputation + 2)
            )
            "YOUNG_COACH_OF_THE_MONTH" -> this.copy(
                monthlyAwards = monthlyAwards + 1,
                youngCoachOfTheMonthTitles = youngCoachOfTheMonthTitles + 1,
                reputation = (reputation + 3).coerceIn(0, 100),
                reputationLevel = determineReputationLevel(reputation + 3)
            )
            "COACH_OF_THE_YEAR" -> this.copy(
                yearlyAwards = yearlyAwards + 1,
                coachOfTheYearTitles = coachOfTheYearTitles + 1,
                reputation = (reputation + 10).coerceIn(0, 100),
                reputationLevel = determineReputationLevel(reputation + 10)
            )
            "AFRICAN_COACH_OF_THE_YEAR" -> this.copy(
                yearlyAwards = yearlyAwards + 1,
                africanCoachOfTheYearTitles = africanCoachOfTheYearTitles + 1,
                reputation = (reputation + 15).coerceIn(0, 100),
                reputationLevel = determineReputationLevel(reputation + 15)
            )
            else -> this
        }
    }

    fun upgradeLicense(newLicense: String): ManagersEntity {
        return this.copy(
            coachingLicense = newLicense,
            reputation = (reputation + 5).coerceIn(0, 100),
            reputationLevel = determineReputationLevel(reputation + 5)
        )
    }

    fun calculateTransferFee(): Int {
        return when {
            transferFee != null -> transferFee
            salary != null -> (salary * 0.3).toInt()
            else -> 50000 * (reputation / 10) * (licenseLevel + 1)
        }
    }

    private fun determineReputationLevel(reputationValue: Int): String {
        return when {
            reputationValue >= 85 -> "World Class"
            reputationValue >= 70 -> "Continental"
            reputationValue >= 50 -> "National"
            else -> "Local"
        }
    }

    companion object {
        fun calculateAgeFromBirthYear(birthYear: Int): Int {
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            return currentYear - birthYear
        }
    }
}

// ============ ENUMS ============

enum class CoachingLicense(val value: String, val level: Int) {
    NONE("NONE", 0),
    NATIONAL_C("NATIONAL_C", 1),
    NATIONAL_B("NATIONAL_B", 2),
    NATIONAL_A("NATIONAL_A", 3),
    PRO("PRO", 4),
    UEFA_PRO("UEFA_PRO", 5),
    CAF_PRO("CAF_PRO", 5)
}

enum class ManagerSpecialAbility(val value: String) {
    TACTICAL_GENIUS("TACTICAL_GENIUS"),
    MOTIVATOR("MOTIVATOR"),
    YOUTH_DEVELOPER("YOUTH_DEVELOPER"),
    DEFENSIVE_SPECIALIST("DEFENSIVE_SPECIALIST"),
    ATTACKING_SPECIALIST("ATTACKING_SPECIALIST"),
    SET_PIECE_GURU("SET_PIECE_GURU"),
    WHEELER_DEALER("WHEELER_DEALER"),
    DISCIPLINARIAN("DISCIPLINARIAN"),
    LEGEND("LEGEND")
}

enum class ManagerStyle(val value: String) {
    BALANCED("Balanced"),
    ATTACKING("Attacking"),
    DEFENSIVE("Defensive"),
    POSSESSION("Possession"),
    COUNTER_ATTACK("Counter Attack"),
    HIGH_PRESS("High Press"),
    PARK_THE_BUS("Park the Bus"),
    TIKI_TAKA("Tiki-Taka"),
    LONG_BALL("Long Ball")
}

enum class ReputationLevel(val value: String, val threshold: Int) {
    LOCAL("Local", 0),
    NATIONAL("National", 50),
    CONTINENTAL("Continental", 70),
    WORLD_CLASS("World Class", 85)
}