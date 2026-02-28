package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.CupStatistic
import com.fameafrica.afm2026.data.database.dao.CupWithCountry
import com.fameafrica.afm2026.data.database.dao.CupsDao
import com.fameafrica.afm2026.data.database.entities.CupsEntity
import com.fameafrica.afm2026.data.database.entities.LeaguesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CupsRepository @Inject constructor(
    private val cupsDao: CupsDao
) {

    // ============ BASIC CRUD ============

    fun getAllCups(): Flow<List<CupsEntity>> = cupsDao.getAll()

    suspend fun getCupById(id: Int): CupsEntity? = cupsDao.getById(id)

    suspend fun getCupByName(name: String): CupsEntity? = cupsDao.getByName(name)

    suspend fun insertCup(cup: CupsEntity) = cupsDao.insert(cup)

    suspend fun insertAllCups(cups: List<CupsEntity>) = cupsDao.insertAll(cups)

    suspend fun updateCup(cup: CupsEntity) = cupsDao.update(cup)

    suspend fun deleteCup(cup: CupsEntity) = cupsDao.delete(cup)

    // ============ DOMESTIC CUPS ============

    fun getDomesticCupsByCountry(countryId: Int): Flow<List<CupsEntity>> =
        cupsDao.getDomesticCupsByCountry(countryId)

    fun getDomesticCupsWithCountries(): Flow<List<CupWithCountry>> =
        cupsDao.getDomesticCupsWithCountries()

    // ============ CONTINENTAL CUPS ============

    fun getContinentalCups(): Flow<List<CupsEntity>> = cupsDao.getContinentalCups()

    fun getCAFCompetitions(): Flow<List<CupsEntity>> = cupsDao.getCAFCompetitions()

    // ============ TYPE-BASED ============

    fun getCupsByType(type: String): Flow<List<CupsEntity>> = cupsDao.getCupsByType(type)

    fun getCupTypes(): Flow<List<String>> = cupsDao.getCupTypes()

    // ============ PRIZE MONEY ============

    fun getHighValueCups(minPrize: Int): Flow<List<CupsEntity>> =
        cupsDao.getHighValueCups(minPrize)

    fun getCupsByPrizeRange(minPrize: Int, maxPrize: Int): Flow<List<CupsEntity>> =
        cupsDao.getCupsByPrizeRange(minPrize, maxPrize)

    suspend fun getTotalPrizeMoneyByCountry(countryId: Int): Long? =
        cupsDao.getTotalPrizeMoneyByCountry(countryId)

    // ============ TEAMS INVOLVED ============

    fun getLargeTournaments(minTeams: Int): Flow<List<CupsEntity>> =
        cupsDao.getLargeTournaments(minTeams)

    fun getSmallTournaments(maxTeams: Int): Flow<List<CupsEntity>> =
        cupsDao.getSmallTournaments(maxTeams)

    // ============ SPONSORS ============

    fun getCupsBySponsor(sponsorName: String): Flow<List<CupsEntity>> =
        cupsDao.getCupsBySponsor(sponsorName)

    // ============ STATISTICS ============

    suspend fun getDomesticCupCount(): Int = cupsDao.getDomesticCupCount()

    suspend fun getContinentalCupCount(): Int = cupsDao.getContinentalCupCount()

    fun getCupStatistics(): Flow<List<CupStatistic>> = cupsDao.getCupStatistics()

    // ============ BUSINESS LOGIC ============

    /**
     * Get all competitions (leagues + cups) for a country
     * Used for: Career mode selection, competition overview
     */
    suspend fun getAllCompetitionsForCountry(
        countryId: Int,
        leaguesRepo: LeaguesRepository
    ): CountryCompetitions {
        val leagues = leaguesRepo.getLeaguesByCountry(countryId).toList()
        val domesticCups = getDomesticCupsByCountry(countryId).toList()
        val continentalCups = getContinentalCups().toList()

        return CountryCompetitions(
            countryId = countryId,
            leagues = leagues,
            domesticCups = domesticCups,
            continentalCups = continentalCups
        )
    }
}

data class CountryCompetitions(
    val countryId: Int,
    val leagues: List<List<LeaguesEntity>>,
    val domesticCups: List<List<CupsEntity>>,
    val continentalCups: List<List<CupsEntity>>
)