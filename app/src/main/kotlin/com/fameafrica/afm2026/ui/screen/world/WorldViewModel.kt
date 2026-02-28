package com.fameafrica.afm2026.ui.screen.world

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameafrica.afm2026.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorldUiState(
    val isLoading: Boolean = true,
    val selectedContinent: String = "Africa",
    val featuredCompetitions: List<CompetitionUiModel> = emptyList(),
    val leaguesByRegion: Map<String, List<LeagueUiModel>> = emptyMap(),
    val majorCups: List<CupUiModel> = emptyList(),
    val nationalTeams: List<NationalTeamUiModel> = emptyList(),
    val continentalRankings: List<RankingUiModel> = emptyList(),
    val internationalFixtures: List<InternationalFixtureUiModel> = emptyList(),
    val transferNews: List<TransferNewsUiModel> = emptyList()
)

data class CompetitionUiModel(
    val id: String,
    val name: String,
    val type: String,
    val confederation: String,
    val teams: Int,
    val logoUrl: String?
)

data class LeagueUiModel(
    val id: String,
    val name: String,
    val country: String,
    val level: Int,
    val prizeMoney: Long,
    val logoUrl: String?
)

data class CupUiModel(
    val id: String,
    val name: String,
    val type: String,
    val prizeMoney: Long,
    val teams: Int,
    val currentStage: String
)

data class NationalTeamUiModel(
    val id: String,
    val name: String,
    val fifaRanking: Int,
    val confederation: String,
    val flagUrl: String?
)

data class RankingUiModel(
    val country: String,
    val points: Int,
    val flagUrl: String?
)

data class InternationalFixtureUiModel(
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val homeFlag: String?,
    val awayFlag: String?,
    val date: String,
    val competition: String
)

data class TransferNewsUiModel(
    val id: Int,
    val player: String,
    val fromTeam: String,
    val toTeam: String,
    val fee: Long
)

@HiltViewModel
class WorldViewModel @Inject constructor(
    private val leaguesRepository: LeaguesRepository,
    private val cupsRepository: CupsRepository,
    private val nationalTeamsRepository: NationalTeamsRepository,
    private val fixturesRepository: FixturesRepository,
    private val transfersRepository: TransfersRepository,
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorldUiState(isLoading = true))
    val uiState: StateFlow<WorldUiState> = _uiState

    init {
        loadWorldData()
    }

    fun selectContinent(continent: String) {
        _uiState.value = _uiState.value.copy(selectedContinent = continent)
        // Filter data by continent
    }

    private fun loadWorldData() {
        viewModelScope.launch {
            // Load featured competitions
            val featured = listOf(
                CompetitionUiModel(
                    id = "caf_cl",
                    name = "CAF Champions League",
                    type = "cup",
                    confederation = "CAF",
                    teams = 16,
                    logoUrl = "cups/caf_cl.png"
                ),
                CompetitionUiModel(
                    id = "uefa_cl",
                    name = "UEFA Champions League",
                    type = "cup",
                    confederation = "UEFA",
                    teams = 32,
                    logoUrl = "cups/uefa_cl.png"
                ),
                CompetitionUiModel(
                    id = "afcon",
                    name = "Africa Cup of Nations",
                    type = "national",
                    confederation = "CAF",
                    teams = 24,
                    logoUrl = "cups/afcon.png"
                ),
                CompetitionUiModel(
                    id = "world_cup",
                    name = "FIFA World Cup",
                    type = "international",
                    confederation = "FIFA",
                    teams = 32,
                    logoUrl = "cups/world_cup.png"
                )
            )

            // Load leagues by region
            val eastAfricaLeagues = listOf(
                LeagueUiModel(
                    id = "tz_premier",
                    name = "Tanzania Premier League",
                    country = "Tanzania",
                    level = 1,
                    prizeMoney = 230_400,
                    logoUrl = "leagues/tanzania.png"
                ),
                LeagueUiModel(
                    id = "ke_premier",
                    name = "Kenyan Premier League",
                    country = "Kenya",
                    level = 1,
                    prizeMoney = 96_339,
                    logoUrl = "leagues/kenya.png"
                ),
                LeagueUiModel(
                    id = "ug_premier",
                    name = "Uganda Premier League",
                    country = "Uganda",
                    level = 1,
                    prizeMoney = 50_000,
                    logoUrl = "leagues/uganda.png"
                )
            )

            val northAfricaLeagues = listOf(
                LeagueUiModel(
                    id = "eg_premier",
                    name = "Egyptian Premier League",
                    country = "Egypt",
                    level = 1,
                    prizeMoney = 480_000,
                    logoUrl = "leagues/egypt.png"
                ),
                LeagueUiModel(
                    id = "ma_botola",
                    name = "Morocco Botola Pro",
                    country = "Morocco",
                    level = 1,
                    prizeMoney = 572_160,
                    logoUrl = "leagues/morocco.png"
                ),
                LeagueUiModel(
                    id = "dz_ligue1",
                    name = "Algeria Ligue 1",
                    country = "Algeria",
                    level = 1,
                    prizeMoney = 250_000,
                    logoUrl = "leagues/algeria.png"
                )
            )

            val westAfricaLeagues = listOf(
                LeagueUiModel(
                    id = "ng_npfl",
                    name = "Nigerian NPFL",
                    country = "Nigeria",
                    level = 1,
                    prizeMoney = 182_400,
                    logoUrl = "leagues/nigeria.png"
                ),
                LeagueUiModel(
                    id = "gh_premier",
                    name = "Ghana Premier League",
                    country = "Ghana",
                    level = 1,
                    prizeMoney = 150_000,
                    logoUrl = "leagues/ghana.png"
                ),
                LeagueUiModel(
                    id = "sn_premier",
                    name = "Senegal Premier League",
                    country = "Senegal",
                    level = 1,
                    prizeMoney = 140_000,
                    logoUrl = "leagues/senegal.png"
                )
            )

            val southernAfricaLeagues = listOf(
                LeagueUiModel(
                    id = "za_psl",
                    name = "South African PSL",
                    country = "South Africa",
                    level = 1,
                    prizeMoney = 770_880,
                    logoUrl = "leagues/south_africa.png"
                ),
                LeagueUiModel(
                    id = "zm_super",
                    name = "Zambia Super League",
                    country = "Zambia",
                    level = 1,
                    prizeMoney = 90_000,
                    logoUrl = "leagues/zambia.png"
                ),
                LeagueUiModel(
                    id = "zw_psl",
                    name = "Zimbabwe Premier League",
                    country = "Zimbabwe",
                    level = 1,
                    prizeMoney = 70_000,
                    logoUrl = "leagues/zimbabwe.png"
                )
            )

            val leaguesByRegion = mapOf(
                "EAST AFRICA" to eastAfricaLeagues,
                "NORTH AFRICA" to northAfricaLeagues,
                "WEST AFRICA" to westAfricaLeagues,
                "SOUTHERN AFRICA" to southernAfricaLeagues
            )

            // Load major cups
            val majorCups = listOf(
                CupUiModel(
                    id = "caf_cl",
                    name = "CAF Champions League",
                    type = "Continental",
                    prizeMoney = 10_300_000,
                    teams = 16,
                    currentStage = "Quarter-finals"
                ),
                CupUiModel(
                    id = "caf_cc",
                    name = "CAF Confederation Cup",
                    type = "Continental",
                    prizeMoney = 2_500_000,
                    teams = 16,
                    currentStage = "Semi-finals"
                ),
                CupUiModel(
                    id = "afcon",
                    name = "Africa Cup of Nations",
                    type = "International",
                    prizeMoney = 7_131_718,
                    teams = 24,
                    currentStage = "Group Stage"
                ),
                CupUiModel(
                    id = "caf_super",
                    name = "CAF Super Cup",
                    type = "Continental",
                    prizeMoney = 462_427,
                    teams = 2,
                    currentStage = "Final"
                )
            )

            // Load national teams
            val nationalTeams = listOf(
                NationalTeamUiModel(
                    id = "egy",
                    name = "Egypt",
                    fifaRanking = 33,
                    confederation = "CAF",
                    flagUrl = "flags/egypt.png"
                ),
                NationalTeamUiModel(
                    id = "sen",
                    name = "Senegal",
                    fifaRanking = 20,
                    confederation = "CAF",
                    flagUrl = "flags/senegal.png"
                ),
                NationalTeamUiModel(
                    id = "mar",
                    name = "Morocco",
                    fifaRanking = 13,
                    confederation = "CAF",
                    flagUrl = "flags/morocco.png"
                ),
                NationalTeamUiModel(
                    id = "nga",
                    name = "Nigeria",
                    fifaRanking = 35,
                    confederation = "CAF",
                    flagUrl = "flags/nigeria.png"
                ),
                NationalTeamUiModel(
                    id = "alg",
                    name = "Algeria",
                    fifaRanking = 30,
                    confederation = "CAF",
                    flagUrl = "flags/algeria.png"
                ),
                NationalTeamUiModel(
                    id = "tun",
                    name = "Tunisia",
                    fifaRanking = 28,
                    confederation = "CAF",
                    flagUrl = "flags/tunisia.png"
                ),
                NationalTeamUiModel(
                    id = "cmr",
                    name = "Cameroon",
                    fifaRanking = 43,
                    confederation = "CAF",
                    flagUrl = "flags/cameroon.png"
                ),
                NationalTeamUiModel(
                    id = "gha",
                    name = "Ghana",
                    fifaRanking = 58,
                    confederation = "CAF",
                    flagUrl = "flags/ghana.png"
                )
            )

            // Load continental rankings
            val rankings = listOf(
                RankingUiModel("Morocco", 1780, "flags/morocco.png"),
                RankingUiModel("Senegal", 1765, "flags/senegal.png"),
                RankingUiModel("Egypt", 1740, "flags/egypt.png"),
                RankingUiModel("Nigeria", 1720, "flags/nigeria.png"),
                RankingUiModel("Algeria", 1705, "flags/algeria.png"),
                RankingUiModel("Tunisia", 1690, "flags/tunisia.png"),
                RankingUiModel("Cameroon", 1675, "flags/cameroon.png"),
                RankingUiModel("Ghana", 1660, "flags/ghana.png"),
                RankingUiModel("South Africa", 1645, "flags/south_africa.png"),
                RankingUiModel("Ivory Coast", 1630, "flags/ivory_coast.png")
            )

            // Load international fixtures
            val fixtures = listOf(
                InternationalFixtureUiModel(
                    id = 1,
                    homeTeam = "Egypt",
                    awayTeam = "Tunisia",
                    homeFlag = "flags/egypt.png",
                    awayFlag = "flags/tunisia.png",
                    date = "25 Mar 2025",
                    competition = "AFCON Qualifiers"
                ),
                InternationalFixtureUiModel(
                    id = 2,
                    homeTeam = "Senegal",
                    awayTeam = "Algeria",
                    homeFlag = "flags/senegal.png",
                    awayFlag = "flags/algeria.png",
                    date = "28 Mar 2025",
                    competition = "FIFA World Cup Qualifiers"
                ),
                InternationalFixtureUiModel(
                    id = 3,
                    homeTeam = "Nigeria",
                    awayTeam = "Ghana",
                    homeFlag = "flags/nigeria.png",
                    awayFlag = "flags/ghana.png",
                    date = "01 Apr 2025",
                    competition = "AFCON Qualifiers"
                )
            )

            // Load transfer news
            val transfers = listOf(
                TransferNewsUiModel(
                    id = 1,
                    player = "Victor Osimhen",
                    fromTeam = "Napoli",
                    toTeam = "Manchester United",
                    fee = 120_000_000
                ),
                TransferNewsUiModel(
                    id = 2,
                    player = "Mohamed Salah",
                    fromTeam = "Liverpool",
                    toTeam = "Al Ittihad",
                    fee = 90_000_000
                ),
                TransferNewsUiModel(
                    id = 3,
                    player = "Hakim Ziyech",
                    fromTeam = "Chelsea",
                    toTeam = "Galatasaray",
                    fee = 15_000_000
                )
            )

            _uiState.value = WorldUiState(
                isLoading = false,
                featuredCompetitions = featured,
                leaguesByRegion = leaguesByRegion,
                majorCups = majorCups,
                nationalTeams = nationalTeams,
                continentalRankings = rankings,
                internationalFixtures = fixtures,
                transferNews = transfers
            )
        }
    }
}