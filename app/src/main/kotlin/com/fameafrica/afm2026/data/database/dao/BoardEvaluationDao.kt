package com.fameafrica.afm2026.data.database.dao

import androidx.room.*
import com.fameafrica.afm2026.data.database.entities.BoardEvaluationEntity
import com.fameafrica.afm2026.data.database.entities.ManagersEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardEvaluationDao {

    // ============ BASIC CRUD ============

    @Query("SELECT * FROM board_evaluation")
    fun getAll(): Flow<List<BoardEvaluationEntity>>

    @Query("SELECT * FROM board_evaluation WHERE id = :id")
    suspend fun getById(id: Int): BoardEvaluationEntity?

    @Query("SELECT * FROM board_evaluation WHERE manager_name = :managerName")
    suspend fun getByManagerName(managerName: String): BoardEvaluationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(evaluation: BoardEvaluationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(evaluations: List<BoardEvaluationEntity>)

    @Update
    suspend fun update(evaluation: BoardEvaluationEntity)

    @Delete
    suspend fun delete(evaluation: BoardEvaluationEntity)

    @Query("DELETE FROM board_evaluation")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM board_evaluation")
    suspend fun getCount(): Int

    // ============ SATISFACTION QUERIES ============

    @Query("SELECT * FROM board_evaluation WHERE board_satisfaction >= :minSatisfaction ORDER BY board_satisfaction DESC")
    fun getHighSatisfaction(minSatisfaction: Int): Flow<List<BoardEvaluationEntity>>

    @Query("SELECT * FROM board_evaluation WHERE board_satisfaction <= :maxSatisfaction ORDER BY board_satisfaction ASC")
    fun getLowSatisfaction(maxSatisfaction: Int): Flow<List<BoardEvaluationEntity>>

    @Query("SELECT AVG(board_satisfaction) FROM board_evaluation")
    suspend fun getAverageSatisfaction(): Double?

    // ============ STATUS QUERIES ============

    @Query("SELECT * FROM board_evaluation WHERE status = :status")
    fun getByStatus(status: String): Flow<List<BoardEvaluationEntity>>

    @Query("SELECT * FROM board_evaluation WHERE status IN ('Under Review', 'On Thin Ice', 'Critical')")
    fun getAtRiskManagers(): Flow<List<BoardEvaluationEntity>>

    @Query("SELECT * FROM board_evaluation WHERE status = 'Sacked'")
    fun getSackedManagers(): Flow<List<BoardEvaluationEntity>>

    // ============ FINANCIAL QUERIES ============

    @Query("SELECT * FROM board_evaluation WHERE financial_status = :financialStatus")
    fun getByFinancialStatus(financialStatus: String): Flow<List<BoardEvaluationEntity>>

    // ============ STATISTICS QUERIES ============

    @Query("""
        SELECT 
            status,
            COUNT(*) as manager_count,
            AVG(board_satisfaction) as avg_satisfaction
        FROM board_evaluation 
        GROUP BY status
    """)
    fun getBoardStatusDistribution(): Flow<List<BoardStatusDistribution>>

    @Query("""
        SELECT 
            financial_status,
            COUNT(*) as club_count,
            AVG(board_satisfaction) as avg_satisfaction
        FROM board_evaluation 
        WHERE financial_status IS NOT NULL
        GROUP BY financial_status
    """)
    fun getFinancialStatusDistribution(): Flow<List<FinancialStatusDistribution>>

    // ============ JOIN QUERIES ============

    @Query("""
        SELECT 
            be.*,
            m.team_id,
            t.name as team_name,
            t.league as team_league
        FROM board_evaluation be
        LEFT JOIN managers m ON be.manager_name = m.name
        LEFT JOIN teams t ON m.team_id = t.id
        WHERE be.manager_name = :managerName
    """)
    suspend fun getBoardEvaluationWithDetails(managerName: String): BoardEvaluationWithDetails?
}

// ============ DATA CLASSES ============

data class BoardStatusDistribution(
    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "manager_count")
    val managerCount: Int,

    @ColumnInfo(name = "avg_satisfaction")
    val averageSatisfaction: Double
)

data class FinancialStatusDistribution(
    @ColumnInfo(name = "financial_status")
    val financialStatus: String,

    @ColumnInfo(name = "club_count")
    val clubCount: Int,

    @ColumnInfo(name = "avg_satisfaction")
    val averageSatisfaction: Double
)

data class BoardEvaluationWithDetails(
    @Embedded
    val evaluation: BoardEvaluationEntity,

    @ColumnInfo(name = "team_id")
    val teamId: Int?,

    @ColumnInfo(name = "team_name")
    val teamName: String?,

    @ColumnInfo(name = "team_league")
    val teamLeague: String?
)