package com.fameafrica.afm2026.data.database.dao

import androidx.room.*
import com.fameafrica.afm2026.data.database.entities.TacticsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TacticsDao {

    // ============ BASIC CRUD ============

    @Query("SELECT * FROM tactics ORDER BY team_name")
    fun getAll(): Flow<List<TacticsEntity>>

    @Query("SELECT * FROM tactics WHERE id = :id")
    suspend fun getById(id: Int): TacticsEntity?

    @Query("SELECT * FROM tactics WHERE team_name = :teamName")
    suspend fun getByTeamName(teamName: String): TacticsEntity?

    @Query("SELECT * FROM tactics WHERE team_name = :teamName")
    fun getByTeamNameFlow(teamName: String): Flow<TacticsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tactics: TacticsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tactics: List<TacticsEntity>)

    @Update
    suspend fun update(tactics: TacticsEntity)

    @Delete
    suspend fun delete(tactics: TacticsEntity)

    @Query("DELETE FROM tactics WHERE team_name = :teamName")
    suspend fun deleteByTeam(teamName: String)

    @Query("DELETE FROM tactics")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM tactics")
    suspend fun getCount(): Int

    // ============ ARCHETYPE-BASED QUERIES ============

    @Query("SELECT * FROM tactics WHERE tactical_archetype = :archetype")
    fun getByArchetype(archetype: String): Flow<List<TacticsEntity>>

    @Query("SELECT tactical_archetype, COUNT(*) as count FROM tactics GROUP BY tactical_archetype ORDER BY count DESC")
    fun getArchetypeDistribution(): Flow<List<ArchetypeCount>>

    // ============ FORMATION-BASED QUERIES ============

    @Query("SELECT * FROM tactics WHERE formation = :formation")
    fun getByFormation(formation: String): Flow<List<TacticsEntity>>

    @Query("SELECT formation, COUNT(*) as count FROM tactics GROUP BY formation ORDER BY count DESC")
    fun getFormationDistribution(): Flow<List<FormationCount>>

    // ============ PLAYSTYLE-BASED QUERIES ============

    @Query("SELECT * FROM tactics WHERE playstyle = :playstyle")
    fun getByPlaystyle(playstyle: String): Flow<List<TacticsEntity>>

    // ============ MANAGER-BASED QUERIES ============

    @Query("SELECT * FROM tactics WHERE manager_id = :managerId")
    suspend fun getByManagerId(managerId: Int): TacticsEntity?

    @Query("SELECT * FROM tactics WHERE manager_id IS NULL")
    fun getDefaultTactics(): Flow<List<TacticsEntity>>

    // ============ THRESHOLD QUERIES ============

    @Query("SELECT * FROM tactics WHERE defensive_threshold >= :minThreshold ORDER BY defensive_threshold DESC")
    fun getMostDefensive(minThreshold: Int): Flow<List<TacticsEntity>>

    @Query("SELECT * FROM tactics WHERE attacking_threshold >= :minThreshold ORDER BY attacking_threshold DESC")
    fun getMostAttacking(minThreshold: Int): Flow<List<TacticsEntity>>

    @Query("SELECT * FROM tactics WHERE press_intensity >= 70 ORDER BY press_intensity DESC")
    fun getHighPressingTeams(): Flow<List<TacticsEntity>>

    @Query("SELECT * FROM tactics WHERE tempo >= 70 ORDER BY tempo DESC")
    fun getFastTempoTeams(): Flow<List<TacticsEntity>>

    @Query("SELECT * FROM tactics WHERE tempo <= 30 ORDER BY tempo ASC")
    fun getSlowTempoTeams(): Flow<List<TacticsEntity>>

    // ============ SEARCH ============

    @Query("SELECT * FROM tactics WHERE formation LIKE '%' || :searchQuery || '%' OR playstyle LIKE '%' || :searchQuery || '%' OR tactical_archetype LIKE '%' || :searchQuery || '%'")
    fun searchTactics(searchQuery: String): Flow<List<TacticsEntity>>
}

// ============ DATA CLASSES ============

data class ArchetypeCount(
    @ColumnInfo(name = "tactical_archetype")
    val archetype: String,

    @ColumnInfo(name = "count")
    val count: Int
)

data class FormationCount(
    @ColumnInfo(name = "formation")
    val formation: String,

    @ColumnInfo(name = "count")
    val count: Int
)