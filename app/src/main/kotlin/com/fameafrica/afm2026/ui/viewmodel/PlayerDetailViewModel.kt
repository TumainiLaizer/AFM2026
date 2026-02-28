package com.fameafrica.afm2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.database.entities.PlayersEntity
import com.fameafrica.afm2026.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import kotlin.random.Random

data class PlayerDetailUiState(
    val isLoading: Boolean = true,
    val player: PlayerDetailUiModel? = null,
    val attributes: PlayerAttributesUiModel? = null,
    val formHistory: List<Int> = emptyList(),
    val seasonStats: SeasonStatsUiModel? = null,
    val contract: ContractUiModel? = null,
    val injuryHistory: List<InjuryUiModel> = emptyList(),
    val agent: AgentUiModel? = null,
    val careerAwards: List<AwardUiModel> = emptyList(),
    val recentInterviews: List<InterviewUiModel> = emptyList()
)

data class PlayerDetailUiModel(
    val id: Int,
    val name: String,
    val age: Int,
    val position: String,
    val nationality: String,
    val nationalityFlag: String?,
    val shirtNumber: Int,
    val preferredFoot: String?,
    val overallRating: Int,
    val potential: Int,
    val form: Int,
    val morale: Int,
    val appearances: Int,
    val goals: Int,
    val assists: Int,
    val isCaptain: Boolean,
    val isViceCaptain: Boolean,
    val marketValue: Int,
    val wage: Double,
    val contractExpiry: String,
    val injuryStatus: String,
    val personality: String,
    val archetype: String?,
    val experience: Int,
    val cleanSheets: Int
)

data class PlayerAttributesUiModel(
    // Technical
    val finishing: Int,
    val passing: Int,
    val dribbling: Int,
    val crossing: Int,
    val heading: Int,
    val longShots: Int,
    val defending: Int,
    val skill: Int,

    // Physical
    val pace: Int,
    val stamina: Int,
    val strength: Int,
    val acceleration: Int,
    val agility: Int,

    // Mental
    val composure: Int,
    val decisions: Int,
    val leadership: Int,
    val vision: Int,
    val workRate: Int,
    val positioning: Int,
    val anticipation: Int,
    val creativity: Int,
    val teamwork: Int,
    val aggression: Int,

    // Goalkeeper
    val goalkeeping: Int,
    val reflexes: Int,
    val handling: Int,
    val aerialAbility: Int,
    val commandOfArea: Int,
    val kicking: Int,

    // Overall
    val overallRating: Int,
    val potential: Int,
    val form: Int
) {
    companion object {
        fun fromPlayersEntity(player: PlayersEntity): PlayerAttributesUiModel {
            return PlayerAttributesUiModel(
                // Technical
                finishing = player.finishing,
                passing = player.passing,
                dribbling = player.dribbling,
                crossing = player.crossing,
                heading = player.heading,
                longShots = player.longShots,
                defending = player.defending,
                skill = player.skill,

                // Physical
                pace = player.pace,
                stamina = player.stamina,
                strength = player.strength,
                acceleration = player.acceleration,
                agility = player.agility,

                // Mental
                composure = player.composure,
                decisions = player.decisions,
                leadership = player.leadership,
                vision = player.vision,
                workRate = when (player.workRate) {
                    "LOW" -> 30
                    "MEDIUM" -> 50
                    "HIGH" -> 70
                    "VERY_HIGH" -> 90
                    else -> 50
                },
                positioning = player.positioning,
                anticipation = player.anticipation,
                creativity = player.creativity,
                teamwork = player.teamwork,
                aggression = player.aggression,

                // Goalkeeper
                goalkeeping = player.goalkeeping,
                reflexes = player.reflexes,
                handling = player.handling,
                aerialAbility = player.aerialAbility,
                commandOfArea = player.commandOfArea,
                kicking = player.kicking,

                // Overall
                overallRating = player.rating,
                potential = player.potential,
                form = player.currentForm
            )
        }
    }
}

data class SeasonStatsUiModel(
    val matches: Int,
    val goals: Int,
    val assists: Int,
    val manOfMatch: Int,
    val yellowCards: Int,
    val redCards: Int,
    val passAccuracy: Int,
    val tackles: Int,
    val shots: Int,
    val shotsOnTarget: Int,
    val fouls: Int,
    val offsides: Int,
    val minutesPlayed: Int,
    val cleanSheets: Int = 0,
    val expectedGoals: Double,
    val expectedAssists: Double,
    val goalConversionRate: Int
)

data class ContractUiModel(
    val salary: Long,
    val expiry: String,
    val isExpiring: Boolean,
    val releaseClause: Int,
    val signingBonus: Int?,
    val isNegotiable: Boolean
)

data class InjuryUiModel(
    val type: String,
    val severity: String,
    val date: String,
    val days: Int,
    val injuryStatus: String,
    val recoveryTime: Int
)

data class AgentUiModel(
    val name: String,
    val agency: String?,
    val negotiationPower: Int,
    val commissionRate: Int,
    val reputation: Int,
    val yearsExperience: Int,
    val successfulDeals: Int,
    val totalDealValue: Long
)

data class AwardUiModel(
    val awardType: String,
    val season: String,
    val category: String,
    val description: String,
    val prizeMoney: Int
)

data class InterviewUiModel(
    val journalistName: String,
    val date: String,
    val topic: String,
    val responseType: String?,
    val impactOnMorale: Int
)

@HiltViewModel
class PlayerDetailViewModel @Inject constructor(
    private val playersRepository: PlayersRepository,
    private val matchEventsRepository: MatchEventsRepository,
    private val playerContractsRepository: PlayerContractsRepository,
    private val playerAgentsRepository: PlayerAgentsRepository,
    private val seasonAwardsRepository: SeasonAwardsRepository,
    private val interviewsRepository: InterviewsRepository,
    private val playerTrainingRepository: PlayerTrainingRepository,
    private val playerReactionsRepository: PlayerReactionsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerDetailUiState(isLoading = true))
    val uiState: StateFlow<PlayerDetailUiState> = _uiState

    fun loadPlayer(playerId: Int) {
        viewModelScope.launch {
            // Get player from repository
            val player = playersRepository.getPlayerById(playerId)

            if (player == null) {
                _uiState.value = PlayerDetailUiState(isLoading = false)
                return@launch
            }

            // Get all related data in parallel for better performance
            val contractDeferred = viewModelScope.async {
                playerContractsRepository.getContractByPlayerName(player.name)
            }

            val agentDeferred = viewModelScope.async {
                playerAgentsRepository.getAgentByPlayerName(player.name)
            }

            val awardsDeferred = viewModelScope.async {
                seasonAwardsRepository.getPlayerAwards(playerId).firstOrNull() ?: emptyList()
            }

            val interviewsDeferred = viewModelScope.async {
                interviewsRepository.getPlayerInterviews(playerId).firstOrNull() ?: emptyList()
            }

            val trainingDeferred = viewModelScope.async {
                playerTrainingRepository.getActiveTrainingForPlayer(player.name)
            }

            // Get match stats
            val goals = matchEventsRepository.getPlayerGoalCount(playerId)
            val assists = matchEventsRepository.getPlayerAssistCount(playerId)
            val yellows = matchEventsRepository.getPlayerYellowCardCount(playerId)
            val reds = matchEventsRepository.getPlayerRedCardCount(playerId)
            val manOfMatch = matchEventsRepository.getPlayerManOfTheMatchCount(playerId)
            val totalXG = matchEventsRepository.getPlayerTotalXG(playerId)

            // Get shot stats for conversion rate
            val playerEvents = matchEventsRepository.getEventsByPlayer(playerId).firstOrNull() ?: emptyList()
            val shots = playerEvents.count { it.eventType in listOf("SHOT", "SHOT_ON_TARGET", "SHOT_OFF_TARGET", "GOAL") }
            val shotsOnTarget = playerEvents.count { it.eventType in listOf("SHOT_ON_TARGET", "GOAL") }

            // Calculate pass accuracy and tackles (simplified)
            val passAccuracy = calculatePassAccuracy(player)
            val tackles = playerEvents.count { it.eventType == "TACKLE" }

            // Generate form history (last 10 matches)
            val formHistory = generateFormHistory(playerId, player.currentForm)

            // Build player detail model
            val playerDetail = PlayerDetailUiModel(
                id = player.id,
                name = player.name,
                age = player.age,
                position = player.position,
                nationality = player.nationality,
                nationalityFlag = player.imageUrl ?: "flags/${player.nationality}.png",
                shirtNumber = extractShirtNumber(player),
                preferredFoot = player.preferredFoot,
                overallRating = player.rating,
                potential = player.potential,
                form = player.currentForm,
                morale = player.morale,
                appearances = player.matches,
                goals = player.goals,
                assists = player.assists,
                isCaptain = player.isCaptain,
                isViceCaptain = player.isViceCaptain,
                marketValue = player.marketValue,
                wage = player.salary,
                contractExpiry = player.contractExpiry,
                injuryStatus = player.injuryStatus,
                personality = player.personalityType,
                archetype = player.archetype,
                experience = player.experience,
                cleanSheets = player.cleanSheets
            )

            // Build attributes model
            val attributes = PlayerAttributesUiModel.fromPlayersEntity(player)

            // Build season stats
            val seasonStats = SeasonStatsUiModel(
                matches = player.matches,
                goals = goals,
                assists = assists,
                manOfMatch = manOfMatch,
                yellowCards = yellows,
                redCards = reds,
                passAccuracy = passAccuracy,
                tackles = tackles,
                shots = shots,
                shotsOnTarget = shotsOnTarget,
                fouls = playerEvents.count { it.eventType == "FOUL" },
                offsides = playerEvents.count { it.eventType == "OFFSIDE" },
                minutesPlayed = player.matches * 90, // Simplified
                cleanSheets = if (player.position == "GK") player.cleanSheets else 0,
                expectedGoals = totalXG,
                expectedAssists = 0.0, // Would need expected assists data
                goalConversionRate = if (shots > 0) (goals * 100 / shots) else 0
            )

            // Await parallel requests
            val contract = contractDeferred.await()
            val agent = agentDeferred.await()
            val awards = awardsDeferred.await()
            val interviews = interviewsDeferred.await()
            val activeTraining = trainingDeferred.await()

            // Build contract model
            val contractUi = contract?.let {
                val expiryYear = extractYearFromDate(it.contractEndDate)
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)

                ContractUiModel(
                    salary = it.salary.toLong(),
                    expiry = it.contractEndDate,
                    isExpiring = expiryYear - currentYear <= 1,
                    releaseClause = it.releaseClause,
                    signingBonus = it.signingBonus,
                    isNegotiable = it.isNegotiable
                )
            }

            // Build agent model
            val agentUi = agent?.let {
                AgentUiModel(
                    name = it.agentName,
                    agency = it.agency,
                    negotiationPower = it.negotiationPower,
                    commissionRate = it.commissionRate,
                    reputation = it.reputation,
                    yearsExperience = it.yearsExperience,
                    successfulDeals = it.successfulDeals,
                    totalDealValue = it.totalDealValue
                )
            }

            // Build awards models
            val awardModels = awards.map { award ->
                AwardUiModel(
                    awardType = award.awardType,
                    season = award.season,
                    category = award.awardCategory,
                    description = award.description ?: "",
                    prizeMoney = award.prizeMoney ?: 0
                )
            }

            // Build interview models
            val interviewModels = interviews.take(5).map { interview ->
                InterviewUiModel(
                    journalistName = interview.journalistName,
                    date = interview.dateRequested,
                    topic = interview.topic,
                    responseType = interview.responseType,
                    impactOnMorale = interview.impactOnMorale ?: 0
                )
            }

            // Build injury history (from player entity)
            val injuryHistory = if (player.isInjured) {
                listOf(
                    InjuryUiModel(
                        type = player.injuryStatus,
                        severity = determineInjurySeverity(player.injuryStatus),
                        date = player.updatedAt,
                        days = player.recoveryTime,
                        injuryStatus = player.injuryStatus,
                        recoveryTime = player.recoveryTime
                    )
                )
            } else {
                emptyList()
            }

            // Update UI state
            _uiState.value = PlayerDetailUiState(
                isLoading = false,
                player = playerDetail,
                attributes = attributes,
                formHistory = formHistory,
                seasonStats = seasonStats,
                contract = contractUi,
                injuryHistory = injuryHistory,
                agent = agentUi,
                careerAwards = awardModels,
                recentInterviews = interviewModels
            )
        }
    }

    private suspend fun generateFormHistory(playerId: Int, currentForm: Int): List<Int> {
        // Get last 10 match ratings from match events repository
        val matchRatings = matchEventsRepository.getPlayerLastMatchRatings(playerId, 10)

        // If we have real data, use it
        if (matchRatings.isNotEmpty()) {
            return matchRatings
        }

        // Otherwise generate realistic placeholder data based on player form
        return List(10) { index ->
            // Generate realistic form variation (±8 points) with trend
            val trend = (index - 5) / 2 // Slight upward/downward trend
            (currentForm + Random.nextInt(-8, 9) + trend).coerceIn(1, 100)
        }
    }

    private fun calculatePassAccuracy(player: PlayersEntity): Int {
        return when {
            player.position in listOf("CDM", "CM", "CAM") -> 85
            player.position in listOf("CB", "LB", "RB") -> 80
            player.position in listOf("LW", "RW", "ST", "CF") -> 75
            player.position == "GK" -> 65
            else -> 70
        }
    }

    private fun extractShirtNumber(player: PlayersEntity): Int {
        // Extract shirt number from name or use default
        // This would need to be stored in the database ideally
        return when (player.position) {
            "GK" -> 1
            "CB" -> 4
            "LB", "RB" -> 2
            "CDM" -> 6
            "CM" -> 8
            "CAM" -> 10
            "LW", "RW" -> 7
            "ST", "CF" -> 9
            else -> (1..99).random()
        }
    }

    private fun extractYearFromDate(date: String): Int {
        return try {
            date.split("-").first().toInt()
        } catch (e: Exception) {
            Calendar.getInstance().get(Calendar.YEAR)
        }
    }

    private fun determineInjurySeverity(injuryStatus: String): String {
        return when {
            injuryStatus.contains("MINOR", ignoreCase = true) -> "MINOR"
            injuryStatus.contains("MODERATE", ignoreCase = true) -> "MODERATE"
            injuryStatus.contains("SEVERE", ignoreCase = true) -> "SEVERE"
            else -> "UNKNOWN"
        }
    }

    fun refreshPlayer(playerId: Int) {
        loadPlayer(playerId)
    }

    fun handleTransferClick() {
        // Navigate to transfer screen
    }

    fun handleLoanClick() {
        // Navigate to loan screen
    }

    fun handleContractClick() {
        // Navigate to contract renewal screen
    }

    fun handleAgentClick() {
        // Navigate to agent details
    }

    fun handleAwardClick(awardId: String) {
        // Navigate to award details
    }

    fun handleInterviewClick(interviewId: Int) {
        // Navigate to interview details
    }
}
