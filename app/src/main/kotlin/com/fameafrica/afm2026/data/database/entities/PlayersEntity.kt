package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore
import java.util.Calendar

@Entity(tableName = "players")
data class PlayersEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    // Basic Info - NEW FIELDS ADDED
    @ColumnInfo(name = "manager_id", defaultValue = "0")
    val managerId: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "team_id")
    val teamId: Int,

    @ColumnInfo(name = "team_name")
    val teamName: String,

    @ColumnInfo(name = "region")
    val region: String?,

    @ColumnInfo(name = "nationality", defaultValue = "Tanzania")
    val nationality: String = "Tanzania",

    @ColumnInfo(name = "age")
    val age: Int,

    @ColumnInfo(name = "height")
    val height: Int,

    @ColumnInfo(name = "preferred_foot", defaultValue = "RIGHT")
    val preferredFoot: String = "RIGHT",

    @ColumnInfo(name = "position")
    val position: String,

    @ColumnInfo(name = "position_category")
    val positionCategory: String,

    @ColumnInfo(name = "shirt_number")
    val shirtNumber: Int,

    // Personality and Archetype - NEW FIELDS ADDED
    @ColumnInfo(name = "personality_type", defaultValue = "PROFESSIONAL")
    val personalityType: String = "PROFESSIONAL",

    @ColumnInfo(name = "archetype")
    val archetype: String?,

    @ColumnInfo(name = "primary_trait")
    val primaryTrait: String?,

    @ColumnInfo(name = "secondary_trait")
    val secondaryTrait: String?,

    @ColumnInfo(name = "gameplay_focus")
    val gameplayFocus: String?,

    // Ratings and potential
    @ColumnInfo(name = "rating", defaultValue = "60")
    val rating: Int = 60,

    @ColumnInfo(name = "potential", defaultValue = "85")
    val potential: Int = 85,

    @ColumnInfo(name = "current_form", defaultValue = "50")
    val currentForm: Int = 50,

    @ColumnInfo(name = "experience", defaultValue = "0")
    val experience: Int = 0,

    @ColumnInfo(name = "morale", defaultValue = "75")
    val morale: Int = 75,

    // ============ TECHNICAL ATTRIBUTES ============
    @ColumnInfo(name = "finishing", defaultValue = "50")
    val finishing: Int = 50,

    @ColumnInfo(name = "passing", defaultValue = "50")
    val passing: Int = 50,

    @ColumnInfo(name = "dribbling", defaultValue = "50")
    val dribbling: Int = 50,

    @ColumnInfo(name = "skill", defaultValue = "50")
    val skill: Int = 50,

    @ColumnInfo(name = "crossing", defaultValue = "50")
    val crossing: Int = 50,

    @ColumnInfo(name = "defending", defaultValue = "50")
    val defending: Int = 50,

    @ColumnInfo(name = "heading", defaultValue = "50")
    val heading: Int = 50,

    @ColumnInfo(name = "long_shots", defaultValue = "50")
    val longShots: Int = 50,

    // ============ PHYSICAL ATTRIBUTES ============
    @ColumnInfo(name = "pace", defaultValue = "50")
    val pace: Int = 50,

    @ColumnInfo(name = "stamina", defaultValue = "99")
    val stamina: Int = 99,

    @ColumnInfo(name = "strength", defaultValue = "50")
    val strength: Int = 50,

    @ColumnInfo(name = "acceleration", defaultValue = "50")
    val acceleration: Int = 50,

    @ColumnInfo(name = "agility", defaultValue = "50")
    val agility: Int = 50,

    // ============ MENTAL ATTRIBUTES ============
    @ColumnInfo(name = "aggression", defaultValue = "30")
    val aggression: Int = 30,

    @ColumnInfo(name = "leadership", defaultValue = "50")
    val leadership: Int = 50,

    @ColumnInfo(name = "motivation", defaultValue = "50")
    val motivation: Int = 50,

    @ColumnInfo(name = "composure", defaultValue = "50")
    val composure: Int = 50,

    @ColumnInfo(name = "vision", defaultValue = "50")
    val vision: Int = 50,

    @ColumnInfo(name = "positioning", defaultValue = "50")
    val positioning: Int = 50,

    @ColumnInfo(name = "anticipation", defaultValue = "50")
    val anticipation: Int = 50,

    @ColumnInfo(name = "decisions", defaultValue = "50")
    val decisions: Int = 50,

    @ColumnInfo(name = "creativity", defaultValue = "50")
    val creativity: Int = 50,

    @ColumnInfo(name = "teamwork", defaultValue = "50")
    val teamwork: Int = 50,

    // ============ GOALKEEPER ATTRIBUTES ============
    @ColumnInfo(name = "goalkeeping", defaultValue = "10")
    val goalkeeping: Int = 10,

    @ColumnInfo(name = "reflexes", defaultValue = "50")
    val reflexes: Int = 50,

    @ColumnInfo(name = "handling", defaultValue = "50")
    val handling: Int = 50,

    @ColumnInfo(name = "aerial_ability", defaultValue = "50")
    val aerialAbility: Int = 50,

    @ColumnInfo(name = "command_of_area", defaultValue = "50")
    val commandOfArea: Int = 50,

    @ColumnInfo(name = "kicking", defaultValue = "50")
    val kicking: Int = 50,

    // ============ INJURY & STATUS ============
    @ColumnInfo(name = "injury_risk", defaultValue = "10")
    val injuryRisk: Int = 10,

    @ColumnInfo(name = "injury_status", defaultValue = "HEALTHY")
    val injuryStatus: String = "HEALTHY",

    @ColumnInfo(name = "recovery_time", defaultValue = "0")
    val recoveryTime: Int = 0,

    @ColumnInfo(name = "suspended", defaultValue = "0")
    val suspended: Boolean = false,

    // ============ CONTRACT & FINANCIAL ============
    @ColumnInfo(name = "market_value")
    val marketValue: Int,

    @ColumnInfo(name = "salary", defaultValue = "500000")
    val salary: Double = 500000.0,

    @ColumnInfo(name = "contract_expiry", defaultValue = "2029-06-30")
    val contractExpiry: String = "2029-06-30",

    @ColumnInfo(name = "free_agent", defaultValue = "0")
    val freeAgent: Boolean = false,

    @ColumnInfo(name = "transfer_list_status", defaultValue = "NOT_LISTED")
    val transferListStatus: String = "NOT_LISTED",

    // ============ CAREER STATISTICS ============
    @ColumnInfo(name = "matches", defaultValue = "0")
    val matches: Int = 0,

    @ColumnInfo(name = "goals", defaultValue = "0")
    val goals: Int = 0,

    @ColumnInfo(name = "assists", defaultValue = "0")
    val assists: Int = 0,

    @ColumnInfo(name = "clean_sheets", defaultValue = "0")
    val cleanSheets: Int = 0,

    @ColumnInfo(name = "red_cards", defaultValue = "0")
    val redCards: Int = 0,

    @ColumnInfo(name = "yellow_cards", defaultValue = "0")
    val yellowCards: Int = 0,

    @ColumnInfo(name = "trophies", defaultValue = "0")
    val trophies: Int = 0,

    @ColumnInfo(name = "man_of_match", defaultValue = "0")
    val manOfMatch: Int = 0,

    // ============ TEAM ROLE ============
    @ColumnInfo(name = "is_starting_xi", defaultValue = "0")
    val isStartingXi: Boolean = false,

    @ColumnInfo(name = "is_captain", defaultValue = "0")
    val isCaptain: Boolean = false,

    @ColumnInfo(name = "is_vice_captain", defaultValue = "0")
    val isViceCaptain: Boolean = false,

    @ColumnInfo(name = "work_rate", defaultValue = "MEDIUM")
    val workRate: String = "MEDIUM",

    // ============ CAREER STATUS ============
    @ColumnInfo(name = "retired", defaultValue = "0")
    val retired: Boolean = false,

    @ColumnInfo(name = "future_role")
    val futureRole: String?,

    @ColumnInfo(name = "player_coach", defaultValue = "0")
    val playerCoach: Boolean = false,

    @ColumnInfo(name = "season")
    val season: String?,

    // ============ MEDIA & PERSONALITY EFFECTS ============
    @ColumnInfo(name = "media_handling", defaultValue = "50")
    val mediaHandling: Int = 50,

    @ColumnInfo(name = "fan_popularity", defaultValue = "50")
    val fanPopularity: Int = 50,

    @ColumnInfo(name = "dressing_room_influence", defaultValue = "50")
    val dressingRoomInfluence: Int = 50,

    // ============ MEDIA ============
    @ColumnInfo(name = "face_image")
    val faceImage: String?,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

    // ============ TIMESTAMPS ============
    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: String = "",

    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
    val updatedAt: String = ""
) {

    // ============ IGNORED PROPERTIES (not stored in DB) ============
    @Ignore
    var isSelected: Boolean = false

    // ============ COMPUTED PROPERTIES ============

    val overallRating: Int
        get() = when {
            isGoalkeeper -> calculateGoalkeeperRating()
            isDefender -> calculateDefenderRating()
            isMidfielder -> calculateMidfielderRating()
            isForward -> calculateForwardRating()
            else -> rating
        }

    val isGoalkeeper: Boolean
        get() = position == "GK"

    val isDefender: Boolean
        get() = position in listOf("CB", "LB", "RB", "SW", "LWB", "RWB")

    val isMidfielder: Boolean
        get() = position in listOf("CDM", "CM", "CAM", "LM", "RM")

    val isForward: Boolean
        get() = position in listOf("LW", "RW", "ST", "CF")

    val ageGroup: String
        get() = when {
            age <= 19 -> "Youth"
            age <= 23 -> "Young"
            age <= 29 -> "Prime"
            age <= 34 -> "Veteran"
            else -> "Senior"
        }

    val potentialGrade: String
        get() = when {
            potential >= 90 -> "World Class"
            potential >= 85 -> "Elite"
            potential >= 80 -> "Very Good"
            potential >= 75 -> "Good"
            potential >= 70 -> "Decent"
            else -> "Average"
        }

    val currentGrade: String
        get() = when {
            rating >= 90 -> "World Class"
            rating >= 85 -> "Elite"
            rating >= 80 -> "Very Good"
            rating >= 75 -> "Good"
            rating >= 70 -> "Decent"
            else -> "Average"
        }

    val isInjured: Boolean
        get() = injuryStatus != "HEALTHY"

    val isAvailable: Boolean
        get() = !isInjured && !suspended && !retired

    val isTransferListed: Boolean
        get() = transferListStatus == "AVAILABLE"

    val isLoanListed: Boolean
        get() = transferListStatus == "LOAN_LISTED"

    val contractStatus: String
        get() {
            val expiry = contractExpiry.split("-")
            if (expiry.size != 3) return "Unknown"

            val expiryYear = expiry[0].toIntOrNull() ?: return "Unknown"
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)

            return when {
                expiryYear <= currentYear -> "Expired"
                expiryYear == currentYear + 1 -> "Expiring Soon"
                expiryYear <= currentYear + 2 -> "Negotiable"
                else -> "Long Term"
            }
        }

    val valueInMillions: Double
        get() = marketValue / 1_000_000.0

    val salaryInMillions: Double
        get() = salary / 1_000_000.0

    val fullName: String
        get() = name

    val displayName: String
        get() = if (name.length > 15) name.split(" ").joinToString("") { it.take(1) } else name

    // ============ ATTRIBUTE CALCULATIONS ============

    private fun calculateGoalkeeperRating(): Int {
        val gkAttributes = listOf(goalkeeping, reflexes, handling, aerialAbility, commandOfArea, kicking)
        return (gkAttributes.average() * 0.7 + rating * 0.3).toInt()
    }

    private fun calculateDefenderRating(): Int {
        val defAttributes = listOf(defending, heading, positioning, anticipation, decisions, strength)
        val physAttributes = listOf(pace, stamina, acceleration)
        return (defAttributes.average() * 0.6 + physAttributes.average() * 0.2 + rating * 0.2).toInt()
    }

    private fun calculateMidfielderRating(): Int {
        val midAttributes = listOf(passing, dribbling, vision, creativity, decisions, teamwork)
        val physAttributes = listOf(pace, stamina, acceleration, agility)
        return (midAttributes.average() * 0.6 + physAttributes.average() * 0.2 + rating * 0.2).toInt()
    }

    private fun calculateForwardRating(): Int {
        val fwdAttributes = listOf(finishing, dribbling, pace, acceleration, composure, longShots)
        val techAttributes = listOf(skill, crossing, heading)
        return (fwdAttributes.average() * 0.6 + techAttributes.average() * 0.2 + rating * 0.2).toInt()
    }

    // ============ BUSINESS METHODS ============

    fun updateAfterMatch(goalsScored: Int, assistsMade: Int, isManOfMatch: Boolean): PlayersEntity {
        val newMatches = matches + 1
        val newGoals = goals + goalsScored
        val newAssists = assists + assistsMade
        val newManOfMatch = manOfMatch + (if (isManOfMatch) 1 else 0)

        // Form update (0-100)
        var formChange = 0
        if (goalsScored > 0) formChange += 5 * goalsScored
        if (assistsMade > 0) formChange += 3 * assistsMade
        if (isManOfMatch) formChange += 10
        if (yellowCards > 0) formChange -= 2 * yellowCards
        if (redCards > 0) formChange -= 5 * redCards

        val newForm = (currentForm + formChange).coerceIn(1, 100)

        // Experience gain
        val newExperience = experience + 1

        return this.copy(
            matches = newMatches,
            goals = newGoals,
            assists = newAssists,
            manOfMatch = newManOfMatch,
            currentForm = newForm,
            experience = newExperience
        )
    }

    fun updateMorale(change: Int): PlayersEntity {
        return this.copy(morale = (morale + change).coerceIn(0, 100))
    }

    fun setInjury(status: String, recoveryDays: Int): PlayersEntity {
        return this.copy(
            injuryStatus = status,
            recoveryTime = recoveryDays,
            updatedAt = Calendar.getInstance().time.toString()
        )
    }

    fun recoverFromInjury(): PlayersEntity {
        return this.copy(
            injuryStatus = "HEALTHY",
            recoveryTime = 0,
            updatedAt = Calendar.getInstance().time.toString()
        )
    }

    fun addSuspension(): PlayersEntity {
        return this.copy(
            suspended = true,
            updatedAt = Calendar.getInstance().time.toString()
        )
    }

    fun removeSuspension(): PlayersEntity {
        return this.copy(
            suspended = false,
            updatedAt = Calendar.getInstance().time.toString()
        )
    }

    fun transferTo(newTeamId: Int, newTeamName: String, newMarketValue: Int? = null): PlayersEntity {
        return this.copy(
            teamId = newTeamId,
            teamName = newTeamName,
            marketValue = newMarketValue ?: marketValue,
            updatedAt = Calendar.getInstance().time.toString()
        )
    }

    fun renewContract(newSalary: Double, newExpiry: String): PlayersEntity {
        return this.copy(
            salary = newSalary,
            contractExpiry = newExpiry,
            updatedAt = Calendar.getInstance().time.toString()
        )
    }

    fun retire(): PlayersEntity {
        return this.copy(
            retired = true,
            updatedAt = Calendar.getInstance().time.toString()
        )
    }

    fun updateRating(newRating: Int): PlayersEntity {
        return this.copy(
            rating = newRating.coerceIn(1, 99),
            updatedAt = Calendar.getInstance().time.toString()
        )
    }

    fun updatePotential(newPotential: Int): PlayersEntity {
        return this.copy(
            potential = newPotential.coerceIn(1, 99),
            updatedAt = Calendar.getInstance().time.toString()
        )
    }

    fun updateAttributes(attributeUpdates: Map<String, Int>): PlayersEntity {
        var updated = this

        attributeUpdates.forEach { (attribute, value) ->
            updated = when (attribute) {
                "finishing" -> updated.copy(finishing = value.coerceIn(1, 99))
                "passing" -> updated.copy(passing = value.coerceIn(1, 99))
                "dribbling" -> updated.copy(dribbling = value.coerceIn(1, 99))
                "skill" -> updated.copy(skill = value.coerceIn(1, 99))
                "crossing" -> updated.copy(crossing = value.coerceIn(1, 99))
                "defending" -> updated.copy(defending = value.coerceIn(1, 99))
                "heading" -> updated.copy(heading = value.coerceIn(1, 99))
                "long_shots" -> updated.copy(longShots = value.coerceIn(1, 99))
                "pace" -> updated.copy(pace = value.coerceIn(1, 99))
                "stamina" -> updated.copy(stamina = value.coerceIn(1, 99))
                "strength" -> updated.copy(strength = value.coerceIn(1, 99))
                "acceleration" -> updated.copy(acceleration = value.coerceIn(1, 99))
                "agility" -> updated.copy(agility = value.coerceIn(1, 99))
                "aggression" -> updated.copy(aggression = value.coerceIn(1, 99))
                "leadership" -> updated.copy(leadership = value.coerceIn(1, 99))
                "motivation" -> updated.copy(motivation = value.coerceIn(1, 99))
                "composure" -> updated.copy(composure = value.coerceIn(1, 99))
                "vision" -> updated.copy(vision = value.coerceIn(1, 99))
                "positioning" -> updated.copy(positioning = value.coerceIn(1, 99))
                "anticipation" -> updated.copy(anticipation = value.coerceIn(1, 99))
                "decisions" -> updated.copy(decisions = value.coerceIn(1, 99))
                "creativity" -> updated.copy(creativity = value.coerceIn(1, 99))
                "teamwork" -> updated.copy(teamwork = value.coerceIn(1, 99))
                "goalkeeping" -> updated.copy(goalkeeping = value.coerceIn(1, 99))
                "reflexes" -> updated.copy(reflexes = value.coerceIn(1, 99))
                "handling" -> updated.copy(handling = value.coerceIn(1, 99))
                "aerial_ability" -> updated.copy(aerialAbility = value.coerceIn(1, 99))
                "command_of_area" -> updated.copy(commandOfArea = value.coerceIn(1, 99))
                "kicking" -> updated.copy(kicking = value.coerceIn(1, 99))
                else -> updated
            }
        }

        return updated.copy(updatedAt = Calendar.getInstance().time.toString())
    }

    companion object {
        fun calculatePositionCategory(position: String): String {
            return when (position) {
                "GK" -> "GOALKEEPER"
                in listOf("CB", "LB", "RB", "SW", "LWB", "RWB") -> "DEFENDER"
                in listOf("CDM", "CM", "CAM", "LM", "RM") -> "MIDFIELDER"
                in listOf("LW", "RW", "ST", "CF") -> "FORWARD"
                else -> "OTHER"
            }
        }
    }
}