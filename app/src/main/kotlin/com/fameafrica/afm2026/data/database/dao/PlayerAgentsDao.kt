package com.fameafrica.afm2026.data.database.dao

import androidx.room.*
import com.fameafrica.afm2026.data.database.entities.PlayerAgentsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerAgentsDao {

    // ============ BASIC CRUD ============

    @Query("SELECT * FROM player_agents ORDER BY agent_name")
    fun getAll(): Flow<List<PlayerAgentsEntity>>

    @Query("SELECT * FROM player_agents WHERE id = :id")
    suspend fun getById(id: Int): PlayerAgentsEntity?

    @Query("SELECT * FROM player_agents WHERE agent_name = :agentName")
    suspend fun getByAgentName(agentName: String): PlayerAgentsEntity?

    @Query("SELECT * FROM player_agents WHERE player_name = :playerName")
    suspend fun getByPlayerName(playerName: String): PlayerAgentsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(agent: PlayerAgentsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(agents: List<PlayerAgentsEntity>)

    @Update
    suspend fun update(agent: PlayerAgentsEntity)

    @Delete
    suspend fun delete(agent: PlayerAgentsEntity)

    @Query("DELETE FROM player_agents WHERE player_name = :playerName")
    suspend fun deleteByPlayer(playerName: String)

    @Query("DELETE FROM player_agents")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM player_agents")
    suspend fun getCount(): Int

    // ============ AGENT-BASED QUERIES ============

    @Query("SELECT * FROM player_agents WHERE reputation >= :minReputation ORDER BY reputation DESC")
    fun getTopAgents(minReputation: Int): Flow<List<PlayerAgentsEntity>>

    @Query("SELECT * FROM player_agents WHERE negotiation_power >= :minPower ORDER BY negotiation_power DESC")
    fun getBestNegotiators(minPower: Int): Flow<List<PlayerAgentsEntity>>

    @Query("SELECT * FROM player_agents WHERE specialization = :specialization ORDER BY reputation DESC")
    fun getAgentsBySpecialization(specialization: String): Flow<List<PlayerAgentsEntity>>

    @Query("SELECT * FROM player_agents WHERE nationality = :nationality ORDER BY reputation DESC")
    fun getAgentsByNationality(nationality: String): Flow<List<PlayerAgentsEntity>>

    @Query("SELECT * FROM player_agents WHERE years_experience >= :minYears ORDER BY years_experience DESC")
    fun getExperiencedAgents(minYears: Int): Flow<List<PlayerAgentsEntity>>

    // ============ STATISTICS QUERIES ============

    @Query("""
        SELECT 
            specialization,
            COUNT(*) as agent_count,
            AVG(negotiation_power) as avg_negotiation,
            AVG(reputation) as avg_reputation
        FROM player_agents 
        WHERE specialization IS NOT NULL
        GROUP BY specialization
        ORDER BY agent_count DESC
    """)
    fun getAgentSpecializationStats(): Flow<List<AgentSpecializationStats>>

    @Query("""
        SELECT 
            nationality,
            COUNT(*) as agent_count,
            AVG(negotiation_power) as avg_negotiation
        FROM player_agents 
        WHERE nationality IS NOT NULL
        GROUP BY nationality
        ORDER BY agent_count DESC
    """)
    fun getAgentNationalityStats(): Flow<List<AgentNationalityStats>>
}

// ============ DATA CLASSES ============

data class AgentSpecializationStats(
    @ColumnInfo(name = "specialization")
    val specialization: String,

    @ColumnInfo(name = "agent_count")
    val agentCount: Int,

    @ColumnInfo(name = "avg_negotiation")
    val averageNegotiation: Double,

    @ColumnInfo(name = "avg_reputation")
    val averageReputation: Double
)

data class AgentNationalityStats(
    @ColumnInfo(name = "nationality")
    val nationality: String,

    @ColumnInfo(name = "agent_count")
    val agentCount: Int,

    @ColumnInfo(name = "avg_negotiation")
    val averageNegotiation: Double
)