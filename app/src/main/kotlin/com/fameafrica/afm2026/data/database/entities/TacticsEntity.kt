package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "tactics",
    foreignKeys = [
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["team_name"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ManagersEntity::class,
            parentColumns = ["id"],
            childColumns = ["manager_id"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["team_name"], unique = true),
        Index(value = ["manager_id"]),
        Index(value = ["tactical_archetype"]),
        Index(value = ["formation"]),
        Index(value = ["playstyle"])
    ]
)
data class TacticsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "team_name")
    val teamName: String,

    @ColumnInfo(name = "manager_id")
    val managerId: Int? = null,

    // ============ CORE TACTICS ============
    @ColumnInfo(name = "formation")
    val formation: String,  // 4-4-2, 4-3-3, 4-2-3-1, 3-5-2, etc.

    @ColumnInfo(name = "tactical_archetype")
    val tacticalArchetype: String,  // POSSESSION, ATTACKING, BALANCED, COUNTER, DEFENSIVE, PRESSING, SPECIALIZED

    @ColumnInfo(name = "playstyle")
    val playstyle: String,  // TIKI_TAKA, GEGENPRESSING, WING_PLAY, DIRECT, etc.

    @ColumnInfo(name = "defensive_threshold")
    val defensiveThreshold: Int = 50,  // 0-100 - higher means more defensive

    @ColumnInfo(name = "attacking_threshold")
    val attackingThreshold: Int = 50,  // 0-100 - higher means more attacking

    @ColumnInfo(name = "tempo")
    val tempo: Int = 50,  // 0-100 - slow to fast

    @ColumnInfo(name = "width")
    val width: Int = 50,  // 0-100 - narrow to wide

    @ColumnInfo(name = "depth")
    val depth: Int = 50,  // 0-100 - deep to high line

    @ColumnInfo(name = "press_intensity")
    val pressIntensity: Int = 50,  // 0-100 - low to high

    @ColumnInfo(name = "passing_directness")
    val passingDirectness: Int = 50,  // 0-100 - short to direct

    @ColumnInfo(name = "creativity")
    val creativity: Int = 50,  // 0-100 - disciplined to expressive

    // ============ MATCHUP PROBABILITIES ============
    // These are calculated based on archetype vs archetype matchups
    @ColumnInfo(name = "win_probability_vs_possession")
    val winProbabilityVsPossession: Int = 33,  // Percentage

    @ColumnInfo(name = "draw_probability_vs_possession")
    val drawProbabilityVsPossession: Int = 33,  // Percentage

    @ColumnInfo(name = "loss_probability_vs_possession")
    val lossProbabilityVsPossession: Int = 34,  // Percentage

    @ColumnInfo(name = "win_probability_vs_attacking")
    val winProbabilityVsAttacking: Int = 33,

    @ColumnInfo(name = "draw_probability_vs_attacking")
    val drawProbabilityVsAttacking: Int = 33,

    @ColumnInfo(name = "loss_probability_vs_attacking")
    val lossProbabilityVsAttacking: Int = 34,

    @ColumnInfo(name = "win_probability_vs_balanced")
    val winProbabilityVsBalanced: Int = 33,

    @ColumnInfo(name = "draw_probability_vs_balanced")
    val drawProbabilityVsBalanced: Int = 34,

    @ColumnInfo(name = "loss_probability_vs_balanced")
    val lossProbabilityVsBalanced: Int = 33,

    @ColumnInfo(name = "win_probability_vs_counter")
    val winProbabilityVsCounter: Int = 33,

    @ColumnInfo(name = "draw_probability_vs_counter")
    val drawProbabilityVsCounter: Int = 34,

    @ColumnInfo(name = "loss_probability_vs_counter")
    val lossProbabilityVsCounter: Int = 33,

    @ColumnInfo(name = "win_probability_vs_defensive")
    val winProbabilityVsDefensive: Int = 33,

    @ColumnInfo(name = "draw_probability_vs_defensive")
    val drawProbabilityVsDefensive: Int = 34,

    @ColumnInfo(name = "loss_probability_vs_defensive")
    val lossProbabilityVsDefensive: Int = 33,

    @ColumnInfo(name = "win_probability_vs_pressing")
    val winProbabilityVsPressing: Int = 33,

    @ColumnInfo(name = "draw_probability_vs_pressing")
    val drawProbabilityVsPressing: Int = 33,

    @ColumnInfo(name = "loss_probability_vs_pressing")
    val lossProbabilityVsPressing: Int = 34,

    // ============ MANAGER INFLUENCE ============
    @ColumnInfo(name = "manager_tactical_flexibility")
    val managerTacticalFlexibility: Int? = null,  // Override from manager

    @ColumnInfo(name = "manager_preferred_style")
    val managerPreferredStyle: String? = null,

    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: String? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val isPossessionBased: Boolean
        get() = tacticalArchetype == "POSSESSION"

    val isAttacking: Boolean
        get() = tacticalArchetype == "ATTACKING"

    val isBalanced: Boolean
        get() = tacticalArchetype == "BALANCED"

    val isCounterAttacking: Boolean
        get() = tacticalArchetype == "COUNTER"

    val isDefensive: Boolean
        get() = tacticalArchetype == "DEFENSIVE"

    val isPressing: Boolean
        get() = tacticalArchetype == "PRESSING"

    val isSpecialized: Boolean
        get() = tacticalArchetype == "SPECIALIZED"

    val defensiveStyle: String
        get() = when {
            defensiveThreshold >= 70 -> "Very Defensive"
            defensiveThreshold >= 60 -> "Defensive"
            defensiveThreshold >= 40 -> "Balanced Defense"
            defensiveThreshold >= 30 -> "Attacking Minded"
            else -> "All-out Attack"
        }

    val attackingStyle: String
        get() = when {
            attackingThreshold >= 70 -> "All-out Attack"
            attackingThreshold >= 60 -> "Attacking"
            attackingThreshold >= 40 -> "Balanced Attack"
            attackingThreshold >= 30 -> "Cautious"
            else -> "Park the Bus"
        }

    val overallStyle: String
        get() = when {
            defensiveThreshold > 60 && attackingThreshold < 40 -> "Ultra Defensive"
            defensiveThreshold > 60 && attackingThreshold < 60 -> "Defensive"
            defensiveThreshold < 40 && attackingThreshold > 60 -> "Ultra Attacking"
            defensiveThreshold < 40 && attackingThreshold > 40 -> "Attacking"
            else -> "Balanced"
        }

    val summary: String
        get() = "$tacticalArchetype - $formation ($playstyle)"

    /**
     * Get win probability against a specific opponent archetype
     */
    fun getWinProbabilityVs(opponentArchetype: String): Int {
        return when (opponentArchetype) {
            "POSSESSION" -> winProbabilityVsPossession
            "ATTACKING" -> winProbabilityVsAttacking
            "BALANCED" -> winProbabilityVsBalanced
            "COUNTER" -> winProbabilityVsCounter
            "DEFENSIVE" -> winProbabilityVsDefensive
            "PRESSING" -> winProbabilityVsPressing
            else -> 33
        }
    }

    /**
     * Get draw probability against a specific opponent archetype
     */
    fun getDrawProbabilityVs(opponentArchetype: String): Int {
        return when (opponentArchetype) {
            "POSSESSION" -> drawProbabilityVsPossession
            "ATTACKING" -> drawProbabilityVsAttacking
            "BALANCED" -> drawProbabilityVsBalanced
            "COUNTER" -> drawProbabilityVsCounter
            "DEFENSIVE" -> drawProbabilityVsDefensive
            "PRESSING" -> drawProbabilityVsPressing
            else -> 33
        }
    }

    /**
     * Get loss probability against a specific opponent archetype
     */
    fun getLossProbabilityVs(opponentArchetype: String): Int {
        return when (opponentArchetype) {
            "POSSESSION" -> lossProbabilityVsPossession
            "ATTACKING" -> lossProbabilityVsAttacking
            "BALANCED" -> lossProbabilityVsBalanced
            "COUNTER" -> lossProbabilityVsCounter
            "DEFENSIVE" -> lossProbabilityVsDefensive
            "PRESSING" -> lossProbabilityVsPressing
            else -> 34
        }
    }

    /**
     * Get complete matchup probabilities as a triple
     */
    fun getMatchupProbabilities(opponentArchetype: String): Triple<Int, Int, Int> {
        return Triple(
            getWinProbabilityVs(opponentArchetype),
            getDrawProbabilityVs(opponentArchetype),
            getLossProbabilityVs(opponentArchetype)
        )
    }
}

// ============ ENUMS ============

enum class TacticalArchetype(val value: String, val description: String) {
    POSSESSION("POSSESSION", "Ball retention, tempo control, chance creation through patience"),
    ATTACKING("ATTACKING", "High chance creation, overloads in attack, momentum swings"),
    BALANCED("BALANCED", "Stability, adaptability, fewer extremes"),
    COUNTER("COUNTER", "Exploits space, punishes possession-heavy sides"),
    DEFENSIVE("DEFENSIVE", "Blocks space, frustrates opponents, high draw probability"),
    PRESSING("PRESSING", "Forces turnovers, creates chaos, high xG chances"),
    SPECIALIZED("SPECIALIZED", "Narrow or wide overloads, situational strengths")
}

enum class Formation(val value: String) {
    FORMATION_442("4-4-2"),
    FORMATION_442_DIAMOND("4-4-2 Diamond"),
    FORMATION_4231("4-2-3-1"),
    FORMATION_433("4-3-3"),
    FORMATION_4321("4-3-2-1"),
    FORMATION_4141("4-1-4-1"),
    FORMATION_41212("4-1-2-1-2"),
    FORMATION_352("3-5-2"),
    FORMATION_343("3-4-3"),
    FORMATION_3412("3-4-1-2"),
    FORMATION_3421("3-4-2-1"),
    FORMATION_532("5-3-2"),
    FORMATION_541("5-4-1"),
    FORMATION_5212("5-2-1-2")
}

enum class Playstyle(val value: String) {
    TIKI_TAKA("Tiki-Taka"),
    GEGENPRESSING("Gegenpressing"),
    WING_PLAY("Wing Play"),
    DIRECT("Direct Football"),
    PARK_THE_BUS("Park the Bus"),
    COUNTER_ATTACK("Counter Attack"),
    HIGH_PRESS("High Press"),
    POSSESSION("Possession"),
    VERTICAL_TIKI_TAKA("Vertical Tiki-Taka"),
    OVERLAPPING_WINGS("Overlapping Wings"),
    FLUID_ATTACK("Fluid Attack"),
    STRUCTURED("Structured"),
    COMPACT_DEFENSE("Compact Defense"),
    FAST_BUILDUP("Fast Build-up"),
    DIAMOND_ATTACK("Diamond Attack")
}