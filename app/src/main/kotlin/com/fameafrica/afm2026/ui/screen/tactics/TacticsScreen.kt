package com.fameafrica.afm2026.ui.screen.tactics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.fameafrica.afm2026.ui.theme.*

@Composable
fun TacticsScreen(
    onBack: () -> Unit,
    viewModel: TacticsViewModel = hiltViewModel(
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
            TacticsTopBar(
                onBack = onBack,
                onSave = viewModel::saveTactics,
                onReset = viewModel::resetTactics
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FameColors.StadiumBlack)
                .padding(paddingValues)
        ) {
            // Formation Selector
            FormationSelector(
                formations = uiState.formations,
                selectedFormation = uiState.selectedFormation,
                onFormationSelected = viewModel::selectFormation,
                modifier = Modifier.padding(12.dp)
            )

            // Tactical Style
            TacticalStyleCard(
                style = uiState.selectedStyle,
                onStyleChange = viewModel::updateStyle,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Tactical Sliders
            TacticalSliders(
                defensiveThreshold = uiState.defensiveThreshold,
                attackingThreshold = uiState.attackingThreshold,
                tempo = uiState.tempo,
                width = uiState.width,
                depth = uiState.depth,
                pressIntensity = uiState.pressIntensity,
                passingDirectness = uiState.passingDirectness,
                creativity = uiState.creativity,
                onValueChange = viewModel::updateSlider,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            // Pitch Preview
            PitchPreview(
                formation = uiState.selectedFormation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TacticsTopBar(
    onBack: () -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "TACTICS",
                style = FameTypography.titleLarge,
                color = FameColors.WarmIvory
            )
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
            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = FameColors.WarmIvory
                )
            }
            IconButton(onClick = onSave) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save",
                    tint = FameColors.PitchGreen
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = FameColors.StadiumBlack
        )
    )
}

@Composable
fun FormationSelector(
    formations: List<String>,
    selectedFormation: String,
    onFormationSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "FORMATION",
            style = FameTypography.labelMedium,
            color = FameColors.ChampionsGold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(formations) { formation ->
                FormationChip(
                    formation = formation,
                    isSelected = formation == selectedFormation,
                    onClick = { onFormationSelected(formation) }
                )
            }
        }
    }
}

@Composable
fun FormationChip(
    formation: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) FameColors.PitchGreen else FameColors.SurfaceDark
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
                imageVector = Icons.Default.SportsSoccer,
                contentDescription = null,
                tint = if (isSelected) FameColors.WarmIvory else FameColors.MutedParchment,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = formation,
                style = FameTypography.labelSmall,
                color = if (isSelected) FameColors.WarmIvory else FameColors.MutedParchment
            )
        }
    }
}

@Composable
fun TacticalStyleCard(
    style: String,
    onStyleChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "TACTICAL STYLE",
                style = FameTypography.labelMedium,
                color = FameColors.AfroSunOrange
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf(
                    "Possession", "Attacking", "Balanced",
                    "Counter", "Defensive", "Pressing"
                )) { styleOption ->
                    StyleChip(
                        style = styleOption,
                        isSelected = style == styleOption,
                        onClick = { onStyleChange(styleOption) }
                    )
                }
            }
        }
    }
}

@Composable
fun StyleChip(
    style: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = style,
                style = FameTypography.labelSmall,
                color = if (isSelected) FameColors.WarmIvory else FameColors.MutedParchment
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = FameColors.AfroSunOrange,
            selectedLabelColor = FameColors.WarmIvory,
            containerColor = FameColors.SurfaceLight,
            labelColor = FameColors.MutedParchment
        )
    )
}

@Composable
fun TacticalSliders(
    defensiveThreshold: Int,
    attackingThreshold: Int,
    tempo: Int,
    width: Int,
    depth: Int,
    pressIntensity: Int,
    passingDirectness: Int,
    creativity: Int,
    onValueChange: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        TacticalSlider(
            label = "Defensive Line",
            value = defensiveThreshold,
            onValueChange = { onValueChange("defensive", it) },
            leftLabel = "Deep",
            rightLabel = "High",
            color = FameColors.KenteRed
        )

        TacticalSlider(
            label = "Attacking Intent",
            value = attackingThreshold,
            onValueChange = { onValueChange("attacking", it) },
            leftLabel = "Cautious",
            rightLabel = "All-out",
            color = FameColors.ChampionsGold
        )

        TacticalSlider(
            label = "Tempo",
            value = tempo,
            onValueChange = { onValueChange("tempo", it) },
            leftLabel = "Slow",
            rightLabel = "Fast",
            color = FameColors.AfroSunOrange
        )

        TacticalSlider(
            label = "Width",
            value = width,
            onValueChange = { onValueChange("width", it) },
            leftLabel = "Narrow",
            rightLabel = "Wide",
            color = FameColors.PitchGreen
        )

        TacticalSlider(
            label = "Defensive Depth",
            value = depth,
            onValueChange = { onValueChange("depth", it) },
            leftLabel = "Deep",
            rightLabel = "High",
            color = FameColors.KenteRed
        )

        TacticalSlider(
            label = "Press Intensity",
            value = pressIntensity,
            onValueChange = { onValueChange("press", it) },
            leftLabel = "Stand-off",
            rightLabel = "Intense",
            color = FameColors.AfricanLegendEmerald
        )

        TacticalSlider(
            label = "Passing",
            value = passingDirectness,
            onValueChange = { onValueChange("passing", it) },
            leftLabel = "Short",
            rightLabel = "Direct",
            color = FameColors.ChampionsGold
        )

        TacticalSlider(
            label = "Creativity",
            value = creativity,
            onValueChange = { onValueChange("creativity", it) },
            leftLabel = "Disciplined",
            rightLabel = "Expressive",
            color = FameColors.AfroSunOrange
        )
    }
}

@Composable
fun TacticalSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    leftLabel: String,
    rightLabel: String,
    color: Color
) {
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
                text = label,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory
            )

            Text(
                text = value.toString(),
                style = FameTypography.bodyMedium,
                color = color
            )
        }

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = FameColors.SurfaceLight
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = leftLabel,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )

            Text(
                text = rightLabel,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )
        }
    }
}

@Composable
fun PitchPreview(
    formation: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            FameColors.PitchGreen,
                            FameColors.PitchGreen.copy(alpha = 0.7f)
                        )
                    )
                )
        ) {
            // Center circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            )

            // Center line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.Center)
                    .background(Color.White.copy(alpha = 0.3f))
            )

            // Formation label
            Text(
                text = formation,
                style = FameTypography.labelLarge,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}