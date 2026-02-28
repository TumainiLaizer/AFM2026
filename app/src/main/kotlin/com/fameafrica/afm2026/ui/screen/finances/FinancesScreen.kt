package com.fameafrica.afm2026.ui.screen.finances

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fameafrica.afm2026.ui.theme.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancesScreen(
    onBack: () -> Unit,
    onRenegotiateSponsor: (Int) -> Unit,
    onUpgradeInfrastructure: () -> Unit,
    viewModel: FinancesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gameContext by viewModel.gameContext.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "FINANCES",
                            style = FameTypography.titleLarge,
                            color = FameColors.WarmIvory
                        )
                        Text(
                            text = gameContext.season,
                            style = FameTypography.labelSmall,
                            color = FameColors.ChampionsGold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FameColors.WarmIvory
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = FameColors.WarmIvory
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FameColors.StadiumBlack
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FameColors.PitchGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FameColors.StadiumBlack)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
            ) {
                // Financial Summary Card
                item {
                    FinancialSummaryCard(
                        summary = uiState.financialSummary,
                        financialTier = uiState.financialTier
                    )
                }

                // Budget Overview Card
                item {
                    BudgetOverviewCard(
                        budget = uiState.budget,
                        bankBalance = uiState.bankBalance,
                        wageBill = uiState.wageBill
                    )
                }

                // Revenue Breakdown Card
                if (uiState.revenueBreakdown.isNotEmpty()) {
                    item {
                        BreakdownCard(
                            title = "REVENUE BREAKDOWN",
                            data = uiState.revenueBreakdown,
                            total = uiState.financialSummary?.revenue ?: 0,
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            color = FameColors.ChampionsGold
                        )
                    }
                }

                // Expense Breakdown Card
                if (uiState.expenseBreakdown.isNotEmpty()) {
                    item {
                        BreakdownCard(
                            title = "EXPENSE BREAKDOWN",
                            data = uiState.expenseBreakdown,
                            total = uiState.financialSummary?.expenses ?: 0,
                            icon = Icons.AutoMirrored.Filled.TrendingDown,
                            color = FameColors.KenteRed
                        )
                    }
                }

                // Profit/Loss Trend Chart (Bar Chart)
                if (uiState.profitLossHistory.isNotEmpty()) {
                    item {
                        ProfitLossBarChartCard(
                            history = uiState.profitLossHistory
                        )
                    }
                }

                // Sponsors Card
                if (uiState.sponsors.isNotEmpty()) {
                    item {
                        SponsorsCard(
                            sponsors = uiState.sponsors,
                            onRenegotiate = onRenegotiateSponsor
                        )
                    }
                }

                // Financial Health Indicators
                item {
                    FinancialHealthCard(
                        financialTier = uiState.financialTier,
                        financialHealth = uiState.financialHealth,
                        isProfitable = uiState.isProfitable
                    )
                }

                // League Comparison
                item {
                    LeagueComparisonCard(
                        teamRevenue = uiState.financialSummary?.revenue ?: 0,
                        leagueAverage = uiState.leagueAverageRevenue,
                        leagueHighest = uiState.leagueHighestRevenue
                    )
                }

                // Action Buttons
                item {
                    FinancialActionButtons(
                        onRenegotiateSponsors = { /* Navigate to all sponsors */ },
                        onUpgradeInfrastructure = onUpgradeInfrastructure,
                        onRequestBudget = { viewModel.requestBudgetIncrease() }
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialSummaryCard(
    summary: FinancialSummaryUiModel?,
    financialTier: String
) {
    if (summary == null) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingSmall),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = ComponentShapes.card
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingLarge)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tier Badge
                Box(
                    modifier = Modifier
                        .clip(ComponentShapes.badge)
                        .background(
                            when (financialTier) {
                                "Rich" -> FameColors.ChampionsGold.copy(alpha = 0.2f)
                                "Upper Middle" -> FameColors.PitchGreen.copy(alpha = 0.2f)
                                "Middle" -> FameColors.AfroSunOrange.copy(alpha = 0.2f)
                                "Lower" -> FameColors.KenteRed.copy(alpha = 0.2f)
                                else -> FameColors.SurfaceLight
                            }
                        )
                        .padding(horizontal = Dimensions.paddingMedium, vertical = Dimensions.paddingSmall)
                ) {
                    Text(
                        text = financialTier.uppercase(),
                        style = FameTypography.reputationBadge,
                        color = when (financialTier) {
                            "Rich" -> FameColors.ChampionsGold
                            "Upper Middle" -> FameColors.PitchGreen
                            "Middle" -> FameColors.AfroSunOrange
                            "Lower" -> FameColors.KenteRed
                            else -> FameColors.WarmIvory
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Profit/Loss Indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (summary.isProfitable) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = if (summary.isProfitable) FameColors.PitchGreen else FameColors.KenteRed,
                        modifier = Modifier.size(Dimensions.iconSmall)
                    )
                    Text(
                        text = if (summary.isProfitable) "Profitable" else "Loss",
                        style = FameTypography.labelSmall,
                        color = if (summary.isProfitable) FameColors.PitchGreen else FameColors.KenteRed,
                        modifier = Modifier.padding(start = Dimensions.paddingExtraSmall)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            // Main Financial Figures
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FinancialFigure(
                    label = "Revenue",
                    value = "€${summary.revenue / 1_000_000}M",
                    color = FameColors.ChampionsGold,
                    icon = Icons.AutoMirrored.Filled.TrendingUp
                )

                FinancialFigure(
                    label = "Expenses",
                    value = "€${summary.expenses / 1_000_000}M",
                    color = FameColors.KenteRed,
                    icon = Icons.AutoMirrored.Filled.TrendingDown
                )

                FinancialFigure(
                    label = "Profit",
                    value = "€${summary.profitLoss / 1_000_000}M",
                    color = if (summary.isProfitable) FameColors.PitchGreen else FameColors.KenteRed,
                    icon = Icons.Default.AccountBalance
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = Dimensions.paddingMedium),
                thickness = DividerDefaults.Thickness,
                color = FameColors.SurfaceLight
            )

            // Bank Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bank Balance",
                    style = FameTypography.bodyLarge,
                    color = FameColors.WarmIvory
                )

                Text(
                    text = "€${summary.bankBalance / 1_000_000}M",
                    style = FameTypography.moneyLarge,
                    color = FameColors.ChampionsGold
                )
            }
        }
    }
}

@Composable
fun FinancialFigure(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(Dimensions.iconMedium)
        )
        Text(
            text = value,
            style = FameTypography.moneyValue,
            color = color
        )
        Text(
            text = label,
            style = FameTypography.labelSmall,
            color = FameColors.MutedParchment
        )
    }
}

@Composable
fun BudgetOverviewCard(
    budget: Long,
    bankBalance: Long,
    wageBill: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingSmall),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = ComponentShapes.card
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingLarge)
        ) {
            Text(
                text = "BUDGET OVERVIEW",
                style = FameTypography.labelMedium,
                color = FameColors.PitchGreen
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            // Transfer Budget
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transfer Budget",
                    style = FameTypography.bodyLarge,
                    color = FameColors.WarmIvory
                )

                Text(
                    text = "€${budget / 1_000_000}M",
                    style = FameTypography.moneyValue,
                    color = FameColors.ChampionsGold
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            // Wage Bill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Wage Bill (Annual)",
                    style = FameTypography.bodyLarge,
                    color = FameColors.WarmIvory
                )

                Text(
                    text = "€${wageBill / 1_000_000}M",
                    style = FameTypography.moneyValue,
                    color = FameColors.AfroSunOrange
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            // Budget Usage Indicator (placeholder - would come from transfer spending)
            val budgetUsed = (budget * 0.7).toLong() // 70% used as example
            val usagePercentage = if (budget > 0) budgetUsed.toFloat() / budget.toFloat() else 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Budget Used",
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )

                Text(
                    text = "${(usagePercentage * 100).toInt()}%",
                    style = FameTypography.labelSmall,
                    color = FameColors.WarmIvory
                )
            }

            LinearProgressIndicator(
                progress = { usagePercentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(Dimensions.radiusSmall)),
                color = FameColors.PitchGreen,
                trackColor = FameColors.SurfaceLight
            )
        }
    }
}

@Composable
fun BreakdownCard(
    title: String,
    data: Map<String, Long>,
    total: Long,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingSmall),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = ComponentShapes.card
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingLarge)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(Dimensions.iconSmall)
                )
                Spacer(modifier = Modifier.width(Dimensions.paddingExtraSmall))
                Text(
                    text = title,
                    style = FameTypography.labelMedium,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            data.entries.forEach { entry ->
                val percentage = if (total > 0) {
                    (entry.value.toFloat() / total.toFloat() * 100).toInt()
                } else 0

                val itemColor = when (entry.key) {
                    "Sponsorship" -> FameColors.ChampionsGold
                    "Broadcasting" -> FameColors.PitchGreen
                    "Matchday" -> FameColors.AfroSunOrange
                    "Merchandise" -> FameColors.AfricanLegendEmerald
                    "Prize Money" -> FameColors.BaobabBrown
                    "Player Sales" -> FameColors.KenteRed
                    "Membership" -> FameColors.NationalSilver
                    "Player Wages" -> FameColors.AfroSunOrange
                    "Staff Wages" -> FameColors.KenteRed
                    "Transfer Spending" -> FameColors.ChampionsGold
                    "Infrastructure" -> FameColors.PitchGreen
                    "Operational" -> FameColors.BaobabBrown
                    else -> FameColors.WarmIvory
                }

                BreakdownRow(
                    label = entry.key,
                    amount = entry.value,
                    percentage = percentage,
                    color = itemColor
                )
            }
        }
    }
}

@Composable
fun BreakdownRow(
    label: String,
    amount: Long,
    percentage: Int,
    color: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.paddingExtraSmall)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(Dimensions.paddingExtraSmall))
                Text(
                    text = label,
                    style = FameTypography.bodySmall,
                    color = FameColors.WarmIvory
                )
            }

            Text(
                text = "€${amount / 1_000_000}M",
                style = FameTypography.moneyValue,
                color = color
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(Dimensions.radiusSmall)),
                color = color,
                trackColor = FameColors.SurfaceLight
            )

            Text(
                text = "$percentage%",
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment,
                modifier = Modifier.padding(start = Dimensions.paddingSmall)
            )
        }
    }
}

@Composable
fun ProfitLossBarChartCard(
    history: List<ProfitLossEntry>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingSmall),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = ComponentShapes.card
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingLarge)
        ) {
            Text(
                text = "PROFIT/LOSS TREND",
                style = FameTypography.labelMedium,
                color = FameColors.AfroSunOrange
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            // Bar Chart using MPAndroidChart
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                factory = { context ->
                    BarChart(context).apply {
                        description.isEnabled = false
                        legend.isEnabled = false

                        // Configure X axis
                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(false)
                            textColor = FameColors.MutedParchment.toArgb()
                            textSize = 12f
                            granularity = 1f
                            valueFormatter = IndexAxisValueFormatter(
                                history.map { it.label }
                            )
                        }

// Configure Y axis (left)
                        axisLeft.apply {
                            setDrawGridLines(true)
                            gridColor = FameColors.SurfaceLight.toArgb()
                            textColor = FameColors.MutedParchment.toArgb()
                            // Let the chart determine the minimum value automatically to handle negative values (losses)
                            // axisMinimum = 0f // Remove this line

                            // Create an anonymous object that inherits from ValueFormatter
                            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                                override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
                                    // Format the float value as an integer with "M" for millions
                                    return "€${value.toInt()}M"
                                }
                            }
                        }

                        // Disable right axis
                        axisRight.isEnabled = false

                        // Chart styling
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        setDrawValueAboveBar(true)
                        setFitBars(true)
                    }
                },
                update = { chart ->
                    // Create bar entries
                    val entries = history.mapIndexed { index, entry ->
                        BarEntry(
                            index.toFloat(),
                            (entry.amount / 1_000_000f).coerceAtLeast(0f) // Use absolute value for bar height
                        )
                    }

                    // Create data set
                    val dataSet = BarDataSet(entries, "Profit/Loss").apply {
                        colors = history.map { entry ->
                            if (entry.amount >= 0) {
                                FameColors.PitchGreen.toArgb()
                            } else {
                                FameColors.KenteRed.toArgb()
                            }
                        }
                        valueTextColor = FameColors.WarmIvory.toArgb()
                        valueTextSize = 10f
                    }

                    // Create and set data
                    val barData = BarData(dataSet)
                    barData.barWidth = 0.7f
                    chart.data = barData
                    chart.invalidate()
                }
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(FameColors.PitchGreen)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.paddingExtraSmall))
                    Text(
                        text = "Profit",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                }

                Spacer(modifier = Modifier.width(Dimensions.paddingMedium))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(FameColors.KenteRed)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.paddingExtraSmall))
                    Text(
                        text = "Loss",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            Text(
                text = "Last 5 Seasons",
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun SponsorsCard(
    sponsors: List<SponsorUiModel>,
    onRenegotiate: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingSmall),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = ComponentShapes.card
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingLarge)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SPONSORS",
                    style = FameTypography.labelMedium,
                    color = FameColors.ChampionsGold
                )

                TextButton(onClick = { /* View all sponsors */ }) {
                    Text(
                        text = "View All",
                        style = FameTypography.labelSmall,
                        color = FameColors.AfroSunOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            sponsors.take(3).forEach { sponsor ->
                SponsorItem(
                    sponsor = sponsor,
                    onRenegotiate = { onRenegotiate(sponsor.id) }
                )
            }

            if (sponsors.size > 3) {
                Text(
                    text = "+${sponsors.size - 3} more sponsors",
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment,
                    modifier = Modifier.padding(top = Dimensions.paddingSmall)
                )
            }
        }
    }
}

@Composable
fun SponsorItem(
    sponsor: SponsorUiModel,
    onRenegotiate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.paddingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(Dimensions.radiusSmall))
                .background(FameColors.SurfaceLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = sponsor.name.take(1),
                style = FameTypography.titleLarge,
                color = FameColors.WarmIvory
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Dimensions.paddingSmall)
        ) {
            Text(
                text = sponsor.name,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = sponsor.type,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "€${sponsor.annualValue / 1_000_000}M",
                style = FameTypography.moneyValue,
                color = FameColors.ChampionsGold
            )

            Text(
                text = "${sponsor.yearsRemaining} yrs left",
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )
        }

        IconButton(
            onClick = onRenegotiate,
            modifier = Modifier.size(Dimensions.iconMedium)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Renegotiate",
                tint = FameColors.AfroSunOrange,
                modifier = Modifier.size(Dimensions.iconSmall)
            )
        }
    }
}

@Composable
fun FinancialHealthCard(
    financialTier: String,
    financialHealth: String,
    isProfitable: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingSmall),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = ComponentShapes.card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "FINANCIAL HEALTH",
                    style = FameTypography.labelMedium,
                    color = FameColors.PitchGreen
                )

                Spacer(modifier = Modifier.height(Dimensions.paddingExtraSmall))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                when (financialHealth) {
                                    "Rich" -> FameColors.ChampionsGold
                                    "Healthy" -> FameColors.PitchGreen
                                    "Stable" -> FameColors.AfroSunOrange
                                    "Breaking Even" -> FameColors.BaobabBrown
                                    else -> FameColors.KenteRed
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(Dimensions.paddingExtraSmall))
                    Text(
                        text = financialHealth,
                        style = FameTypography.bodyLarge,
                        color = FameColors.WarmIvory
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Tier: $financialTier",
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )

                Text(
                    text = if (isProfitable) "Operating Profit" else "Operating Loss",
                    style = FameTypography.labelSmall,
                    color = if (isProfitable) FameColors.PitchGreen else FameColors.KenteRed
                )
            }
        }
    }
}

@Composable
fun LeagueComparisonCard(
    teamRevenue: Long,
    leagueAverage: Long,
    leagueHighest: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingSmall),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = ComponentShapes.card
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingLarge)
        ) {
            Text(
                text = "LEAGUE COMPARISON",
                style = FameTypography.labelMedium,
                color = FameColors.AfricanLegendEmerald
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            ComparisonBar(
                label = "Your Revenue",
                value = teamRevenue,
                maxValue = leagueHighest,
                color = FameColors.ChampionsGold
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            ComparisonBar(
                label = "League Average",
                value = leagueAverage,
                maxValue = leagueHighest,
                color = FameColors.MutedParchment
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            ComparisonBar(
                label = "League Highest",
                value = leagueHighest,
                maxValue = leagueHighest,
                color = FameColors.PitchGreen
            )
        }
    }
}

@Composable
fun ComparisonBar(
    label: String,
    value: Long,
    maxValue: Long,
    color: Color
) {
    val percentage = if (maxValue > 0) (value.toFloat() / maxValue.toFloat()) else 0f

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )

            Text(
                text = "€${value / 1_000_000}M",
                style = FameTypography.moneyValue,
                color = color
            )
        }

        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(Dimensions.radiusSmall)),
            color = color,
            trackColor = FameColors.SurfaceLight
        )
    }
}

@Composable
fun FinancialActionButtons(
    onRenegotiateSponsors: () -> Unit,
    onUpgradeInfrastructure: () -> Unit,
    onRequestBudget: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingMedium),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
    ) {
        OutlinedButton(
            onClick = onRenegotiateSponsors,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = FameColors.ChampionsGold
            ),
            border = BorderStroke(1.dp, FameColors.ChampionsGold),
            shape = ComponentShapes.button
        ) {
            Icon(
                imageVector = Icons.Default.Handshake,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.iconSmall)
            )
            Spacer(modifier = Modifier.width(Dimensions.paddingExtraSmall))
            Text("Sponsors", style = FameTypography.labelMedium)
        }

        OutlinedButton(
            onClick = onUpgradeInfrastructure,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = FameColors.PitchGreen
            ),
            border = BorderStroke(1.dp, FameColors.PitchGreen),
            shape = ComponentShapes.button
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.iconSmall)
            )
            Spacer(modifier = Modifier.width(Dimensions.paddingExtraSmall))
            Text("Upgrade", style = FameTypography.labelMedium)
        }

        Button(
            onClick = onRequestBudget,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = FameColors.AfroSunOrange
            ),
            shape = ComponentShapes.button
        ) {
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.iconSmall)
            )
            Spacer(modifier = Modifier.width(Dimensions.paddingExtraSmall))
            Text("Request", style = FameTypography.labelMedium)
        }
    }
}

// ============ PREVIEW ============

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FinancesScreenPreview() {
    AFM2026Theme {
        FinancesScreen(
            onBack = {},
            onRenegotiateSponsor = {},
            onUpgradeInfrastructure = {}
        )
    }
}