package com.fameafrica.afm2026.ui.screen.world

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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

@Composable
fun WorldScreen(
    onLeagueClick: (String) -> Unit,
    onCupClick: (String) -> Unit,
    onNationalTeamClick: (String) -> Unit,
    viewModel: WorldViewModel = hiltViewModel(
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
            WorldTopBar(
                onSearchClick = { /* Navigate to search */ },
                onFilterClick = { /* Open filters */ }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FameColors.StadiumBlack)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Continental Tabs
            item {
                ContinentalTabs(
                    selectedContinent = uiState.selectedContinent,
                    onContinentSelected = viewModel::selectContinent
                )
            }

            // Featured Competitions
            if (uiState.featuredCompetitions.isNotEmpty()) {
                item {
                    FeaturedCompetitionsRow(
                        competitions = uiState.featuredCompetitions,
                        onCompetitionClick = { id, type ->
                            if (type == "league") onLeagueClick(id) else onCupClick(id)
                        }
                    )
                }
            }

            // Top Leagues by Region
            uiState.leaguesByRegion.forEach { (region, leagues) ->
                item {
                    LeagueSection(
                        region = region,
                        leagues = leagues,
                        onLeagueClick = onLeagueClick
                    )
                }
            }

            // Major Cups
            item {
                CupsSection(
                    cups = uiState.majorCups,
                    onCupClick = onCupClick
                )
            }

            // National Teams
            item {
                NationalTeamsSection(
                    teams = uiState.nationalTeams,
                    onTeamClick = onNationalTeamClick
                )
            }

            // Continental Rankings
            item {
                ContinentalRankingsCard(
                    rankings = uiState.continentalRankings
                )
            }

            // Upcoming International Fixtures
            if (uiState.internationalFixtures.isNotEmpty()) {
                item {
                    InternationalFixturesCard(
                        fixtures = uiState.internationalFixtures,
                        onFixtureClick = { /* Navigate to match */ }
                    )
                }
            }

            // Transfer News
            if (uiState.transferNews.isNotEmpty()) {
                item {
                    TransferNewsCard(
                        news = uiState.transferNews,
                        onNewsClick = { /* Navigate to news */ }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldTopBar(
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "WORLD",
                style = FameTypography.titleLarge,
                color = FameColors.WarmIvory
            )
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
fun ContinentalTabs(
    selectedContinent: String,
    onContinentSelected: (String) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = when (selectedContinent) {
            "Africa" -> 0
            "Europe" -> 1
            "Asia" -> 2
            "Americas" -> 3
            "Oceania" -> 4
            else -> 0
        },
        containerColor = FameColors.StadiumBlack,
        edgePadding = 12.dp,
        divider = {},
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[when (selectedContinent) {
                    "Africa" -> 0
                    "Europe" -> 1
                    "Asia" -> 2
                    "Americas" -> 3
                    "Oceania" -> 4
                    else -> 0
                }]),
                height = 2.dp,
                color = FameColors.ChampionsGold
            )
        }
    ) {
        listOf("🌍 Africa", "🌍 Europe", "🌏 Asia", "🌎 Americas", "🌏 Oceania").forEachIndexed { index, continent ->
            val selected = when (index) {
                0 -> selectedContinent == "Africa"
                1 -> selectedContinent == "Europe"
                2 -> selectedContinent == "Asia"
                3 -> selectedContinent == "Americas"
                4 -> selectedContinent == "Oceania"
                else -> false
            }

            Tab(
                selected = selected,
                onClick = {
                    onContinentSelected(
                        when (index) {
                            0 -> "Africa"
                            1 -> "Europe"
                            2 -> "Asia"
                            3 -> "Americas"
                            4 -> "Oceania"
                            else -> "Africa"
                        }
                    )
                },
                text = {
                    Text(
                        text = continent,
                        style = FameTypography.labelMedium,
                        color = if (selected) FameColors.PitchGreen else FameColors.MutedParchment
                    )
                }
            )
        }
    }
}

@Composable
fun FeaturedCompetitionsRow(
    competitions: List<CompetitionUiModel>,
    onCompetitionClick: (String, String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "FEATURED COMPETITIONS",
            style = FameTypography.labelMedium,
            color = FameColors.ChampionsGold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(competitions) { competition ->
                FeaturedCompetitionCard(
                    competition = competition,
                    onClick = { onCompetitionClick(competition.id, competition.type) }
                )
            }
        }
    }
}

@Composable
fun FeaturedCompetitionCard(
    competition: CompetitionUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Competition Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                when (competition.confederation) {
                                    "CAF" -> FameColors.PitchGreen
                                    "UEFA" -> FameColors.ChampionsGold
                                    "AFC" -> FameColors.AfroSunOrange
                                    "CONMEBOL" -> FameColors.KenteRed
                                    "CONCACAF" -> FameColors.AfricanLegendEmerald
                                    else -> FameColors.BaobabBrown
                                }.copy(alpha = 0.3f),
                                FameColors.SurfaceLight
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = competition.logoUrl,
                    contentDescription = competition.name,
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = competition.name,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = competition.confederation,
                style = FameTypography.labelSmall,
                color = when (competition.confederation) {
                    "CAF" -> FameColors.PitchGreen
                    "UEFA" -> FameColors.ChampionsGold
                    "AFC" -> FameColors.AfroSunOrange
                    "CONMEBOL" -> FameColors.KenteRed
                    "CONCACAF" -> FameColors.AfricanLegendEmerald
                    else -> FameColors.BaobabBrown
                }
            )

            Text(
                text = "${competition.teams} teams",
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )
        }
    }
}

@Composable
fun LeagueSection(
    region: String,
    leagues: List<LeagueUiModel>,
    onLeagueClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = region.uppercase(),
                style = FameTypography.labelMedium,
                color = FameColors.PitchGreen
            )

            TextButton(onClick = { /* View all leagues in region */ }) {
                Text(
                    text = "View All",
                    style = FameTypography.labelSmall,
                    color = FameColors.PitchGreen
                )
            }
        }

        leagues.take(5).forEach { league ->
            LeagueItem(
                league = league,
                onClick = { onLeagueClick(league.id) }
            )
        }
    }
}

@Composable
fun LeagueItem(
    league: LeagueUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
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
            // League Logo
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(FameColors.SurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = league.logoUrl,
                    contentDescription = league.name,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = league.name,
                    style = FameTypography.bodySmall,
                    color = FameColors.WarmIvory
                )

                Text(
                    text = "${league.country} • Level ${league.level}",
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "€${league.prizeMoney / 1_000_000}M",
                    style = FameTypography.bodyMedium,
                    color = FameColors.ChampionsGold
                )

                Text(
                    text = "Prize Pool",
                    style = FameTypography.labelSmall,
                    color = FameColors.MutedParchment
                )
            }
        }
    }
}

@Composable
fun CupsSection(
    cups: List<CupUiModel>,
    onCupClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "MAJOR CUPS",
            style = FameTypography.labelMedium,
            color = FameColors.AfroSunOrange,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cups) { cup ->
                CupCard(
                    cup = cup,
                    onClick = { onCupClick(cup.id) }
                )
            }
        }
    }
}

@Composable
fun CupCard(
    cup: CupUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        when (cup.type) {
                            "Continental" -> FameColors.ChampionsGold.copy(alpha = 0.2f)
                            "International" -> FameColors.AfricanLegendEmerald.copy(alpha = 0.2f)
                            else -> FameColors.AfroSunOrange.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = when (cup.type) {
                        "Continental" -> FameColors.ChampionsGold
                        "International" -> FameColors.AfricanLegendEmerald
                        else -> FameColors.AfroSunOrange
                    },
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = cup.name,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = cup.type,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )

            Text(
                text = "€${cup.prizeMoney / 1_000_000}M",
                style = FameTypography.bodyMedium,
                color = FameColors.ChampionsGold
            )
        }
    }
}

@Composable
fun NationalTeamsSection(
    teams: List<NationalTeamUiModel>,
    onTeamClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "NATIONAL TEAMS",
            style = FameTypography.labelMedium,
            color = FameColors.AfricanLegendEmerald,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(teams) { team ->
                NationalTeamCard(
                    team = team,
                    onClick = { onTeamClick(team.id) }
                )
            }
        }
    }
}

@Composable
fun NationalTeamCard(
    team: NationalTeamUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flag
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(FameColors.SurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = team.flagUrl,
                    contentDescription = team.name,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = team.name,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Text(
                text = "FIFA: ${team.fifaRanking}",
                style = FameTypography.labelSmall,
                color = FameColors.ChampionsGold
            )
        }
    }
}

@Composable
fun ContinentalRankingsCard(
    rankings: List<RankingUiModel>?
) {
    if (rankings.isNullOrEmpty()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "CONTINENTAL RANKINGS",
                style = FameTypography.labelMedium,
                color = FameColors.ChampionsGold
            )

            Spacer(modifier = Modifier.height(12.dp))

            rankings.take(5).forEachIndexed { index, ranking ->
                RankingItem(
                    rank = index + 1,
                    ranking = ranking
                )
            }

            TextButton(
                onClick = { /* View full rankings */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "View Full Rankings",
                    style = FameTypography.labelSmall,
                    color = FameColors.ChampionsGold
                )
            }
        }
    }
}

@Composable
fun RankingItem(
    rank: Int,
    ranking: RankingUiModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when (rank) {
                        1 -> FameColors.ChampionsGold.copy(alpha = 0.2f)
                        2 -> FameColors.NationalSilver.copy(alpha = 0.2f)
                        3 -> FameColors.LocalBronze.copy(alpha = 0.2f)
                        else -> FameColors.SurfaceLight
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                style = FameTypography.labelSmall,
                color = when (rank) {
                    1 -> FameColors.ChampionsGold
                    2 -> FameColors.NationalSilver
                    3 -> FameColors.LocalBronze
                    else -> FameColors.MutedParchment
                }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Flag
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(FameColors.SurfaceLight)
        ) {
            AsyncImage(
                model = ranking.flagUrl,
                contentDescription = ranking.country,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = ranking.country,
            style = FameTypography.bodySmall,
            color = FameColors.WarmIvory,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = ranking.points.toString(),
            style = FameTypography.bodyMedium,
            color = FameColors.ChampionsGold
        )
    }
}

@Composable
fun InternationalFixturesCard(
    fixtures: List<InternationalFixtureUiModel>,
    onFixtureClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = FameColors.SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "INTERNATIONAL FIXTURES",
                style = FameTypography.labelMedium,
                color = FameColors.AfroSunOrange
            )

            Spacer(modifier = Modifier.height(12.dp))

            fixtures.take(3).forEach { fixture ->
                InternationalFixtureItem(
                    fixture = fixture,
                    onClick = { onFixtureClick(fixture.id) }
                )
            }
        }
    }
}

@Composable
fun InternationalFixtureItem(
    fixture: InternationalFixtureUiModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Team
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(FameColors.SurfaceLight)
            ) {
                AsyncImage(
                    model = fixture.homeFlag,
                    contentDescription = fixture.homeTeam,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = fixture.homeTeam,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Match Info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = fixture.date,
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )

            Text(
                text = fixture.competition,
                style = FameTypography.labelSmall,
                color = FameColors.AfroSunOrange
            )
        }

        // Away Team
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = fixture.awayTeam,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(FameColors.SurfaceLight)
            ) {
                AsyncImage(
                    model = fixture.awayFlag,
                    contentDescription = fixture.awayTeam,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun TransferNewsCard(
    news: List<TransferNewsUiModel>,
    onNewsClick: (Int) -> Unit
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "TRANSFER RUMORS",
                style = FameTypography.labelMedium,
                color = FameColors.KenteRed
            )

            Spacer(modifier = Modifier.height(12.dp))

            news.take(3).forEach { item ->
                TransferNewsItem(
                    news = item,
                    onClick = { onNewsClick(item.id) }
                )
            }
        }
    }
}

@Composable
fun TransferNewsItem(
    news: TransferNewsUiModel,
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
                .size(32.dp)
                .clip(CircleShape)
                .background(FameColors.KenteRed.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = null,
                tint = FameColors.KenteRed,
                modifier = Modifier.size(16.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = news.player,
                style = FameTypography.bodySmall,
                color = FameColors.WarmIvory
            )

            Text(
                text = "${news.fromTeam} → ${news.toTeam}",
                style = FameTypography.labelSmall,
                color = FameColors.MutedParchment
            )
        }

        Text(
            text = "€${news.fee / 1_000_000}M",
            style = FameTypography.bodyMedium,
            color = FameColors.KenteRed
        )
    }
}