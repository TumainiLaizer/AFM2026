package com.fameafrica.afm2026.ui.screen.squad

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toDrawable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fameafrica.afm2026.ui.theme.*
import com.fameafrica.afm2026.ui.viewmodel.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailScreen(
    playerId: Int,
    onBack: () -> Unit,
    viewModel: PlayerDetailViewModel = hiltViewModel()
) {
    // Load player data when screen is opened
    LaunchedEffect(playerId) {
        viewModel.loadPlayer(playerId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.player?.name ?: "Player",
                            style = FameTypography.titleLarge,
                            color = FameColors.WarmIvory
                        )
                        uiState.player?.position?.let {
                            Text(
                                text = it,
                                style = FameTypography.labelSmall,
                                color = when (uiState.player?.position) {
                                    "GK" -> FameColors.ChampionsGold
                                    "DEF" -> FameColors.PitchGreen
                                    "MID" -> FameColors.AfroSunOrange
                                    else -> FameColors.KenteRed
                                }
                            )
                        }
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
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
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
                    .padding(paddingValues)
            ) {
                // Player Header Card
                item {
                    PlayerHeaderCard(player = uiState.player)
                }

                // Quick Stats Row
                item {
                    PlayerQuickStats(player = uiState.player)
                }

                // Intelligent Radar Chart
                item {
                    PlayerRadarChartCard(
                        attributes = uiState.attributes,
                        playerPosition = uiState.player?.position
                    )
                }

                // Attributes List
                uiState.attributes?.let {
                    item {
                        PlayerAttributesList(attributes = it)
                    }
                }

                // Form Trend Chart (now using MPAndroidChart)
                if (uiState.formHistory.isNotEmpty()) {
                    item {
                        PlayerFormChart(formHistory = uiState.formHistory)
                    }
                }

                // Season Stats
                uiState.seasonStats?.let {
                    item {
                        PlayerSeasonStatsCard(stats = it)
                    }
                }

                // Contract Info
                uiState.contract?.let {
                    item {
                        PlayerContractCard(contract = it)
                    }
                }

                // Injury History
                if (uiState.injuryHistory.isNotEmpty()) {
                    item {
                        InjuryHistoryCard(injuries = uiState.injuryHistory)
                    }
                }

                // Actions
                item {
                    PlayerActionButtons(
                        onTransfer = { /* Navigate to transfer */ },
                        onLoan = { /* Navigate to loan */ },
                        onContract = { /* Navigate to contract renewal */ }
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerHeaderCard(
    player: PlayerDetailUiModel?
) {
    if (player == null) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                // Large Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    when (player.position) {
                                        "GK" -> FameColors.ChampionsGold
                                        "DEF" -> FameColors.PitchGreen
                                        "MID" -> FameColors.AfroSunOrange
                                        else -> FameColors.KenteRed
                                    }.copy(alpha = 0.3f),
                                    FameColors.SurfaceLight
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.name.take(1),
                        style = FameTypography.displayLarge,
                        color = when (player.position) {
                            "GK" -> FameColors.ChampionsGold
                            "DEF" -> FameColors.PitchGreen
                            "MID" -> FameColors.AfroSunOrange
                            else -> FameColors.KenteRed
                        }
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Nationality and Age
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Flag placeholder
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(FameColors.SurfaceLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = player.nationality.take(2),
                                style = FameTypography.labelSmall,
                                color = FameColors.MutedParchment
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${player.nationality} • ${player.age} years",
                            style = FameTypography.bodySmall,
                            color = FameColors.MutedParchment
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Shirt Number and Position
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(FameColors.SurfaceLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${player.shirtNumber}",
                                style = FameTypography.labelSmall,
                                color = FameColors.WarmIvory
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = player.position,
                            style = FameTypography.bodyMedium,
                            color = when (player.position) {
                                "GK" -> FameColors.ChampionsGold
                                "DEF" -> FameColors.PitchGreen
                                "MID" -> FameColors.AfroSunOrange
                                else -> FameColors.KenteRed
                            }
                        )
                    }
                }

                // Overall Rating
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        FameColors.PitchGreen,
                                        FameColors.ChampionsGold,
                                        FameColors.AfroSunOrange,
                                        FameColors.PitchGreen
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = player.overallRating.toString(),
                            style = FameTypography.displayMedium,
                            color = FameColors.WarmIvory
                        )
                    }

                    Text(
                        text = "OVR",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                }
            }

            // Potential and Value
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Potential: ${player.potential}",
                    style = FameTypography.labelSmall,
                    color = if (player.potential > player.overallRating)
                        FameColors.ChampionsGold else FameColors.MutedParchment
                )

                Text(
                    text = "Value: €${player.marketValue / 1_000_000}M",
                    style = FameTypography.labelSmall,
                    color = FameColors.ChampionsGold
                )
            }

            // Captain/Vice Captain badge
            if (player.isCaptain || player.isViceCaptain) {
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (player.isCaptain) FameColors.ChampionsGold.copy(alpha = 0.1f)
                            else FameColors.MutedParchment.copy(alpha = 0.1f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (player.isCaptain) "CAPTAIN" else "VICE CAPTAIN",
                        style = FameTypography.labelSmall,
                        color = if (player.isCaptain) FameColors.ChampionsGold else FameColors.MutedParchment
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerQuickStats(
    player: PlayerDetailUiModel?
) {
    if (player == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickStatBox(
            value = player.appearances.toString(),
            label = "Apps",
            color = FameColors.WarmIvory,
            modifier = Modifier.weight(1f)
        )

        QuickStatBox(
            value = player.goals.toString(),
            label = "Goals",
            color = FameColors.ChampionsGold,
            modifier = Modifier.weight(1f)
        )

        QuickStatBox(
            value = player.assists.toString(),
            label = "Assists",
            color = FameColors.AfroSunOrange,
            modifier = Modifier.weight(1f)
        )

        QuickStatBox(
            value = player.form.toString(),
            label = "Form",
            color = when {
                player.form >= 75 -> FameColors.PitchGreen
                player.form >= 50 -> FameColors.AfroSunOrange
                else -> FameColors.KenteRed
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickStatBox(
    value: String,
    label: String,
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
            Text(
                text = value,
                style = FameTypography.titleLarge,
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

@Composable
fun PlayerRadarChartCard(
    attributes: PlayerAttributesUiModel?,
    playerPosition: String?
) {
    if (attributes == null || playerPosition == null) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val positionCategory = when (playerPosition) {
                "GK" -> "GOALKEEPER"
                in listOf("CB", "LB", "RB", "SW", "LWB", "RWB") -> "DEFENDER"
                in listOf("CDM", "CM", "CAM", "LM", "RM") -> "MIDFIELDER"
                else -> "FORWARD"
            }

            val chartColor = when (positionCategory) {
                "GOALKEEPER" -> FameColors.ChampionsGold
                "DEFENDER" -> FameColors.PitchGreen
                "MIDFIELDER" -> FameColors.AfroSunOrange
                else -> FameColors.KenteRed
            }

            Text(
                text = "$positionCategory ATTRIBUTES",
                style = FameTypography.labelMedium,
                color = chartColor
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            val attributesToShow = when (positionCategory) {
                "GOALKEEPER" -> listOf(
                    "Reflexes" to attributes.reflexes,
                    "Handling" to attributes.handling,
                    "Aerial" to attributes.aerialAbility,
                    "Command" to attributes.commandOfArea,
                    "Kicking" to attributes.kicking,
                    "Positioning" to attributes.positioning
                )
                "DEFENDER" -> listOf(
                    "Defending" to attributes.defending,
                    "Heading" to attributes.heading,
                    "Strength" to attributes.strength,
                    "Pace" to attributes.pace,
                    "Positioning" to attributes.positioning,
                    "Tackling" to (attributes.defending + (0..3).random())
                )
                "MIDFIELDER" -> listOf(
                    "Passing" to attributes.passing,
                    "Vision" to attributes.vision,
                    "Dribbling" to attributes.dribbling,
                    "Stamina" to attributes.stamina,
                    "Creativity" to attributes.creativity,
                    "Decisions" to attributes.decisions
                )
                else -> listOf(
                    "Finishing" to attributes.finishing,
                    "Pace" to attributes.pace,
                    "Dribbling" to attributes.dribbling,
                    "Composure" to attributes.composure,
                    "Heading" to attributes.heading,
                    "Long Shots" to attributes.longShots
                )
            }

            val entries = attributesToShow.map { RadarEntry(it.second.toFloat()) }
            val labels = attributesToShow.map { it.first }

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                factory = { context ->
                    RadarChart(context).apply {
                        description.isEnabled = false
                        legend.isEnabled = false
                        isRotationEnabled = false

                        webLineWidth = 1f
                        webLineWidthInner = 1f
                        webAlpha = 100

                        yAxis.apply {
                            setDrawLabels(false)
                            axisMinimum = 0f
                            axisMaximum = 100f
                            setLabelCount(5, false)
                        }

                        xAxis.apply {
                            textColor = FameColors.WarmIvory.toArgb()
                            textSize = 12f
                            setDrawGridLines(false)
                            position = XAxis.XAxisPosition.BOTTOM
                            valueFormatter = IndexAxisValueFormatter(labels)
                        }

                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }
                },
                update = { chart ->
                    val dataSet = RadarDataSet(entries, "Attributes").apply {
                        color = chartColor.toArgb()
                        valueTextColor = FameColors.WarmIvory.toArgb()
                        lineWidth = 2f
                        setDrawFilled(true)
                        fillColor = chartColor.toArgb()
                        fillAlpha = 100
                        setDrawValues(false)
                    }

                    chart.data = RadarData(dataSet)
                    chart.invalidate()
                }
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(chartColor)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Key $positionCategory Attributes",
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )
            }
        }
    }
}

@Composable
fun PlayerAttributesList(
    attributes: PlayerAttributesUiModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "ALL ATTRIBUTES",
                style = FameTypography.labelMedium,
                color = FameColors.ChampionsGold
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            // Technical Attributes
            AttributeSection(
                title = "Technical",
                attributes = listOf(
                    "Finishing" to attributes.finishing,
                    "Passing" to attributes.passing,
                    "Dribbling" to attributes.dribbling,
                    "Crossing" to attributes.crossing,
                    "Heading" to attributes.heading,
                    "Long Shots" to attributes.longShots,
                    "Defending" to attributes.defending,
                    "Tackling" to (attributes.defending + (0..3).random())
                )
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            // Physical Attributes
            AttributeSection(
                title = "Physical",
                attributes = listOf(
                    "Pace" to attributes.pace,
                    "Stamina" to attributes.stamina,
                    "Strength" to attributes.strength,
                    "Acceleration" to attributes.acceleration,
                    "Agility" to attributes.agility
                )
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            // Mental Attributes
            AttributeSection(
                title = "Mental",
                attributes = listOf(
                    "Composure" to attributes.composure,
                    "Decisions" to attributes.decisions,
                    "Leadership" to attributes.leadership,
                    "Vision" to attributes.vision,
                    "Work Rate" to attributes.workRate,
                    "Positioning" to attributes.positioning,
                    "Anticipation" to attributes.anticipation,
                    "Creativity" to attributes.creativity,
                    "Teamwork" to attributes.teamwork
                )
            )

            // Goalkeeper Attributes (if applicable)
            if (attributes.goalkeeping > 0) {
                Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

                AttributeSection(
                    title = "Goalkeeping",
                    attributes = listOf(
                        "Goalkeeping" to attributes.goalkeeping,
                        "Reflexes" to attributes.reflexes,
                        "Handling" to attributes.handling,
                        "Aerial" to attributes.aerialAbility,
                        "Command" to attributes.commandOfArea,
                        "Kicking" to attributes.kicking
                    )
                )
            }
        }
    }
}

@Composable
fun AttributeSection(
    title: String,
    attributes: List<Pair<String, Int>>
) {
    Column {
        Text(
            text = title,
            style = FameTypography.playerAttributeLabel,
            color = FameColors.MutedParchment,
            modifier = Modifier.padding(bottom = Dimensions.paddingSmall)
        )

        attributes.chunked(2).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimensions.paddingExtraSmall),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
            ) {
                row.forEach { (name, value) ->
                    AttributeRow(
                        name = name,
                        value = value,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (row.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun AttributeRow(
    name: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = FameTypography.playerAttributeLabel,
            color = FameColors.MutedParchment
        )

        Box(
            modifier = Modifier
                .width(40.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    when {
                        value >= 80 -> FameColors.ChampionsGold
                        value >= 60 -> FameColors.AfroSunOrange
                        else -> FameColors.SurfaceLight
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                style = FameTypography.playerAttribute,
                color = if (value >= 60) FameColors.StadiumBlack else FameColors.WarmIvory
            )
        }
    }
}

@Composable
fun PlayerFormChart(
    formHistory: List<Int>
) {
    if (formHistory.isEmpty()) return

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
                text = "FORM TREND",
                style = FameTypography.labelMedium,
                color = FameColors.AfroSunOrange
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            // Create line chart with MPAndroidChart
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                factory = { context ->
                    LineChart(context).apply {
                        description.isEnabled = false
                        legend.isEnabled = false

                        // Configure axis
                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(false)
                            textColor = FameColors.MutedParchment.toArgb()
                            setLabelCount(5, true)
                            valueFormatter = object : IndexAxisValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    return (value + 1).toInt().toString()
                                }
                            }
                        }

                        axisLeft.apply {
                            setDrawGridLines(true)
                            gridColor = FameColors.SurfaceLight.toArgb()
                            textColor = FameColors.MutedParchment.toArgb()
                            axisMinimum = 0f
                            axisMaximum = 100f
                            setLabelCount(5, true)
                        }

                        axisRight.isEnabled = false

                        setBackgroundColor(android.graphics.Color.TRANSPARENT)

                        // Enable touch gestures
                        setTouchEnabled(true)
                        isDragEnabled = true
                        setScaleEnabled(true)
                    }
                },
                update = { chart ->
                    // Create entries
                    val entries = formHistory.mapIndexed { index, value ->
                        Entry(index.toFloat(), value.toFloat())
                    }

                    // Create data set
                    val dataSet = LineDataSet(entries, "Form").apply {
                        color = FameColors.AfroSunOrange.toArgb()
                        setCircleColor(FameColors.AfroSunOrange.toArgb())
                        lineWidth = 2f
                        circleRadius = 4f
                        setDrawCircleHole(false)
                        setDrawValues(false)
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        cubicIntensity = 0.2f

                        // Fill under line
                        setDrawFilled(true)
                        fillDrawable =
                            FameColors.AfroSunOrange.copy(alpha = 0.3f).toArgb().toDrawable()
                    }

                    // Create data
                    val lineData = LineData(dataSet)
                    chart.data = lineData
                    chart.invalidate()
                }
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            Text(
                text = "Last ${formHistory.size} matches",
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun PlayerSeasonStatsCard(
    stats: SeasonStatsUiModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "SEASON STATISTICS",
                style = FameTypography.labelMedium,
                color = FameColors.PitchGreen
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingLarge)
            ) {
                StatColumn(
                    label = "Matches",
                    value = stats.matches.toString(),
                    modifier = Modifier.weight(1f)
                )

                StatColumn(
                    label = "Goals",
                    value = stats.goals.toString(),
                    color = FameColors.ChampionsGold,
                    modifier = Modifier.weight(1f)
                )

                StatColumn(
                    label = "Assists",
                    value = stats.assists.toString(),
                    color = FameColors.AfroSunOrange,
                    modifier = Modifier.weight(1f)
                )

                StatColumn(
                    label = "MOTM",
                    value = stats.manOfMatch.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingLarge)
            ) {
                StatColumn(
                    label = "Yellow",
                    value = stats.yellowCards.toString()
                )

                StatColumn(
                    label = "Red",
                    value = stats.redCards.toString()
                )

                StatColumn(
                    label = "Pass %",
                    value = "${stats.passAccuracy}%"
                )

                StatColumn(
                    label = "Tackles",
                    value = stats.tackles.toString()
                )
            }

            if (stats.cleanSheets > 0) {
                Spacer(modifier = Modifier.height(Dimensions.paddingSmall))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    StatColumn(
                        label = "Clean Sheets",
                        value = stats.cleanSheets.toString(),
                        color = FameColors.PitchGreen
                    )
                }
            }
        }
    }
}

@Composable
fun StatColumn(
    label: String,
    value: String,
    color: Color = FameColors.WarmIvory,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = value,
            style = FameTypography.statValue,
            color = color
        )

        Text(
            text = label,
            style = FameTypography.statLabel,
            color = FameColors.MutedParchment
        )
    }
}

@Composable
fun PlayerContractCard(
    contract: ContractUiModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "CONTRACT DETAILS",
                style = FameTypography.labelMedium,
                color = FameColors.BaobabBrown
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Salary",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )

                    Text(
                        text = "€${contract.salary / 1_000_000}M p/a",
                        style = FameTypography.moneyValue,
                        color = FameColors.ChampionsGold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Expires",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )

                    Text(
                        text = contract.expiry,
                        style = FameTypography.bodyLarge,
                        color = if (contract.isExpiring) FameColors.AfroSunOrange else FameColors.WarmIvory
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Release Clause",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )

                    Text(
                        text = "€${contract.releaseClause / 1_000_000}M",
                        style = FameTypography.moneyValue,
                        color = FameColors.WarmIvory
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Agent",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )

                    Text(
                        text = "Unknown",
                        style = FameTypography.bodyMedium,
                        color = FameColors.WarmIvory
                    )
                }
            }
        }
    }
}

@Composable
fun InjuryHistoryCard(
    injuries: List<InjuryUiModel>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "INJURY HISTORY",
                style = FameTypography.labelMedium,
                color = FameColors.KenteRed
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            if (injuries.isEmpty()) {
                Text(
                    text = "No injury history",
                    style = FameTypography.bodyMedium,
                    color = FameColors.MutedParchment,
                    modifier = Modifier.padding(vertical = Dimensions.paddingSmall)
                )
            } else {
                injuries.forEach { injury ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimensions.paddingExtraSmall),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = injury.type,
                                style = FameTypography.bodySmall,
                                color = when (injury.severity) {
                                    "MINOR" -> FameColors.AfroSunOrange
                                    "MODERATE" -> FameColors.KenteRed
                                    else -> FameColors.KenteRed.copy(alpha = 0.7f)
                                }
                            )

                            Text(
                                text = injury.date,
                                style = FameTypography.labelSmall,
                                color = FameColors.MutedParchment
                            )
                        }

                        Text(
                            text = "${injury.days} days",
                            style = FameTypography.bodySmall,
                            color = FameColors.WarmIvory
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerActionButtons(
    onTransfer: () -> Unit,
    onLoan: () -> Unit,
    onContract: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onTransfer,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = FameColors.ChampionsGold
            ),
            border = BorderStroke(1.dp, FameColors.ChampionsGold),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Transfer")
        }

        OutlinedButton(
            onClick = onLoan,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = FameColors.AfroSunOrange
            ),
            border = BorderStroke(1.dp, FameColors.AfroSunOrange),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Loan")
        }

        Button(
            onClick = onContract,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = FameColors.PitchGreen
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Contract")
        }
    }
}