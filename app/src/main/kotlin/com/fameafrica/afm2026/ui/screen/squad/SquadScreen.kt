package com.fameafrica.afm2026.ui.screen.squad

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.fameafrica.afm2026.ui.theme.*
import com.fameafrica.afm2026.ui.viewmodel.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquadScreen(
    onPlayerClick: (Int) -> Unit,
    onTacticsClick: () -> Unit,
    onTrainingClick: () -> Unit,
    viewModel: SquadViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FameColors.StadiumBlack)
    ) {
        // Header
        SquadStatsHeader(
            stats = uiState.squadStats,
            formation = uiState.formation,
            onTacticsClick = onTacticsClick,
            onTrainingClick = onTrainingClick
        )

        // Search Bar
        SquadSearchBar(
            query = uiState.searchQuery,
            onQueryChange = viewModel::updateSearchQuery,
            onSearch = { viewModel.filterPlayers() }
        )

        // Filter Tabs
        SquadFilterTabs(
            selectedTab = uiState.selectedTab,
            onTabSelected = viewModel::selectTab,
            injuredCount = uiState.squadStats.injuredCount,
            suspendedCount = uiState.squadStats.suspendedCount
        )

        // Sort Bar
        SquadSortBar(
            sortBy = uiState.sortBy,
            sortAscending = uiState.sortAscending,
            onSortClick = { viewModel.updateSortOption(it) }
        )

        // Player Count and Total Value
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${uiState.filteredPlayers.size} Players",
                style = FameTypography.labelMedium,
                color = FameColors.MutedParchment
            )

            Text(
                text = "Total Value: €${uiState.squadStats.totalMarketValue / 1_000_000}M",
                style = FameTypography.labelMedium,
                color = FameColors.ChampionsGold
            )
        }

        // Player List
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FameColors.PitchGreen)
            }
        } else if (uiState.filteredPlayers.isEmpty()) {
            EmptySquadView(
                onClearFilters = {
                    viewModel.selectTab("ALL")
                    viewModel.updateSearchQuery("")
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimensions.paddingLarge),
                verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
            ) {
                items(
                    items = uiState.filteredPlayers,
                    key = { it.id }
                ) { player ->
                    PlayerCard(
                        player = player,
                        onClick = { onPlayerClick(player.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
                }
            }
        }
    }
}

@Composable
fun SquadStatsHeader(
    stats: SquadStatsUiModel,
    formation: String,
    onTacticsClick: () -> Unit,
    onTrainingClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.paddingLarge),
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
                // Quick Stats
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatPill(
                        value = stats.totalPlayers.toString(),
                        label = "Players",
                        color = FameColors.WarmIvory
                    )

                    Spacer(modifier = Modifier.width(Dimensions.paddingSmall))

                    StatPill(
                        value = String.format(Locale.US, "%.1f", stats.averageRating),
                        label = "Avg Rating",
                        color = FameColors.ChampionsGold
                    )

                    Spacer(modifier = Modifier.width(Dimensions.paddingSmall))

                    StatPill(
                        value = String.format(Locale.US, "%.1f", stats.averageAge),
                        label = "Avg Age",
                        color = FameColors.AfroSunOrange
                    )
                }

                // Formation
                Box(
                    modifier = Modifier
                        .clip(ComponentShapes.badge)
                        .background(FameColors.PitchGreen.copy(alpha = 0.2f))
                        .padding(horizontal = Dimensions.paddingMedium, vertical = Dimensions.paddingSmall)
                ) {
                    Text(
                        text = formation,
                        style = FameTypography.labelMedium,
                        color = FameColors.PitchGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            // Position Distribution
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PositionDistribution(
                    count = stats.goalkeepers,
                    label = "GK",
                    color = FameColors.ChampionsGold
                )

                PositionDistribution(
                    count = stats.defenders,
                    label = "DEF",
                    color = FameColors.PitchGreen
                )

                PositionDistribution(
                    count = stats.midfielders,
                    label = "MID",
                    color = FameColors.AfroSunOrange
                )

                PositionDistribution(
                    count = stats.forwards,
                    label = "FWD",
                    color = FameColors.KenteRed
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            // Average Height and Injury/Suspension Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Average Height
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = null,
                        tint = FameColors.MutedParchment,
                        modifier = Modifier.size(Dimensions.iconSmall)
                    )
                    Text(
                        text = "Avg Ht: ${String.format(Locale.US, "%.1f", stats.averageHeight)}cm",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment,
                        modifier = Modifier.padding(start = Dimensions.paddingExtraSmall)
                    )
                }

                // Injury/Suspension Summary
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (stats.injuredCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(FameColors.KenteRed)
                        )
                        Text(
                            text = " ${stats.injuredCount} Injured",
                            style = FameTypography.labelSmall,
                            color = FameColors.MutedParchment
                        )
                    }
                    if (stats.suspendedCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(FameColors.AfroSunOrange)
                        )
                        Text(
                            text = " ${stats.suspendedCount} Susp",
                            style = FameTypography.labelSmall,
                            color = FameColors.MutedParchment
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
            ) {
                OutlinedButton(
                    onClick = onTacticsClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = FameColors.PitchGreen
                    ),
                    border = BorderStroke(1.dp, FameColors.PitchGreen),
                    shape = ComponentShapes.button
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = null,
                        modifier = Modifier.size(Dimensions.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.paddingExtraSmall))
                    Text("Tactics", style = FameTypography.labelMedium)
                }

                OutlinedButton(
                    onClick = onTrainingClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = FameColors.AfroSunOrange
                    ),
                    border = BorderStroke(1.dp, FameColors.AfroSunOrange),
                    shape = ComponentShapes.button
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(Dimensions.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.paddingExtraSmall))
                    Text("Training", style = FameTypography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun StatPill(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = FameTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
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
fun PositionDistribution(
    count: Int,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                style = FameTypography.bodyMedium,
                color = color
            )
        }
        Text(
            text = label,
            style = FameTypography.labelSmall,
            color = FameColors.MutedParchment,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquadSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
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
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search players...",
                    style = FameTypography.bodyMedium,
                    color = FameColors.DisabledText
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = FameColors.MutedParchment
                )
            },
            trailingIcon = if (query.isNotEmpty()) {
                {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = FameColors.MutedParchment
                        )
                    }
                }
            } else {
                null
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch() }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Transparent,
                unfocusedContainerColor = Transparent,
                disabledContainerColor = Transparent,
                cursorColor = FameColors.PitchGreen,
                focusedIndicatorColor = Transparent,
                unfocusedIndicatorColor = Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquadFilterTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    injuredCount: Int,
    suspendedCount: Int
) {
    val tabs = listOf(
        "ALL", "GK", "DEF", "MID", "FWD", "INJURED", "SUSPENDED"
    )
    val selectedTabIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)

    SecondaryScrollableTabRow(
        selectedTabIndex,
        Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingSmall),
        Transparent,
        TabRowDefaults.primaryContentColor,
        0.dp,
        { tabPositions: List<TabPosition> ->  // Explicit type declaration
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    height = 2.dp,
                    color = FameColors.PitchGreen
                )
            },
        @Composable { HorizontalDivider() } as TabIndicatorScope.() -> Unit,
        {
            tabs.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = tab)
                            if (tab == "INJURED" && injuredCount > 0) {
                                Badge(count = injuredCount)
                            } else if (tab == "SUSPENDED" && suspendedCount > 0) {
                                Badge(count = suspendedCount)
                            }
                        }
                    },
                    selectedContentColor = FameColors.PitchGreen,
                    unselectedContentColor = FameColors.MutedParchment
                )
            }
        })
}

fun SecondaryScrollableTabRow(
    selectedTabIndex: Int,
    modifier: androidx.compose.ui.Modifier,
    scrollState: androidx.compose.ui.graphics.Color,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor2: androidx.compose.ui.unit.Dp,
    edgePadding: @Composable (kotlin.collections.List<androidx.compose.material3.TabPosition>) -> Unit,
    indicator: androidx.compose.material3.TabIndicatorScope.() -> Unit,
    divider: @Composable () -> Unit
) {
}

@Composable
private fun Badge(count: Int) {
    Box(
        modifier = Modifier
            .padding(start = 4.dp)
            .size(16.dp)
            .clip(CircleShape)
            .background(FameColors.KenteRed),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            color = Color.White,
            style = FameTypography.labelSmall
        )
    }
}

@Composable
fun SquadSortBar(
    sortBy: SortOption,
    sortAscending: Boolean,
    onSortClick: (SortOption) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingSmall),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Sort by:",
            style = FameTypography.labelMedium,
            color = FameColors.MutedParchment
        )

        SortChip(SortOption.RATING, sortBy, onSortClick)
        SortChip(SortOption.AGE, sortBy, onSortClick)
        SortChip(SortOption.VALUE, sortBy, onSortClick)

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = { /* Toggle sort direction */ }) {
            Icon(
                imageVector = if (sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = "Sort direction",
                tint = FameColors.MutedParchment
            )
        }
    }
}

@Composable
fun SortChip(
    option: SortOption,
    currentSort: SortOption,
    onClick: (SortOption) -> Unit
) {
    val selected = option == currentSort

    Box(
        modifier = Modifier
            .clip(ComponentShapes.chip)
            .background(if (selected) FameColors.SurfaceLight else Transparent)
            .clickable { onClick(option) }
            .padding(horizontal = Dimensions.paddingMedium, vertical = Dimensions.paddingSmall)
    ) {
        Text(
            text = option.name,
            style = FameTypography.labelMedium,
            color = if (selected) FameColors.WarmIvory else FameColors.MutedParchment
        )
    }
}

@Composable
fun PlayerCard(
    player: PlayerUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = ComponentShapes.card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player Info (Avatar, Name, Position)
            Box(
                modifier = Modifier
                    .size(Dimensions.avatarLarge)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                when (player.positionCategory) {
                                    "GK" -> FameColors.ChampionsGold.copy(alpha = 0.3f)
                                    "DEF" -> FameColors.PitchGreen.copy(alpha = 0.3f)
                                    "MID" -> FameColors.AfroSunOrange.copy(alpha = 0.3f)
                                    else -> FameColors.KenteRed.copy(alpha = 0.3f)
                                },
                                FameColors.SurfaceLight
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.take(1),
                    style = FameTypography.displayMedium,
                    color = when (player.positionCategory) {
                        "GK" -> FameColors.ChampionsGold
                        "DEF" -> FameColors.PitchGreen
                        "MID" -> FameColors.AfroSunOrange
                        else -> FameColors.KenteRed
                    }
                )
            }

            Spacer(modifier = Modifier.width(Dimensions.paddingMedium))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = player.name,
                    style = FameTypography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${player.position} | ${player.age} yrs | ${player.nationality.take(3).uppercase()}",
                        style = FameTypography.bodySmall,
                        color = FameColors.MutedParchment
                    )

                    if (player.isCaptain) {
                        Icon(
                            imageVector = Icons.Default.Copyright,
                            contentDescription = "Captain",
                            tint = FameColors.ChampionsGold,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(12.dp)
                        )
                    } else if (player.isViceCaptain) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.StarHalf,
                            contentDescription = "Vice-Captain",
                            tint = FameColors.MutedParchment,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(12.dp)
                        )
                    }
                }

                if (player.isInjured) {
                    Text(
                        text = "Injured",
                        style = FameTypography.labelSmall,
                        color = FameColors.KenteRed
                    )
                } else if (player.isSuspended) {
                    Text(
                        text = "Suspended",
                        style = FameTypography.labelSmall,
                        color = FameColors.AfroSunOrange
                    )
                }
            }

            // Rating
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = Dimensions.paddingSmall)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    FameColors.PitchGreen, FameColors.ChampionsGold, FameColors.PitchGreen
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.rating.toString(),
                        style = FameTypography.titleLarge,
                        color = FameColors.WarmIvory
                    )
                }

                Text(
                    text = "OVR",
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )
            }

            // Market Value
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = Dimensions.paddingSmall)
            ) {
                Text(
                    text = "€${player.marketValue / 1_000_000}M",
                    style = FameTypography.bodyLarge,
                    color = FameColors.ChampionsGold
                )

                Text(
                    text = "Value",
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )
            }
        }
    }
}

@Composable
fun EmptySquadView(
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SentimentDissatisfied,
            contentDescription = null,
            tint = FameColors.MutedParchment,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

        Text(
            text = "No players found",
            style = FameTypography.titleMedium,
            color = FameColors.MutedParchment
        )

        Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

        Text(
            text = "Try adjusting your search or filter criteria.",
            style = FameTypography.bodyMedium,
            color = FameColors.MutedParchment
        )

        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

        Button(
            onClick = onClearFilters,
            colors = ButtonDefaults.buttonColors(
                containerColor = FameColors.PitchGreen
            ),
            shape = ComponentShapes.button
        ) {
            Text("Clear Filters")
        }
    }
}
