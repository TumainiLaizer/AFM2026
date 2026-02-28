package com.fameafrica.afm2026.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fameafrica.afm2026.data.database.AFMDatabase
import com.fameafrica.afm2026.data.database.dao.*
import com.fameafrica.afm2026.data.initializer.GameInitializer
import com.fameafrica.afm2026.data.repository.*
import com.fameafrica.afm2026.utils.SettingsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ============ DATABASE ============

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AFMDatabase {

        // CRITICAL: Enable Foreign Key constraints
        val fkCallback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.execSQL("PRAGMA foreign_keys=ON;")
                Log.i("AFM2026", "✅ Foreign key constraints ENABLED on database open")
            }

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("PRAGMA foreign_keys=ON;")
                Log.i("AFM2026", "✅ Foreign key constraints ENABLED on database creation")
            }
        }

        return Room.databaseBuilder(
            context,
            AFMDatabase::class.java,
            "afm2026.db"
        )
            .createFromAsset("databases/afm2026_prepopulated.db")
            .addCallback(fkCallback)
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideDatabaseScope(): CoroutineScope = CoroutineScope(Dispatchers.IO)

    // ============ DAOs ============

    @Provides
    @Singleton
    fun provideNationalitiesDao(database: AFMDatabase) = database.nationalitiesDao()

    @Provides
    @Singleton
    fun provideRefereesDao(database: AFMDatabase) = database.refereesDao()

    @Provides
    @Singleton
    fun provideLeaguesDao(database: AFMDatabase) = database.leaguesDao()

    @Provides
    @Singleton
    fun provideCupsDao(database: AFMDatabase) = database.cupsDao()

    @Provides
    @Singleton
    fun provideTeamsDao(database: AFMDatabase) = database.teamsDao()

    @Provides
    @Singleton
    fun providePlayersDao(database: AFMDatabase) = database.playersDao()

    @Provides
    @Singleton
    fun provideManagersDao(database: AFMDatabase) = database.managersDao()

    @Provides
    @Singleton
    fun provideStaffDao(database: AFMDatabase) = database.staffDao()

    @Provides
    @Singleton
    fun provideFixturesDao(database: AFMDatabase) = database.fixturesDao()

    @Provides
    @Singleton
    fun provideFixturesResultsDao(database: AFMDatabase) = database.fixturesResultsDao()

    @Provides
    @Singleton
    fun provideMatchEventsDao(database: AFMDatabase) = database.matchEventsDao()

    @Provides
    @Singleton
    fun provideLeagueStandingsDao(database: AFMDatabase) = database.leagueStandingsDao()

    @Provides
    @Singleton
    fun provideCupGroupStandingsDao(database: AFMDatabase) = database.cupGroupStandingsDao()

    @Provides
    @Singleton
    fun provideCupBracketsDao(database: AFMDatabase) = database.cupBracketsDao()

    @Provides
    @Singleton
    fun provideKnockoutMatchesDao(database: AFMDatabase) = database.knockoutMatchesDao()

    @Provides
    @Singleton
    fun provideJournalistsDao(database: AFMDatabase) = database.journalistsDao()

    @Provides
    @Singleton
    fun provideNewsDao(database: AFMDatabase) = database.newsDao()

    @Provides
    @Singleton
    fun providePressConferencesDao(database: AFMDatabase) = database.pressConferencesDao()

    @Provides
    @Singleton
    fun provideInterviewsDao(database: AFMDatabase) = database.interviewsDao()

    @Provides
    @Singleton
    fun provideTransfersDao(database: AFMDatabase) = database.transfersDao()

    @Provides
    @Singleton
    fun provideTransferWindowsDao(database: AFMDatabase) = database.transferWindowsDao()

    @Provides
    @Singleton
    fun provideScoutAssignmentsDao(database: AFMDatabase) = database.scoutAssignmentsDao()

    @Provides
    @Singleton
    fun providePlayerContractsDao(database: AFMDatabase) = database.playerContractsDao()

    @Provides
    @Singleton
    fun providePlayerLoansDao(database: AFMDatabase) = database.playerLoansDao()

    @Provides
    @Singleton
    fun providePlayerAgentsDao(database: AFMDatabase) = database.playerAgentsDao()

    @Provides
    @Singleton
    fun providePlayerTrainingDao(database: AFMDatabase) = database.playerTrainingDao()

    @Provides
    @Singleton
    fun provideFinancesDao(database: AFMDatabase) = database.financesDao()

    @Provides
    @Singleton
    fun provideInfrastructureUpgradesDao(database: AFMDatabase) = database.infrastructureUpgradesDao()

    @Provides
    @Singleton
    fun provideNationalTeamsDao(database: AFMDatabase) = database.nationalTeamsDao()

    @Provides
    @Singleton
    fun provideNationalTeamPlayersDao(database: AFMDatabase) = database.nationalTeamPlayersDao()

    @Provides
    @Singleton
    fun providePrizesLeaguesDao(database: AFMDatabase) = database.prizesLeaguesDao()

    @Provides
    @Singleton
    fun providePrizesCupDao(database: AFMDatabase) = database.prizesCupDao()

    @Provides
    @Singleton
    fun provideEloHistoryDao(database: AFMDatabase) = database.eloHistoryDao()

    @Provides
    @Singleton
    fun provideBoardEvaluationDao(database: AFMDatabase) = database.boardEvaluationDao()

    @Provides
    @Singleton
    fun provideBoardRequestsDao(database: AFMDatabase) = database.boardRequestsDao()

    @Provides
    @Singleton
    fun provideFanExpectationsDao(database: AFMDatabase) = database.fanExpectationsDao()

    @Provides
    @Singleton
    fun provideFanReactionsDao(database: AFMDatabase) = database.fanReactionsDao()

    @Provides
    @Singleton
    fun provideObjectivesDao(database: AFMDatabase) = database.objectivesDao()

    @Provides
    @Singleton
    fun providePreseasonScheduleDao(database: AFMDatabase) = database.preseasonScheduleDao()

    @Provides
    @Singleton
    fun provideClubLegendsDao(database: AFMDatabase) = database.clubLegendsDao()

    @Provides
    @Singleton
    fun provideCommunityShieldDao(database: AFMDatabase) = database.communityShieldDao()

    @Provides
    @Singleton
    fun provideManagerOffersDao(database: AFMDatabase) = database.managerOffersDao()

    @Provides
    @Singleton
    fun provideManagerOffersForRetiredPlayersDao(database: AFMDatabase) = database.managerOffersForRetiredPlayersDao()

    @Provides
    @Singleton
    fun provideTrophiesDao(database: AFMDatabase) = database.trophiesDao()

    @Provides
    @Singleton
    fun provideMatchFixingCasesDao(database: AFMDatabase) = database.matchFixingCasesDao()

    @Provides
    @Singleton
    fun provideArchetypeTraitsDao(database: AFMDatabase) = database.archetypeTraitsDao()

    @Provides
    @Singleton
    fun providePersonalityTypesDao(database: AFMDatabase) = database.personalityTypesDao()

    // ============ SETTINGS DAOs ============

    @Provides
    @Singleton
    fun provideRegionalSettingsDao(database: AFMDatabase) = database.regionalSettingsDao()

    @Provides
    @Singleton
    fun provideCurrencyExchangeRatesDao(database: AFMDatabase) = database.currencyExchangeRatesDao()

    @Provides
    @Singleton
    fun provideGameSettingsDao(database: AFMDatabase) = database.gameSettingsDao()

    @Provides
    @Singleton
    fun provideSettingsHistoryDao(database: AFMDatabase) = database.settingsHistoryDao()

    @Provides
    @Singleton
    fun provideGameStatesDao(database: AFMDatabase) = database.gameStatesDao()

    @Provides
    @Singleton
    fun provideUserAnalyticsDao(database: AFMDatabase) = database.userAnalyticsDao()

    @Provides
    @Singleton
    fun provideUserPreferencesDao(database: AFMDatabase) = database.userPreferencesDao()

// ============ SETTINGS MANAGER ============

    @Provides
    @Singleton
    fun provideSettingsManager(
        gameSettingsDao: GameSettingsDao,
        regionalSettingsDao: RegionalSettingsDao,
        currencyExchangeRatesDao: CurrencyExchangeRatesDao,
        settingsHistoryDao: SettingsHistoryDao,
        userAnalyticsDao: UserAnalyticsDao,
        userPreferencesDao: UserPreferencesDao,
        @ApplicationContext context: Context
    ): SettingsManager {
        return SettingsManager(
            gameSettingsDao = gameSettingsDao,
            regionalSettingsDao = regionalSettingsDao,
            currencyExchangeRatesDao = currencyExchangeRatesDao,
            settingsHistoryDao = settingsHistoryDao,
            userPreferencesDao = userPreferencesDao,
            context = context,
            userAnalyticsDao = userAnalyticsDao
        )
    }

    // ============ REPOSITORIES ============

    @Provides
    @Singleton
    fun provideNationalitiesRepository(
        nationalitiesDao: NationalitiesDao
    ) = NationalitiesRepository(nationalitiesDao)

    @Provides
    @Singleton
    fun provideRefereesRepository(
        refereesDao: RefereesDao
    ) = RefereesRepository(refereesDao)

    @Provides
    @Singleton
    fun provideLeaguesRepository(
        leaguesDao: LeaguesDao
    ) = LeaguesRepository(leaguesDao)

    @Provides
    @Singleton
    fun provideCupsRepository(
        cupsDao: CupsDao
    ) = CupsRepository(cupsDao)

    @Provides
    @Singleton
    fun provideTeamsRepository(
        teamsDao: TeamsDao,
        playersRepository: PlayersRepository
    ) = TeamsRepository(teamsDao, playersRepository)

    @Provides
    @Singleton
    fun providePlayersRepository(
        playersDao: PlayersDao
    ) = PlayersRepository(playersDao)

    @Provides
    @Singleton
    fun provideManagersRepository(
        managersDao: ManagersDao
    ) = ManagersRepository(managersDao)

    @Provides
    @Singleton
    fun provideStaffRepository(
        staffDao: StaffDao,
        teamsRepository: TeamsRepository,
        playersRepository: PlayersRepository
    ) = StaffRepository(
        staffDao,
        teamsRepository,
        playersRepository
    )

    @Provides
    @Singleton
    fun provideFixturesRepository(
        fixturesDao: FixturesDao,
        teamsRepository: TeamsRepository,
        leaguesRepository: LeaguesRepository,
        cupsRepository: CupsRepository,
        refereesRepository: RefereesRepository
    ) = FixturesRepository(
        fixturesDao,
        teamsRepository,
        leaguesRepository,
        cupsRepository,
        refereesRepository
    )

    @Provides
    @Singleton
    fun provideFixturesResultsRepository(
        fixturesResultsDao: FixturesResultsDao,
        fixturesRepository: FixturesRepository,
        teamsRepository: TeamsRepository,
        playersRepository: PlayersRepository,
        refereesRepository: RefereesRepository,
        leagueStandingsRepository: LeagueStandingsRepository,
        cupGroupStandingsRepository: CupGroupStandingsRepository,
        matchEventsRepository: MatchEventsRepository
    ) = FixturesResultsRepository(
        fixturesResultsDao,
        fixturesRepository,
        teamsRepository,
        playersRepository,
        refereesRepository,
        leagueStandingsRepository,
        cupGroupStandingsRepository,
        matchEventsRepository
    )

    @Provides
    @Singleton
    fun provideMatchEventsRepository(
        matchEventsDao: MatchEventsDao,
        playersRepository: PlayersRepository,
        fixturesRepository: FixturesRepository
    ) = MatchEventsRepository(
        matchEventsDao,
        playersRepository,
        fixturesRepository
    )

    @Provides
    @Singleton
    fun provideLeagueStandingsRepository(
        leagueStandingsDao: LeagueStandingsDao
    ) = LeagueStandingsRepository(leagueStandingsDao)

    @Provides
    @Singleton
    fun provideCupGroupStandingsRepository(
        cupGroupStandingsDao: CupGroupStandingsDao
    ) = CupGroupStandingsRepository(cupGroupStandingsDao)

    @Provides
    @Singleton
    fun provideCupBracketsRepository(
        cupBracketsDao: CupBracketsDao,
        cupsRepository: CupsRepository,
        teamsRepository: TeamsRepository,
        fixturesRepository: FixturesRepository
    ) = CupBracketsRepository(
        cupBracketsDao,
        cupsRepository,
        teamsRepository,
        fixturesRepository,
        fixturesRepository = fixturesRepository
    )

    @Provides
    @Singleton
    fun provideKnockoutMatchesRepository(
        knockoutMatchesDao: KnockoutMatchesDao,
        cupsRepository: CupsRepository,
        teamsRepository: TeamsRepository,
        fixturesRepository: FixturesRepository,
        cupBracketsRepository: CupBracketsRepository,
        refereesRepository: RefereesRepository
    ) = KnockoutMatchesRepository(
        knockoutMatchesDao,
        cupsRepository,
        teamsRepository,
        fixturesRepository,
        cupBracketsRepository,
        refereesRepository
    )

    @Provides
    @Singleton
    fun provideJournalistsRepository(
        journalistsDao: JournalistsDao
    ) = JournalistsRepository(journalistsDao)

    @Provides
    @Singleton
    fun provideNewsRepository(
        newsDao: NewsDao
    ) = NewsRepository(newsDao)

    @Provides
    @Singleton
    fun providePressConferencesRepository(
        pressConferencesDao: PressConferencesDao,
        journalistsRepository: JournalistsRepository,
        managersRepository: ManagersRepository,
        newsRepository: NewsRepository
    ) = PressConferencesRepository(
        pressConferencesDao,
        journalistsRepository,
        managersRepository,
        newsRepository
    )

    @Provides
    @Singleton
    fun provideInterviewsRepository(
        interviewsDao: InterviewsDao,
        journalistsRepository: JournalistsRepository,
        managersRepository: ManagersRepository,
        playersRepository: PlayersRepository,
        newsRepository: NewsRepository
    ) = InterviewsRepository(
        interviewsDao,
        journalistsRepository,
        managersRepository,
        playersRepository,
        newsRepository
    )

    @Provides
    @Singleton
    fun provideTransfersRepository(
        transfersDao: TransfersDao,
        playersRepository: PlayersRepository,
        teamsRepository: TeamsRepository,
        newsRepository: NewsRepository,
        leaguesRepository: LeaguesRepository,
        transferWindowsRepository: TransferWindowsRepository
    ) = TransfersRepository(
        transfersDao,
        playersRepository,
        teamsRepository,
        leaguesRepository,
        newsRepository,
        transferWindowsRepository
    )

    @Provides
    @Singleton
    fun provideTransferWindowsRepository(
        transferWindowsDao: TransferWindowsDao,
        teamsRepository: TeamsRepository,
        playersRepository: PlayersRepository,
        leaguesRepository: LeaguesRepository
    ) = TransferWindowsRepository(
        transferWindowsDao,
        teamsRepository,
        playersRepository,
        leaguesRepository
    )

    @Provides
    @Singleton
    fun provideScoutAssignmentsRepository(
        scoutAssignmentsDao: ScoutAssignmentsDao,
        staffRepository: StaffRepository,
        playersRepository: PlayersRepository
    ) = ScoutAssignmentsRepository(
        scoutAssignmentsDao,
        staffRepository,
        playersRepository
    )

    @Provides
    @Singleton
    fun providePlayerContractsRepository(
        playerContractsDao: PlayerContractsDao
    ) = PlayerContractsRepository(playerContractsDao)

    @Provides
    @Singleton
    fun providePlayerLoansRepository(
        playerLoansDao: PlayerLoansDao
    ) = PlayerLoansRepository(playerLoansDao)

    @Provides
    @Singleton
    fun providePlayerAgentsRepository(
        playerAgentsDao: PlayerAgentsDao
    ) = PlayerAgentsRepository(playerAgentsDao)

    @Provides
    @Singleton
    fun providePlayerTrainingRepository(
        playerTrainingDao: PlayerTrainingDao,
        playersRepository: PlayersRepository,
        staffRepository: StaffRepository
    ) = PlayerTrainingRepository(
        playerTrainingDao,
        playersRepository,
        staffRepository
    )

    @Provides
    @Singleton
    fun provideFinancesRepository(
        financesDao: FinancesDao,
        teamsDao: TeamsDao,
        leaguesDao: LeaguesDao
    ) = FinancesRepository(financesDao, teamsDao, leaguesDao)

    @Provides
    @Singleton
    fun provideInfrastructureUpgradesRepository(
        infrastructureUpgradesDao: InfrastructureUpgradesDao,
        teamsDao: TeamsDao,
        financesDao: FinancesDao,
        financesRepository: FinancesRepository
    ) = InfrastructureUpgradesRepository(
        infrastructureUpgradesDao,
        teamsDao,
        financesDao,
        financesRepository
    )

    @Provides
    @Singleton
    fun provideNationalTeamsRepository(
        nationalTeamsDao: NationalTeamsDao,
        nationalitiesRepository: NationalitiesRepository,
        playersRepository: PlayersRepository,
        nationalTeamPlayersRepository: NationalTeamPlayersRepository
    ) = NationalTeamsRepository(
        nationalTeamsDao,
        nationalitiesRepository,
        playersRepository,
        nationalTeamPlayersRepository
    )

    @Provides
    @Singleton
    fun provideNationalTeamPlayersRepository(
        nationalTeamPlayersDao: NationalTeamPlayersDao,
        playersDao: PlayersDao,
        nationalTeamsDao: NationalTeamsDao
    ) = NationalTeamPlayersRepository(
        nationalTeamPlayersDao,
        playersDao,
        nationalTeamsDao
    )

    @Provides
    @Singleton
    fun providePrizesRepository(
        prizesLeaguesDao: PrizesLeaguesDao,
        prizesCupDao: PrizesCupDao,
        leaguesDao: LeaguesDao,
        cupsDao: CupsDao,
        teamsDao: TeamsDao
    ) = PrizesRepository(
        prizesLeaguesDao,
        prizesCupDao,
        leaguesDao,
        cupsDao,
        teamsDao
    )

    @Provides
    @Singleton
    fun provideEloHistoryRepository(
        eloHistoryDao: EloHistoryDao,
        teamsDao: TeamsDao
    ) = EloHistoryRepository(eloHistoryDao, teamsDao)

    @Provides
    @Singleton
    fun provideBoardEvaluationRepository(
        boardEvaluationDao: BoardEvaluationDao
    ) = BoardEvaluationRepository(boardEvaluationDao)

    @Provides
    @Singleton
    fun provideBoardRequestsRepository(
        boardRequestsDao: BoardRequestsDao
    ) = BoardRequestsRepository(boardRequestsDao)

    @Provides
    @Singleton
    fun provideFanExpectationsRepository(
        fanExpectationsDao: FanExpectationsDao
    ) = FanExpectationsRepository(fanExpectationsDao)

    @Provides
    @Singleton
    fun provideFanReactionsRepository(
        fanReactionsDao: FanReactionsDao
    ) = FanReactionsRepository(fanReactionsDao)

    @Provides
    @Singleton
    fun provideObjectivesRepository(
        objectivesDao: ObjectivesDao
    ) = ObjectivesRepository(objectivesDao)

    @Provides
    @Singleton
    fun providePreseasonScheduleRepository(
        preseasonScheduleDao: PreseasonScheduleDao,
        teamsRepository: TeamsRepository
    ) = PreseasonScheduleRepository(preseasonScheduleDao, teamsRepository)

    @Provides
    @Singleton
    fun provideClubLegendsRepository(
        clubLegendsDao: ClubLegendsDao
    ) = ClubLegendsRepository(clubLegendsDao)

    @Provides
    @Singleton
    fun provideCommunityShieldRepository(
        communityShieldDao: CommunityShieldDao,
        leaguesRepository: LeaguesRepository,
        teamsRepository: TeamsRepository,
        fixturesRepository: FixturesRepository,
        leagueStandingsRepository: LeagueStandingsRepository
    ) = CommunityShieldRepository(
        communityShieldDao,
        leaguesRepository,
        teamsRepository,
        fixturesRepository,
        leagueStandingsRepository
    )

    @Provides
    @Singleton
    fun provideManagerOffersRepository(
        managerOffersDao: ManagerOffersDao,
        managersDao: ManagersDao,
        teamsDao: TeamsDao,
        leaguesDao: LeaguesDao
    ) = ManagerOffersRepository(
        managerOffersDao,
        managersDao,
        teamsDao,
        leaguesDao
    )

    @Provides
    @Singleton
    fun provideManagerOffersForRetiredPlayersRepository(
        managerOffersForRetiredPlayersDao: ManagerOffersForRetiredPlayersDao,
        playersDao: PlayersDao,
        teamsDao: TeamsDao,
        leaguesDao: LeaguesDao
    ) = ManagerOffersForRetiredPlayersRepository(
        managerOffersForRetiredPlayersDao,
        playersDao,
        teamsDao,
        leaguesDao
    )

    @Provides
    @Singleton
    fun provideTrophiesRepository(
        trophiesDao: TrophiesDao,
        managersDao: ManagersDao,
        teamsDao: TeamsDao) = TrophiesRepository(
        trophiesDao,
        managersDao,
        teamsDao
    )


    @Provides
    @Singleton
    fun provideSeasonAwardsRepository(
        seasonAwardsDao: SeasonAwardsDao,
        playersDao: PlayersDao,
        managersDao: ManagersDao,
        teamsDao: TeamsDao,
        leaguesDao: LeaguesDao,
        trophiesRepository: TrophiesRepository
    ) = SeasonAwardsRepository(
        seasonAwardsDao,
        playersDao,
        teamsDao,
        trophiesRepository
    )

    @Provides
    @Singleton
    fun provideSeasonHistoryRepository(
        seasonHistoryDao: SeasonHistoryDao,
        leagueStandingsDao: LeagueStandingsDao, // <-- INJECT it here
        teamsDao: TeamsDao                     // <-- INJECT it here
    ) = SeasonHistoryRepository(
        seasonHistoryDao,
        leagueStandingsDao,
        teamsDao
    )

    @Provides
    @Singleton
    fun provideGameStatesRepository(
        gameStatesDao: GameStatesDao,
        seasonHistoryDao: SeasonHistoryDao
    ) = GameStatesRepository(
        gameStatesDao,
        seasonHistoryDao
    )

    @Provides
    @Singleton
    fun provideSponsorsRepository(
        sponsorsDao: SponsorsDao,
        transferFundingRequestsDao: TransferFundingRequestsDao,
        teamsDao: TeamsDao,
        leaguesDao: LeaguesDao,
        cupsDao: CupsDao,
        financesRepository: FinancesRepository
    ) = SponsorsRepository(
        sponsorsDao,
        transferFundingRequestsDao,
        teamsDao,
        leaguesDao,
        cupsDao,
        financesRepository
    )

    @Provides
    @Singleton
    fun provideMatchFixingCasesRepository(
        matchFixingCasesDao: MatchFixingCasesDao
    ) = MatchFixingCasesRepository(matchFixingCasesDao)

    @Provides
    @Singleton
    fun provideArchetypeTraitsRepository(
        archetypeTraitsDao: ArchetypeTraitsDao
    ) = ArchetypeTraitsRepository(archetypeTraitsDao)

    @Provides
    @Singleton
    fun providePersonalityTypesRepository(
        personalityTypesDao: PersonalityTypesDao
    ) = PersonalityTypesRepository(personalityTypesDao)

    // ============ GAME INITIALIZER ============S

    @Provides
    @Singleton
    fun provideGameInitializer(
        eloHistoryRepository: EloHistoryRepository,
        leaguesRepository: LeaguesRepository,
        cupsRepository: CupsRepository,
        teamsRepository: TeamsRepository,
        playersRepository: PlayersRepository,
        journalistsRepository: JournalistsRepository,
        archetypeTraitsRepository: ArchetypeTraitsRepository,
        personalityTypesRepository: PersonalityTypesRepository,
        prizesRepository: PrizesRepository,
        financesRepository: FinancesRepository,
        fixturesRepository: FixturesRepository,
        transferWindowsRepository: TransferWindowsRepository,
        boardEvaluationRepository: BoardEvaluationRepository,
        fanExpectationsRepository: FanExpectationsRepository,
        sponsorsRepository: SponsorsRepository,
        staffRepository: StaffRepository,
        managersRepository: ManagersRepository,
        gameStatesRepository: GameStatesRepository,
        settingsManager: SettingsManager
    ): GameInitializer {
        return GameInitializer(
            eloHistoryRepository = eloHistoryRepository,
            leaguesRepository = leaguesRepository,
            cupsRepository = cupsRepository,
            teamsRepository = teamsRepository,
            playersRepository = playersRepository,
            journalistsRepository = journalistsRepository,
            archetypeTraitsRepository = archetypeTraitsRepository,
            personalityTypesRepository = personalityTypesRepository,
            prizesRepository = prizesRepository,
            financesRepository = financesRepository,
            fixturesRepository = fixturesRepository,
            transferWindowsRepository = transferWindowsRepository,
            boardEvaluationRepository = boardEvaluationRepository,
            fanExpectationsRepository = fanExpectationsRepository,
            sponsorsRepository = sponsorsRepository,
            staffRepository = staffRepository,
            managersRepository = managersRepository,
            gameStatesRepository = gameStatesRepository,
            settingsManager = settingsManager
        )
    }
}