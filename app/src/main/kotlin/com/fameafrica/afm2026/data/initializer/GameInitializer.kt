package com.fameafrica.afm2026.data.initializer

import com.fameafrica.afm2026.data.repository.*
import com.fameafrica.afm2026.utils.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameInitializer @Inject constructor(
    private val eloHistoryRepository: EloHistoryRepository,
    private val leaguesRepository: LeaguesRepository,
    private val cupsRepository: CupsRepository,
    private val teamsRepository: TeamsRepository,
    private val playersRepository: PlayersRepository,
    private val journalistsRepository: JournalistsRepository,
    private val archetypeTraitsRepository: ArchetypeTraitsRepository,
    private val personalityTypesRepository: PersonalityTypesRepository,
    private val prizesRepository: PrizesRepository,
    private val financesRepository: FinancesRepository,
    private val fixturesRepository: FixturesRepository,
    private val transferWindowsRepository: TransferWindowsRepository,
    private val boardEvaluationRepository: BoardEvaluationRepository,
    private val fanExpectationsRepository: FanExpectationsRepository,
    private val sponsorsRepository: SponsorsRepository,
    private val staffRepository: StaffRepository,
    private val managersRepository: ManagersRepository,
    private val gameStatesRepository: GameStatesRepository,
    private val settingsManager: SettingsManager
) {

    /**
     * Complete game initialization
     * Called at application startup and when starting a new game
     */
    suspend fun initializeGame() {
        android.util.Log.i("AFM2026", "🚀 Starting complete game initialization...")

        try {
            // ============ STEP 1: Core Data ============
            android.util.Log.i("AFM2026", "📊 Initializing core data...")

            // Initialize journalists (African football media)
            journalistsRepository.initializeAfricanJournalists()
            android.util.Log.i("AFM2026", "✅ Journalists initialized")

            // Initialize player archetypes
            archetypeTraitsRepository.initializeDefaultArchetypes()
            android.util.Log.i("AFM2026", "✅ Player archetypes initialized")

            // Initialize personality types
            personalityTypesRepository.initializeDefaultPersonalities()
            android.util.Log.i("AFM2026", "✅ Personality types initialized")

            // Initialize settings
            settingsManager.initializeSettings()
            android.util.Log.i("AFM2026", "✅ Settings initialized")

            // ============ STEP 2: ELO History ============
            android.util.Log.i("AFM2026", "📊 Initializing Elo history from prepopulated team ratings...")

            eloHistoryRepository.initializeAllElo()
            eloHistoryRepository.syncWithTeamsTable()
            android.util.Log.i("AFM2026", "✅ Elo history initialized")

            // ============ STEP 3: Prize Money ============
            android.util.Log.i("AFM2026", "📊 Initializing prize money distribution...")

            prizesRepository.initializeAllPrizes()
            android.util.Log.i("AFM2026", "✅ Prize money initialized")

            // ============ STEP 4: Season Finances ============
            android.util.Log.i("AFM2026", "📊 Initializing season finances...")

            val currentSeason = "2024/25"
            financesRepository.initializeSeasonFinances(currentSeason)
            android.util.Log.i("AFM2026", "✅ Season finances initialized")

            // ============ STEP 5: Transfer Windows ============
            android.util.Log.i("AFM2026", "📊 Initializing transfer windows...")

            transferWindowsRepository.initializeSeasonWindows(currentSeason)
            android.util.Log.i("AFM2026", "✅ Transfer windows initialized")

            // ============ STEP 6: Board & Fan Expectations ============
            android.util.Log.i("AFM2026", "📊 Initializing board and fan expectations...")

            initializeBoardAndFanExpectations()
            android.util.Log.i("AFM2026", "✅ Board and fan expectations initialized")

            // ============ STEP 7: Generate Season Fixtures ============
            android.util.Log.i("AFM2026", "📊 Generating season fixtures...")

            generateSeasonFixtures(currentSeason)
            android.util.Log.i("AFM2026", "✅ Season fixtures generated")

            // ============ STEP 8: Verification ============
            android.util.Log.i("AFM2026", "📊 Verifying initialization...")
            verifyInitialization()

            android.util.Log.i("AFM2026", "🎉 Game initialization complete!")

        } catch (e: Exception) {
            android.util.Log.e("AFM2026", "❌ Game initialization failed", e)
        }
    }

    private suspend fun initializeBoardAndFanExpectations() {
        val teams = teamsRepository.getAllTeams().firstOrNull() ?: return

        teams.forEach { team ->
            boardEvaluationRepository.initializeBoardEvaluation(team.name)
            fanExpectationsRepository.initializeFanExpectations(team.name)
        }
    }

    private suspend fun generateSeasonFixtures(season: String) {
        val leagues = leaguesRepository.getAllLeagues().firstOrNull() ?: return
        val startDate = "2024-08-01 15:00"

        leagues.forEach { league ->
            val teams = teamsRepository.getTeamsByLeague(league.name).firstOrNull()
            if (!teams.isNullOrEmpty()) {
                fixturesRepository.generateLeagueFixtures(
                    league = league,
                    season = season,
                    teams = teams,
                    startDate = startDate,
                    daysBetweenRounds = 7
                )
            }
        }
    }

    private suspend fun verifyInitialization() {
        val teamsCount = teamsRepository.getAllTeams().firstOrNull()?.size ?: 0
        val playersCount = playersRepository.getAllPlayers().firstOrNull()?.size ?: 0
        val fixturesCount = fixturesRepository.getAllFixtures().firstOrNull()?.size ?: 0

        android.util.Log.i("AFM2026", "📊 Teams: $teamsCount")
        android.util.Log.i("AFM2026", "📊 Players: $playersCount")
        android.util.Log.i("AFM2026", "📊 Fixtures: $fixturesCount")

        // Check Elo history sync
        val eloCount = eloHistoryRepository.getAllEloHistory().firstOrNull()?.size ?: 0
        android.util.Log.i("AFM2026", "📊 Elo history: $eloCount (${if (eloCount == teamsCount) "✅ SYNCED" else "❌ MISMATCH"})")

        // Check top teams
        val topTeams = eloHistoryRepository.getTopEloTeams(5).firstOrNull() ?: emptyList()
        android.util.Log.i("AFM2026", "🏆 Top 5 teams by Elo rating:")
        topTeams.forEachIndexed { index, team ->
            android.util.Log.i("AFM2026", "   ${index + 1}. ${team.teamName}: ${team.currentElo}")
        }
    }

    /**
     * Initialize a new save game for a manager
     */
    suspend fun initializeNewSave(
        managerId: Int,
        managerName: String,
        teamId: Int,
        teamName: String,
        saveName: String
    ) {
        android.util.Log.i("AFM2026", "💾 Creating new save: $saveName")

        // Create game state
        val gameState = gameStatesRepository.createNewSave(
            managerId = managerId,
            managerName = managerName,
            teamId = teamId,
            teamName = teamName,
            saveName = saveName,
            season = "2024/25",
            week = 1
        )

        android.util.Log.i("AFM2026", "✅ New save created with ID: ${gameState.id}")
    }
}