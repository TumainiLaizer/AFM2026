package com.fameafrica.afm2026.ui.screen.training

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.fameafrica.afm2026.ui.theme.*

@Composable
fun TrainingScreen(
    onBack: () -> Unit,
    viewModel: TrainingViewModel = hiltViewModel(
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
            TrainingTopBar(
                onBack = onBack,
                onSchedule = { /* Open schedule */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FameColors.StadiumBlack)
                .padding(paddingValues)
        ) {
            // Training Stats
            TrainingStatsCard(
                overallProgress = uiState.overallProgress,
                averageMorale = uiState.averageMorale,
                injuryRisk = uiState.injuryRisk,
                fitnessLevel = uiState.fitnessLevel,
                modifier = Modifier.padding(12.dp)
            )

            // Drill Categories
            DrillCategories(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = viewModel::selectCategory,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Current Training Session
            if (uiState.currentSession != null) {
                CurrentTrainingCard(
                    session = uiState.currentSession!!,
                    onComplete = viewModel::completeDrill,
                    onCancel = viewModel::cancelDrill,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Available Drills
            Text(
                text = "AVAILABLE DRILLS",
                style = FameTypography.labelMedium,
                color = FameColors.ChampionsGold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.drills) { drill ->
                    DrillCard(
                        drill = drill,
                        onStart = { viewModel.startDrill(drill.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingTopBar(
    onBack: () -> Unit,
    onSchedule: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "TRAINING CENTER",
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
            IconButton(onClick = onSchedule) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Schedule",
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
fun TrainingStatsCard(
    overallProgress: Int,
    averageMorale: Int,
    injuryRisk: Int,
    fitnessLevel: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TrainingStatItem(
                value = "$overallProgress%",
                label = "Progress",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                color = FameColors.PitchGreen
            )

            TrainingStatItem(
                value = "$averageMorale%",
                label = "Morale",
                icon = Icons.Default.Favorite,
                color = FameColors.ChampionsGold
            )

            TrainingStatItem(
                value = "$injuryRisk%",
                label = "Injury Risk",
                icon = Icons.Default.Warning,
                color = if (injuryRisk < 30) FameColors.PitchGreen else FameColors.KenteRed
            )

            TrainingStatItem(
                value = "$fitnessLevel%",
                label = "Fitness",
                icon = Icons.Default.FitnessCenter,
                color = FameColors.AfricanLegendEmerald
            )
        }
    }
}

@Composable
fun TrainingStatItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
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

@Composable
fun DrillCategories(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(listOf("ALL", "TECHNICAL", "TACTICAL", "PHYSICAL", "MENTAL", "GOALKEEPING")) { category ->
            val isSelected = category == selectedCategory

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category,
                        style = FameTypography.labelSmall,
                        color = if (isSelected) FameColors.PitchGreen else FameColors.MutedParchment
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = FameColors.PitchGreen.copy(alpha = 0.1f),
                    selectedLabelColor = FameColors.PitchGreen,
                    containerColor = FameColors.SurfaceDark,
                    labelColor = FameColors.MutedParchment
                )
            )
        }
    }
}

@Composable
fun CurrentTrainingCard(
    session: TrainingSessionUiModel,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.AfroSunOrange.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = FameColors.AfroSunOrange,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = "CURRENT SESSION",
                    style = FameTypography.labelMedium,
                    color = FameColors.AfroSunOrange,
                    modifier = Modifier.padding(start = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(FameColors.AfroSunOrange)
                )

                Text(
                    text = " LIVE",
                    style = FameTypography.labelSmall,
                    color = FameColors.AfroSunOrange
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = session.drillName,
                style = FameTypography.bodyLarge,
                color = FameColors.WarmIvory
            )

            Text(
                text = "${session.playersInvolved} players • Focus: ${session.focus}",
                style = FameTypography.bodySmall,
                color = FameColors.MutedParchment,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )

                    Text(
                        text = "${session.progress}%",
                        style = FameTypography.bodySmall,
                        color = FameColors.AfroSunOrange
                    )
                }

                LinearProgressIndicator(
                    progress = { session.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = FameColors.AfroSunOrange,
                    trackColor = FameColors.SurfaceLight
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FameColors.PitchGreen
                    )
                ) {
                    Text("Complete Session")
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = FameColors.KenteRed
                    ),
                    border = BorderStroke(1.dp, FameColors.KenteRed)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun DrillCard(
    drill: DrillUiModel,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStart() },
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drill icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when (drill.category) {
                            "TECHNICAL" -> FameColors.PitchGreen.copy(alpha = 0.2f)
                            "TACTICAL" -> FameColors.ChampionsGold.copy(alpha = 0.2f)
                            "PHYSICAL" -> FameColors.AfroSunOrange.copy(alpha = 0.2f)
                            "MENTAL" -> FameColors.AfricanLegendEmerald.copy(alpha = 0.2f)
                            else -> FameColors.KenteRed.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (drill.category) {
                        "TECHNICAL" -> Icons.Default.SportsSoccer
                        "TACTICAL" -> Icons.Default.Schedule
                        "PHYSICAL" -> Icons.Default.FitnessCenter
                        "MENTAL" -> Icons.Default.Psychology
                        else -> Icons.Default.HealthAndSafety
                    },
                    contentDescription = null,
                    tint = when (drill.category) {
                        "TECHNICAL" -> FameColors.PitchGreen
                        "TACTICAL" -> FameColors.ChampionsGold
                        "PHYSICAL" -> FameColors.AfroSunOrange
                        "MENTAL" -> FameColors.AfricanLegendEmerald
                        else -> FameColors.KenteRed
                    }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = drill.name,
                    style = FameTypography.bodySmall,
                    color = FameColors.WarmIvory
                )

                Text(
                    text = "${drill.duration} min • Focus: ${drill.focus}",
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )

                Text(
                    text = "Improves: ${drill.attributes}",
                    style = FameTypography.labelSmall,
                    color = FameColors.ChampionsGold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (drill.difficulty) {
                                "Easy" -> FameColors.PitchGreen
                                "Medium" -> FameColors.AfroSunOrange
                                "Hard" -> FameColors.KenteRed
                                else -> FameColors.MutedParchment
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = drill.difficulty.take(1),
                        style = FameTypography.labelSmall,
                        color = FameColors.WarmIvory
                    )
                }

                Text(
                    text = "Risk: ${drill.injuryRisk}%",
                    style = FameTypography.labelSmall,
                    color = if (drill.injuryRisk < 20) FameColors.PitchGreen else FameColors.KenteRed
                )
            }
        }
    }
}