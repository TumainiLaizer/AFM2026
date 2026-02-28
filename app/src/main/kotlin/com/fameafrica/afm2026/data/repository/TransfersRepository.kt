package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.TeamSalesStats
import com.fameafrica.afm2026.data.database.dao.TeamTransferStats
import com.fameafrica.afm2026.data.database.dao.TransferTypeStats
import com.fameafrica.afm2026.data.database.dao.TransfersDao
import com.fameafrica.afm2026.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransfersRepository @Inject constructor(
    private val transfersDao: TransfersDao,
    private val playersRepository: PlayersRepository,
    private val teamsRepository: TeamsRepository,
    private val leaguesRepository: LeaguesRepository,
    private val newsRepository: NewsRepository,
    private val transferWindowsRepository: TransferWindowsRepository
) {

    // ============ BASIC CRUD ============

    fun getAllTransfers(): Flow<List<TransfersEntity>> = transfersDao.getAll()

    suspend fun getTransferById(id: Int): TransfersEntity? = transfersDao.getById(id)

    suspend fun insertTransfer(transfer: TransfersEntity) = transfersDao.insert(transfer)

    suspend fun updateTransfer(transfer: TransfersEntity) = transfersDao.update(transfer)

    suspend fun deleteTransfer(transfer: TransfersEntity) = transfersDao.delete(transfer)

    // ============ TRANSFER CREATION ============

    /**
     * Create a new transfer bid/offer
     */
    suspend fun createTransfer(
        playerId: Int,
        targetTeam: String,
        transferType: String,
        transferFee: Int,
        monthlyWage: Int,
        contractLength: Int = 3,
        isLoanToBuy: Boolean = false,
        loanBuyFee: Int? = null,
        agentFee: Int? = null,
        sellOnPercentage: Int? = null,
        signingBonus: Int? = null,
        rumours: String? = null,
        scoutRating: Int = 70
    ): TransfersEntity? {

        val player = playersRepository.getPlayerById(playerId) ?: return null
        val currentTeam = player.teamName

        // Check if transfer window is open
        val currentWindow = transferWindowsRepository.getCurrentWindow()
        if (currentWindow == null && transferType != TransferType.FREE.value) {
            return null // Can only make free transfers outside window
        }

        val transfer = TransfersEntity(
            playerId = playerId,
            playerName = player.name,
            currentTeam = currentTeam,
            targetTeam = targetTeam,
            transferFee = transferFee,
            contractLength = contractLength,
            monthlyWage = monthlyWage,
            transferType = transferType,
            transferStatus = TransferStatus.PENDING.value,
            rumours = rumours,
            scoutRating = scoutRating,
            windowId = currentWindow?.id,
            isLoanToBuy = isLoanToBuy,
            loanBuyFee = loanBuyFee,
            agentFee = agentFee,
            sellOnPercentage = sellOnPercentage,
            signingBonus = signingBonus
        )

        transfersDao.insert(transfer)

        // Generate transfer rumour if significant
        if (transferFee >= 5_000_000 || player.rating >= 75) {
            generateTransferRumour(transfer)
        }

        return transfer
    }

    /**
     * Validate if a transfer is allowed based on foreign player limits
     */
    suspend fun validateTransferForeignPlayerRules(
        playerId: Int,
        targetTeam: String
    ): TransferValidationResult {
        val player = playersRepository.getPlayerById(playerId) ?: return TransferValidationResult(
            isValid = false,
            message = "Player not found"
        )

        val team = teamsRepository.getTeamByName(targetTeam) ?: return TransferValidationResult(
            isValid = false,
            message = "Team not found"
        )

        val league = leaguesRepository.getLeagueByName(team.league) ?: return TransferValidationResult(
            isValid = false,
            message = "League not found"
        )

        // Check if player is considered foreign in target league
        val isForeign = ForeignPlayerRules.isPlayerForeign(
            playerNationality = player.nationality,
            leagueCountryId = league.countryId ?: 0
        )

        if (!isForeign) {
            // Local player - always allowed
            return TransferValidationResult(
                isValid = true,
                message = "Local player - no foreign player restrictions"
            )
        }

        // Foreign player - check limits
        val maxForeign = ForeignPlayerRules.getMaxForeignPlayersByCountry(
            countryId = league.countryId ?: 0,
            leagueName = league.name
        )

        val currentForeign = transferWindowsRepository.getCurrentForeignPlayerCount(targetTeam)
        val remaining = maxForeign - currentForeign

        return if (remaining > 0) {
            TransferValidationResult(
                isValid = true,
                message = "Foreign player allowed. $remaining slots remaining."
            )
        } else {
            TransferValidationResult(
                isValid = false,
                message = "Cannot sign foreign player. Team has reached maximum of $maxForeign foreign players."
            )
        }
    }

    data class TransferValidationResult(
        val isValid: Boolean,
        val message: String
    )

    /**
     * Accept a transfer offer
     */
    suspend fun acceptTransfer(transferId: Int): Boolean {
        val transfer = transfersDao.getById(transferId) ?: return false

        val updated = transfer.copy(transferStatus = TransferStatus.ACCEPTED.value)
        transfersDao.update(updated)

        return true
    }

    /**
     * Reject a transfer offer
     */
    suspend fun rejectTransfer(transferId: Int): Boolean {
        val transfer = transfersDao.getById(transferId) ?: return false

        val updated = transfer.copy(transferStatus = TransferStatus.REJECTED.value)
        transfersDao.update(updated)

        return true
    }

    /**
     * Complete a transfer (player moves)
     */
    suspend fun completeTransfer(transferId: Int): Boolean {
        val transfer = transfersDao.getById(transferId) ?: return false

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        val updated = transfer.copy(
            transferStatus = TransferStatus.COMPLETED.value,
            completedDate = today
        )

        transfersDao.update(updated)

        // Update player's team
        when (transfer.transferType) {
            TransferType.BUY.value, TransferType.FREE.value -> {
                // Permanent transfer
                val targetTeamId = teamsRepository.getTeamByName(transfer.targetTeam)?.id
                if (targetTeamId != null) {
                    playersRepository.transferPlayer(
                        playerId = transfer.playerId,
                        newTeamId = targetTeamId,
                        newTeamName = transfer.targetTeam,
                        newMarketValue = transfer.transferFee // Market value updates to transfer fee
                    )

                    // Renew contract
                    playersRepository.renewContract(
                        playerId = transfer.playerId,
                        newSalary = transfer.monthlyWage.toDouble(),
                        newExpiry = "${Calendar.getInstance().get(Calendar.YEAR) + transfer.contractLength}-06-30"
                    )
                }
            }
            TransferType.LOAN.value -> {
                // Loan move - handled by loan system
                // This would integrate with loan system
            }
        }

        // Generate transfer news
        generateTransferNews(updated)

        return true
    }

    /**
     * Cancel a transfer
     */
    suspend fun cancelTransfer(transferId: Int): Boolean {
        val transfer = transfersDao.getById(transferId) ?: return false

        val updated = transfer.copy(transferStatus = TransferStatus.CANCELLED.value)
        transfersDao.update(updated)

        return true
    }

    /**
     * Negotiate a transfer (update offer)
     */
    suspend fun negotiateTransfer(
        transferId: Int,
        newFee: Int? = null,
        newWage: Int? = null,
        newContractLength: Int? = null
    ): Boolean {
        val transfer = transfersDao.getById(transferId) ?: return false

        val updated = transfer.copy(
            transferFee = newFee ?: transfer.transferFee,
            monthlyWage = newWage ?: transfer.monthlyWage,
            contractLength = newContractLength ?: transfer.contractLength,
            transferStatus = TransferStatus.NEGOTIATING.value
        )

        transfersDao.update(updated)
        return true
    }

    // ============ TEAM-BASED ============

    fun getIncomingTransfers(teamName: String): Flow<List<TransfersEntity>> =
        transfersDao.getIncomingTransfers(teamName)

    fun getOutgoingTransfers(teamName: String): Flow<List<TransfersEntity>> =
        transfersDao.getOutgoingTransfers(teamName)

    fun getAllTransfersByTeam(teamName: String): Flow<List<TransfersEntity>> =
        transfersDao.getAllTransfersByTeam(teamName)

    suspend fun getPendingIncomingTransfers(teamName: String): List<TransfersEntity> =
        transfersDao.getPendingIncomingTransfers(teamName).firstOrNull() ?: emptyList()

    // ============ RUMOUR GENERATION ============

    private suspend fun generateTransferRumour(transfer: TransfersEntity) {
        val player = playersRepository.getPlayerById(transfer.playerId) ?: return

        val rumourText = when (transfer.transferType) {
            TransferType.BUY.value -> {
                "${player.name} linked with move to ${transfer.targetTeam} for ${transfer.transferFee / 1_000_000}M"
            }
            TransferType.LOAN.value -> {
                "${player.name} could be heading to ${transfer.targetTeam} on loan"
            }
            TransferType.FREE.value -> {
                "${player.name} available on free transfer, ${transfer.targetTeam} interested"
            }
            else -> {
                "${player.name} linked with ${transfer.targetTeam}"
            }
        }

        // Update transfer with rumour
        val updated = transfer.copy(rumours = rumourText)
        transfersDao.update(updated)

        // Create news article
        newsRepository.createTransferRumor(
            playerName = player.name,
            fromTeam = transfer.currentTeam,
            toTeam = transfer.targetTeam,
            fee = if (transfer.transferType == TransferType.BUY.value) transfer.transferFee else null,
            journalistName = "Transfer Insider"
        )
    }

    private suspend fun generateTransferNews(transfer: TransfersEntity) {
        val player = playersRepository.getPlayerById(transfer.playerId) ?: return

        val headline = when (transfer.transferType) {
            TransferType.BUY.value -> {
                "CONFIRMED: ${player.name} joins ${transfer.targetTeam} for ${transfer.transferFee / 1_000_000}M"
            }
            TransferType.LOAN.value -> {
                "${player.name} completes loan move to ${transfer.targetTeam}"
            }
            TransferType.FREE.value -> {
                "${player.name} signs for ${transfer.targetTeam} on free transfer"
            }
            else -> {
                "${player.name} transfer to ${transfer.targetTeam} completed"
            }
        }

        newsRepository.createNewsArticle(
            headline = headline,
            content = "${player.name} has completed a move to ${transfer.targetTeam} from ${transfer.currentTeam}.",
            category = "TRANSFER",
            journalistName = "Transfer News",
            relatedPlayer = player.name,
            relatedTeam = transfer.targetTeam,
            isTopNews = transfer.transferFee >= 10_000_000
        )
    }

    // ============ STATISTICS ============

    suspend fun getTotalSpentByTeam(teamName: String): Int? =
        transfersDao.getTotalSpentByTeam(teamName)

    suspend fun getTotalReceivedByTeam(teamName: String): Int? =
        transfersDao.getTotalReceivedByTeam(teamName)

    fun getTransferTypeStatistics(): Flow<List<TransferTypeStats>> =
        transfersDao.getTransferTypeStatistics()

    fun getBiggestSpenders(limit: Int): Flow<List<TeamTransferStats>> =
        transfersDao.getBiggestSpenders(limit)

    fun getBiggestSellers(limit: Int): Flow<List<TeamSalesStats>> =
        transfersDao.getBiggestSellers(limit)

    // ============ DASHBOARD ============

    suspend fun getTeamTransferDashboard(teamName: String): TeamTransferDashboard {
        val allTransfers = transfersDao.getAllTransfersByTeam(teamName).firstOrNull() ?: emptyList()
        val incoming = allTransfers.filter { it.targetTeam == teamName }
        val outgoing = allTransfers.filter { it.currentTeam == teamName }

        val pendingIncoming = incoming.filter { it.transferStatus == TransferStatus.PENDING.value }
        val pendingOutgoing = outgoing.filter { it.transferStatus == TransferStatus.PENDING.value }

        val completedIncoming = incoming.filter { it.transferStatus == TransferStatus.COMPLETED.value }
        val completedOutgoing = outgoing.filter { it.transferStatus == TransferStatus.COMPLETED.value }

        val totalSpent = completedIncoming.sumOf { it.transferFee }
        val totalReceived = completedOutgoing.sumOf { it.transferFee }

        val netSpend = totalSpent - totalReceived

        return TeamTransferDashboard(
            totalTransfers = allTransfers.size,
            incomingTransfers = incoming.size,
            outgoingTransfers = outgoing.size,
            pendingIncoming = pendingIncoming.size,
            pendingOutgoing = pendingOutgoing.size,
            completedIncoming = completedIncoming.size,
            completedOutgoing = completedOutgoing.size,
            totalSpent = totalSpent,
            totalReceived = totalReceived,
            netSpend = netSpend,
            pendingIncomingList = pendingIncoming,
            recentCompleted = (completedIncoming + completedOutgoing)
                .sortedByDescending { it.completedDate }
                .take(10)
        )
    }
}

// ============ DATA CLASSES ============

data class TeamTransferDashboard(
    val totalTransfers: Int,
    val incomingTransfers: Int,
    val outgoingTransfers: Int,
    val pendingIncoming: Int,
    val pendingOutgoing: Int,
    val completedIncoming: Int,
    val completedOutgoing: Int,
    val totalSpent: Int,
    val totalReceived: Int,
    val netSpend: Int,
    val pendingIncomingList: List<TransfersEntity>,
    val recentCompleted: List<TransfersEntity>
)