package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.AgentNationalityStats
import com.fameafrica.afm2026.data.database.dao.AgentSpecializationStats
import com.fameafrica.afm2026.data.database.dao.PlayerAgentsDao
import com.fameafrica.afm2026.data.database.entities.PlayerAgentsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PlayerAgentsRepository @Inject constructor(
    private val playerAgentsDao: PlayerAgentsDao
) {

    // ============ BASIC CRUD ============

    fun getAllAgents(): Flow<List<PlayerAgentsEntity>> = playerAgentsDao.getAll()

    suspend fun getAgentById(id: Int): PlayerAgentsEntity? = playerAgentsDao.getById(id)

    suspend fun getAgentByPlayerName(playerName: String): PlayerAgentsEntity? =
        playerAgentsDao.getByPlayerName(playerName)

    suspend fun insertAgent(agent: PlayerAgentsEntity) = playerAgentsDao.insert(agent)

    suspend fun updateAgent(agent: PlayerAgentsEntity) = playerAgentsDao.update(agent)

    suspend fun deleteAgent(agent: PlayerAgentsEntity) = playerAgentsDao.delete(agent)

    suspend fun deleteByPlayer(playerName: String) = playerAgentsDao.deleteByPlayer(playerName)

    // ============ AGENT ASSIGNMENT ============

    /**
     * Assign an agent to a player
     */
    suspend fun assignAgentToPlayer(
        agentName: String,
        playerName: String,
        agency: String? = null,
        negotiationPower: Int = 50,
        commissionRate: Int = 10
    ): PlayerAgentsEntity {

        // Check if player already has an agent
        val existing = playerAgentsDao.getByPlayerName(playerName)
        existing?.let { deleteAgent(it) }

        val agent = PlayerAgentsEntity(
            agentName = agentName,
            agency = agency,
            playerName = playerName,
            negotiationPower = negotiationPower,
            commissionRate = commissionRate,
            reputation = calculateAgentReputation(negotiationPower),
            yearsExperience = calculateExperience(negotiationPower),
            successfulDeals = Random.nextInt(5, 50),
            totalDealValue = Random.nextLong(1000000, 50000000)
        )

        playerAgentsDao.insert(agent)
        return agent
    }

    private fun calculateAgentReputation(negotiationPower: Int): Int {
        return when {
            negotiationPower >= 90 -> 95
            negotiationPower >= 80 -> 85
            negotiationPower >= 70 -> 75
            negotiationPower >= 60 -> 65
            else -> 55
        }
    }

    private fun calculateExperience(negotiationPower: Int): Int {
        return when {
            negotiationPower >= 90 -> 20
            negotiationPower >= 80 -> 15
            negotiationPower >= 70 -> 10
            negotiationPower >= 60 -> 7
            else -> 3
        }
    }

    /**
     * Release a player from their agent
     */
    suspend fun releasePlayerFromAgent(playerName: String): Boolean {
        val agent = playerAgentsDao.getByPlayerName(playerName) ?: return false
        playerAgentsDao.delete(agent)
        return true
    }

    /**
     * Update agent negotiation power (after successful deals)
     */
    suspend fun updateNegotiationPower(agentId: Int, newPower: Int): PlayerAgentsEntity? {
        val agent = playerAgentsDao.getById(agentId) ?: return null

        val updated = agent.copy(
            negotiationPower = newPower.coerceIn(0, 100),
            reputation = calculateAgentReputation(newPower)
        )

        playerAgentsDao.update(updated)
        return updated
    }

    /**
     * Record a successful deal for an agent
     */
    suspend fun recordSuccessfulDeal(agentId: Int, dealValue: Long): PlayerAgentsEntity? {
        val agent = playerAgentsDao.getById(agentId) ?: return null

        val updated = agent.copy(
            successfulDeals = agent.successfulDeals + 1,
            totalDealValue = agent.totalDealValue + dealValue
        )

        playerAgentsDao.update(updated)
        return updated
    }

    // ============ QUERIES ============

    fun getTopAgents(minReputation: Int): Flow<List<PlayerAgentsEntity>> =
        playerAgentsDao.getTopAgents(minReputation)

    fun getBestNegotiators(minPower: Int): Flow<List<PlayerAgentsEntity>> =
        playerAgentsDao.getBestNegotiators(minPower)

    fun getAgentsBySpecialization(specialization: String): Flow<List<PlayerAgentsEntity>> =
        playerAgentsDao.getAgentsBySpecialization(specialization)

    // ============ STATISTICS ============

    fun getAgentSpecializationStats(): Flow<List<AgentSpecializationStats>> =
        playerAgentsDao.getAgentSpecializationStats()

    fun getAgentNationalityStats(): Flow<List<AgentNationalityStats>> =
        playerAgentsDao.getAgentNationalityStats()

    // ============ DASHBOARD ============

    suspend fun getAgentDashboard(): AgentDashboard {
        val allAgents = playerAgentsDao.getAll().firstOrNull() ?: emptyList()
        val topAgents = allAgents.sortedByDescending { it.reputation }.take(10)
        val bestNegotiators = allAgents.sortedByDescending { it.negotiationPower }.take(10)

        val totalDealValue = allAgents.sumOf { it.totalDealValue }
        val totalSuccessfulDeals = allAgents.sumOf { it.successfulDeals }

        return AgentDashboard(
            totalAgents = allAgents.size,
            topAgents = topAgents,
            bestNegotiators = bestNegotiators,
            totalDealValue = totalDealValue,
            totalSuccessfulDeals = totalSuccessfulDeals
        )
    }
}

// ============ DATA CLASSES ============

data class AgentDashboard(
    val totalAgents: Int,
    val topAgents: List<PlayerAgentsEntity>,
    val bestNegotiators: List<PlayerAgentsEntity>,
    val totalDealValue: Long,
    val totalSuccessfulDeals: Int
)