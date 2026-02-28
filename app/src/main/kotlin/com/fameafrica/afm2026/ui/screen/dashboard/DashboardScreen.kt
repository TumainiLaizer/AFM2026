package com.fameafrica.afm2026.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.fameafrica.afm2026.domain.manager.GameManager.GameDate
import com.fameafrica.afm2026.ui.theme.*

@Composable
fun DashboardScreen(
    onNavigateToMatch: (Int) -> Unit,
    onNavigateToSquad: () -> Unit,
    onNavigateToTransfers: () -> Unit,
    onNavigateToClub: () -> Unit,
    onNavigateToWorld: () -> Unit,
    onHandleInterview: () -> Unit,
    onBoardRequest: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(viewModelStoreOwner, key)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gameContext by viewModel.gameContext.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DashboardTopBar(
                clubName = uiState.clubName,
                reputationLevel = uiState.reputationLevel,
                onNotificationClick = { /* Navigate to notifications */ }
            )
        },
        bottomBar = {
            // This will be replaced by main bottom navigation
            DashboardBottomNav(
                selectedTab = 0,
                onTabSelected = { /* Handled by main nav */ }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingSpinner()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FameColors.StadiumBlack)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Welcome Card with Game Date
                item {
                    WelcomeCard(
                        managerName = uiState.managerName,
                        gameDate = gameContext.gameDate,
                        week = gameContext.week,
                        season = gameContext.season,
                        boardSatisfaction = uiState.boardSatisfaction,
                        saveName = gameContext.saveName
                    )
                }

                // Next Match Card (Priority)
                if (uiState.nextMatch != null) {
                    item {
                        NextMatchCard(
                            match = uiState.nextMatch!!,
                            onClick = { onNavigateToMatch(uiState.nextMatch!!.id) }
                        )
                    }
                }

                // Quick Stats Row
                item {
                    QuickStatsRow(
                        leaguePosition = uiState.leaguePosition,
                        form = uiState.form,
                        morale = uiState.morale,
                        transferBudget = uiState.transferBudget,
                        bankBalance = uiState.bankBalance,
                        financialTier = uiState.financialTier
                    )
                }

                // Quick Actions
                item {
                    QuickActionsRow(
                        onPickTeam = onNavigateToSquad,
                        onNextMatch = { if (uiState.nextMatch != null) onNavigateToMatch(uiState.nextMatch!!.id) },
                        onHandleInterview = onHandleInterview,
                        onBoardRequest = onBoardRequest
                    )
                }

                // Board Objectives
                if (uiState.objectives.isNotEmpty()) {
                    item {
                        ObjectivesCard(
                            objectives = uiState.objectives,
                            onViewAll = { /* Navigate to objectives */ }
                        )
                    }
                }

                // Recent Results
                if (uiState.recentResults.isNotEmpty()) {
                    item {
                        RecentResultsCard(
                            results = uiState.recentResults,
                            onViewAll = { /* Navigate to fixtures */ }
                        )
                    }
                }

                // Upcoming Fixtures
                if (uiState.upcomingFixtures.isNotEmpty()) {
                    item {
                        UpcomingFixturesCard(
                            fixtures = uiState.upcomingFixtures,
                            onFixtureClick = { fixtureId ->
                                onNavigateToMatch(fixtureId)
                            }
                        )
                    }
                }

                // Latest News
                if (uiState.latestNews.isNotEmpty()) {
                    item {
                        NewsFeedCard(
                            news = uiState.latestNews,
                            onNewsClick = { /* Navigate to news detail */ }
                        )
                    }
                }

                // Notifications Preview
                if (uiState.unreadNotifications > 0) {
                    item {
                        NotificationsPreviewCard(
                            count = uiState.unreadNotifications,
                            onClick = { /* Navigate to inbox */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingSpinner() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = FameColors.ChampionsGold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    clubName: String,
    reputationLevel: String,
    onNotificationClick: () -> Unit
) {
    val reputationColor = when (reputationLevel) {
        "African Legend" -> FameColors.AfricanLegendEmerald
        "Continental" -> FameColors.ContinentalGold
        "National" -> FameColors.NationalSilver
        "Local" -> FameColors.LocalBronze
        else -> FameColors.PitchGreen
    }

    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = clubName,
                    style = FameTypography.titleLarge,
                    color = FameColors.WarmIvory,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = reputationLevel,
                    style = FameTypography.reputationBadge,
                    color = reputationColor
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { /* Open drawer */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = FameColors.WarmIvory
                )
            }
        },
        actions = {
            IconButton(onClick = onNotificationClick) {
                BadgedBox(
                    badge = {
                        // This will be updated with actual count
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(FameColors.AfroSunOrange, CircleShape)
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = FameColors.WarmIvory
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = FameColors.StadiumBlack
        )
    )
}

@Composable
fun DashboardBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = FameColors.SurfaceDark,
        contentColor = FameColors.MutedParchment
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Dashboard"
                )
            },
            label = { Text("Home", style = FameTypography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FameColors.PitchGreen,
                selectedTextColor = FameColors.PitchGreen,
                unselectedIconColor = FameColors.MutedParchment,
                unselectedTextColor = FameColors.MutedParchment,
                indicatorColor = FameColors.PitchGreen.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Squad"
                )
            },
            label = { Text("Squad", style = FameTypography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FameColors.PitchGreen,
                selectedTextColor = FameColors.PitchGreen,
                unselectedIconColor = FameColors.MutedParchment,
                unselectedTextColor = FameColors.MutedParchment,
                indicatorColor = FameColors.PitchGreen.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Transfers"
                )
            },
            label = { Text("Transfers", style = FameTypography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FameColors.PitchGreen,
                selectedTextColor = FameColors.PitchGreen,
                unselectedIconColor = FameColors.MutedParchment,
                unselectedTextColor = FameColors.MutedParchment,
                indicatorColor = FameColors.PitchGreen.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = "Club"
                )
            },
            label = { Text("Club", style = FameTypography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FameColors.PitchGreen,
                selectedTextColor = FameColors.PitchGreen,
                unselectedIconColor = FameColors.MutedParchment,
                unselectedTextColor = FameColors.MutedParchment,
                indicatorColor = FameColors.PitchGreen.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "World"
                )
            },
            label = { Text("World", style = FameTypography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FameColors.PitchGreen,
                selectedTextColor = FameColors.PitchGreen,
                unselectedIconColor = FameColors.MutedParchment,
                unselectedTextColor = FameColors.MutedParchment,
                indicatorColor = FameColors.PitchGreen.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
fun WelcomeCard(
    managerName: String,
    gameDate: GameDate?,
    week: Int,
    season: String,
    boardSatisfaction: Int,
    saveName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
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
                    text = "Welcome back,",
                    style = FameTypography.bodyMedium,
                    color = FameColors.MutedParchment
                )
                Text(
                    text = managerName,
                    style = FameTypography.titleLarge,
                    color = FameColors.WarmIvory
                )

                // Game Date Display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = FameColors.ChampionsGold,
                        modifier = Modifier.size(14.dp)
                    )

                    Text(
                        text = gameDate?.toDisplayString() ?: "Season $season",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Text(
                        text = " • Week $week",
                        style = FameTypography.labelSmall,
                        color = FameColors.AfroSunOrange,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Text(
                    text = saveName,
                    style = FameTypography.labelSmall,
                    color = FameColors.DisabledText,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            BoardSatisfactionGauge(satisfaction = boardSatisfaction)
        }
    }
}

@Composable
fun BoardSatisfactionGauge(satisfaction: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            FameColors.PitchGreen,
                            FameColors.ChampionsGold,
                            when {
                                satisfaction >= 70 -> FameColors.PitchGreen
                                satisfaction >= 40 -> FameColors.AfroSunOrange
                                else -> FameColors.KenteRed
                            }
                        )
                    )
                )
        ) {
            Text(
                text = "$satisfaction%",
                style = FameTypography.playerRating,
                color = FameColors.WarmIvory
            )
        }
        Text(
            text = "Board",
            style = FameTypography.labelSmall,
            color = FameColors.MutedParchment,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun NextMatchCard(
    match: NextMatchUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            FameColors.SurfaceMedium,
                            FameColors.SurfaceDark
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NEXT MATCH",
                    style = FameTypography.labelMedium,
                    color = FameColors.AfroSunOrange,
                    modifier = Modifier
                        .background(
                            color = FameColors.AfroSunOrange.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Text(
                    text = match.time,
                    style = FameTypography.bodySmall,
                    color = FameColors.MutedParchment
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamVsColumn(
                    teamName = match.homeTeam,
                    isHome = true
                )

                Text(
                    text = "VS",
                    style = FameTypography.matchClock,
                    color = FameColors.ChampionsGold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                TeamVsColumn(
                    teamName = match.awayTeam,
                    isHome = false
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.competition,
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )

                Text(
                    text = match.stadium,
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )
            }
        }
    }
}

@Composable
fun TeamVsColumn(teamName: String, isHome: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    color = if (isHome) FameColors.PitchGreen.copy(alpha = 0.2f)
                    else FameColors.BaobabBrown.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = teamName.take(1),
                style = FameTypography.titleLarge,
                color = if (isHome) FameColors.PitchGreen else FameColors.BaobabBrown
            )
        }

        Text(
            text = teamName,
            style = FameTypography.bodySmall,
            color = FameColors.WarmIvory,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(100.dp)
        )
    }
}

@Composable
fun QuickStatsRow(
    leaguePosition: Int,
    form: String,
    morale: Int,
    transferBudget: Long,
    bankBalance: Long,
    financialTier: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = "POS",
            value = leaguePosition.toString(),
            icon = Icons.Default.Star,
            color = FameColors.ChampionsGold,
            modifier = Modifier.weight(1f)
        )

        StatCard(
            title = "FORM",
            value = form,
            icon = Icons.Default.TrendingUp,
            color = when {
                form.all { it == 'W' } -> FameColors.PitchGreen
                form.all { it == 'L' } -> FameColors.KenteRed
                else -> FameColors.AfroSunOrange
            },
            modifier = Modifier.weight(1f)
        )

        StatCard(
            title = "MORALE",
            value = "$morale%",
            icon = Icons.Default.Favorite,
            color = when {
                morale >= 70 -> FameColors.PitchGreen
                morale >= 40 -> FameColors.AfroSunOrange
                else -> FameColors.KenteRed
            },
            modifier = Modifier.weight(1f)
        )

        StatCard(
            title = "BUDGET",
            value = "€${transferBudget / 1_000_000}M",
            icon = Icons.Default.AttachMoney,
            color = when (financialTier) {
                "Rich" -> FameColors.AfricanLegendEmerald
                "Upper Middle" -> FameColors.PitchGreen
                "Middle" -> FameColors.ChampionsGold
                "Lower" -> FameColors.AfroSunOrange
                else -> FameColors.KenteRed
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = value,
                style = FameTypography.tableCellLarge,
                color = FameColors.WarmIvory,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Text(
                text = title,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )
        }
    }
}

@Composable
fun QuickActionsRow(
    onPickTeam: () -> Unit,
    onNextMatch: () -> Unit,
    onHandleInterview: () -> Unit,
    onBoardRequest: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "QUICK ACTIONS",
                style = FameTypography.labelMedium,
                color = FameColors.ChampionsGold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionChip(
                    icon = Icons.Default.Group,
                    label = "Pick Team",
                    color = FameColors.PitchGreen,
                    onClick = onPickTeam,
                    modifier = Modifier.weight(1f)
                )

                QuickActionChip(
                    icon = Icons.Default.SportsSoccer,
                    label = "Next Match",
                    color = FameColors.AfroSunOrange,
                    onClick = onNextMatch,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionChip(
                    icon = Icons.Default.RecordVoiceOver,
                    label = "Interview",
                    color = FameColors.ChampionsGold,
                    onClick = onHandleInterview,
                    modifier = Modifier.weight(1f)
                )

                QuickActionChip(
                    icon = Icons.Default.Assignment,
                    label = "Board",
                    color = FameColors.AfricanLegendEmerald,
                    onClick = onBoardRequest,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun QuickActionChip(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = FameTypography.labelSmall,
                color = color,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun ObjectivesCard(
    objectives: List<ObjectiveUiModel>,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BOARD OBJECTIVES",
                    style = FameTypography.labelMedium,
                    color = FameColors.ChampionsGold
                )

                TextButton(onClick = onViewAll) {
                    Text(
                        text = "View All",
                        style = FameTypography.labelSmall,
                        color = FameColors.AfroSunOrange
                    )
                }
            }

            objectives.take(3).forEach { objective ->
                ObjectiveItem(objective = objective)
            }
        }
    }
}

@Composable
fun ObjectiveItem(objective: ObjectiveUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = objective.title,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory
            )

            LinearProgressIndicator(
                progress = { objective.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .height(4.dp),
                color = if (objective.progress >= 70) FameColors.PitchGreen
                else if (objective.progress >= 40) FameColors.AfroSunOrange
                else FameColors.KenteRed,
                trackColor = FameColors.SurfaceLight,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
            )
        }

        Text(
            text = "${objective.progress}%",
            style = FameTypography.tableCell,
            color = FameColors.MutedParchment,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun RecentResultsCard(
    results: List<ResultUiModel>,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT RESULTS",
                    style = FameTypography.labelMedium,
                    color = FameColors.AfroSunOrange
                )

                TextButton(onClick = onViewAll) {
                    Text(
                        text = "View All",
                        style = FameTypography.labelSmall,
                        color = FameColors.AfroSunOrange
                    )
                }
            }

            results.take(3).forEach { result ->
                ResultItem(result = result)
            }
        }
    }
}

@Composable
fun ResultItem(result: ResultUiModel) {
    val resultColor = when {
        result.isWin -> FameColors.PitchGreen
        result.isDraw -> FameColors.AfroSunOrange
        else -> FameColors.KenteRed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = result.competition,
            style = FameTypography.labelSmall,
            color = FameColors.MutedParchment,
            modifier = Modifier.width(60.dp)
        )

        Text(
            text = "${result.homeTeam} ${result.homeScore} - ${result.awayScore} ${result.awayTeam}",
            style = FameTypography.bodySmall,
            color = FameColors.WarmIvory,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(resultColor)
        )
    }
}

@Composable
fun UpcomingFixturesCard(
    fixtures: List<FixtureUiModel>,
    onFixtureClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "UPCOMING FIXTURES",
                style = FameTypography.labelMedium,
                color = FameColors.AfroSunOrange
            )

            fixtures.take(3).forEach { fixture ->
                FixtureItem(
                    fixture = fixture,
                    onClick = { onFixtureClick(fixture.id) }
                )
            }
        }
    }
}

@Composable
fun FixtureItem(
    fixture: FixtureUiModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fixture.date,
            style = FameTypography.labelSmall,
            color = FameColors.MutedParchment,
            modifier = Modifier.width(60.dp)
        )

        Text(
            text = "${fixture.homeTeam} vs ${fixture.awayTeam}",
            style = FameTypography.bodySmall,
            color = FameColors.WarmIvory,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = fixture.competition,
            style = FameTypography.labelSmall,
            color = FameColors.ChampionsGold
        )
    }
}

@Composable
fun NewsFeedCard(
    news: List<NewsUiModel>,
    onNewsClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "LATEST NEWS",
                style = FameTypography.labelMedium,
                color = FameColors.AfroSunOrange
            )

            news.take(2).forEach { item ->
                NewsItem(
                    news = item,
                    onClick = { onNewsClick(item.id) }
                )
            }
        }
    }
}

@Composable
fun NewsItem(
    news: NewsUiModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(FameColors.SurfaceLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Article,
                contentDescription = null,
                tint = FameColors.MutedParchment,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = news.headline,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = news.time,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )
        }
    }
}

@Composable
fun NotificationsPreviewCard(
    count: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = FameColors.AfroSunOrange,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = " Inbox",
                    style = FameTypography.bodyMedium,
                    color = FameColors.WarmIvory,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Badge(
                containerColor = FameColors.KenteRed
            ) {
                Text(
                    text = count.toString(),
                    style = FameTypography.labelSmall,
                    color = FameColors.WarmIvory
                )
            }
        }
    }
}

// ============ PREVIEW ============

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    AFM2026Theme {
        DashboardScreen(
            onNavigateToMatch = {},
            onNavigateToSquad = {},
            onNavigateToTransfers = {},
            onNavigateToClub = {},
            onNavigateToWorld = {},
            onHandleInterview = {},
            onBoardRequest = {}
        )
    }
}