package com.fameafrica.afm2026.ui.screen.match

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import coil.compose.AsyncImage
import com.fameafrica.afm2026.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MatchScreen(
    matchId: Int,
    onBack: () -> Unit,
    viewModel: MatchViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MatchTopBar(
                match = uiState.matchInfo,
                onBack = onBack,
                onSettings = { /* Open match settings */ }
            )
        }
    ) { paddingValues ->
        when (uiState.matchStatus) {
            MatchStatus.PRE_MATCH -> PreMatchScreen(
                match = uiState.matchInfo,
                onStartMatch = viewModel::startMatch,
                modifier = Modifier.padding(paddingValues)
            )
            MatchStatus.LIVE -> LiveMatchScreen(
                match = uiState.matchInfo,
                events = uiState.events,
                commentary = uiState.commentary,
                stats = uiState.stats,
                possession = uiState.possession,
                currentMinute = uiState.currentMinute,
                onPause = viewModel::pauseMatch,
                onSpeedChange = viewModel::changeSpeed,
                speed = uiState.matchSpeed,
                onEventClick = viewModel::highlightEvent,
                modifier = Modifier.padding(paddingValues)
            )
            MatchStatus.HALFTIME -> HalfTimeScreen(
                match = uiState.matchInfo,
                stats = uiState.stats,
                onStartSecondHalf = viewModel::startSecondHalf,
                modifier = Modifier.padding(paddingValues)
            )
            MatchStatus.FULL_TIME -> FullTimeScreen(
                match = uiState.matchInfo,
                stats = uiState.stats,
                events = uiState.events,
                onViewReport = { /* Navigate to match report */ },
                onContinue = onBack,
                modifier = Modifier.padding(paddingValues)
            )
            MatchStatus.PAUSED -> PausedScreen(
                onResume = viewModel::resumeMatch,
                onSpeedChange = viewModel::changeSpeed,
                speed = uiState.matchSpeed,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchTopBar(
    match: MatchInfoUiModel?,
    onBack: () -> Unit,
    onSettings: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            if (match != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = match.homeTeam,
                        style = FameTypography.matchScore.copy(fontSize = 18.sp),
                        color = FameColors.WarmIvory
                    )

                    Text(
                        text = "${match.homeScore} - ${match.awayScore}",
                        style = FameTypography.matchScore.copy(fontSize = 24.sp),
                        color = FameColors.ChampionsGold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Text(
                        text = match.awayTeam,
                        style = FameTypography.matchScore.copy(fontSize = 18.sp),
                        color = FameColors.WarmIvory
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = FameColors.WarmIvory
                )
            }
        },
        actions = {
            IconButton(onClick = onSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = FameColors.WarmIvory
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = FameColors.StadiumBlack
        )
    )
}

@Composable
fun PreMatchScreen(
    match: MatchInfoUiModel?,
    onStartMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (match == null) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FameColors.PitchGreen,
                        FameColors.StadiumBlack
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Match Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = match.competition.uppercase(),
                    style = FameTypography.labelLarge,
                    color = FameColors.ChampionsGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = match.stadium,
                    style = FameTypography.bodyMedium,
                    color = FameColors.MutedParchment
                )
            }

            // Team VS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        FameColors.PitchGreen.copy(alpha = 0.3f),
                                        FameColors.SurfaceLight
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = match.homeLogo,
                            contentDescription = match.homeTeam,
                            modifier = Modifier.size(70.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = match.homeTeam,
                        style = FameTypography.titleLarge,
                        color = FameColors.WarmIvory
                    )

                    Text(
                        text = "League: ${match.homePosition}th",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                }

                // VS
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "VS",
                        style = FameTypography.matchScore,
                        color = FameColors.ChampionsGold
                    )

                    Text(
                        text = match.kickoff,
                        style = FameTypography.matchClock,
                        color = FameColors.AfroSunOrange
                    )
                }

                // Away Team
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        FameColors.KenteRed.copy(alpha = 0.3f),
                                        FameColors.SurfaceLight
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = match.awayLogo,
                            contentDescription = match.awayTeam,
                            modifier = Modifier.size(70.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = match.awayTeam,
                        style = FameTypography.titleLarge,
                        color = FameColors.WarmIvory
                    )

                    Text(
                        text = "League: ${match.awayPosition}th",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                }
            }

            // Pre-match stats
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = FameColors.SurfaceDark.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    PreMatchStatRow(
                        label = "Head to Head",
                        homeValue = "${match.h2hHomeWins} wins",
                        awayValue = "${match.h2hAwayWins} wins",
                        draws = "${match.h2hDraws} draws"
                    )

                    Divider(
                        color = FameColors.SurfaceLight,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    PreMatchStatRow(
                        label = "Recent Form",
                        homeValue = match.homeForm,
                        awayValue = match.awayForm,
                        draws = null
                    )

                    Divider(
                        color = FameColors.SurfaceLight,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    PreMatchStatRow(
                        label = "Average Goals",
                        homeValue = String.format("%.1f", match.homeAvgGoals),
                        awayValue = String.format("%.1f", match.awayAvgGoals),
                        draws = null
                    )
                }
            }

            // Start Match Button
            Button(
                onClick = onStartMatch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FameColors.PitchGreen
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "KICK OFF",
                    style = FameTypography.titleMedium,
                    color = FameColors.WarmIvory
                )
            }
        }
    }
}

@Composable
fun PreMatchStatRow(
    label: String,
    homeValue: String,
    awayValue: String,
    draws: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = homeValue,
            style = FameTypography.bodySmall,
            color = FameColors.PitchGreen,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(80.dp)
        ) {
            Text(
                text = label,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )

            if (draws != null) {
                Text(
                    text = draws,
                    style = FameTypography.labelSmall,
                    color = FameColors.AfroSunOrange
                )
            }
        }

        Text(
            text = awayValue,
            style = FameTypography.bodySmall,
            color = FameColors.KenteRed,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun LiveMatchScreen(
    match: MatchInfoUiModel?,
    events: List<MatchEventUiModel>,
    commentary: List<CommentaryUiModel>,
    stats: MatchStatsUiModel?,
    possession: Float,
    currentMinute: Int,
    onPause: () -> Unit,
    onSpeedChange: (Int) -> Unit,
    speed: Int,
    onEventClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (match == null) return

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FameColors.StadiumBlack)
    ) {
        // Live Score Header
        LiveScoreHeader(
            match = match,
            currentMinute = currentMinute,
            possession = possession,
            onPause = onPause,
            onSpeedChange = onSpeedChange,
            speed = speed
        )

        // Match Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = FameColors.StadiumBlack,
            edgePadding = 12.dp,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 2.dp,
                    color = FameColors.ChampionsGold
                )
            }
        ) {
            listOf("LIVE", "EVENTS", "STATS", "LINEUPS").forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = tab,
                            style = FameTypography.labelMedium,
                            color = if (selectedTab == index) FameColors.PitchGreen else FameColors.MutedParchment
                        )
                    }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> LiveCommentaryView(
                commentary = commentary,
                onEventClick = onEventClick,
                modifier = Modifier.weight(1f)
            )
            1 -> MatchEventsView(
                events = events,
                onEventClick = onEventClick,
                modifier = Modifier.weight(1f)
            )
            2 -> MatchStatsView(
                stats = stats,
                possession = possession,
                modifier = Modifier.weight(1f)
            )
            3 -> LineupsView(
                homeTeam = match.homeTeam,
                awayTeam = match.awayTeam,
                homeFormation = match.homeFormation,
                awayFormation = match.awayFormation,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LiveScoreHeader(
    match: MatchInfoUiModel,
    currentMinute: Int,
    possession: Float,
    onPause: () -> Unit,
    onSpeedChange: (Int) -> Unit,
    speed: Int
) {
    val pulseAnimation = rememberInfiniteTransition()
    val livePulse = pulseAnimation.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = match.homeLogo,
                        contentDescription = match.homeTeam,
                        modifier = Modifier.size(48.dp)
                    )

                    Text(
                        text = match.homeTeam,
                        style = FameTypography.bodySmall,
                        color = FameColors.WarmIvory,
                        maxLines = 1
                    )
                }

                // Score and Time
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = match.homeScore.toString(),
                            style = FameTypography.matchScore,
                            color = FameColors.WarmIvory
                        )

                        Text(
                            text = "-",
                            style = FameTypography.matchScore,
                            color = FameColors.ChampionsGold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Text(
                            text = match.awayScore.toString(),
                            style = FameTypography.matchScore,
                            color = FameColors.WarmIvory
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        // Live indicator
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(FameColors.KenteRed)
                                .scale(livePulse.value)
                        )

                        Text(
                            text = " $currentMinute'",
                            style = FameTypography.matchClock,
                            color = FameColors.KenteRed
                        )
                    }
                }

                // Away Team
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = match.awayLogo,
                        contentDescription = match.awayTeam,
                        modifier = Modifier.size(48.dp)
                    )

                    Text(
                        text = match.awayTeam,
                        style = FameTypography.bodySmall,
                        color = FameColors.WarmIvory,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Possession Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${possession.toInt()}%",
                    style = FameTypography.labelSmall,
                    color = FameColors.PitchGreen,
                    modifier = Modifier.width(40.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(FameColors.SurfaceLight)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(possession)
                                .background(FameColors.PitchGreen)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(100 - possession)
                                .background(FameColors.KenteRed)
                        )
                    }
                }

                Text(
                    text = "${(100 - possession).toInt()}%",
                    style = FameTypography.labelSmall,
                    color = FameColors.KenteRed,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.End
                )
            }

            // Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pause Button
                IconButton(onClick = onPause) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = FameColors.WarmIvory
                    )
                }

                // Speed Controls
                SpeedControl(
                    speed = speed,
                    onSpeedChange = onSpeedChange
                )
            }
        }
    }
}

@Composable
fun SpeedControl(
    speed: Int,
    onSpeedChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(FameColors.SurfaceLight)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        SpeedButton(
            speed = 1,
            currentSpeed = speed,
            onClick = { onSpeedChange(1) },
            label = "1x"
        )

        SpeedButton(
            speed = 2,
            currentSpeed = speed,
            onClick = { onSpeedChange(2) },
            label = "2x"
        )

        SpeedButton(
            speed = 3,
            currentSpeed = speed,
            onClick = { onSpeedChange(3) },
            label = "3x"
        )

        SpeedButton(
            speed = 4,
            currentSpeed = speed,
            onClick = { onSpeedChange(4) },
            label = "4x"
        )
    }
}

@Composable
fun SpeedButton(
    speed: Int,
    currentSpeed: Int,
    onClick: () -> Unit,
    label: String
) {
    val isSelected = speed == currentSpeed

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) FameColors.PitchGreen
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = FameTypography.labelSmall,
            color = if (isSelected) FameColors.WarmIvory else FameColors.MutedParchment
        )
    }
}

@Composable
fun LiveCommentaryView(
    commentary: List<CommentaryUiModel>,
    onEventClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        reverseLayout = true
    ) {
        items(commentary) { item ->
            CommentaryItem(
                commentary = item,
                onClick = { onEventClick(item.id) },
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
fun CommentaryItem(
    commentary: CommentaryUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (commentary.type) {
                "GOAL" -> FameColors.AfroSunOrange.copy(alpha = 0.1f)
                "CARD" -> FameColors.KenteRed.copy(alpha = 0.1f)
                "SUB" -> FameColors.AfricanLegendEmerald.copy(alpha = 0.1f)
                else -> FameColors.SurfaceDark
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Minute
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when (commentary.type) {
                            "GOAL" -> FameColors.AfroSunOrange
                            "CARD" -> FameColors.KenteRed
                            "SUB" -> FameColors.AfricanLegendEmerald
                            else -> FameColors.SurfaceLight
                        }
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = commentary.minute,
                    style = FameTypography.labelSmall,
                    color = FameColors.WarmIvory
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Commentary Text
            Text(
                text = commentary.text,
                style = FameTypography.matchEvent,
                color = when (commentary.type) {
                    "GOAL" -> FameColors.AfroSunOrange
                    "CARD" -> FameColors.KenteRed
                    "SUB" -> FameColors.AfricanLegendEmerald
                    else -> FameColors.WarmIvory
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MatchEventsView(
    events: List<MatchEventUiModel>,
    onEventClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        items(events) { event ->
            MatchEventItem(
                event = event,
                onClick = { onEventClick(event.id) }
            )
        }
    }
}

@Composable
fun MatchEventItem(
    event: MatchEventUiModel,
    onClick: () -> Unit
) {
    val isHome = event.team == event.homeTeam

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isHome) {
            // Home team event
            Text(
                text = event.minute,
                style = FameTypography.matchClock.copy(fontSize = 14.sp),
                color = FameColors.ChampionsGold,
                modifier = Modifier.width(40.dp)
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when (event.type) {
                            "GOAL" -> FameColors.AfroSunOrange
                            "YELLOW" -> FameColors.AfroSunOrange.copy(alpha = 0.5f)
                            "RED" -> FameColors.KenteRed
                            "SUB" -> FameColors.AfricanLegendEmerald
                            else -> FameColors.SurfaceLight
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = event.icon,
                    style = FameTypography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.player,
                    style = FameTypography.bodySmall,
                    color = FameColors.WarmIvory
                )

                if (event.detail.isNotBlank()) {
                    Text(
                        text = event.detail,
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                }
            }
        } else {
            // Away team event (align right)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = event.player,
                    style = FameTypography.bodySmall,
                    color = FameColors.WarmIvory
                )

                if (event.detail.isNotBlank()) {
                    Text(
                        text = event.detail,
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when (event.type) {
                            "GOAL" -> FameColors.AfroSunOrange
                            "YELLOW" -> FameColors.AfroSunOrange.copy(alpha = 0.5f)
                            "RED" -> FameColors.KenteRed
                            "SUB" -> FameColors.AfricanLegendEmerald
                            else -> FameColors.SurfaceLight
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = event.icon,
                    style = FameTypography.bodyMedium
                )
            }

            Text(
                text = event.minute,
                style = FameTypography.matchClock.copy(fontSize = 14.sp),
                color = FameColors.ChampionsGold,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun MatchStatsView(
    stats: MatchStatsUiModel?,
    possession: Float,
    modifier: Modifier = Modifier
) {
    if (stats == null) return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            StatBar(
                label = "Possession",
                homeValue = possession.toInt(),
                awayValue = (100 - possession).toInt(),
                homeColor = FameColors.PitchGreen,
                awayColor = FameColors.KenteRed
            )
        }

        item {
            StatBar(
                label = "Shots",
                homeValue = stats.homeShots,
                awayValue = stats.awayShots,
                homeColor = FameColors.PitchGreen,
                awayColor = FameColors.KenteRed
            )
        }

        item {
            StatBar(
                label = "Shots on Target",
                homeValue = stats.homeShotsOnTarget,
                awayValue = stats.awayShotsOnTarget,
                homeColor = FameColors.PitchGreen,
                awayColor = FameColors.KenteRed
            )
        }

        item {
            StatBar(
                label = "Corners",
                homeValue = stats.homeCorners,
                awayValue = stats.awayCorners,
                homeColor = FameColors.PitchGreen,
                awayColor = FameColors.KenteRed
            )
        }

        item {
            StatBar(
                label = "Fouls",
                homeValue = stats.homeFouls,
                awayValue = stats.awayFouls,
                homeColor = FameColors.PitchGreen,
                awayColor = FameColors.KenteRed,
                reverseColors = true
            )
        }

        item {
            StatBar(
                label = "Yellow Cards",
                homeValue = stats.homeYellowCards,
                awayValue = stats.awayYellowCards,
                homeColor = FameColors.AfroSunOrange,
                awayColor = FameColors.AfroSunOrange
            )
        }

        item {
            StatBar(
                label = "Red Cards",
                homeValue = stats.homeRedCards,
                awayValue = stats.awayRedCards,
                homeColor = FameColors.KenteRed,
                awayColor = FameColors.KenteRed
            )
        }

        item {
            StatBar(
                label = "Offsides",
                homeValue = stats.homeOffsides,
                awayValue = stats.awayOffsides,
                homeColor = FameColors.MutedParchment,
                awayColor = FameColors.MutedParchment
            )
        }
    }
}

@Composable
fun StatBar(
    label: String,
    homeValue: Int,
    awayValue: Int,
    homeColor: Color,
    awayColor: Color,
    reverseColors: Boolean = false
) {
    val total = homeValue + awayValue
    val homePercent = if (total > 0) homeValue.toFloat() / total else 0.5f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = homeValue.toString(),
                style = FameTypography.bodyMedium,
                color = homeColor
            )

            Text(
                text = label,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )

            Text(
                text = awayValue.toString(),
                style = FameTypography.bodyMedium,
                color = awayColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(FameColors.SurfaceLight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(if (reverseColors) 1 - homePercent else homePercent)
                    .background(
                        if (reverseColors) awayColor else homeColor
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(if (reverseColors) homePercent else 1 - homePercent)
                    .background(
                        if (reverseColors) homeColor else awayColor
                    )
            )
        }
    }
}

@Composable
fun LineupsView(
    homeTeam: String,
    awayTeam: String,
    homeFormation: String,
    awayFormation: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            // Home Team Formation
            Text(
                text = homeTeam,
                style = FameTypography.titleMedium,
                color = FameColors.PitchGreen,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Formation: $homeFormation",
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Formation visualization placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                FameColors.PitchGreen,
                                FameColors.PitchGreen.copy(alpha = 0.3f)
                            )
                        )
                    )
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Away Team Formation
            Text(
                text = awayTeam,
                style = FameTypography.titleMedium,
                color = FameColors.KenteRed
            )

            Text(
                text = "Formation: $awayFormation",
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                FameColors.KenteRed,
                                FameColors.KenteRed.copy(alpha = 0.3f)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun HalfTimeScreen(
    match: MatchInfoUiModel?,
    stats: MatchStatsUiModel?,
    onStartSecondHalf: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (match == null) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FameColors.StadiumBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Half Time Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "HALF TIME",
                    style = FameTypography.matchHalfTime,
                    color = FameColors.ChampionsGold
                )

                Text(
                    text = match.stadium,
                    style = FameTypography.bodyMedium,
                    color = FameColors.MutedParchment
                )
            }

            // Score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 32.dp)
            ) {
                Text(
                    text = match.homeTeam,
                    style = FameTypography.titleLarge,
                    color = FameColors.WarmIvory
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "${match.homeScore} - ${match.awayScore}",
                    style = FameTypography.matchScore,
                    color = FameColors.ChampionsGold
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = match.awayTeam,
                    style = FameTypography.titleLarge,
                    color = FameColors.WarmIvory
                )
            }

            // Half Time Stats Summary
            if (stats != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = FameColors.SurfaceDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        HalfTimeStatRow(
                            label = "Possession",
                            homeValue = "${stats.homePossession}%",
                            awayValue = "${stats.awayPossession}%"
                        )

                        HalfTimeStatRow(
                            label = "Shots",
                            homeValue = stats.homeShots.toString(),
                            awayValue = stats.awayShots.toString()
                        )

                        HalfTimeStatRow(
                            label = "On Target",
                            homeValue = stats.homeShotsOnTarget.toString(),
                            awayValue = stats.awayShotsOnTarget.toString()
                        )

                        HalfTimeStatRow(
                            label = "Corners",
                            homeValue = stats.homeCorners.toString(),
                            awayValue = stats.awayCorners.toString()
                        )

                        HalfTimeStatRow(
                            label = "Fouls",
                            homeValue = stats.homeFouls.toString(),
                            awayValue = stats.awayFouls.toString()
                        )
                    }
                }
            }

            // Second Half Button
            Button(
                onClick = onStartSecondHalf,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FameColors.AfroSunOrange
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "START SECOND HALF",
                    style = FameTypography.titleMedium,
                    color = FameColors.WarmIvory
                )
            }
        }
    }
}

@Composable
fun HalfTimeStatRow(
    label: String,
    homeValue: String,
    awayValue: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = homeValue,
            style = FameTypography.bodySmall,
            color = FameColors.PitchGreen,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Start
        )

        Text(
            text = label,
            style = FameTypography.labelSmall,
            color = FameColors.MutedParchment,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Text(
            text = awayValue,
            style = FameTypography.bodySmall,
            color = FameColors.KenteRed,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun FullTimeScreen(
    match: MatchInfoUiModel?,
    stats: MatchStatsUiModel?,
    events: List<MatchEventUiModel>,
    onViewReport: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (match == null) return

    val winner = when {
        match.homeScore > match.awayScore -> match.homeTeam
        match.awayScore > match.homeScore -> match.awayTeam
        else -> null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FameColors.StadiumBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Full Time Title
            Text(
                text = "FULL TIME",
                style = FameTypography.matchFullTime,
                color = FameColors.ChampionsGold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Final Score
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = FameColors.SurfaceDark
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = match.homeTeam,
                            style = FameTypography.titleLarge,
                            color = if (winner == match.homeTeam) FameColors.ChampionsGold else FameColors.WarmIvory,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )

                        Text(
                            text = "${match.homeScore} - ${match.awayScore}",
                            style = FameTypography.matchScore,
                            color = FameColors.ChampionsGold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Text(
                            text = match.awayTeam,
                            style = FameTypography.titleLarge,
                            color = if (winner == match.awayTeam) FameColors.ChampionsGold else FameColors.WarmIvory,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start
                        )
                    }

                    if (winner != null) {
                        Text(
                            text = "$winner WINS!",
                            style = FameTypography.titleMedium,
                            color = FameColors.ChampionsGold
                        )
                    } else {
                        Text(
                            text = "IT'S A DRAW!",
                            style = FameTypography.titleMedium,
                            color = FameColors.MutedParchment
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Key Events
            if (events.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = FameColors.SurfaceDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "KEY MOMENTS",
                            style = FameTypography.labelMedium,
                            color = FameColors.AfroSunOrange,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        events
                            .filter { it.type == "GOAL" || it.type == "RED" }
                            .take(5)
                            .forEach { event ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = event.minute,
                                        style = FameTypography.labelSmall,
                                        color = FameColors.ChampionsGold,
                                        modifier = Modifier.width(40.dp)
                                    )

                                    Text(
                                        text = "${event.player} - ${event.detail}",
                                        style = FameTypography.bodySmall,
                                        color = FameColors.WarmIvory,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                    )
                                }
                            }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Summary
            if (stats != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = FameColors.SurfaceDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "STATISTICS",
                            style = FameTypography.labelMedium,
                            color = FameColors.PitchGreen,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        FullTimeStatRow(
                            label = "Possession",
                            homeValue = "${stats.homePossession}%",
                            awayValue = "${stats.awayPossession}%"
                        )

                        FullTimeStatRow(
                            label = "Shots",
                            homeValue = stats.homeShots.toString(),
                            awayValue = stats.awayShots.toString()
                        )

                        FullTimeStatRow(
                            label = "On Target",
                            homeValue = stats.homeShotsOnTarget.toString(),
                            awayValue = stats.awayShotsOnTarget.toString()
                        )

                        FullTimeStatRow(
                            label = "Corners",
                            homeValue = stats.homeCorners.toString(),
                            awayValue = stats.awayCorners.toString()
                        )

                        FullTimeStatRow(
                            label = "Fouls",
                            homeValue = stats.homeFouls.toString(),
                            awayValue = stats.awayFouls.toString()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onViewReport,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = FameColors.ChampionsGold,
                        borderColor = FameColors.ChampionsGold
                    )
                ) {
                    Text("Match Report")
                }

                Button(
                    onClick = onContinue,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FameColors.PitchGreen
                    )
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

@Composable
fun FullTimeStatRow(
    label: String,
    homeValue: String,
    awayValue: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = homeValue,
            style = FameTypography.bodySmall,
            color = FameColors.PitchGreen,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Start
        )

        Text(
            text = label,
            style = FameTypography.labelSmall,
            color = FameColors.MutedParchment,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Text(
            text = awayValue,
            style = FameTypography.bodySmall,
            color = FameColors.KenteRed,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun PausedScreen(
    onResume: () -> Unit,
    onSpeedChange: (Int) -> Unit,
    speed: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FameColors.StadiumBlack.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MATCH PAUSED",
                style = FameTypography.matchHalfTime,
                color = FameColors.ChampionsGold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Speed Controls
            SpeedControl(
                speed = speed,
                onSpeedChange = onSpeedChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Resume Button
            Button(
                onClick = onResume,
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FameColors.PitchGreen
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("RESUME")
            }
        }
    }
}