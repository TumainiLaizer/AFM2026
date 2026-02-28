package com.fameafrica.afm2026.utils.tactics

import com.fameafrica.afm2026.data.database.entities.TacticsEntity
import kotlin.random.Random

/**
 * Tactical Matchup Engine
 * Calculates match probabilities based on archetype matchups and manager influence
 */
object TacticalMatchupEngine {

    // ============ BASE PROBABILITY MATRIX ============
    // Format: [win%, draw%, loss%]
    // Rows: Archetype A, Columns: Archetype B

    private val probabilityMatrix = mapOf(
        // Possession vs [Opponent]
        "POSSESSION" to mapOf(
            "POSSESSION" to Triple(33, 34, 33),
            "ATTACKING" to Triple(40, 30, 30),
            "BALANCED" to Triple(35, 35, 30),
            "COUNTER" to Triple(40, 25, 35),  // Classic duel
            "DEFENSIVE" to Triple(45, 35, 20),
            "PRESSING" to Triple(35, 25, 40)
        ),
        // Attacking vs [Opponent]
        "ATTACKING" to mapOf(
            "POSSESSION" to Triple(30, 30, 40),
            "ATTACKING" to Triple(33, 34, 33),
            "BALANCED" to Triple(40, 30, 30),
            "COUNTER" to Triple(45, 25, 30),
            "DEFENSIVE" to Triple(50, 30, 20),
            "PRESSING" to Triple(30, 30, 40)
        ),
        // Balanced vs [Opponent]
        "BALANCED" to mapOf(
            "POSSESSION" to Triple(30, 35, 35),
            "ATTACKING" to Triple(30, 30, 40),
            "BALANCED" to Triple(33, 34, 33),
            "COUNTER" to Triple(35, 35, 30),
            "DEFENSIVE" to Triple(35, 40, 25),
            "PRESSING" to Triple(30, 35, 35)
        ),
        // Counter vs [Opponent]
        "COUNTER" to mapOf(
            "POSSESSION" to Triple(35, 25, 40),
            "ATTACKING" to Triple(30, 25, 45),
            "BALANCED" to Triple(30, 35, 35),
            "COUNTER" to Triple(30, 40, 30),
            "DEFENSIVE" to Triple(30, 40, 30),
            "PRESSING" to Triple(30, 20, 50)
        ),
        // Defensive vs [Opponent]
        "DEFENSIVE" to mapOf(
            "POSSESSION" to Triple(20, 35, 45),
            "ATTACKING" to Triple(20, 30, 50),
            "BALANCED" to Triple(25, 40, 35),
            "COUNTER" to Triple(30, 40, 30),
            "DEFENSIVE" to Triple(20, 60, 20),  // Stalemate
            "PRESSING" to Triple(20, 25, 55)
        ),
        // Pressing vs [Opponent]
        "PRESSING" to mapOf(
            "POSSESSION" to Triple(40, 25, 35),
            "ATTACKING" to Triple(40, 30, 30),
            "BALANCED" to Triple(35, 35, 30),
            "COUNTER" to Triple(50, 20, 30),
            "DEFENSIVE" to Triple(55, 25, 20),
            "PRESSING" to Triple(33, 34, 33)
        )
    )

    /**
     * Calculate match outcome probabilities based on tactical matchup
     */
    fun calculateMatchupProbabilities(
        homeTactics: TacticsEntity,
        awayTactics: TacticsEntity
    ): Triple<Int, Int, Int> {

        val baseProbs = probabilityMatrix[homeTactics.tacticalArchetype]?.get(awayTactics.tacticalArchetype)
            ?: Triple(33, 34, 33)

        // Apply manager influence
        val homeInfluence = calculateManagerInfluence(homeTactics)
        val awayInfluence = calculateManagerInfluence(awayTactics)

        var win = baseProbs.first
        var draw = baseProbs.second
        var loss = baseProbs.third

        // Home advantage (+5% to win, -5% from loss)
        win = (win + 5).coerceIn(0, 100)
        loss = (loss - 5).coerceIn(0, 100)

        // Apply manager tactical flexibility
        win = (win + homeInfluence - awayInfluence).coerceIn(0, 100)
        loss = (loss - homeInfluence + awayInfluence).coerceIn(0, 100)

        // Ensure draw is adjusted to keep total 100
        draw = (100 - win - loss).coerceIn(0, 100)

        return Triple(win, draw, loss)
    }

    /**
     * Calculate manager influence on tactics
     */
    private fun calculateManagerInfluence(tactics: TacticsEntity): Int {
        var influence = 0

        // Tactical flexibility helps adapt to opponent
        tactics.managerTacticalFlexibility?.let { flexibility ->
            influence += (flexibility - 50) / 10
        }

        // Style match bonus
        tactics.managerPreferredStyle?.let { preferredStyle ->
            if (preferredStyle == tactics.playstyle) {
                influence += 5
            }
        }

        return influence.coerceIn(-10, 10)
    }

    /**
     * Simulate match result based on tactical matchup
     */
    fun simulateMatchResult(
        homeTactics: TacticsEntity,
        awayTactics: TacticsEntity
    ): String {
        val (winProb, drawProb, lossProb) = calculateMatchupProbabilities(homeTactics, awayTactics)

        val random = Random.nextInt(100)

        return when {
            random < winProb -> "HOME_WIN"
            random < winProb + drawProb -> "DRAW"
            else -> "AWAY_WIN"
        }
    }

    /**
     * Get tactical advantage description
     */
    fun getTacticalAdvantageDescription(
        homeTactics: TacticsEntity,
        awayTactics: TacticsEntity
    ): String {
        val (winProb, drawProb, lossProb) = calculateMatchupProbabilities(homeTactics, awayTactics)

        return when {
            winProb > 45 -> "Home team has clear tactical advantage"
            winProb > 40 -> "Home team slightly favored tactically"
            winProb in 35..40 && lossProb in 35..40 -> "Tactically balanced matchup"
            lossProb > 40 -> "Away team has tactical advantage"
            else -> "Even tactical battle expected"
        }
    }

    /**
     * Get archetype description
     */
    fun getArchetypeDescription(archetype: String): String {
        return when (archetype) {
            "POSSESSION" -> "Ball retention, tempo control, chance creation through patience"
            "ATTACKING" -> "High chance creation, overloads in attack, momentum swings"
            "BALANCED" -> "Stability, adaptability, fewer extremes"
            "COUNTER" -> "Exploits space, punishes possession-heavy sides"
            "DEFENSIVE" -> "Blocks space, frustrates opponents, high draw probability"
            "PRESSING" -> "Forces turnovers, creates chaos, high xG chances"
            else -> "Specialized tactics"
        }
    }
}