package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.BoardEvaluationDao
import com.fameafrica.afm2026.data.database.entities.BoardEvaluationEntity
import com.fameafrica.afm2026.domain.model.enums.BoardStatus
import com.fameafrica.afm2026.domain.model.enums.FinancialStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardEvaluationRepository @Inject constructor(
    private val boardEvaluationDao: BoardEvaluationDao
) {

    // ============ BASIC CRUD ============

    fun getAllEvaluations(): Flow<List<BoardEvaluationEntity>> = boardEvaluationDao.getAll()

    suspend fun getEvaluationById(id: Int): BoardEvaluationEntity? = boardEvaluationDao.getById(id)

    suspend fun getEvaluationByManagerName(managerName: String): BoardEvaluationEntity? =
        boardEvaluationDao.getByManagerName(managerName)

    suspend fun insertEvaluation(evaluation: BoardEvaluationEntity) = boardEvaluationDao.insert(evaluation)

    suspend fun updateEvaluation(evaluation: BoardEvaluationEntity) = boardEvaluationDao.update(evaluation)

    suspend fun deleteEvaluation(evaluation: BoardEvaluationEntity) = boardEvaluationDao.delete(evaluation)

    // ============ SATISFACTION MANAGEMENT ============

    suspend fun updateBoardSatisfaction(managerName: String, newSatisfaction: Int) {
        val evaluation = boardEvaluationDao.getByManagerName(managerName)
        evaluation?.let {
            val updated = it.copy(boardSatisfaction = newSatisfaction.coerceIn(0, 100))
            boardEvaluationDao.update(updated)
        }
    }

    suspend fun adjustBoardSatisfaction(managerName: String, adjustment: Int) {
        val evaluation = boardEvaluationDao.getByManagerName(managerName)
        evaluation?.let {
            val newSatisfaction = (evaluation.boardSatisfaction + adjustment).coerceIn(0, 100)
            val updated = it.copy(boardSatisfaction = newSatisfaction)
            boardEvaluationDao.update(updated)
        }
    }

    // ============ STATUS MANAGEMENT ============

    suspend fun updateBoardStatus(managerName: String, newStatus: String) {
        val evaluation = boardEvaluationDao.getByManagerName(managerName)
        evaluation?.let {
            val updated = it.copy(status = newStatus)
            boardEvaluationDao.update(updated)
        }
    }

    suspend fun evaluateBoardStatus(managerName: String) {
        val evaluation = boardEvaluationDao.getByManagerName(managerName) ?: return

        val newStatus = when {
            evaluation.boardSatisfaction >= 60 -> BoardStatus.SAFE.value
            evaluation.boardSatisfaction >= 45 -> BoardStatus.UNDER_REVIEW.value
            evaluation.boardSatisfaction >= 30 -> BoardStatus.ON_THIN_ICE.value
            evaluation.boardSatisfaction >= 15 -> BoardStatus.CRITICAL.value
            else -> BoardStatus.SACKED.value
        }

        if (evaluation.status != newStatus) {
            updateBoardStatus(managerName, newStatus)
        }
    }

    // ============ FINANCIAL STATUS ============

    suspend fun updateFinancialStatus(managerName: String, financialStatus: String) {
        val evaluation = boardEvaluationDao.getByManagerName(managerName)
        evaluation?.let {
            val updated = it.copy(financialStatus = financialStatus)
            boardEvaluationDao.update(updated)
        }
    }

    // ============ RECENT RESULTS ============

    suspend fun updateRecentResults(managerName: String, resultsJson: String) {
        val evaluation = boardEvaluationDao.getByManagerName(managerName)
        evaluation?.let {
            val updated = it.copy(recentResults = resultsJson)
            boardEvaluationDao.update(updated)
        }
    }

    suspend fun addMatchResult(managerName: String, result: String) {
        val evaluation = boardEvaluationDao.getByManagerName(managerName) ?: return

        // Parse existing results
        val currentResults = evaluation.recentResults?.let {
            it.substring(1, it.length - 1).split(",").map { s -> s.trim('"') }
        } ?: emptyList()

        // Add new result, keep last 5
        val updatedResults = (currentResults + result).takeLast(5)
        val resultsJson = "[\"${updatedResults.joinToString("\",\"")}\"]"

        val updated = evaluation.copy(recentResults = resultsJson)
        boardEvaluationDao.update(updated)
    }

    // ============ INITIALIZATION ============

    suspend fun initializeBoardEvaluation(managerName: String): BoardEvaluationEntity {
        val existing = boardEvaluationDao.getByManagerName(managerName)

        return if (existing != null) {
            existing
        } else {
            val evaluation = BoardEvaluationEntity(
                managerName = managerName,
                boardSatisfaction = 50,
                financialStatus = FinancialStatus.STABLE.value,
                status = BoardStatus.SAFE.value
            )
            boardEvaluationDao.insert(evaluation)
            evaluation
        }
    }

    // ============ DASHBOARD ============

    suspend fun getBoardDashboard(managerName: String): BoardDashboard {
        val evaluation = boardEvaluationDao.getByManagerName(managerName)
            ?: return BoardDashboard.empty()

        val details = boardEvaluationDao.getBoardEvaluationWithDetails(managerName)

        return BoardDashboard(
            managerName = evaluation.managerName,
            boardSatisfaction = evaluation.boardSatisfaction,
            satisfactionLevel = evaluation.satisfactionLevel,
            status = evaluation.status,
            financialStatus = evaluation.financialStatus ?: "Unknown",
            recentResults = evaluation.recentResults,
            teamName = details?.teamName,
            teamLeague = details?.teamLeague,
            isAtRisk = evaluation.status in listOf(
                BoardStatus.UNDER_REVIEW.value,
                BoardStatus.ON_THIN_ICE.value,
                BoardStatus.CRITICAL.value
            )
        )
    }
}

// ============ DATA CLASSES ============

data class BoardDashboard(
    val managerName: String,
    val boardSatisfaction: Int,
    val satisfactionLevel: String,
    val status: String,
    val financialStatus: String,
    val recentResults: String?,
    val teamName: String?,
    val teamLeague: String?,
    val isAtRisk: Boolean
) {
    companion object {
        fun empty(): BoardDashboard = BoardDashboard(
            managerName = "",
            boardSatisfaction = 0,
            satisfactionLevel = "",
            status = "",
            financialStatus = "",
            recentResults = null,
            teamName = null,
            teamLeague = null,
            isAtRisk = false
        )
    }
}