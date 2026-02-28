package com.fameafrica.afm2026.ui.screen.club

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import coil.compose.AsyncImage
import com.fameafrica.afm2026.ui.theme.*
//import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
//import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
//import com.patrykandpatrick.vico.compose.chart.Chart
//import com.patrykandpatrick.vico.compose.chart.line.lineChart
//import com.patrykandpatrick.vico.compose.chart.line.lineSpec
//import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable
fun ClubScreen(
    onFinancesClick: () -> Unit,
    onInfrastructureClick: () -> Unit,
    onHistoryClick: () -> Unit,
    viewModel: ClubViewModel = hiltViewModel(
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
            ClubTopBar(
                clubName = uiState.clubName,
                reputationLevel = uiState.reputationLevel,
                onNotificationClick = { /* Navigate to notifications */ }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FameColors.StadiumBlack)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Club Header Card
            item {
                ClubHeaderCard(
                    club = uiState.clubInfo,
                    onFinancesClick = onFinancesClick,
                    onInfrastructureClick = onInfrastructureClick,
                    onHistoryClick = onHistoryClick
                )
            }

            // Financial Overview
            item {
                FinancialOverviewCard(
                    finances = uiState.finances,
                    onViewAll = onFinancesClick
                )
            }

            // Infrastructure Status
            item {
                InfrastructureStatusCard(
                    infrastructure = uiState.infrastructure,
                    onViewAll = onInfrastructureClick
                )
            }

            // Revenue Breakdown Chart
            item {
                RevenueBreakdownCard(
                    revenueData = uiState.revenueBreakdown
                )
            }

            // Sponsors Card
            item {
                SponsorsCard(
                    sponsors = uiState.sponsors,
                    onSponsorClick = { /* Navigate to sponsor details */ }
                )
            }

            // Upgrades in Progress
            if (uiState.activeUpgrades.isNotEmpty()) {
                item {
                    ActiveUpgradesCard(
                        upgrades = uiState.activeUpgrades,
                        onUpgradeClick = { /* Navigate to upgrade details */ }
                    )
                }
            }

            // Recent History
            item {
                RecentHistoryCard(
                    history = uiState.recentHistory,
                    onViewAll = onHistoryClick
                )
            }

            // Club Legends
            if (uiState.legends.isNotEmpty()) {
                item {
                    ClubLegendsCard(
                        legends = uiState.legends,
                        onLegendClick = { /* Navigate to legend details */ }
                    )
                }
            }

            // Quick Stats Row
            item {
                QuickStatsRow(
                    stats = uiState.quickStats
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubTopBar(
    clubName: String,
    reputationLevel: String,
    onNotificationClick: () -> Unit
) {
    val backgroundColor = when (reputationLevel) {
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
                    style = FameTypography.labelSmall,
                    color = backgroundColor
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {}) {
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
            containerColor = FameColors.StadiumBlack,
            titleContentColor = FameColors.WarmIvory
        )
    )
}

@Composable
fun ClubHeaderCard(
    club: ClubInfoUiModel?,
    onFinancesClick: () -> Unit,
    onInfrastructureClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    if (club == null) return

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Club Logo
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    FameColors.PitchGreen,
                                    FameColors.SurfaceLight
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = club.logoUrl,
                        contentDescription = club.name,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Club Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = club.name,
                        style = FameTypography.titleLarge,
                        color = FameColors.WarmIvory
                    )

                    Text(
                        text = club.league,
                        style = FameTypography.bodyMedium,
                        color = FameColors.MutedParchment
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = FameColors.MutedParchment,
                            modifier = Modifier.size(14.dp)
                        )

                        Text(
                            text = "${club.stadium} • ${club.stadiumCapacity} seats",
                            style = FameTypography.labelSmall,
                            color = FameColors.MutedParchment,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }

                // Reputation Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            when (club.reputationLevel) {
                                "World Class" -> FameColors.ChampionsGold.copy(alpha = 0.2f)
                                "Continental" -> FameColors.AfricanLegendEmerald.copy(alpha = 0.2f)
                                "National" -> FameColors.NationalSilver.copy(alpha = 0.2f)
                                else -> FameColors.LocalBronze.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = club.reputationLevel,
                        style = FameTypography.labelSmall,
                        color = when (club.reputationLevel) {
                            "World Class" -> FameColors.ChampionsGold
                            "Continental" -> FameColors.AfricanLegendEmerald
                            "National" -> FameColors.NationalSilver
                            else -> FameColors.LocalBronze
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ClubActionButton(
                    icon = Icons.Default.TrendingUp,
                    label = "Finances",
                    color = FameColors.ChampionsGold,
                    onClick = onFinancesClick,
                    modifier = Modifier.weight(1f)
                )

                ClubActionButton(
                    icon = Icons.Default.Build,
                    label = "Infrastructure",
                    color = FameColors.PitchGreen,
                    onClick = onInfrastructureClick,
                    modifier = Modifier.weight(1f)
                )

                ClubActionButton(
                    icon = Icons.Default.History,
                    label = "History",
                    color = FameColors.BaobabBrown,
                    onClick = onHistoryClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ClubActionButton(
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = label,
                style = FameTypography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
fun FinancialOverviewCard(
    finances: FinancialUiModel?,
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
                    text = "FINANCIAL OVERVIEW",
                    style = FameTypography.labelMedium,
                    color = FameColors.ChampionsGold
                )

                TextButton(onClick = onViewAll) {
                    Text(
                        text = "Details",
                        style = FameTypography.labelSmall,
                        color = FameColors.ChampionsGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (finances != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FinancialMetric(
                        label = "Revenue",
                        value = "€${finances.revenue / 1_000_000}M",
                        change = finances.revenueChange,
                        color = FameColors.PitchGreen,
                        modifier = Modifier.weight(1f)
                    )

                    FinancialMetric(
                        label = "Expenses",
                        value = "€${finances.expenses / 1_000_000}M",
                        change = finances.expensesChange,
                        color = FameColors.KenteRed,
                        modifier = Modifier.weight(1f)
                    )

                    FinancialMetric(
                        label = "Profit",
                        value = "€${finances.profit / 1_000_000}M",
                        change = finances.profitChange,
                        color = if (finances.profit >= 0) FameColors.AfricanLegendEmerald else FameColors.KenteRed,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Budget Bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Transfer Budget",
                            style = FameTypography.labelSmall,
                            color = FameColors.MutedParchment
                        )

                        Text(
                            text = "€${finances.budget / 1_000_000}M",
                            style = FameTypography.bodyMedium,
                            color = FameColors.WarmIvory
                        )
                    }

                    LinearProgressIndicator(
                        progress = finances.budgetUsed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = FameColors.PitchGreen,
                        trackColor = FameColors.SurfaceLight
                    )

                    Text(
                        text = "${(finances.budgetUsed * 100).toInt()}% of season budget used",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialMetric(
    label: String,
    value: String,
    change: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = FameTypography.labelSmall,
            color = FameColors.MutedParchment
        )

        Text(
            text = value,
            style = FameTypography.bodyLarge,
            color = color
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (change >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = if (change >= 0) FameColors.PitchGreen else FameColors.KenteRed,
                modifier = Modifier.size(12.dp)
            )

            Text(
                text = "${String.format("%.1f", change)}%",
                style = FameTypography.labelSmall,
                color = if (change >= 0) FameColors.PitchGreen else FameColors.KenteRed
            )
        }
    }
}

@Composable
fun InfrastructureStatusCard(
    infrastructure: InfrastructureUiModel?,
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
                    text = "INFRASTRUCTURE",
                    style = FameTypography.labelMedium,
                    color = FameColors.PitchGreen
                )

                TextButton(onClick = onViewAll) {
                    Text(
                        text = "Upgrade",
                        style = FameTypography.labelSmall,
                        color = FameColors.PitchGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (infrastructure != null) {
                InfrastructureItem(
                    label = "Stadium",
                    level = infrastructure.stadiumLevel,
                    maxLevel = 5,
                    value = infrastructure.stadiumCapacity,
                    unit = "seats",
                    color = FameColors.ChampionsGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfrastructureItem(
                    label = "Training Facility",
                    level = infrastructure.trainingLevel,
                    maxLevel = 5,
                    value = infrastructure.trainingEfficiency,
                    unit = "%",
                    color = FameColors.AfroSunOrange
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfrastructureItem(
                    label = "Youth Academy",
                    level = infrastructure.youthLevel,
                    maxLevel = 5,
                    value = infrastructure.youthTalent,
                    unit = "%",
                    color = FameColors.AfricanLegendEmerald
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfrastructureItem(
                    label = "Medical Center",
                    level = infrastructure.medicalLevel,
                    maxLevel = 5,
                    value = infrastructure.injuryRecovery,
                    unit = "%",
                    color = FameColors.KenteRed
                )
            }
        }
    }
}

@Composable
fun InfrastructureItem(
    label: String,
    level: Int,
    maxLevel: Int,
    value: Int,
    unit: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(maxLevel) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < level) color
                                else FameColors.SurfaceLight
                            )
                            .padding(end = if (index < maxLevel - 1) 2.dp else 0.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Level $level",
                    style = FameTypography.labelSmall,
                    color = color
                )
            }
        }

        Text(
            text = "$value $unit",
            style = FameTypography.bodyMedium,
            color = color
        )
    }
}

@Composable
fun RevenueBreakdownCard(
    revenueData: List<RevenueItemUiModel>?
) {
    if (revenueData.isNullOrEmpty()) return

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
                text = "REVENUE BREAKDOWN",
                style = FameTypography.labelMedium,
                color = FameColors.AfricanLegendEmerald
            )

            Spacer(modifier = Modifier.height(12.dp))

            revenueData.forEach { item ->
                RevenueItem(item = item)
            }
        }
    }
}

@Composable
fun RevenueItem(
    item: RevenueItemUiModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(item.color)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = item.label,
            style = FameTypography.bodySmall,
            color = FameColors.WarmIvory,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "€${item.amount / 1_000_000}M",
            style = FameTypography.bodyMedium,
            color = item.color
        )

        Text(
            text = "(${item.percentage}%)",
            style = FameTypography.labelSmall,
            color = FameColors.MutedParchment,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun SponsorsCard(
    sponsors: List<SponsorUiModel>?,
    onSponsorClick: (Int) -> Unit
) {
    if (sponsors.isNullOrEmpty()) return

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
                text = "SPONSORS",
                style = FameTypography.labelMedium,
                color = FameColors.ChampionsGold
            )

            Spacer(modifier = Modifier.height(12.dp))

            sponsors.take(3).forEach { sponsor ->
                SponsorItem(
                    sponsor = sponsor,
                    onClick = { onSponsorClick(sponsor.id) }
                )
            }

            if (sponsors.size > 3) {
                TextButton(
                    onClick = { /* View all sponsors */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "View all ${sponsors.size} sponsors",
                        style = FameTypography.labelSmall,
                        color = FameColors.ChampionsGold
                    )
                }
            }
        }
    }
}

@Composable
fun SponsorItem(
    sponsor: SponsorUiModel,
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
            AsyncImage(
                model = sponsor.logoUrl,
                contentDescription = sponsor.name,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = sponsor.name,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory
            )

            Text(
                text = sponsor.type,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )
        }

        Text(
            text = "€${sponsor.value / 1_000_000}M",
            style = FameTypography.bodyMedium,
            color = FameColors.ChampionsGold
        )
    }
}

@Composable
fun ActiveUpgradesCard(
    upgrades: List<UpgradeUiModel>,
    onUpgradeClick: (Int) -> Unit
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
                text = "ACTIVE UPGRADES",
                style = FameTypography.labelMedium,
                color = FameColors.AfroSunOrange
            )

            Spacer(modifier = Modifier.height(12.dp))

            upgrades.forEach { upgrade ->
                UpgradeItem(
                    upgrade = upgrade,
                    onClick = { onUpgradeClick(upgrade.id) }
                )
            }
        }
    }
}

@Composable
fun UpgradeItem(
    upgrade: UpgradeUiModel,
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
                .clip(CircleShape)
                .background(
                    when (upgrade.type) {
                        "STADIUM" -> FameColors.ChampionsGold.copy(alpha = 0.2f)
                        "TRAINING_FACILITY" -> FameColors.AfroSunOrange.copy(alpha = 0.2f)
                        "YOUTH_ACADEMY" -> FameColors.AfricanLegendEmerald.copy(alpha = 0.2f)
                        "MEDICAL_CENTER" -> FameColors.KenteRed.copy(alpha = 0.2f)
                        else -> FameColors.PitchGreen.copy(alpha = 0.2f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (upgrade.type) {
                    "STADIUM" -> Icons.Default.Place
                    "TRAINING_FACILITY" -> Icons.Default.FitnessCenter
                    "YOUTH_ACADEMY" -> Icons.Default.School
                    "MEDICAL_CENTER" -> Icons.Default.LocalHospital
                    else -> Icons.Default.Build
                },
                contentDescription = null,
                tint = when (upgrade.type) {
                    "STADIUM" -> FameColors.ChampionsGold
                    "TRAINING_FACILITY" -> FameColors.AfroSunOrange
                    "YOUTH_ACADEMY" -> FameColors.AfricanLegendEmerald
                    "MEDICAL_CENTER" -> FameColors.KenteRed
                    else -> FameColors.PitchGreen
                }
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = upgrade.name,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory
            )

            Text(
                text = "Level ${upgrade.currentLevel} → ${upgrade.targetLevel}",
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${upgrade.progress}%",
                style = FameTypography.bodyMedium,
                color = FameColors.AfroSunOrange
            )

            Text(
                text = upgrade.remainingDays,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )
        }
    }
}

@Composable
fun RecentHistoryCard(
    history: List<HistoryUiModel>?,
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
                    text = "CLUB HISTORY",
                    style = FameTypography.labelMedium,
                    color = FameColors.BaobabBrown
                )

                TextButton(onClick = onViewAll) {
                    Text(
                        text = "View All",
                        style = FameTypography.labelSmall,
                        color = FameColors.BaobabBrown
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!history.isNullOrEmpty()) {
                history.take(3).forEach { item ->
                    HistoryItem(item = item)
                }
            } else {
                Text(
                    text = "No history records yet",
                    style = FameTypography.bodyMedium,
                    color = FameColors.MutedParchment,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun HistoryItem(
    item: HistoryUiModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    when (item.type) {
                        "Trophy" -> FameColors.ChampionsGold.copy(alpha = 0.2f)
                        "League" -> FameColors.PitchGreen.copy(alpha = 0.2f)
                        else -> FameColors.SurfaceLight
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (item.type) {
                    "Trophy" -> Icons.Default.EmojiEvents
                    "League" -> Icons.Default.Star
                    else -> Icons.Default.History
                },
                contentDescription = null,
                tint = when (item.type) {
                    "Trophy" -> FameColors.ChampionsGold
                    "League" -> FameColors.PitchGreen
                    else -> FameColors.MutedParchment
                }
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = item.title,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory
            )

            Text(
                text = item.season,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )
        }

        Text(
            text = item.achievement,
            style = FameTypography.bodyMedium,
            color = when (item.type) {
                "Trophy" -> FameColors.ChampionsGold
                "League" -> FameColors.PitchGreen
                else -> FameColors.WarmIvory
            }
        )
    }
}

@Composable
fun ClubLegendsCard(
    legends: List<LegendUiModel>,
    onLegendClick: (Int) -> Unit
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
                text = "CLUB LEGENDS",
                style = FameTypography.labelMedium,
                color = FameColors.ChampionsGold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(legends) { legend ->
                    LegendItem(
                        legend = legend,
                        onClick = { onLegendClick(legend.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(
    legend: LegendUiModel,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            FameColors.ChampionsGold.copy(alpha = 0.3f),
                            FameColors.SurfaceLight
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = legend.name.take(1),
                style = FameTypography.displayMedium,
                color = FameColors.ChampionsGold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = legend.name,
            style = FameTypography.bodySmall,
            color = FameColors.WarmIvory,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = legend.era,
            style = FameTypography.labelSmall,
            color = FameColors.MutedParchment
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = FameColors.ChampionsGold,
                modifier = Modifier.size(12.dp)
            )

            Text(
                text = "${legend.titles} titles",
                style = FameTypography.labelSmall,
                color = FameColors.ChampionsGold
            )
        }
    }
}

@Composable
fun QuickStatsRow(
    stats: QuickStatsUiModel?
) {
    if (stats == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickStatCard(
            value = stats.leaguePosition.toString(),
            label = "League Pos",
            icon = Icons.Default.Leaderboard,
            color = FameColors.ChampionsGold,
            modifier = Modifier.weight(1f)
        )

        QuickStatCard(
            value = stats.overallStars.toString(),
            label = "Reputation",
            icon = Icons.Default.Star,
            color = FameColors.PitchGreen,
            modifier = Modifier.weight(1f)
        )

        QuickStatCard(
            value = stats.fanLoyalty.toString(),
            label = "Fan Loyalty",
            icon = Icons.Default.Favorite,
            color = when {
                stats.fanLoyalty >= 80 -> FameColors.ChampionsGold
                stats.fanLoyalty >= 50 -> FameColors.AfroSunOrange
                else -> FameColors.KenteRed
            },
            modifier = Modifier.weight(1f)
        )

        QuickStatCard(
            value = "${stats.seasons} seasons",
            label = "Founded",
            icon = Icons.Default.Schedule,
            color = FameColors.BaobabBrown,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickStatCard(
    value: String,
    label: String,
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
                style = FameTypography.bodyLarge,
                color = color
            )

            Text(
                text = label,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )
        }
    }
}