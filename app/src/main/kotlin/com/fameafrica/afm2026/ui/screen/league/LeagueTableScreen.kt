package com.fameafrica.afm2026.ui.screen.league

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.fameafrica.afm2026.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueTableScreen(
    leagueName: String,
    onBack: () -> Unit,
    viewModel: LeagueTableViewModel = hiltViewModel(
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
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = leagueName,
                            style = FameTypography.titleLarge,
                            color = FameColors.WarmIvory
                        )
                        Text(
                            text = "Season ${uiState.season}",
                            style = FameTypography.labelSmall,
                            color = FameColors.MutedParchment
                        )
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
                    IconButton(onClick = { /* Refresh */ }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FameColors.StadiumBlack)
                .padding(paddingValues)
        ) {
            // Table Header
            LeagueTableHeader()

            // Table Rows
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(uiState.standings) { index, team ->
                    LeagueTableRow(
                        position = index + 1,
                        team = team,
                        isUserTeam = team.id == uiState.userTeamId
                    )
                }
            }
        }
    }
}

@Composable
fun LeagueTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FameColors.SurfaceDark)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#",
            style = FameTypography.labelSmall,
            color = FameColors.ChampionsGold,
            modifier = Modifier.width(32.dp)
        )

        Text(
            text = "Team",
            style = FameTypography.labelSmall,
            color = FameColors.ChampionsGold,
            modifier = Modifier.weight(2f)
        )

        Text(
            text = "P",
            style = FameTypography.labelSmall,
            color = FameColors.ChampionsGold,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "W",
            style = FameTypography.labelSmall,
            color = FameColors.ChampionsGold,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "D",
            style = FameTypography.labelSmall,
            color = FameColors.ChampionsGold,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "L",
            style = FameTypography.labelSmall,
            color = FameColors.ChampionsGold,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "GD",
            style = FameTypography.labelSmall,
            color = FameColors.ChampionsGold,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Pts",
            style = FameTypography.labelSmall,
            color = FameColors.ChampionsGold,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Form",
            style = FameTypography.labelSmall,
            color = FameColors.ChampionsGold,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LeagueTableRow(
    position: Int,
    team: LeagueStandingUiModel,
    isUserTeam: Boolean
) {
    val backgroundColor = if (isUserTeam) {
        FameColors.PitchGreen.copy(alpha = 0.1f)
    } else if (position % 2 == 0) {
        FameColors.SurfaceDark
    } else {
        FameColors.StadiumBlack
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Position with color coding
        Text(
            text = position.toString(),
            style = FameTypography.bodySmall,
            color = when (position) {
                1 -> FameColors.ChampionsGold
                2 -> FameColors.NationalSilver
                3 -> FameColors.LocalBronze
                in 17..20 -> FameColors.KenteRed
                else -> FameColors.WarmIvory
            },
            modifier = Modifier.width(32.dp)
        )

        // Team Name
        Text(
            text = team.name,
            style = FameTypography.bodySmall,
            color = if (isUserTeam) FameColors.PitchGreen else FameColors.WarmIvory,
            modifier = Modifier.weight(2f),
            maxLines = 1
        )

        // Stats
        Text(
            text = team.played.toString(),
            style = FameTypography.bodySmall,
            color = FameColors.WarmIvory,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = team.wins.toString(),
            style = FameTypography.bodySmall,
            color = FameColors.PitchGreen,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = team.draws.toString(),
            style = FameTypography.bodySmall,
            color = FameColors.AfroSunOrange,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = team.losses.toString(),
            style = FameTypography.bodySmall,
            color = FameColors.KenteRed,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )

        // Goal Difference
        val gdColor = when {
            team.gd > 0 -> FameColors.PitchGreen
            team.gd < 0 -> FameColors.KenteRed
            else -> FameColors.WarmIvory
        }
        Text(
            text = team.gd.toString(),
            style = FameTypography.bodySmall,
            color = gdColor,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center
        )

        // Points
        Text(
            text = team.points.toString(),
            style = FameTypography.bodyLarge,
            color = FameColors.ChampionsGold,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center
        )

        // Form
        Row(
            modifier = Modifier.width(80.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            team.form.take(5).forEach { result ->
                FormBadge(result = result)
            }
        }
    }
}

@Composable
fun FormBadge(result: Char) {
    val (color, text) = when (result) {
        'W' -> FameColors.PitchGreen to "W"
        'D' -> FameColors.AfroSunOrange to "D"
        'L' -> FameColors.KenteRed to "L"
        else -> FameColors.MutedParchment to "-"
    }

    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = FameTypography.labelSmall,
            color = FameColors.WarmIvory,
            fontSize = 8.sp
        )
    }
}