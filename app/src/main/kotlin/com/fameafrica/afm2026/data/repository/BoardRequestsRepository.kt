package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.BoardRequestsDao
import com.fameafrica.afm2026.data.database.dao.ManagerRequestStatistics
import com.fameafrica.afm2026.data.database.dao.RequestTypeStatistics
import com.fameafrica.afm2026.data.database.entities.BoardRequestsEntity
import com.fameafrica.afm2026.data.database.entities.BoardRequestType
import com.fameafrica.afm2026.data.database.entities.BoardRequestStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRequestsRepository @Inject constructor(
    private val boardRequestsDao: BoardRequestsDao
) {

    // ============ BASIC CRUD ============

    fun getAllRequests(): Flow<List<BoardRequestsEntity>> = boardRequestsDao.getAll()

    suspend fun getRequestById(id: Int): BoardRequestsEntity? = boardRequestsDao.getById(id)

    suspend fun insertRequest(request: BoardRequestsEntity) = boardRequestsDao.insert(request)

    suspend fun updateRequest(request: BoardRequestsEntity) = boardRequestsDao.update(request)

    suspend fun deleteRequest(request: BoardRequestsEntity) = boardRequestsDao.delete(request)

    // ============ MANAGER-BASED ============

    fun getRequestsByManager(managerName: String): Flow<List<BoardRequestsEntity>> =
        boardRequestsDao.getRequestsByManager(managerName)

    fun getPendingRequestsByManager(managerName: String): Flow<List<BoardRequestsEntity>> =
        boardRequestsDao.getPendingRequestsByManager(managerName)

    // ============ TEAM-BASED ============

    fun getRequestsByTeam(teamName: String): Flow<List<BoardRequestsEntity>> =
        boardRequestsDao.getRequestsByTeam(teamName)

    fun getPendingRequestsByTeam(teamName: String): Flow<List<BoardRequestsEntity>> =
        boardRequestsDao.getPendingRequestsByTeam(teamName)

    // ============ REQUEST MANAGEMENT ============

    suspend fun createRequest(
        managerName: String,
        teamName: String,
        requestType: String,
        description: String
    ): BoardRequestsEntity {
        val request = BoardRequestsEntity(
            requestType = requestType,
            requestDescription = description,
            requestStatus = BoardRequestStatus.PENDING.value,
            managerName = managerName,
            teamName = teamName
        )
        boardRequestsDao.insert(request)
        return request
    }

    suspend fun approveRequest(requestId: Int): Boolean {
        val request = boardRequestsDao.getById(requestId) ?: return false
        if (request.requestStatus != BoardRequestStatus.PENDING.value) return false

        val updated = request.copy(requestStatus = BoardRequestStatus.APPROVED.value)
        boardRequestsDao.update(updated)
        return true
    }

    suspend fun rejectRequest(requestId: Int): Boolean {
        val request = boardRequestsDao.getById(requestId) ?: return false
        if (request.requestStatus != BoardRequestStatus.PENDING.value) return false

        val updated = request.copy(requestStatus = BoardRequestStatus.REJECTED.value)
        boardRequestsDao.update(updated)
        return true
    }

    suspend fun completeRequest(requestId: Int): Boolean {
        val request = boardRequestsDao.getById(requestId) ?: return false
        if (request.requestStatus != BoardRequestStatus.APPROVED.value) return false

        val updated = request.copy(requestStatus = BoardRequestStatus.COMPLETED.value)
        boardRequestsDao.update(updated)
        return true
    }

    // ============ STATISTICS ============

    fun getRequestTypeStatistics(): Flow<List<RequestTypeStatistics>> =
        boardRequestsDao.getRequestTypeStatistics()

    fun getMostRequestingManagers(limit: Int): Flow<List<ManagerRequestStatistics>> =
        boardRequestsDao.getMostRequestingManagers(limit)

    suspend fun getPendingCount(): Int = boardRequestsDao.getPendingCount()

    // ============ DASHBOARD ============

    suspend fun getManagerRequestsDashboard(managerName: String): ManagerRequestsDashboard {
        val allRequests = boardRequestsDao.getRequestsByManager(managerName).firstOrNull() ?: emptyList()
        val pending = allRequests.filter { it.requestStatus == BoardRequestStatus.PENDING.value }
        val approved = allRequests.filter { it.requestStatus == BoardRequestStatus.APPROVED.value }
        val rejected = allRequests.filter { it.requestStatus == BoardRequestStatus.REJECTED.value }
        val completed = allRequests.filter { it.requestStatus == BoardRequestStatus.COMPLETED.value }

        val approvalRate = if (allRequests.isNotEmpty()) {
            (approved.size + completed.size).toDouble() / allRequests.size * 100
        } else 0.0

        return ManagerRequestsDashboard(
            totalRequests = allRequests.size,
            pendingRequests = pending.size,
            approvedRequests = approved.size,
            rejectedRequests = rejected.size,
            completedRequests = completed.size,
            approvalRate = approvalRate,
            recentRequests = allRequests.takeLast(5).reversed(),
            pendingRequestsList = pending
        )
    }
}

// ============ DATA CLASSES ============

data class ManagerRequestsDashboard(
    val totalRequests: Int,
    val pendingRequests: Int,
    val approvedRequests: Int,
    val rejectedRequests: Int,
    val completedRequests: Int,
    val approvalRate: Double,
    val recentRequests: List<BoardRequestsEntity>,
    val pendingRequestsList: List<BoardRequestsEntity>
)