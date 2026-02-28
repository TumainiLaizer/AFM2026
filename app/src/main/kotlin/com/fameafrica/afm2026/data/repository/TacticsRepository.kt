package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.ArchetypeCount
import com.fameafrica.afm2026.data.database.dao.FormationCount
import com.fameafrica.afm2026.data.database.dao.TacticsDao
import com.fameafrica.afm2026.data.database.dao.ManagersDao
import com.fameafrica.afm2026.data.database.entities.*
import com.fameafrica.afm2026.utils.tactics.TacticalMatchupEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TacticsRepository @Inject constructor(
    private val tacticsDao: TacticsDao,
    private val managersDao: ManagersDao
) {

    // ============ BASIC CRUD ============

    fun getAllTactics(): Flow<List<TacticsEntity>> = tacticsDao.getAll()

    suspend fun getTacticsById(id: Int): TacticsEntity? = tacticsDao.getById(id)

    suspend fun getTacticsByTeam(teamName: String): TacticsEntity? = tacticsDao.getByTeamName(teamName)

    fun getTacticsByTeamFlow(teamName: String): Flow<TacticsEntity?> = tacticsDao.getByTeamNameFlow(teamName)

    suspend fun insertTactics(tactics: TacticsEntity) = tacticsDao.insert(tactics)

    suspend fun updateTactics(tactics: TacticsEntity) = tacticsDao.update(tactics)

    suspend fun deleteTactics(tactics: TacticsEntity) = tacticsDao.delete(tactics)

    suspend fun deleteByTeam(teamName: String) = tacticsDao.deleteByTeam(teamName)

    // ============ TACTICS CREATION ============

    /**
     * Create default tactics for a team based on manager preferences
     */
    suspend fun createDefaultTactics(
        teamName: String,
        managerId: Int? = null
    ): TacticsEntity {
        val manager = managerId?.let { managersDao.getById(it) }

        // Base tactics from manager or default
        val formation = manager?.preferredFormation ?: "4-4-2"
        val style = manager?.style ?: "Balanced"

        // Map manager style to tactical archetype
        val archetype = mapStyleToArchetype(style)

        // Set thresholds based on archetype
        val (defensiveThreshold, attackingThreshold, tempo, pressIntensity) =
            getThresholdsForArchetype(archetype)

        // Calculate matchup probabilities
        val tactics = createTacticsEntity(
            teamName = teamName,
            managerId = managerId,
            formation = formation,
            archetype = archetype,
            playstyle = style,
            defensiveThreshold = defensiveThreshold,
            attackingThreshold = attackingThreshold,
            tempo = tempo,
            pressIntensity = pressIntensity,
            managerTacticalFlexibility = manager?.tacticalFlexibility,
            managerPreferredStyle = manager?.style
        )

        // Calculate all matchup probabilities
        val tacticsWithProbabilities = calculateAllMatchupProbabilities(tactics)

        tacticsDao.insert(tacticsWithProbabilities)
        return tacticsWithProbabilities
    }

    /**
     * Update tactics based on manager changes
     */
    suspend fun updateTacticsFromManager(
        teamName: String,
        managerId: Int
    ): TacticsEntity? {
        val manager = managersDao.getById(managerId) ?: return null
        val existingTactics = tacticsDao.getByTeamName(teamName)

        val updatedTactics = existingTactics?.copy(
            managerId = managerId,
            formation = manager.preferredFormation ?: existingTactics.formation,
            playstyle = manager.style ?: existingTactics.playstyle,
            tacticalArchetype = mapStyleToArchetype(manager.style ?: "Balanced"),
            managerTacticalFlexibility = manager.tacticalFlexibility,
            managerPreferredStyle = manager.style,
            lastUpdated = getCurrentDateTime()
        )?.let { calculateAllMatchupProbabilities(it) }

        if (updatedTactics != null) {
            tacticsDao.update(updatedTactics)
        }

        return updatedTactics
    }

    // ============ TACTICS CUSTOMIZATION ============

    /**
     * Customize team tactics
     */
    suspend fun customizeTactics(
        teamName: String,
        formation: String? = null,
        archetype: String? = null,
        playstyle: String? = null,
        defensiveThreshold: Int? = null,
        attackingThreshold: Int? = null,
        tempo: Int? = null,
        width: Int? = null,
        depth: Int? = null,
        pressIntensity: Int? = null,
        passingDirectness: Int? = null,
        creativity: Int? = null
    ): TacticsEntity? {

        val existing = tacticsDao.getByTeamName(teamName) ?: return null

        val updated = existing.copy(
            formation = formation ?: existing.formation,
            tacticalArchetype = archetype ?: existing.tacticalArchetype,
            playstyle = playstyle ?: existing.playstyle,
            defensiveThreshold = defensiveThreshold ?: existing.defensiveThreshold,
            attackingThreshold = attackingThreshold ?: existing.attackingThreshold,
            tempo = tempo ?: existing.tempo,
            width = width ?: existing.width,
            depth = depth ?: existing.depth,
            pressIntensity = pressIntensity ?: existing.pressIntensity,
            passingDirectness = passingDirectness ?: existing.passingDirectness,
            creativity = creativity ?: existing.creativity,
            lastUpdated = getCurrentDateTime()
        )

        // Recalculate probabilities based on new archetype
        val finalTactics = calculateAllMatchupProbabilities(updated)

        tacticsDao.update(finalTactics)
        return finalTactics
    }

    // ============ MATCHUP ANALYSIS ============

    /**
     * Analyze matchup between two teams
     */
    suspend fun analyzeMatchup(
        homeTeam: String,
        awayTeam: String
    ): MatchupAnalysis? {

        val homeTactics = tacticsDao.getByTeamName(homeTeam) ?: return null
        val awayTactics = tacticsDao.getByTeamName(awayTeam) ?: return null

        val probabilities = TacticalMatchupEngine.calculateMatchupProbabilities(homeTactics, awayTactics)
        val advantage = TacticalMatchupEngine.getTacticalAdvantageDescription(homeTactics, awayTactics)
        val predictedResult = TacticalMatchupEngine.simulateMatchResult(homeTactics, awayTactics)

        return MatchupAnalysis(
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            homeTactics = homeTactics,
            awayTactics = awayTactics,
            homeWinProb = probabilities.first,
            drawProb = probabilities.second,
            awayWinProb = probabilities.third,
            tacticalAdvantage = advantage,
            predictedResult = predictedResult,
            homeArchetypeDescription = TacticalMatchupEngine.getArchetypeDescription(homeTactics.tacticalArchetype),
            awayArchetypeDescription = TacticalMatchupEngine.getArchetypeDescription(awayTactics.tacticalArchetype)
        )
    }

    /**
     * Get tactical recommendations against a specific opponent
     */
    suspend fun getTacticalRecommendations(
        teamName: String,
        opponentTeam: String
    ): List<TacticalSuggestion> {

        val teamTactics = tacticsDao.getByTeamName(teamName) ?: return emptyList()
        val opponentTactics = tacticsDao.getByTeamName(opponentTeam) ?: return emptyList()

        val suggestions = mutableListOf<TacticalSuggestion>()

        // Suggest based on opponent's weaknesses
        when (opponentTactics.tacticalArchetype) {
            "POSSESSION" -> {
                suggestions.add(TacticalSuggestion(
                    "Use Counter Attack",
                    "Possession-based teams are vulnerable to quick counters",
                    "COUNTER"
                ))
                suggestions.add(TacticalSuggestion(
                    "High Press",
                    "Disrupt their buildup with intense pressing",
                    "PRESSING"
                ))
            }
            "ATTACKING" -> {
                suggestions.add(TacticalSuggestion(
                    "Compact Defense",
                    "Block space and hit on the counter",
                    "DEFENSIVE"
                ))
                suggestions.add(TacticalSuggestion(
                    "Balanced Approach",
                    "Match their energy but stay organized",
                    "BALANCED"
                ))
            }
            "COUNTER" -> {
                suggestions.add(TacticalSuggestion(
                    "Possession Control",
                    "Keep the ball to deny counter opportunities",
                    "POSSESSION"
                ))
                suggestions.add(TacticalSuggestion(
                    "High Line",
                    "Catch them offside with a high defensive line",
                    "PRESSING"
                ))
            }
            "DEFENSIVE" -> {
                suggestions.add(TacticalSuggestion(
                    "Attacking Football",
                    "Break down their defense with sustained pressure",
                    "ATTACKING"
                ))
                suggestions.add(TacticalSuggestion(
                    "Wide Play",
                    "Stretch their compact defense with width",
                    "ATTACKING"
                ))
            }
            "PRESSING" -> {
                suggestions.add(TacticalSuggestion(
                    "Quick Transitions",
                    "Bypass the press with direct passing",
                    "COUNTER"
                ))
                suggestions.add(TacticalSuggestion(
                    "Patient Buildup",
                    "Draw them in then exploit spaces",
                    "POSSESSION"
                ))
            }
        }

        return suggestions
    }

    // ============ HELPER FUNCTIONS ============

    private fun mapStyleToArchetype(style: String): String {
        return when (style.lowercase()) {
            "attacking" -> "ATTACKING"
            "defensive" -> "DEFENSIVE"
            "possession" -> "POSSESSION"
            "counter attack" -> "COUNTER"
            "high press" -> "PRESSING"
            "tiki-taka" -> "POSSESSION"
            "gegenpressing" -> "PRESSING"
            else -> "BALANCED"
        }
    }

    private fun getThresholdsForArchetype(archetype: String): Quadruple<Int, Int, Int, Int> {
        return when (archetype) {
            "POSSESSION" -> Quadruple(40, 60, 40, 50)  // def, att, tempo, press
            "ATTACKING" -> Quadruple(30, 70, 70, 60)
            "BALANCED" -> Quadruple(50, 50, 50, 50)
            "COUNTER" -> Quadruple(60, 40, 80, 40)
            "DEFENSIVE" -> Quadruple(80, 20, 30, 30)
            "PRESSING" -> Quadruple(60, 60, 80, 90)
            else -> Quadruple(50, 50, 50, 50)
        }
    }

    private fun calculateMatchupProbabilitiesForArchetype(
        homeArchetype: String,
        awayArchetype: String
    ): Triple<Int, Int, Int> {
        // This would use the probability matrix
        // For now, return balanced probabilities
        return when (homeArchetype to awayArchetype) {
            "POSSESSION" to "COUNTER" -> Triple(40, 25, 35)
            "POSSESSION" to "DEFENSIVE" -> Triple(45, 35, 20)
            "POSSESSION" to "PRESSING" -> Triple(35, 25, 40)
            "ATTACKING" to "DEFENSIVE" -> Triple(50, 30, 20)
            "PRESSING" to "DEFENSIVE" -> Triple(55, 25, 20)
            "PRESSING" to "COUNTER" -> Triple(50, 20, 30)
            else -> Triple(33, 34, 33)
        }
    }

    private fun createTacticsEntity(
        teamName: String,
        managerId: Int?,
        formation: String,
        archetype: String,
        playstyle: String,
        defensiveThreshold: Int,
        attackingThreshold: Int,
        tempo: Int,
        pressIntensity: Int,
        managerTacticalFlexibility: Int?,
        managerPreferredStyle: String?
    ): TacticsEntity {
        return TacticsEntity(
            teamName = teamName,
            managerId = managerId,
            formation = formation,
            tacticalArchetype = archetype,
            playstyle = playstyle,
            defensiveThreshold = defensiveThreshold,
            attackingThreshold = attackingThreshold,
            tempo = tempo,
            width = 50,  // Default
            depth = 50,  // Default
            pressIntensity = pressIntensity,
            passingDirectness = 50,  // Default
            creativity = 50,  // Default
            managerTacticalFlexibility = managerTacticalFlexibility,
            managerPreferredStyle = managerPreferredStyle,
            lastUpdated = getCurrentDateTime()
        )
    }

    private fun calculateAllMatchupProbabilities(tactics: TacticsEntity): TacticsEntity {
        // This would calculate probabilities against all archetypes
        // For now, return with default values
        return tactics
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // ============ STATISTICS ============

    fun getArchetypeDistribution(): Flow<List<ArchetypeCount>> = tacticsDao.getArchetypeDistribution()

    fun getFormationDistribution(): Flow<List<FormationCount>> = tacticsDao.getFormationDistribution()

    fun getMostDefensiveTeams(minThreshold: Int = 70): Flow<List<TacticsEntity>> =
        tacticsDao.getMostDefensive(minThreshold)

    fun getMostAttackingTeams(minThreshold: Int = 70): Flow<List<TacticsEntity>> =
        tacticsDao.getMostAttacking(minThreshold)

    fun getHighPressingTeams(): Flow<List<TacticsEntity>> = tacticsDao.getHighPressingTeams()

    // ============ DASHBOARD ============

    suspend fun getTeamTacticsDashboard(teamName: String): TeamTacticsDashboard {
        val tactics = tacticsDao.getByTeamName(teamName) ?: return TeamTacticsDashboard.empty()

        return TeamTacticsDashboard(
            teamName = teamName,
            formation = tactics.formation,
            tacticalArchetype = tactics.tacticalArchetype,
            playstyle = tactics.playstyle,
            defensiveStyle = tactics.defensiveStyle,
            attackingStyle = tactics.attackingStyle,
            overallStyle = tactics.overallStyle,
            defensiveThreshold = tactics.defensiveThreshold,
            attackingThreshold = tactics.attackingThreshold,
            tempo = tactics.tempo,
            width = tactics.width,
            depth = tactics.depth,
            pressIntensity = tactics.pressIntensity,
            passingDirectness = tactics.passingDirectness,
            creativity = tactics.creativity,
            archetypeDescription = TacticalMatchupEngine.getArchetypeDescription(tactics.tacticalArchetype),
            managerId = tactics.managerId,
            lastUpdated = tactics.lastUpdated
        )
    }
}

// ============ DATA CLASSES ============

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

data class MatchupAnalysis(
    val homeTeam: String,
    val awayTeam: String,
    val homeTactics: TacticsEntity,
    val awayTactics: TacticsEntity,
    val homeWinProb: Int,
    val drawProb: Int,
    val awayWinProb: Int,
    val tacticalAdvantage: String,
    val predictedResult: String,
    val homeArchetypeDescription: String,
    val awayArchetypeDescription: String
)

data class TacticalSuggestion(
    val title: String,
    val description: String,
    val recommendedArchetype: String
)

data class TeamTacticsDashboard(
    val teamName: String,
    val formation: String,
    val tacticalArchetype: String,
    val playstyle: String,
    val defensiveStyle: String,
    val attackingStyle: String,
    val overallStyle: String,
    val defensiveThreshold: Int,
    val attackingThreshold: Int,
    val tempo: Int,
    val width: Int,
    val depth: Int,
    val pressIntensity: Int,
    val passingDirectness: Int,
    val creativity: Int,
    val archetypeDescription: String,
    val managerId: Int?,
    val lastUpdated: String?
) {
    companion object {
        fun empty(): TeamTacticsDashboard = TeamTacticsDashboard(
            teamName = "",
            formation = "",
            tacticalArchetype = "",
            playstyle = "",
            defensiveStyle = "",
            attackingStyle = "",
            overallStyle = "",
            defensiveThreshold = 0,
            attackingThreshold = 0,
            tempo = 0,
            width = 0,
            depth = 0,
            pressIntensity = 0,
            passingDirectness = 0,
            creativity = 0,
            archetypeDescription = "",
            managerId = null,
            lastUpdated = null
        )
    }
}