package com.fameafrica.afm2026.ui.screen.negotiation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.database.entities.*
import com.fameafrica.afm2026.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NegotiationViewModel @Inject constructor(
    private val playerContractsRepository: PlayerContractsRepository,
    private val playerLoansRepository: PlayerLoansRepository,
    private val transfersRepository: TransfersRepository,
    private val playersRepository: PlayersRepository,
    private val teamsRepository: TeamsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NegotiationUiState())
    val uiState: StateFlow<NegotiationUiState> = _uiState

    private val _selectedTab = MutableStateFlow(0)

    init {
        loadData()
    }

    fun onEvent(event: NegotiationEvent) {
        when (event) {
            is NegotiationEvent.ChangeTab -> _selectedTab.value = event.tabIndex
            is NegotiationEvent.SelectContract -> selectContract(event.contractId)
            is NegotiationEvent.SelectTransfer -> selectTransfer(event.transferId)
            is NegotiationEvent.SelectLoan -> selectLoan(event.loanId)
            is NegotiationEvent.RenewContract -> renewContract(event.contractId, event.newSalary, event.newLength)
            is NegotiationEvent.TerminateContract -> terminateContract(event.contractId, event.reason)
            is NegotiationEvent.AcceptTransfer -> acceptTransfer(event.transferId)
            is NegotiationEvent.RejectTransfer -> rejectTransfer(event.transferId)
            is NegotiationEvent.NegotiateTransfer -> negotiateTransfer(event.transferId, event.newFee, event.newWage)
            is NegotiationEvent.CompleteTransfer -> completeTransfer(event.transferId)
            is NegotiationEvent.CreateLoan -> createLoan(event.loanRequest)
            is NegotiationEvent.TriggerBuyOption -> triggerBuyOption(event.loanId)
            is NegotiationEvent.EarlyReturnLoan -> earlyReturnLoan(event.loanId, event.reason)
            is NegotiationEvent.UpdateOffer -> updateOffer(event.field, event.value)
            is NegotiationEvent.StartNewNegotiation -> startNewNegotiation(event.playerId, event.type)
            is NegotiationEvent.SendCounterOffer -> sendCounterOffer()
            is NegotiationEvent.AcceptCounterOffer -> acceptCounterOffer()
            is NegotiationEvent.RejectCounterOffer -> rejectCounterOffer()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            // Load contracts
            playerContractsRepository.getAllContracts().collect { contracts ->
                _uiState.value = _uiState.value.copy(
                    contracts = contracts,
                    expiringContracts = contracts.filter { it.contractStatus == "EXPIRING" }
                )
            }

            // Load transfers
            transfersRepository.getAllTransfers().collect { transfers ->
                _uiState.value = _uiState.value.copy(
                    pendingTransfers = transfers.filter { it.transferStatus == "PENDING" },
                    activeTransfers = transfers.filter {
                        it.transferStatus in listOf("PENDING", "NEGOTIATING", "ACCEPTED")
                    }
                )
            }

            // Load loans
            playerLoansRepository.getAllLoans().collect { loans ->
                _uiState.value = _uiState.value.copy(
                    activeLoans = loans.filter { it.status == "ACTIVE" },
                    pendingLoans = loans.filter { it.status == "PENDING" }
                )
            }

            // Load teams for selection
            teamsRepository.getAllTeams().collect { teams ->
                _uiState.value = _uiState.value.copy(
                    teams = teams,
                    isLoading = false
                )
            }
        }
    }

    private fun selectContract(contractId: Int) {
        viewModelScope.launch {
            val contract = playerContractsRepository.getContractById(contractId)
            val player = contract?.playerName?.let { playersRepository.getPlayerByName(it) }

            _uiState.value = _uiState.value.copy(
                selectedContract = contract,
                selectedPlayer = player,
                selectedTab = 0,
                showDetailDialog = true
            )
        }
    }

    private fun selectTransfer(transferId: Int) {
        viewModelScope.launch {
            val transfer = transfersRepository.getTransferById(transferId)
            val player = transfer?.playerId?.let { playersRepository.getPlayerById(it) }

            _uiState.value = _uiState.value.copy(
                selectedTransfer = transfer,
                selectedPlayer = player,
                selectedTab = 1,
                showDetailDialog = true
            )
        }
    }

    private fun selectLoan(loanId: Int) {
        viewModelScope.launch {
            val loan = playerLoansRepository.getLoanById(loanId)
            val player = loan?.playerId?.let { playersRepository.getPlayerById(it) }

            _uiState.value = _uiState.value.copy(
                selectedLoan = loan,
                selectedPlayer = player,
                selectedTab = 2,
                showDetailDialog = true
            )
        }
    }

    private fun renewContract(contractId: Int, newSalary: Int, newLength: Int) {
        viewModelScope.launch {
            val result = playerContractsRepository.renewContract(
                contractId = contractId,
                newSalary = newSalary,
                newContractLength = newLength
            )

            if (result != null) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Contract renewed successfully",
                    showDetailDialog = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Failed to renew contract"
                )
            }
        }
    }

    private fun terminateContract(contractId: Int, reason: String) {
        viewModelScope.launch {
            val result = playerContractsRepository.terminateContract(contractId, reason)

            if (result != null) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Contract terminated",
                    showDetailDialog = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Failed to terminate contract"
                )
            }
        }
    }

    private fun acceptTransfer(transferId: Int) {
        viewModelScope.launch {
            val success = transfersRepository.acceptTransfer(transferId)

            if (success) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Transfer offer accepted",
                    showDetailDialog = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Failed to accept transfer"
                )
            }
        }
    }

    private fun rejectTransfer(transferId: Int) {
        viewModelScope.launch {
            val success = transfersRepository.rejectTransfer(transferId)

            if (success) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Transfer offer rejected",
                    showDetailDialog = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Failed to reject transfer"
                )
            }
        }
    }

    private fun negotiateTransfer(transferId: Int, newFee: Int?, newWage: Int?) {
        viewModelScope.launch {
            val success = transfersRepository.negotiateTransfer(
                transferId = transferId,
                newFee = newFee,
                newWage = newWage
            )

            if (success) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Counter-offer sent",
                    showDetailDialog = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Failed to send counter-offer"
                )
            }
        }
    }

    private fun completeTransfer(transferId: Int) {
        viewModelScope.launch {
            val success = transfersRepository.completeTransfer(transferId)

            if (success) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Transfer completed",
                    showDetailDialog = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Failed to complete transfer"
                )
            }
        }
    }

    private fun createLoan(loanRequest: LoanRequest) {
        viewModelScope.launch {
            val loan = playerLoansRepository.createLoan(
                playerName = loanRequest.playerName,
                playerId = loanRequest.playerId,
                loaningTeam = loanRequest.loaningTeam,
                loaningTeamId = loanRequest.loaningTeamId,
                receivingTeam = loanRequest.receivingTeam,
                receivingTeamId = loanRequest.receivingTeamId,
                season = loanRequest.season,
                durationMonths = loanRequest.durationMonths,
                loanFee = loanRequest.loanFee,
                wageContribution = loanRequest.wageContribution,
                optionToBuy = loanRequest.optionToBuy,
                buyOptionFee = loanRequest.buyOptionFee,
                recallOption = loanRequest.recallOption
            )

            _uiState.value = _uiState.value.copy(
                snackbarMessage = "Loan agreement created",
                showCreateLoanDialog = false
            )
        }
    }

    private fun triggerBuyOption(loanId: Int) {
        viewModelScope.launch {
            val result = playerLoansRepository.triggerBuyOption(loanId)

            if (result != null) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Buy option triggered",
                    showDetailDialog = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Failed to trigger buy option"
                )
            }
        }
    }

    private fun earlyReturnLoan(loanId: Int, reason: String) {
        viewModelScope.launch {
            val result = playerLoansRepository.earlyReturn(loanId, reason)

            if (result != null) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Player returned early",
                    showDetailDialog = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Failed to process early return"
                )
            }
        }
    }

    private fun updateOffer(field: String, value: String) {
        val currentOffer = _uiState.value.currentOffer
        val updatedOffer = when (field) {
            "fee" -> currentOffer.copy(fee = value.toIntOrNull() ?: 0)
            "wage" -> currentOffer.copy(wage = value.toIntOrNull() ?: 0)
            "length" -> currentOffer.copy(length = value.toIntOrNull() ?: 3)
            "bonus" -> currentOffer.copy(bonus = value.toIntOrNull() ?: 0)
            "releaseClause" -> currentOffer.copy(releaseClause = value.toIntOrNull() ?: 0)
            else -> currentOffer
        }
        _uiState.value = _uiState.value.copy(currentOffer = updatedOffer)
    }

    private fun startNewNegotiation(playerId: Int, type: String) {
        viewModelScope.launch {
            val player = playersRepository.getPlayerById(playerId)
            val defaultOffer = TransferOffer(
                playerId = playerId,
                playerName = player?.name ?: "",
                type = type,
                fee = player?.marketValue ?: 1000000,
                wage = 50000,
                length = 3,
                bonus = 500000,
                releaseClause = player?.marketValue?.times(2) ?: 5000000
            )

            _uiState.value = _uiState.value.copy(
                currentOffer = defaultOffer,
                showNegotiationDialog = true
            )
        }
    }

    private fun sendCounterOffer() {
        viewModelScope.launch {
            val offer = _uiState.value.currentOffer

            when (offer.type) {
                "TRANSFER" -> {
                    transfersRepository.createTransfer(
                        playerId = offer.playerId,
                        targetTeam = _uiState.value.selectedTeam ?: "",
                        transferType = TransferType.BUY.value,
                        transferFee = offer.fee,
                        monthlyWage = offer.wage,
                        contractLength = offer.length,
                        signingBonus = offer.bonus
                        //releaseClause = offer.releaseClause
                    )
                }
                "LOAN" -> {
                    // Handle loan offer
                }
                "CONTRACT" -> {
                    // Handle contract renewal
                }
            }

            _uiState.value = _uiState.value.copy(
                snackbarMessage = "Counter-offer sent",
                showNegotiationDialog = false
            )
        }
    }

    private fun acceptCounterOffer() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                snackbarMessage = "Counter-offer accepted",
                showNegotiationDialog = false
            )
        }
    }

    private fun rejectCounterOffer() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                snackbarMessage = "Counter-offer rejected",
                showNegotiationDialog = false
            )
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(
            showDetailDialog = false,
            showCreateLoanDialog = false,
            showNegotiationDialog = false
        )
    }
}

data class NegotiationUiState(
    val isLoading: Boolean = true,
    val selectedTab: Int = 0,
    val contracts: List<PlayerContractsEntity> = emptyList(),
    val expiringContracts: List<PlayerContractsEntity> = emptyList(),
    val pendingTransfers: List<TransfersEntity> = emptyList(),
    val activeTransfers: List<TransfersEntity> = emptyList(),
    val activeLoans: List<PlayerLoansEntity> = emptyList(),
    val pendingLoans: List<PlayerLoansEntity> = emptyList(),
    val teams: List<TeamsEntity> = emptyList(),
    val selectedContract: PlayerContractsEntity? = null,
    val selectedTransfer: TransfersEntity? = null,
    val selectedLoan: PlayerLoansEntity? = null,
    val selectedPlayer: PlayersEntity? = null,
    val selectedTeam: String? = null,
    val currentOffer: TransferOffer = TransferOffer(),
    val showDetailDialog: Boolean = false,
    val showCreateLoanDialog: Boolean = false,
    val showNegotiationDialog: Boolean = false,
    val snackbarMessage: String? = null
)

data class TransferOffer(
    val playerId: Int = 0,
    val playerName: String = "",
    val type: String = "TRANSFER",
    val fee: Int = 0,
    val wage: Int = 0,
    val length: Int = 3,
    val bonus: Int = 0,
    val releaseClause: Int = 0
)

data class LoanRequest(
    val playerName: String,
    val playerId: Int,
    val loaningTeam: String,
    val loaningTeamId: Int,
    val receivingTeam: String,
    val receivingTeamId: Int,
    val season: String,
    val durationMonths: Int,
    val loanFee: Int? = null,
    val wageContribution: Int = 100,
    val optionToBuy: Boolean = false,
    val buyOptionFee: Int? = null,
    val recallOption: Boolean = false
)

sealed class NegotiationEvent {
    data class ChangeTab(val tabIndex: Int) : NegotiationEvent()
    data class SelectContract(val contractId: Int) : NegotiationEvent()
    data class SelectTransfer(val transferId: Int) : NegotiationEvent()
    data class SelectLoan(val loanId: Int) : NegotiationEvent()
    data class RenewContract(val contractId: Int, val newSalary: Int, val newLength: Int) : NegotiationEvent()
    data class TerminateContract(val contractId: Int, val reason: String) : NegotiationEvent()
    data class AcceptTransfer(val transferId: Int) : NegotiationEvent()
    data class RejectTransfer(val transferId: Int) : NegotiationEvent()
    data class NegotiateTransfer(val transferId: Int, val newFee: Int?, val newWage: Int?) : NegotiationEvent()
    data class CompleteTransfer(val transferId: Int) : NegotiationEvent()
    data class CreateLoan(val loanRequest: LoanRequest) : NegotiationEvent()
    data class TriggerBuyOption(val loanId: Int) : NegotiationEvent()
    data class EarlyReturnLoan(val loanId: Int, val reason: String) : NegotiationEvent()
    data class UpdateOffer(val field: String, val value: String) : NegotiationEvent()
    data class StartNewNegotiation(val playerId: Int, val type: String) : NegotiationEvent()
    object SendCounterOffer : NegotiationEvent()
    object AcceptCounterOffer : NegotiationEvent()
    object RejectCounterOffer : NegotiationEvent()
}

@Composable
fun NegotiationScreen(
    viewModel: NegotiationViewModel,
    onNavigateToPlayer: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            scaffoldState.snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { NegotiationTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Navigate to create negotiation */ }
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Negotiation")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NegotiationTabs(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.onEvent(NegotiationEvent.ChangeTab(it)) }
            )

            when (uiState.selectedTab) {
                0 -> ContractTab(
                    contracts = uiState.contracts,
                    expiringContracts = uiState.expiringContracts,
                    onContractClick = { viewModel.onEvent(NegotiationEvent.SelectContract(it)) }
                )
                1 -> TransferTab(
                    pendingTransfers = uiState.pendingTransfers,
                    activeTransfers = uiState.activeTransfers,
                    onTransferClick = { viewModel.onEvent(NegotiationEvent.SelectTransfer(it)) }
                )
                2 -> LoanTab(
                    activeLoans = uiState.activeLoans,
                    pendingLoans = uiState.pendingLoans,
                    onLoanClick = { viewModel.onEvent(NegotiationEvent.SelectLoan(it)) }
                )
                3 -> NegotiationDashboardTab(
                    uiState = uiState
                )
            }
        }
    }

    // Detail Dialog
    if (uiState.showDetailDialog) {
        NegotiationDetailDialog(
            uiState = uiState,
            onDismiss = { viewModel.dismissDialog() },
            onRenew = { contractId, salary, length ->
                viewModel.onEvent(NegotiationEvent.RenewContract(contractId, salary, length))
            },
            onTerminate = { contractId, reason ->
                viewModel.onEvent(NegotiationEvent.TerminateContract(contractId, reason))
            },
            onAcceptTransfer = { transferId ->
                viewModel.onEvent(NegotiationEvent.AcceptTransfer(transferId))
            },
            onRejectTransfer = { transferId ->
                viewModel.onEvent(NegotiationEvent.RejectTransfer(transferId))
            },
            onNegotiateTransfer = { transferId, fee, wage ->
                viewModel.onEvent(NegotiationEvent.NegotiateTransfer(transferId, fee, wage))
            },
            onCompleteTransfer = { transferId ->
                viewModel.onEvent(NegotiationEvent.CompleteTransfer(transferId))
            },
            onTriggerBuyOption = { loanId ->
                viewModel.onEvent(NegotiationEvent.TriggerBuyOption(loanId))
            },
            onEarlyReturn = { loanId, reason ->
                viewModel.onEvent(NegotiationEvent.EarlyReturnLoan(loanId, reason))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegotiationTopBar() {
    TopAppBar(
        title = { Text("Negotiations") },
        actions = {
            IconButton(onClick = { /* Refresh */ }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    )
}

@Composable
fun NegotiationTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(selectedTabIndex = selectedTab) {
        listOf("Contracts", "Transfers", "Loans", "Dashboard").forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
fun ContractTab(
    contracts: List<PlayerContractsEntity>,
    expiringContracts: List<PlayerContractsEntity>,
    onContractClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (expiringContracts.isNotEmpty()) {
            item {
                Text(
                    text = "Expiring Soon",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(expiringContracts) { contract ->
                ExpiringContractCard(
                    contract = contract,
                    onClick = { onContractClick(contract.id) }
                )
            }
        }

        item {
            Text(
                text = "All Contracts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = if (expiringContracts.isNotEmpty()) 8.dp else 0.dp)
            )
        }

        items(contracts) { contract ->
            ContractCard(
                contract = contract,
                onClick = { onContractClick(contract.id) }
            )
        }
    }
}

@Composable
fun ExpiringContractCard(
    contract: PlayerContractsEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contract.playerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${contract.teamName} • Salary: ${formatCurrency(contract.salary)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Expires: ${contract.contractEndDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Button(
                    onClick = onClick,
                    modifier = Modifier.padding(top = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Renew")
                }
            }
        }
    }
}

@Composable
fun ContractCard(
    contract: PlayerContractsEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = contract.playerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${contract.teamName} • ${contract.contractStatus}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCurrency(contract.salary),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Until: ${contract.contractEndDate}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun TransferTab(
    pendingTransfers: List<TransfersEntity>,
    activeTransfers: List<TransfersEntity>,
    onTransferClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (pendingTransfers.isNotEmpty()) {
            item {
                Text(
                    text = "Pending Offers",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(pendingTransfers) { transfer ->
                PendingTransferCard(
                    transfer = transfer,
                    onClick = { onTransferClick(transfer.id) }
                )
            }
        }

        if (activeTransfers.isNotEmpty()) {
            item {
                Text(
                    text = "Active Negotiations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(activeTransfers) { transfer ->
                ActiveTransferCard(
                    transfer = transfer,
                    onClick = { onTransferClick(transfer.id) }
                )
            }
        }

        if (pendingTransfers.isEmpty() && activeTransfers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active transfers",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun PendingTransferCard(
    transfer: TransfersEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transfer.playerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                AssistChip(
                    onClick = {},
                    label = { Text(transfer.transferType) }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${transfer.currentTeam} → ${transfer.targetTeam}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatCurrency(transfer.transferFee),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Wage: ${formatCurrency(transfer.monthlyWage)}/month",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ActiveTransferCard(
    transfer: TransfersEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transfer.playerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${transfer.currentTeam} → ${transfer.targetTeam}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCurrency(transfer.transferFee),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = transfer.transferStatus,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun LoanTab(
    activeLoans: List<PlayerLoansEntity>,
    pendingLoans: List<PlayerLoansEntity>,
    onLoanClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (activeLoans.isNotEmpty()) {
            item {
                Text(
                    text = "Active Loans",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(activeLoans) { loan ->
                ActiveLoanCard(
                    loan = loan,
                    onClick = { onLoanClick(loan.id) }
                )
            }
        }

        if (pendingLoans.isNotEmpty()) {
            item {
                Text(
                    text = "Pending Loans",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(pendingLoans) { loan ->
                PendingLoanCard(
                    loan = loan,
                    onClick = { onLoanClick(loan.id) }
                )
            }
        }

        if (activeLoans.isEmpty() && pendingLoans.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active loans",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveLoanCard(
    loan: PlayerLoansEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = loan.playerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (loan.optionToBuy) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Option to Buy") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${loan.loaningTeam} → ${loan.receivingTeam}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Until: ${loan.endDate}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${loan.gamesPlayed} apps, ${loan.goalsScored} goals",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            if (loan.monthsRemaining <= 1) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚠️ Expiring soon!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun PendingLoanCard(
    loan: PlayerLoansEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = loan.playerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${loan.loaningTeam} → ${loan.receivingTeam}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                if (loan.loanFee != null) {
                    Text(
                        text = formatCurrency(loan.loanFee),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "${loan.duration} months",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun NegotiationDashboardTab(
    uiState: NegotiationUiState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            NegotiationStatsCard(uiState)
        }

        item {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Recent contracts
        items(uiState.contracts.take(3)) { contract ->
            RecentContractItem(contract)
        }

        // Recent transfers
        items(uiState.activeTransfers.take(3)) { transfer ->
            RecentTransferItem(transfer)
        }

        // Recent loans
        items(uiState.activeLoans.take(3)) { loan ->
            RecentLoanItem(loan)
        }
    }
}

@Composable
fun NegotiationStatsCard(uiState: NegotiationUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Negotiation Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCircle(
                    value = uiState.expiringContracts.size.toString(),
                    label = "Expiring",
                    color = MaterialTheme.colorScheme.error
                )
                StatCircle(
                    value = uiState.pendingTransfers.size.toString(),
                    label = "Pending",
                    color = MaterialTheme.colorScheme.tertiary
                )
                StatCircle(
                    value = uiState.activeLoans.size.toString(),
                    label = "Active Loans",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val totalWageBill = uiState.contracts.sumOf { it.salary }
            Text(
                text = "Total Wage Bill: ${formatCurrency(totalWageBill)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatCircle(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.2f),
            contentColor = color
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RecentContractItem(contract: PlayerContractsEntity) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = contract.playerName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = contract.teamName,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Text(
                text = formatCurrency(contract.salary),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RecentTransferItem(transfer: TransfersEntity) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.SwapHoriz,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = transfer.playerName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${transfer.currentTeam} → ${transfer.targetTeam}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCurrency(transfer.transferFee),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transfer.transferStatus,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun RecentLoanItem(loan: PlayerLoansEntity) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.SwapHoriz,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = loan.playerName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${loan.loaningTeam} → ${loan.receivingTeam}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Text(
                text = "${loan.goalsScored} goals",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun NegotiationDetailDialog(
    uiState: NegotiationUiState,
    onDismiss: () -> Unit,
    onRenew: (Int, Int, Int) -> Unit,
    onTerminate: (Int, String) -> Unit,
    onAcceptTransfer: (Int) -> Unit,
    onRejectTransfer: (Int) -> Unit,
    onNegotiateTransfer: (Int, Int?, Int?) -> Unit,
    onCompleteTransfer: (Int) -> Unit,
    onTriggerBuyOption: (Int) -> Unit,
    onEarlyReturn: (Int, String) -> Unit
) {
    var counterFee by remember { mutableStateOf<Int?>(null) }
    var counterWage by remember { mutableStateOf<Int?>(null) }
    var newSalary by remember { mutableStateOf("") }
    var newLength by remember { mutableStateOf("3") }
    var terminationReason by remember { mutableStateOf("") }
    var earlyReturnReason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when {
                    uiState.selectedContract != null -> "Contract Details"
                    uiState.selectedTransfer != null -> "Transfer Details"
                    uiState.selectedLoan != null -> "Loan Details"
                    else -> "Details"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.selectedContract != null) {
                    val contract = uiState.selectedContract
                    Text("Player: ${contract.playerName}", fontWeight = FontWeight.Bold)
                    Text("Team: ${contract.teamName}")
                    Text("Salary: ${formatCurrency(contract.salary)}")
                    Text("Start: ${contract.contractStartDate}")
                    Text("End: ${contract.contractEndDate}")
                    Text("Status: ${contract.contractStatus}")

                    if (contract.signingBonus != null) {
                        Text("Signing Bonus: ${formatCurrency(contract.signingBonus)}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (contract.contractStatus == "ACTIVE") {
                        OutlinedTextField(
                            value = newSalary,
                            onValueChange = { newSalary = it },
                            label = { Text("New Salary") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newLength,
                            onValueChange = { newLength = it },
                            label = { Text("Contract Length (years)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    newSalary.toIntOrNull()?.let { salary ->
                                        onRenew(contract.id, salary, newLength.toIntOrNull() ?: 3)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Renew")
                            }

                            OutlinedTextField(
                                value = terminationReason,
                                onValueChange = { terminationReason = it },
                                label = { Text("Termination Reason") },
                                modifier = Modifier.weight(2f)
                            )

                            Button(
                                onClick = { onTerminate(contract.id, terminationReason) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Terminate")
                            }
                        }
                    }
                }

                if (uiState.selectedTransfer != null) {
                    val transfer = uiState.selectedTransfer
                    Text("Player: ${transfer.playerName}", fontWeight = FontWeight.Bold)
                    Text("From: ${transfer.currentTeam}")
                    Text("To: ${transfer.targetTeam}")
                    Text("Fee: ${formatCurrency(transfer.transferFee)}")
                    Text("Wage: ${formatCurrency(transfer.monthlyWage)}/month")
                    Text("Length: ${transfer.contractLength} years")
                    Text("Status: ${transfer.transferStatus}")

                    if (transfer.isLoanToBuy) {
                        Text("Loan-to-Buy: Yes")
                        transfer.loanBuyFee?.let {
                            Text("Buy Fee: ${formatCurrency(it)}")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    when (transfer.transferStatus) {
                        "PENDING" -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = counterFee?.toString() ?: "",
                                    onValueChange = { counterFee = it.toIntOrNull() },
                                    label = { Text("Counter Fee") },
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = counterWage?.toString() ?: "",
                                    onValueChange = { counterWage = it.toIntOrNull() },
                                    label = { Text("Counter Wage") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onAcceptTransfer(transfer.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary
                                    )
                                ) {
                                    Text("Accept")
                                }

                                Button(
                                    onClick = {
                                        onNegotiateTransfer(
                                            transfer.id,
                                            counterFee,
                                            counterWage
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Negotiate")
                                }

                                Button(
                                    onClick = { onRejectTransfer(transfer.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Reject")
                                }
                            }
                        }
                        "ACCEPTED" -> {
                            Button(
                                onClick = { onCompleteTransfer(transfer.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Complete Transfer")
                            }
                        }
                    }
                }

                if (uiState.selectedLoan != null) {
                    val loan = uiState.selectedLoan
                    Text("Player: ${loan.playerName}", fontWeight = FontWeight.Bold)
                    Text("From: ${loan.loaningTeam}")
                    Text("To: ${loan.receivingTeam}")
                    Text("Period: ${loan.startDate} → ${loan.endDate}")
                    Text("Duration: ${loan.duration} months")
                    if (loan.loanFee != null) {
                        Text("Loan Fee: ${formatCurrency(loan.loanFee)}")
                    }
                    Text("Wage Contribution: ${loan.wageContribution}%")
                    Text("Status: ${loan.status}")

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Games: ${loan.gamesPlayed}")
                    Text("Goals: ${loan.goalsScored}")
                    Text("Assists: ${loan.assistsMade}")

                    if (loan.optionToBuy) {
                        Text("Option to Buy: Yes")
                        loan.buyOptionFee?.let {
                            Text("Buy Fee: ${formatCurrency(it)}")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (loan.optionToBuy && loan.status == "ACTIVE") {
                        Button(
                            onClick = { onTriggerBuyOption(loan.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Text("Trigger Buy Option")
                        }
                    }

                    if (loan.recallOption && loan.status == "ACTIVE") {
                        OutlinedTextField(
                            value = earlyReturnReason,
                            onValueChange = { earlyReturnReason = it },
                            label = { Text("Early Return Reason") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { onEarlyReturn(loan.id, earlyReturnReason) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Early Return")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}