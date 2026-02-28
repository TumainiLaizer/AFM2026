package com.fameafrica.afm2026.ui.screen.transfers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fameafrica.afm2026.data.database.entities.TransferStatus
import com.fameafrica.afm2026.data.database.entities.TransferType
import com.fameafrica.afm2026.ui.theme.FameColors
import com.fameafrica.afm2026.ui.theme.FameTheme
import com.fameafrica.afm2026.ui.theme.FameTypography

@Composable
fun TransfersScreen(
    onScoutClick: () -> Unit,
    onNegotiationClick: (Int) -> Unit,
    onCreateOffer: () -> Unit,
    viewModel: TransfersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TransfersTopBar(
                budget = uiState.transferBudget,
                wageBudget = uiState.wageBudget,
                onSearchClick = viewModel::toggleSearch,
                onFilterClick = viewModel::toggleFilters
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateOffer,
                containerColor = FameColors.PitchGreen,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Offer",
                    tint = FameColors.WarmIvory
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FameColors.StadiumBlack)
                .padding(paddingValues)
        ) {
            // Transfer Tabs
            TransferTabs(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::selectTab,
                incomingCount = uiState.incomingOffers.size,
                outgoingCount = uiState.outgoingOffers.size,
                completedCount = uiState.completedTransfers.size,
                rumoursCount = uiState.transferRumours.size
            )

            // Search Bar (if enabled)
            if (uiState.showSearch) {
                TransferSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::updateSearchQuery,
                    onSearch = viewModel::searchPlayers,
                    onClose = viewModel::toggleSearch,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Filter Chips (if enabled)
            if (uiState.showFilters) {
                TransferFilters(
                    selectedType = uiState.selectedTransferType,
                    selectedStatus = uiState.selectedStatus,
                    minRating = uiState.minRating,
                    maxFee = uiState.maxFee,
                    onTypeSelected = viewModel::setTransferType,
                    onStatusSelected = viewModel::setStatus,
                    onRatingChanged = viewModel::setMinRating,
                    onFeeChanged = viewModel::setMaxFee,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Content based on selected tab
            Box(modifier = Modifier.weight(1f)) {
                when (uiState.selectedTab) {
                    0 -> TransferMarketView(
                        players = uiState.marketPlayers,
                        searchResults = uiState.searchResults,
                        showSearch = uiState.showSearch,
                        onPlayerClick = { playerId ->
                            viewModel.initiateTransfer(playerId)
                            onNegotiationClick(playerId)
                        },
                        onAddToShortlist = viewModel::addToShortlist,
                        modifier = Modifier.fillMaxSize()
                    )
                    1 -> IncomingOffersView(
                        offers = uiState.incomingOffers,
                        onOfferClick = { offerId -> onNegotiationClick(offerId) },
                        onAccept = viewModel::acceptTransfer,
                        onReject = viewModel::rejectTransfer,
                        onCounter = viewModel::counterOffer,
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> OutgoingOffersView(
                        offers = uiState.outgoingOffers,
                        onOfferClick = { offerId -> onNegotiationClick(offerId) },
                        onWithdraw = viewModel::withdrawOffer,
                        onModify = viewModel::modifyOffer,
                        modifier = Modifier.fillMaxSize()
                    )
                    3 -> CompletedTransfersView(
                        transfers = uiState.completedTransfers,
                        onTransferClick = { transferId -> onNegotiationClick(transferId) },
                        modifier = Modifier.fillMaxSize()
                    )
                    4 -> TransferRumoursView(
                        rumours = uiState.transferRumours,
                        onRumourClick = { playerName -> viewModel.searchPlayer(playerName) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Transfer Window Status
            TransferWindowStatus(
                isOpen = uiState.isTransferWindowOpen,
                daysRemaining = uiState.windowDaysRemaining,
                windowType = uiState.windowType,
                foreignSlots = uiState.foreignPlayerSlots,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransfersTopBar(
    budget: Long,
    wageBudget: Long,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TRANSFERS",
                    style = FameTypography.titleLarge,
                    color = FameColors.WarmIvory
                )
                Text(
                    text = "Budget: €${budget / 1_000_000}M | Wage: €${wageBudget / 1_000}K",
                    style = FameTypography.labelSmall,
                    color = FameColors.ChampionsGold
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
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = FameColors.WarmIvory
                )
            }
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
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
fun TransferTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    incomingCount: Int,
    outgoingCount: Int,
    completedCount: Int,
    rumoursCount: Int
) {
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
        listOf("MARKET", "IN", "OUT", "DONE", "RUMOURS").forEachIndexed { index, tab ->
            val badgeCount = when (index) {
                1 -> incomingCount
                2 -> outgoingCount
                3 -> completedCount
                4 -> rumoursCount
                else -> 0
            }

            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = tab,
                            style = FameTypography.labelMedium,
                            color = if (selectedTab == index) FameColors.PitchGreen else FameColors.MutedParchment
                        )

                        if (badgeCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(FameColors.KenteRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = badgeCount.toString(),
                                    style = FameTypography.labelSmall,
                                    color = FameColors.WarmIvory
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun TransferSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit,
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
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = FameColors.MutedParchment,
                modifier = Modifier.size(20.dp)
            )

            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Search players...",
                        style = FameTypography.bodySmall,
                        color = FameColors.DisabledText
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = FameColors.PitchGreen
                )
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = FameColors.MutedParchment,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = FameColors.MutedParchment,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun TransferFilters(
    selectedType: String?,
    selectedStatus: String?,
    minRating: Int,
    maxFee: Int,
    onTypeSelected: (String?) -> Unit,
    onStatusSelected: (String?) -> Unit,
    onRatingChanged: (Int) -> Unit,
    onFeeChanged: (Int) -> Unit,
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
                text = "FILTERS",
                style = FameTypography.labelMedium,
                color = FameColors.ChampionsGold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Transfer Type
            Text(
                text = "Transfer Type",
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(listOf("All", "Buy", "Loan", "Free")) { type ->
                    val isSelected = when (type) {
                        "All" -> selectedType == null
                        else -> selectedType == type
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onTypeSelected(if (type == "All") null else type)
                        },
                        label = {
                            Text(
                                text = type,
                                style = FameTypography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FameColors.PitchGreen,
                            selectedLabelColor = FameColors.WarmIvory,
                            containerColor = FameColors.SurfaceLight,
                            labelColor = FameColors.MutedParchment
                        )
                    )
                }
            }

            // Status
            Text(
                text = "Status",
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(listOf("All", "Pending", "Negotiating", "Accepted", "Rejected", "Completed")) { status ->
                    val isSelected = when (status) {
                        "All" -> selectedStatus == null
                        else -> selectedStatus == status
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onStatusSelected(if (status == "All") null else status)
                        },
                        label = {
                            Text(
                                text = status,
                                style = FameTypography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (status) {
                                "Pending" -> FameColors.AfroSunOrange
                                "Negotiating" -> FameColors.ChampionsGold
                                "Accepted" -> FameColors.PitchGreen
                                "Rejected" -> FameColors.KenteRed
                                "Completed" -> FameColors.AfricanLegendEmerald
                                else -> FameColors.PitchGreen
                            },
                            selectedLabelColor = FameColors.WarmIvory,
                            containerColor = FameColors.SurfaceLight,
                            labelColor = FameColors.MutedParchment
                        )
                    )
                }
            }

            // Rating Range
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Min Rating: $minRating",
                    style = FameTypography.labelSmall,
                    color = FameColors.WarmIvory
                )

                Slider(
                    value = minRating.toFloat(),
                    onValueChange = { onRatingChanged(it.toInt()) },
                    valueRange = 50f..99f,
                    steps = 49,
                    colors = SliderDefaults.colors(
                        thumbColor = FameColors.ChampionsGold,
                        activeTrackColor = FameColors.ChampionsGold,
                        inactiveTrackColor = FameColors.SurfaceLight
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Max Fee
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Max Fee: €${maxFee / 1_000_000}M",
                    style = FameTypography.labelSmall,
                    color = FameColors.WarmIvory
                )

                Slider(
                    value = maxFee.toFloat(),
                    onValueChange = { onFeeChanged(it.toInt()) },
                    valueRange = 0f..50_000_000f,
                    steps = 50,
                    colors = SliderDefaults.colors(
                        thumbColor = FameColors.AfroSunOrange,
                        activeTrackColor = FameColors.AfroSunOrange,
                        inactiveTrackColor = FameColors.SurfaceLight
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TransferMarketView(
    players: List<TransferPlayerUiModel>,
    searchResults: List<TransferPlayerUiModel>,
    showSearch: Boolean,
    onPlayerClick: (Int) -> Unit,
    onAddToShortlist: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayPlayers = if (showSearch && searchResults.isNotEmpty()) searchResults else players

    if (displayPlayers.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.PersonSearch,
            message = "No players found",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = displayPlayers,
                key = { it.id }
            ) { player ->
                TransferPlayerCard(
                    player = player,
                    onClick = { onPlayerClick(player.id) },
                    onAddToShortlist = { onAddToShortlist(player.id) }
                )
            }
        }
    }
}

@Composable
fun TransferPlayerCard(
    player: TransferPlayerUiModel,
    onClick: () -> Unit,
    onAddToShortlist: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player Avatar with rating color
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                player.rating >= 85 -> FameColors.ChampionsGold
                                player.rating >= 75 -> FameColors.AfroSunOrange
                                player.rating >= 65 -> FameColors.PitchGreen
                                else -> FameColors.SurfaceLight
                            }.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.name.take(1),
                        style = FameTypography.titleLarge,
                        color = when {
                            player.rating >= 85 -> FameColors.ChampionsGold
                            player.rating >= 75 -> FameColors.AfroSunOrange
                            player.rating >= 65 -> FameColors.PitchGreen
                            else -> FameColors.MutedParchment
                        }
                    )
                }

                // Player Info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = player.name,
                        style = FameTypography.bodySmall,
                        color = FameColors.WarmIvory,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "${player.age}yrs • ${player.position} • ${player.nationality}",
                            style = FameTypography.labelSmall,
                            color = FameColors.MutedParchment
                        )

                        if (player.isForeign) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(FameColors.AfroSunOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "F",
                                    style = FameTypography.labelSmall,
                                    color = FameColors.WarmIvory,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = player.club,
                        style = FameTypography.labelSmall,
                        color = FameColors.ChampionsGold
                    )
                }

                // Rating and Value
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    player.rating >= 85 -> FameColors.ChampionsGold
                                    player.rating >= 75 -> FameColors.AfroSunOrange
                                    player.rating >= 65 -> FameColors.PitchGreen
                                    else -> FameColors.SurfaceLight
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = player.rating.toString(),
                            style = FameTypography.labelSmall,
                            color = if (player.rating >= 65) FameColors.StadiumBlack else FameColors.WarmIvory
                        )
                    }

                    Text(
                        text = "€${player.value / 1_000_000}M",
                        style = FameTypography.bodyMedium,
                        color = FameColors.ChampionsGold,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Text(
                        text = "€${player.wage / 1_000}K p/w",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                }

                // Shortlist button
                IconButton(
                    onClick = onAddToShortlist,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (player.onShortlist) Icons.Default.Star else Icons.Default.StarOutline,
                        contentDescription = "Shortlist",
                        tint = if (player.onShortlist) FameColors.ChampionsGold else FameColors.MutedParchment,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Scout rating indicator
            if (player.scoutRating > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            when {
                                player.scoutRating >= 85 -> FameColors.ChampionsGold
                                player.scoutRating >= 70 -> FameColors.AfroSunOrange
                                else -> FameColors.SurfaceLight
                            }
                        )
                )
            }
        }
    }
}

@Composable
fun IncomingOffersView(
    offers: List<TransferOfferUiModel>,
    onOfferClick: (Int) -> Unit,
    onAccept: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onCounter: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (offers.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.ArrowDownward,
            message = "No incoming offers",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = offers,
                key = { it.id }
            ) { offer ->
                IncomingOfferCard(
                    offer = offer,
                    onClick = { onOfferClick(offer.id) },
                    onAccept = { onAccept(offer.id) },
                    onReject = { onReject(offer.id) },
                    onCounter = { onCounter(offer.id) }
                )
            }
        }
    }
}

@Composable
fun IncomingOfferCard(
    offer: TransferOfferUiModel,
    onClick: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onCounter: () -> Unit
) {
    val statusColor = when (offer.status) {
        TransferStatus.PENDING.value -> FameColors.AfroSunOrange
        TransferStatus.NEGOTIATING.value -> FameColors.ChampionsGold
        TransferStatus.ACCEPTED.value -> FameColors.PitchGreen
        TransferStatus.REJECTED.value -> FameColors.KenteRed
        TransferStatus.COMPLETED.value -> FameColors.AfricanLegendEmerald
        else -> FameColors.MutedParchment
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header with type and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Transfer type badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (offer.transferType) {
                                TransferType.BUY.value -> FameColors.PitchGreen
                                TransferType.LOAN.value -> FameColors.AfroSunOrange
                                TransferType.FREE.value -> FameColors.ChampionsGold
                                else -> FameColors.SurfaceLight
                            }.copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = offer.transferType,
                        style = FameTypography.labelSmall,
                        color = when (offer.transferType) {
                            TransferType.BUY.value -> FameColors.PitchGreen
                            TransferType.LOAN.value -> FameColors.AfroSunOrange
                            TransferType.FREE.value -> FameColors.ChampionsGold
                            else -> FameColors.MutedParchment
                        }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Status indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )

                Text(
                    text = " ${offer.status}",
                    style = FameTypography.labelSmall,
                    color = statusColor,
                    modifier = Modifier.weight(1f)
                )

                // Urgent indicator
                if (offer.isUrgent) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(FameColors.KenteRed.copy(alpha = 0.1f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "URGENT",
                            style = FameTypography.labelSmall,
                            color = FameColors.KenteRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Player info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = offer.playerName,
                        style = FameTypography.bodyMedium,
                        color = FameColors.WarmIvory
                    )

                    Text(
                        text = "${offer.playerAge}yrs • ${offer.playerPosition}",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                }

                // From team
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = offer.fromTeam,
                        style = FameTypography.bodySmall,
                        color = FameColors.WarmIvory
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(horizontal = 4.dp)
                    )

                    Text(
                        text = offer.toTeam,
                        style = FameTypography.bodySmall,
                        color = FameColors.WarmIvory
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Offer details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Fee
                Column {
                    Text(
                        text = "Fee",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                    Text(
                        text = if (offer.transferType == TransferType.FREE.value) "Free" else "€${offer.fee / 1_000_000}M",
                        style = FameTypography.bodyMedium,
                        color = FameColors.ChampionsGold
                    )
                }

                // Wage
                Column {
                    Text(
                        text = "Wage",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                    Text(
                        text = "€${offer.wage / 1_000}K",
                        style = FameTypography.bodyMedium,
                        color = FameColors.AfroSunOrange
                    )
                }

                // Contract
                Column {
                    Text(
                        text = "Contract",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                    Text(
                        text = "${offer.contractLength} years",
                        style = FameTypography.bodyMedium,
                        color = FameColors.WarmIvory
                    )
                }

                // Loan details if applicable
                if (offer.isLoanToBuy) {
                    Column {
                        Text(
                            text = "Option",
                            style = FameTypography.labelSmall,
                            color = FameColors.MutedParchment
                        )
                        Text(
                            text = "€${offer.loanBuyFee?.div(1_000_000)}M",
                            style = FameTypography.bodyMedium,
                            color = FameColors.AfricanLegendEmerald
                        )
                    }
                }
            }

            // Scout rating if available
            if (offer.scoutRating > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = FameColors.MutedParchment,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = " Scout Rating: ${offer.scoutRating}",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment
                    )
                }
            }

            // Expiry
            if (offer.expiryDate != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Expires: ${offer.expiryDate}",
                    style = FameTypography.labelSmall,
                    color = if (offer.isUrgent) FameColors.KenteRed else FameColors.MutedParchment
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

// Action buttons (only for pending/negotiating)
            if (offer.status == TransferStatus.PENDING.value ||
                offer.status == TransferStatus.NEGOTIATING.value) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FameColors.PitchGreen
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 4.dp,
                            hoveredElevation = 2.dp,
                            focusedElevation = 2.dp
                        )
                    ) {
                        Text("Accept")
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = FameColors.KenteRed
                        ),
                        border = BorderStroke(1.dp, FameColors.KenteRed),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 4.dp,
                            hoveredElevation = 2.dp,
                            focusedElevation = 2.dp
                        )
                    ) {
                        Text("Reject")
                    }

                    OutlinedButton(
                        onClick = onCounter,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = FameColors.AfroSunOrange
                        ),
                        border = BorderStroke(1.dp, FameColors.AfroSunOrange),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 4.dp,
                            hoveredElevation = 2.dp,
                            focusedElevation = 2.dp
                        )
                    ) {
                        Text("Counter")
                    }
                }
            }
        }
    }
}

@Composable
fun OutgoingOffersView(
    offers: List<TransferOfferUiModel>,
    onOfferClick: (Int) -> Unit,
    onWithdraw: (Int) -> Unit,
    onModify: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (offers.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.ArrowUpward,
            message = "No outgoing offers",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = offers,
                key = { it.id }
            ) { offer ->
                OutgoingOfferCard(
                    offer = offer,
                    onClick = { onOfferClick(offer.id) },
                    onWithdraw = { onWithdraw(offer.id) },
                    onModify = { onModify(offer.id) }
                )
            }
        }
    }
}

@Composable
fun OutgoingOfferCard(
    offer: TransferOfferUiModel,
    onClick: () -> Unit,
    onWithdraw: () -> Unit,
    onModify: () -> Unit
) {
    val statusColor = when (offer.status) {
        TransferStatus.PENDING.value -> FameColors.AfroSunOrange
        TransferStatus.NEGOTIATING.value -> FameColors.ChampionsGold
        TransferStatus.ACCEPTED.value -> FameColors.PitchGreen
        TransferStatus.REJECTED.value -> FameColors.KenteRed
        TransferStatus.COMPLETED.value -> FameColors.AfricanLegendEmerald
        else -> FameColors.MutedParchment
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            // Player info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = offer.playerName,
                    style = FameTypography.bodySmall,
                    color = FameColors.WarmIvory
                )

                Text(
                    text = "To: ${offer.toTeam}",
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "€${offer.fee / 1_000_000}M",
                        style = FameTypography.bodySmall,
                        color = FameColors.ChampionsGold
                    )

                    Text(
                        text = " • ${offer.contractLength}yrs",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Status
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                        .align(Alignment.End)
                )

                Text(
                    text = offer.status,
                    style = FameTypography.labelSmall,
                    color = statusColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Actions
            Column {
                IconButton(
                    onClick = onModify,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modify",
                        tint = FameColors.AfroSunOrange,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onWithdraw,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Withdraw",
                        tint = FameColors.KenteRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CompletedTransfersView(
    transfers: List<TransferOfferUiModel>,
    onTransferClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (transfers.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.CheckCircle,
            message = "No completed transfers",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = transfers,
                key = { it.id }
            ) { transfer ->
                CompletedTransferCard(
                    transfer = transfer,
                    onClick = { onTransferClick(transfer.id) }
                )
            }
        }
    }
}

@Composable
fun CompletedTransferCard(
    transfer: TransferOfferUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            // Completion Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FameColors.AfricanLegendEmerald.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = FameColors.AfricanLegendEmerald,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transfer Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transfer.playerName,
                    style = FameTypography.bodyMedium,
                    color = FameColors.WarmIvory
                )

                Text(
                    text = "${transfer.fromTeam} → ${transfer.toTeam}",
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Transfer type badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (transfer.transferType) {
                                    TransferType.BUY.value -> FameColors.PitchGreen
                                    TransferType.LOAN.value -> FameColors.AfroSunOrange
                                    TransferType.FREE.value -> FameColors.ChampionsGold
                                    else -> FameColors.SurfaceLight
                                }.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = transfer.transferType,
                            style = FameTypography.labelSmall,
                            color = when (transfer.transferType) {
                                TransferType.BUY.value -> FameColors.PitchGreen
                                TransferType.LOAN.value -> FameColors.AfroSunOrange
                                TransferType.FREE.value -> FameColors.ChampionsGold
                                else -> FameColors.MutedParchment
                            }
                        )
                    }

                    if (transfer.transferType != TransferType.FREE.value) {
                        Text(
                            text = " • €${transfer.fee / 1_000_000}M",
                            style = FameTypography.labelSmall,
                            color = FameColors.ChampionsGold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Text(
                        text = " • ${transfer.completedDate ?: "Today"}",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TransferRumoursView(
    rumours: List<TransferRumourUiModel>,
    onRumourClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (rumours.isEmpty()) {
        EmptyStateView(
            icon = Icons.AutoMirrored.Filled.Announcement,
            message = "No transfer rumours",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = rumours,
                key = { it.playerName }
            ) { rumour ->
                TransferRumourCard(
                    rumour = rumour,
                    onClick = { onRumourClick(rumour.playerName) }
                )
            }
        }
    }
}

@Composable
fun TransferRumourCard(
    rumour: TransferRumourUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            // Rumour Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FameColors.AfroSunOrange.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Announcement,
                    contentDescription = null,
                    tint = FameColors.AfroSunOrange,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Rumour Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rumour.playerName,
                    style = FameTypography.bodyMedium,
                    color = FameColors.WarmIvory
                )

                Text(
                    text = rumour.headline,
                    style = FameTypography.bodySmall,
                    color = FameColors.MutedParchment,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rumour.source,
                        style = FameTypography.labelSmall,
                        color = FameColors.ChampionsGold
                    )

                    Text(
                        text = " • ${rumour.timeAgo}",
                        style = FameTypography.labelSmall,
                        color = FameColors.MutedParchment,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    if (rumour.confidence > 0) {
                        Text(
                            text = " • ${rumour.confidence}% confidence",
                            style = FameTypography.labelSmall,
                            color = when {
                                rumour.confidence >= 70 -> FameColors.PitchGreen
                                rumour.confidence >= 40 -> FameColors.AfroSunOrange
                                else -> FameColors.KenteRed
                            },
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransferWindowStatus(
    isOpen: Boolean,
    daysRemaining: Int,
    windowType: String,
    foreignSlots: ForeignPlayerSlotsUiModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Window Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isOpen) FameColors.PitchGreen else FameColors.KenteRed)
                )

                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = if (isOpen) "TRANSFER WINDOW OPEN" else "TRANSFER WINDOW CLOSED",
                        style = FameTypography.labelMedium,
                        color = if (isOpen) FameColors.PitchGreen else FameColors.KenteRed
                    )

                    if (isOpen) {
                        Text(
                            text = "$daysRemaining days remaining • $windowType",
                            style = FameTypography.labelSmall,
                            color = FameColors.MutedParchment
                        )
                    }
                }
            }

            // Foreign Player Slots
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Foreign Players",
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${foreignSlots.current} / ${foreignSlots.max}",
                        style = FameTypography.bodyMedium,
                        color = if (foreignSlots.current >= foreignSlots.max)
                            FameColors.KenteRed else FameColors.ChampionsGold
                    )

                    if (foreignSlots.current >= foreignSlots.max) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = FameColors.KenteRed,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 4.dp)
                        )
                    }
                }

                if (foreignSlots.nextSeason > 0) {
                    Text(
                        text = "Next season: ${foreignSlots.nextSeason} slots",
                        style = FameTypography.labelSmall,
                        color = FameColors.AfroSunOrange
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FameColors.MutedParchment.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = FameTypography.bodyMedium,
                color = FameColors.MutedParchment,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ============ PREVIEWS ============

@Preview(showBackground = true)
@Composable
fun TransferPlayerCardPreview() {
    FameTheme {
        TransferPlayerCard(
            player = TransferPlayerUiModel(
                id = 1,
                name = "Sadio Mané",
                age = 28,
                position = "LW",
                nationality = "SEN",
                club = "Al Nassr",
                rating = 89,
                value = 25_000_000,
                wage = 350_000,
                isForeign = true,
                onShortlist = false,
                scoutRating = 92
            ),
            onClick = {},
            onAddToShortlist = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IncomingOfferCardPreview() {
    FameTheme {
        IncomingOfferCard(
            offer = TransferOfferUiModel(
                id = 1,
                playerId = 1,
                playerName = "Mohamed Salah",
                playerAge = 29,
                playerPosition = "RW",
                fromTeam = "Liverpool",
                toTeam = "Al Ahly",
                transferType = TransferType.BUY.value,
                status = TransferStatus.PENDING.value,
                fee = 15_000_000,
                wage = 200_000,
                contractLength = 3,
                isLoanToBuy = false,
                loanBuyFee = null,
                scoutRating = 88,
                isUrgent = true,
                expiryDate = "2024-08-31"
            ),
            onClick = {},
            onAccept = {},
            onReject = {},
            onCounter = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransferWindowStatusPreview() {
    FameTheme {
        TransferWindowStatus(
            isOpen = true,
            daysRemaining = 15,
            windowType = "Summer Window",
            foreignSlots = ForeignPlayerSlotsUiModel(
                current = 5,
                max = 7,
                nextSeason = 8
            ),
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransferTabsPreview() {
    FameTheme {
        TransferTabs(
            selectedTab = 0,
            onTabSelected = {},
            incomingCount = 3,
            outgoingCount = 2,
            completedCount = 5,
            rumoursCount = 4
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    FameTheme {
        EmptyStateView(
            icon = Icons.Default.PersonSearch,
            message = "No players found"
        )
    }
}