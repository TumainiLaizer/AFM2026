package com.fameafrica.afm2026.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fameafrica.afm2026.data.database.entities.PlayersEntity
import com.fameafrica.afm2026.data.database.entities.TeamsEntity
import com.fameafrica.afm2026.data.database.entities.ManagersEntity
import com.fameafrica.afm2026.data.database.entities.FixturesEntity
import com.fameafrica.afm2026.data.database.entities.LeaguesEntity
import com.fameafrica.afm2026.data.database.entities.CupsEntity
import com.fameafrica.afm2026.data.database.entities.ArchetypeTraitsEntity
import com.fameafrica.afm2026.data.database.entities.BoardEvaluationEntity
import com.fameafrica.afm2026.data.database.entities.BoardRequestsEntity
import com.fameafrica.afm2026.data.database.entities.ClubLegendsEntity
import com.fameafrica.afm2026.data.database.entities.CommunityShieldEntity
import com.fameafrica.afm2026.data.database.entities.CupBracketsEntity
import com.fameafrica.afm2026.data.database.entities.CupGroupStandingsEntity
import com.fameafrica.afm2026.data.database.entities.CurrencyExchangeRatesEntity
import com.fameafrica.afm2026.data.database.entities.EloHistoryEntity
import com.fameafrica.afm2026.data.database.entities.FanExpectationsEntity
import com.fameafrica.afm2026.data.database.entities.FanReactionsEntity
import com.fameafrica.afm2026.data.database.entities.FinancesEntity
import com.fameafrica.afm2026.data.database.entities.FixturesResultsEntity
import com.fameafrica.afm2026.data.database.entities.GameSettingsEntity
import com.fameafrica.afm2026.data.database.entities.GameStatesEntity
import com.fameafrica.afm2026.data.database.entities.InfrastructureUpgradesEntity
import com.fameafrica.afm2026.data.database.entities.InterviewsEntity
import com.fameafrica.afm2026.data.database.entities.JournalistsEntity
import com.fameafrica.afm2026.data.database.entities.KnockoutMatchesEntity
import com.fameafrica.afm2026.data.database.entities.LeagueStandingsEntity
import com.fameafrica.afm2026.data.database.entities.ManagerOffersEntity
import com.fameafrica.afm2026.data.database.entities.ManagerOffersForRetiredPlayersEntity
import com.fameafrica.afm2026.data.database.entities.MatchCommentaryEntity
import com.fameafrica.afm2026.data.database.entities.MatchEventsEntity
import com.fameafrica.afm2026.data.database.entities.MatchFixingCasesEntity
import com.fameafrica.afm2026.data.database.entities.NationalTeamPlayersEntity
import com.fameafrica.afm2026.data.database.entities.NationalTeamsEntity
import com.fameafrica.afm2026.data.database.entities.NationalitiesEntity
import com.fameafrica.afm2026.data.database.entities.NewsEntity
import com.fameafrica.afm2026.data.database.entities.NotificationsEntity
import com.fameafrica.afm2026.data.database.entities.ObjectivesEntity
import com.fameafrica.afm2026.data.database.entities.PersonalityTypesEntity
import com.fameafrica.afm2026.data.database.entities.PlayerAgentsEntity
import com.fameafrica.afm2026.data.database.entities.PlayerContractsEntity
import com.fameafrica.afm2026.data.database.entities.PlayerLoansEntity
import com.fameafrica.afm2026.data.database.entities.PlayerReactionsEntity
import com.fameafrica.afm2026.data.database.entities.PlayerTrainingEntity
import com.fameafrica.afm2026.data.database.entities.PreseasonScheduleEntity
import com.fameafrica.afm2026.data.database.entities.PressConferencesEntity
import com.fameafrica.afm2026.data.database.entities.PrizesCupEntity
import com.fameafrica.afm2026.data.database.entities.PrizesLeaguesEntity
import com.fameafrica.afm2026.data.database.entities.RefereesEntity
import com.fameafrica.afm2026.data.database.entities.RegionalSettingsEntity
import com.fameafrica.afm2026.data.database.entities.ScoutAssignmentsEntity
import com.fameafrica.afm2026.data.database.entities.SeasonAwardsEntity
import com.fameafrica.afm2026.data.database.entities.SeasonHistoryEntity
import com.fameafrica.afm2026.data.database.entities.SettingsHistoryEntity
import com.fameafrica.afm2026.data.database.entities.SponsorsEntity
import com.fameafrica.afm2026.data.database.entities.StaffEntity
import com.fameafrica.afm2026.data.database.entities.TacticsEntity
import com.fameafrica.afm2026.data.database.entities.TransferWindowsEntity
import com.fameafrica.afm2026.data.database.entities.TransfersEntity
import com.fameafrica.afm2026.data.database.entities.TrophiesEntity
import com.fameafrica.afm2026.data.database.entities.UserAnalyticsEntity
import com.fameafrica.afm2026.data.database.entities.UserPreferencesEntity
import com.fameafrica.afm2026.data.database.dao.PlayersDao
import com.fameafrica.afm2026.data.database.dao.TeamsDao
import com.fameafrica.afm2026.data.database.dao.ManagersDao
import com.fameafrica.afm2026.data.database.dao.FixturesDao
import com.fameafrica.afm2026.data.database.dao.LeaguesDao
import com.fameafrica.afm2026.data.database.dao.CupsDao
import com.fameafrica.afm2026.data.database.dao.ArchetypeTraitsDao
import com.fameafrica.afm2026.data.database.dao.BoardEvaluationDao
import com.fameafrica.afm2026.data.database.dao.BoardRequestsDao
import com.fameafrica.afm2026.data.database.dao.ClubLegendsDao
import com.fameafrica.afm2026.data.database.dao.CommunityShieldDao
import com.fameafrica.afm2026.data.database.dao.CupBracketsDao
import com.fameafrica.afm2026.data.database.dao.CupGroupStandingsDao
import com.fameafrica.afm2026.data.database.dao.CurrencyExchangeRatesDao
import com.fameafrica.afm2026.data.database.dao.EloHistoryDao
import com.fameafrica.afm2026.data.database.dao.FanExpectationsDao
import com.fameafrica.afm2026.data.database.dao.FanReactionsDao
import com.fameafrica.afm2026.data.database.dao.FinancesDao
import com.fameafrica.afm2026.data.database.dao.FixturesResultsDao
import com.fameafrica.afm2026.data.database.dao.GameSettingsDao
import com.fameafrica.afm2026.data.database.dao.GameStatesDao
import com.fameafrica.afm2026.data.database.dao.InfrastructureUpgradesDao
import com.fameafrica.afm2026.data.database.dao.InterviewsDao
import com.fameafrica.afm2026.data.database.dao.JournalistsDao
import com.fameafrica.afm2026.data.database.dao.KnockoutMatchesDao
import com.fameafrica.afm2026.data.database.dao.LeagueStandingsDao
import com.fameafrica.afm2026.data.database.dao.ManagerOffersDao
import com.fameafrica.afm2026.data.database.dao.ManagerOffersForRetiredPlayersDao
import com.fameafrica.afm2026.data.database.dao.MatchCommentaryDao
import com.fameafrica.afm2026.data.database.dao.MatchEventsDao
import com.fameafrica.afm2026.data.database.dao.MatchFixingCasesDao
import com.fameafrica.afm2026.data.database.dao.NationalTeamPlayersDao
import com.fameafrica.afm2026.data.database.dao.NationalTeamsDao
import com.fameafrica.afm2026.data.database.dao.NationalitiesDao
import com.fameafrica.afm2026.data.database.dao.NewsDao
import com.fameafrica.afm2026.data.database.dao.NotificationsDao
import com.fameafrica.afm2026.data.database.dao.ObjectivesDao
import com.fameafrica.afm2026.data.database.dao.PersonalityTypesDao
import com.fameafrica.afm2026.data.database.dao.PlayerAgentsDao
import com.fameafrica.afm2026.data.database.dao.PlayerContractsDao
import com.fameafrica.afm2026.data.database.dao.PlayerLoansDao
import com.fameafrica.afm2026.data.database.dao.PlayerReactionsDao
import com.fameafrica.afm2026.data.database.dao.PlayerTrainingDao
import com.fameafrica.afm2026.data.database.dao.PreseasonScheduleDao
import com.fameafrica.afm2026.data.database.dao.PressConferencesDao
import com.fameafrica.afm2026.data.database.dao.PrizesCupDao
import com.fameafrica.afm2026.data.database.dao.PrizesLeaguesDao
import com.fameafrica.afm2026.data.database.dao.RefereesDao
import com.fameafrica.afm2026.data.database.dao.RegionalSettingsDao
import com.fameafrica.afm2026.data.database.dao.ScoutAssignmentsDao
import com.fameafrica.afm2026.data.database.dao.SeasonAwardsDao
import com.fameafrica.afm2026.data.database.dao.SeasonHistoryDao
import com.fameafrica.afm2026.data.database.dao.SettingsHistoryDao
import com.fameafrica.afm2026.data.database.dao.SponsorsDao
import com.fameafrica.afm2026.data.database.dao.StaffDao
import com.fameafrica.afm2026.data.database.dao.TacticsDao
import com.fameafrica.afm2026.data.database.dao.TransferWindowsDao
import com.fameafrica.afm2026.data.database.dao.TransfersDao
import com.fameafrica.afm2026.data.database.dao.TrophiesDao
import com.fameafrica.afm2026.data.database.dao.UserAnalyticsDao
import com.fameafrica.afm2026.data.database.dao.UserPreferencesDao
import com.fameafrica.afm2026.data.database.entities.Specialization
import com.fameafrica.afm2026.data.database.entities.StaffRole

@Database(
    entities = [
        PlayersEntity::class,
        TeamsEntity::class,
        ManagersEntity::class,
        FixturesEntity::class,
        LeaguesEntity::class,
        CupsEntity::class,
        ArchetypeTraitsEntity::class,
        BoardEvaluationEntity::class,
        BoardRequestsEntity::class,
        ClubLegendsEntity::class,
        CommunityShieldEntity::class,
        CupBracketsEntity::class,
        CupGroupStandingsEntity::class,
        CurrencyExchangeRatesEntity::class,
        EloHistoryEntity::class,
        FanExpectationsEntity::class,
        FanReactionsEntity::class,
        FinancesEntity::class,
        FixturesResultsEntity::class,
        GameSettingsEntity::class,
        GameStatesEntity::class,
        InfrastructureUpgradesEntity::class,
        InterviewsEntity::class,
        JournalistsEntity::class,
        KnockoutMatchesEntity::class,
        LeagueStandingsEntity::class,
        ManagerOffersEntity::class,
        ManagerOffersForRetiredPlayersEntity::class,
        MatchCommentaryEntity::class,
        MatchEventsEntity::class,
        MatchFixingCasesEntity::class,
        NationalTeamPlayersEntity::class,
        NationalTeamsEntity::class,
        NationalitiesEntity::class,
        NewsEntity::class,
        NotificationsEntity::class,
        ObjectivesEntity::class,
        PersonalityTypesEntity::class,
        PlayerAgentsEntity::class,
        PlayerContractsEntity::class,
        PlayerLoansEntity::class,
        PlayerReactionsEntity::class,
        PlayerTrainingEntity::class,
        PreseasonScheduleEntity::class,
        PressConferencesEntity::class,
        PrizesCupEntity::class,
        PrizesLeaguesEntity::class,
        RefereesEntity::class,
        RegionalSettingsEntity::class,
        ScoutAssignmentsEntity::class,
        SeasonAwardsEntity::class,
        SeasonHistoryEntity::class,
        SettingsHistoryEntity::class,
        SponsorsEntity::class,
        StaffEntity::class,
        TacticsEntity::class,
        TransferWindowsEntity::class,
        TransfersEntity::class,
        TrophiesEntity::class,
        UserAnalyticsEntity::class,
        UserPreferencesEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AFMDatabase : RoomDatabase() {
    abstract fun playersDao(): PlayersDao
    abstract fun teamsDao(): TeamsDao
    abstract fun managersDao(): ManagersDao
    abstract fun fixturesDao(): FixturesDao
    abstract fun leaguesDao(): LeaguesDao
    abstract fun cupsDao(): CupsDao
    abstract fun archetypeTraitsDao(): ArchetypeTraitsDao
    abstract fun boardEvaluationDao(): BoardEvaluationDao
    abstract fun boardRequestsDao(): BoardRequestsDao
    abstract fun clubLegendsDao(): ClubLegendsDao
    abstract fun communityShieldDao(): CommunityShieldDao
    abstract fun cupBracketsDao(): CupBracketsDao
    abstract fun cupGroupStandingsDao(): CupGroupStandingsDao
    abstract fun currencyExchangeRatesDao(): CurrencyExchangeRatesDao
    abstract fun eloHistoryDao(): EloHistoryDao
    abstract fun fanExpectationsDao(): FanExpectationsDao
    abstract fun fanReactionsDao(): FanReactionsDao
    abstract fun financesDao(): FinancesDao
    abstract fun fixturesResultsDao(): FixturesResultsDao
    abstract fun gameSettingsDao(): GameSettingsDao
    abstract fun gameStatesDao(): GameStatesDao
    abstract fun infrastructureUpgradesDao(): InfrastructureUpgradesDao
    abstract fun interviewsDao(): InterviewsDao
    abstract fun journalistsDao(): JournalistsDao
    abstract fun knockoutMatchesDao(): KnockoutMatchesDao
    abstract fun leagueStandingsDao(): LeagueStandingsDao
    abstract fun managerOffersDao(): ManagerOffersDao
    abstract fun managerOffersForRetiredPlayersDao(): ManagerOffersForRetiredPlayersDao
    abstract fun matchCommentaryDao(): MatchCommentaryDao
    abstract fun matchEventsDao(): MatchEventsDao
    abstract fun matchFixingCasesDao(): MatchFixingCasesDao
    abstract fun nationalTeamPlayersDao(): NationalTeamPlayersDao
    abstract fun nationalTeamsDao(): NationalTeamsDao
    abstract fun nationalitiesDao(): NationalitiesDao
    abstract fun newsDao(): NewsDao
    abstract fun notificationsDao(): NotificationsDao
    abstract fun objectivesDao(): ObjectivesDao
    abstract fun personalityTypesDao(): PersonalityTypesDao
    abstract fun playerAgentsDao(): PlayerAgentsDao
    abstract fun playerContractsDao(): PlayerContractsDao
    abstract fun playerLoansDao(): PlayerLoansDao
    abstract fun playerReactionsDao(): PlayerReactionsDao
    abstract fun playerTrainingDao(): PlayerTrainingDao
    abstract fun preseasonScheduleDao(): PreseasonScheduleDao
    abstract fun pressConferencesDao(): PressConferencesDao
    abstract fun prizesCupDao(): PrizesCupDao
    abstract fun prizesLeaguesDao(): PrizesLeaguesDao
    abstract fun refereesDao(): RefereesDao
    abstract fun regionalSettingsDao(): RegionalSettingsDao
    abstract fun scoutAssignmentsDao(): ScoutAssignmentsDao
    abstract fun seasonAwardsDao(): SeasonAwardsDao
    abstract fun seasonHistoryDao(): SeasonHistoryDao
    abstract fun settingsHistoryDao(): SettingsHistoryDao
    abstract fun sponsorsDao(): SponsorsDao
    abstract fun staffDao(): StaffDao
    abstract fun tacticsDao(): TacticsDao
    abstract fun transferWindowsDao(): TransferWindowsDao
    abstract fun transfersDao(): TransfersDao
    abstract fun trophiesDao(): TrophiesDao
    abstract fun userAnalyticsDao(): UserAnalyticsDao
    abstract fun userPreferencesDao(): UserPreferencesDao
}


