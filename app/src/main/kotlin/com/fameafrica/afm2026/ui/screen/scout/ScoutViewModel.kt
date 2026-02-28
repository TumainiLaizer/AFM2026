package com.fameafrica.afm2026.ui.screen.scout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.database.entities.ScoutAssignmentsEntity
import com.fameafrica.afm2026.data.repository.ScoutAssignmentsRepository
import com.fameafrica.afm2026.data.repository.ScoutsRepository
import com.fameafrica.afm2026.data.repository.PlayersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ScoutViewModel @Inject constructor(
    private val scoutAssignmentsRepository: ScoutAssignmentsRepository,
    //private val scoutsRepository: ScoutsRepository,
    private val playersRepository: PlayersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScoutUiState())
    val uiState: StateFlow<ScoutUiState> = _uiState

    private val _selectedScoutId = MutableStateFlow<Int?>(null)
    private val _selectedTab = MutableStateFlow(0)

    init {
        loadScouts()
        loadAssignments()
    }

    fun onEvent(event: ScoutEvent) {
        when (event) {
            is ScoutEvent.SelectScout -> {
                _selectedScoutId.value = event.scoutId
                loadScoutAssignments(event.scoutId)
            }
            is ScoutEvent.ChangeTab -> _selectedTab.value = event.tabIndex
            is ScoutEvent.CreateAssignment -> createAssignment(event.scoutId, event.playerId, event.priority)
            is ScoutEvent.CompleteAssignment -> completeAssignment(event.assignmentId, event.report)
            is ScoutEvent.FailAssignment -> failAssignment(event.assignmentId)
            is ScoutEvent.AutoGenerateReport -> autoGenerateReport(event.assignmentId)
            is ScoutEvent.SearchPlayers -> searchPlayers(event.query)
            is ScoutEvent.ClearSearch -> clearSearch()
        }
    }

    private fun loadScouts() {
        viewModelScope.launch {
            scoutsRepository.getAllScouts().collect { scouts ->
                _uiState.value = _uiState.value.copy(
                    scouts = scouts,
                    isLoading = false
                )
            }
        }
    }

    private fun loadAssignments() {
        viewModelScope.launch {
            combine(
                scoutAssignmentsRepository.getAllAssignments(),
                scoutAssignmentsRepository.getScoutPerformanceStats(),
                scoutAssignmentsRepository.getPriorityDistribution()
            ) { assignments, performanceStats, priorityDist ->
                _uiState.value = _uiState.value.copy(
                    allAssignments = assignments,
                    performanceStats = performanceStats,
                    priorityDistribution = priorityDist
                )
            }.collect {}
        }
    }

    private fun loadScoutAssignments(scoutId: Int) {
        viewModelScope.launch {
            val dashboard = scoutAssignmentsRepository.getScoutAssignmentsDashboard(scoutId)
            _uiState.value = _uiState.value.copy(
                scoutDashboard = dashboard,
                selectedScoutId = scoutId
            )
        }
    }

    private fun createAssignment(scoutId: Int, playerId: Int, priority: String) {
        viewModelScope.launch {
            val assignment = scoutAssignmentsRepository.assignScoutToPlayer(
                scoutId = scoutId,
                playerId = playerId,
                priority = priority
            )
            if (assignment != null) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Assignment created successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Failed to create assignment"
                )
            }
        }
    }

    private fun completeAssignment(assignmentId: Int, report: String) {
        viewModelScope.launch {
            val success = scoutAssignmentsRepository.completeAssignment(
                assignmentId = assignmentId,
                report = report,
                rating = 70, // This would come from UI
                estimatedValue = 1000000, // This would come from UI
                potentialRating = 80, // This would come from UI
                verdict = ScoutVerdict.RECOMMENDED.value
            )
            if (success) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Assignment completed"
                )
            }
        }
    }

    private fun failAssignment(assignmentId: Int) {
        viewModelScope.launch {
            val success = scoutAssignmentsRepository.failAssignment(assignmentId)
            if (success) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Assignment marked as failed"
                )
            }
        }
    }

    private fun autoGenerateReport(assignmentId: Int) {
        viewModelScope.launch {
            val success = scoutAssignmentsRepository.autoGenerateScoutReport(assignmentId)
            if (success) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Report generated successfully"
                )
            }
        }
    }

    private fun searchPlayers(query: String) {
        viewModelScope.launch {
            val results = playersRepository.searchPlayers(query).firstOrNull() ?: emptyList()
            _uiState.value = _uiState.value.copy(
                searchResults = results,
                isSearching = query.isNotEmpty()
            )
        }
    }

    private fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            isSearching = false
        )
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}

data class ScoutUiState(
    val isLoading: Boolean = true,
    val scouts: List<ScoutEntity> = emptyList(),
    val allAssignments: List<ScoutAssignmentsEntity> = emptyList(),
    val selectedScoutId: Int? = null,
    val scoutDashboard: ScoutAssignmentsDashboard? = null,
    val performanceStats: List<ScoutPerformanceStats> = emptyList(),
    val priorityDistribution: List<PriorityDistribution> = emptyList(),
    val searchResults: List<PlayersEntity> = emptyList(),
    val isSearching: Boolean = false,
    val snackbarMessage: String? = null
)

sealed class ScoutEvent {
    data class SelectScout(val scoutId: Int) : ScoutEvent()
    data class ChangeTab(val tabIndex: Int) : ScoutEvent()
    data class CreateAssignment(val scoutId: Int, val playerId: Int, val priority: String) : ScoutEvent()
    data class CompleteAssignment(val assignmentId: Int, val report: String) : ScoutEvent()
    data class FailAssignment(val assignmentId: Int) : ScoutEvent()
    data class AutoGenerateReport(val assignmentId: Int) : ScoutEvent()
    data class SearchPlayers(val query: String) : ScoutEvent()
    object ClearSearch : ScoutEvent()
}

@Composable
fun ScoutScreen(
    viewModel: ScoutViewModel,
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
        topBar = { ScoutTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Navigate to create assignment */ }
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Assignment")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScoutTabs(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.onEvent(ScoutEvent.ChangeTab(it)) }
            )

            when (uiState.selectedTab) {
                0 -> ScoutDashboardTab(
                    uiState = uiState,
                    onSelectScout = { viewModel.onEvent(ScoutEvent.SelectScout(it)) },
                    onGenerateReport = { viewModel.onEvent(ScoutEvent.AutoGenerateReport(it)) }
                )
                1 -> ActiveAssignmentsTab(
                    assignments = uiState.scoutDashboard?.activeList ?: emptyList(),
                    onComplete = { viewModel.onEvent(ScoutEvent.CompleteAssignment(it, "Report")) },
                    onFail = { viewModel.onEvent(ScoutEvent.FailAssignment(it)) }
                )
                2 -> ScoutPerformanceTab(
                    performanceStats = uiState.performanceStats,
                    priorityDistribution = uiState.priorityDistribution
                )
                3 -> PlayerSearchTab(
                    searchResults = uiState.searchResults,
                    isSearching = uiState.isSearching,
                    onSearchQueryChange = { viewModel.onEvent(ScoutEvent.SearchPlayers(it)) },
                    onPlayerClick = onNavigateToPlayer,
                    onCreateAssignment = { playerId ->
                        uiState.selectedScoutId?.let { scoutId ->
                            viewModel.onEvent(
                                ScoutEvent.CreateAssignment(
                                    scoutId = scoutId,
                                    playerId = playerId,
                                    priority = "Normal"
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ScoutTopBar() {
    TopAppBar(
        title = { Text("Scouting Center") },
        actions = {
            IconButton(onClick = { /* Refresh */ }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    )
}

@Composable
fun ScoutTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(selectedTabIndex = selectedTab) {
        listOf("Dashboard", "Active", "Performance", "Search").forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
fun ScoutDashboardTab(
    uiState: ScoutUiState,
    onSelectScout: (Int) -> Unit,
    onGenerateReport: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ScoutOverviewCard(
                totalScouts = uiState.scouts.size,
                activeAssignments = uiState.scoutDashboard?.activeAssignments ?: 0,
                completedAssignments = uiState.scoutDashboard?.completedAssignments ?: 0,
                averageRating = uiState.scoutDashboard?.averageScoutRating ?: 0.0
            )
        }

        item {
            Text(
                text = "Scouts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(uiState.scouts) { scout ->
            ScoutCard(
                scout = scout,
                activeCount = uiState.allAssignments.count { it.scoutId == scout.id && it.reportStatus == "In Progress" },
                onClick = { onSelectScout(scout.id) }
            )
        }

        if (uiState.scoutDashboard != null) {
            item {
                Text(
                    text = "Active Assignments",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(uiState.scoutDashboard.activeList.take(3)) { assignment ->
                ActiveAssignmentCard(
                    assignment = assignment,
                    onGenerateReport = { onGenerateReport(assignment.id) }
                )
            }
        }
    }
}

@Composable
fun ScoutOverviewCard(
    totalScouts: Int,
    activeAssignments: Int,
    completedAssignments: Int,
    averageRating: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Scouting Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OverviewStat(
                    value = totalScouts.toString(),
                    label = "Scouts"
                )
                OverviewStat(
                    value = activeAssignments.toString(),
                    label = "Active"
                )
                OverviewStat(
                    value = completedAssignments.toString(),
                    label = "Completed"
                )
                OverviewStat(
                    value = String.format("%.1f", averageRating),
                    label = "Avg Rating"
                )
            }
        }
    }
}

@Composable
fun OverviewStat(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ScoutCard(
    scout: ScoutEntity,
    activeCount: Int,
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scout.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${scout.nationality} • ${scout.specialization}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Chip(
                onClick = {},
                colors = ChipDefaults.chipColors(
                    containerColor = if (activeCount > 0)
                        MaterialTheme.colorScheme.tertiaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Active: $activeCount")
            }

            Text(
                text = "${scout.impactRating}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun ActiveAssignmentCard(
    assignment: ScoutAssignmentsEntity,
    onGenerateReport: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (assignment.isHighPriority)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
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
                Column {
                    Text(
                        text = assignment.playerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Scout: ${assignment.scoutName}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (assignment.isHighPriority) {
                    AssistChip(
                        onClick = {},
                        label = { Text("High Priority") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Assigned: ${formatDate(assignment.assignedDate)}",
                style = MaterialTheme.typography.bodySmall
            )

            if (assignment.assignmentNotes != null) {
                Text(
                    text = "Notes: ${assignment.assignmentNotes}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onGenerateReport,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate Report")
            }
        }
    }
}

@Composable
fun ActiveAssignmentsTab(
    assignments: List<ScoutAssignmentsEntity>,
    onComplete: (Int) -> Unit,
    onFail: (Int) -> Unit
) {
    if (assignments.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Assignment,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
                Text(
                    text = "No active assignments",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assignments) { assignment ->
                ActiveAssignmentDetailCard(
                    assignment = assignment,
                    onComplete = { onComplete(assignment.id) },
                    onFail = { onFail(assignment.id) }
                )
            }
        }
    }
}

@Composable
fun ActiveAssignmentDetailCard(
    assignment: ScoutAssignmentsEntity,
    onComplete: () -> Unit,
    onFail: () -> Unit
) {
    Card {
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
                    text = assignment.playerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                PriorityBadge(priority = assignment.priority)
            }

            Text(
                text = "Scout: ${assignment.scoutName}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onFail,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Fail")
                }

                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Complete")
                }
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val (backgroundColor, contentColor) = when (priority) {
        "Urgent" -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        "High" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "Normal" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = priority,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun ScoutPerformanceTab(
    performanceStats: List<ScoutPerformanceStats>,
    priorityDistribution: List<PriorityDistribution>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Priority Distribution",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    priorityDistribution.forEach { priority ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(priority.priority)
                            Text(
                                text = priority.count.toString(),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (priority != priorityDistribution.last()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Scout Performance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(performanceStats) { stat ->
            ScoutPerformanceCard(stat)
        }
    }
}

@Composable
fun ScoutPerformanceCard(stat: ScoutPerformanceStats) {
    Card(
        modifier = Modifier.fillMaxWidth()
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
                    text = stat.scoutName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Avg: ${String.format("%.1f", stat.averageRating ?: 0.0)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PerformanceStat(
                    value = stat.totalAssignments.toString(),
                    label = "Total"
                )
                PerformanceStat(
                    value = stat.completed.toString(),
                    label = "Completed",
                    color = MaterialTheme.colorScheme.tertiary
                )
                PerformanceStat(
                    value = stat.failed.toString(),
                    label = "Failed",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun PerformanceStat(
    value: String,
    label: String,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun PlayerSearchTab(
    searchResults: List<PlayersEntity>,
    isSearching: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onPlayerClick: (Int) -> Unit,
    onCreateAssignment: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search players...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        if (isSearching && searchResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No players found")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { player ->
                    PlayerSearchResultCard(
                        player = player,
                        onPlayerClick = { onPlayerClick(player.id) },
                        onCreateAssignment = { onCreateAssignment(player.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerSearchResultCard(
    player: PlayersEntity,
    onPlayerClick: () -> Unit,
    onCreateAssignment: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayerClick() }
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
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${player.position} • ${player.teamName} • Rating: ${player.rating}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = onCreateAssignment) {
                Icon(
                    Icons.Default.Assignment,
                    contentDescription = "Assign Scout",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return format.format(date)
}