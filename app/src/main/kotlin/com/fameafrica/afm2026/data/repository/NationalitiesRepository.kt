package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.ConfederationDistribution
import com.fameafrica.afm2026.data.database.dao.NationalitiesDao
import com.fameafrica.afm2026.data.database.dao.RegionDistribution
import com.fameafrica.afm2026.data.database.entities.Confederation
import com.fameafrica.afm2026.data.database.entities.NationalitiesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NationalitiesRepository @Inject constructor(
    private val nationalitiesDao: NationalitiesDao
) {

    // ============ BASIC CRUD ============

    fun getAllNationalities(): Flow<List<NationalitiesEntity>> = nationalitiesDao.getAll()

    suspend fun getNationalityById(id: Int): NationalitiesEntity? = nationalitiesDao.getById(id)

    suspend fun getNationalityByName(name: String): NationalitiesEntity? = nationalitiesDao.getByNationality(name)

    suspend fun insertNationality(nationality: NationalitiesEntity) = nationalitiesDao.insert(nationality)

    suspend fun insertAllNationalities(nationalities: List<NationalitiesEntity>) = nationalitiesDao.insertAll(nationalities)

    suspend fun updateNationality(nationality: NationalitiesEntity) = nationalitiesDao.update(nationality)

    suspend fun deleteNationality(nationality: NationalitiesEntity) = nationalitiesDao.delete(nationality)

    suspend fun getCount(): Int = nationalitiesDao.getCount()

    // ============ FIFA CODE LOOKUPS ============

    suspend fun getIdByFifaCode(fifaCode: String): Int? = nationalitiesDao.getIdByFifaCode(fifaCode)

    suspend fun getFifaCodeById(id: Int): String? = nationalitiesDao.getFifaCodeById(id)

    suspend fun getNationalityByFifaCode(fifaCode: String): String? = nationalitiesDao.getNationalityByFifaCode(fifaCode)

    // ============ CONFEDERATION QUERIES ============

    fun getByConfederation(confederation: String): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.getByConfederation(confederation)

    fun getAfricanNations(): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.getAfricanNations()

    fun getEuropeanNations(): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.getEuropeanNations()

    fun getSouthAmericanNations(): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.getSouthAmericanNations()

    fun getNorthAmericanNations(): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.getNorthAmericanNations()

    fun getAsianNations(): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.getAsianNations()

    fun getOceanianNations(): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.getOceanianNations()

    // ============ AFRICAN REGION QUERIES ============

    fun getNorthAfricanNations(): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.getNorthAfricanNations()

    fun getWestAfricanNations(): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.getWestAfricanNations()

    fun getEastAfricanNations(): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.getEastAfricanNations()

    fun getCentralAfricanNations(): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.getCentralAfricanNations()

    fun getSouthernAfricanNations(): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.getSouthernAfricanNations()

    // ============ SEARCH ============

    fun searchNationalities(searchQuery: String): Flow<List<NationalitiesEntity>> =
        nationalitiesDao.searchNationalities(searchQuery)

    // ============ STATISTICS ============

    fun getConfederationDistribution(): Flow<List<ConfederationDistribution>> =
        nationalitiesDao.getConfederationDistribution()

    fun getAfricanRegionDistribution(): Flow<List<RegionDistribution>> =
        nationalitiesDao.getAfricanRegionDistribution()

    // ============ UTILITY METHODS ============

    suspend fun isAfricanCountry(nationalityId: Int): Boolean {
        val nationality = nationalitiesDao.getById(nationalityId)
        return nationality?.confederation == Confederation.CAF.value
    }

    suspend fun getFifaCodesByConfederation(confederation: String): List<String> {
        val nations = nationalitiesDao.getByConfederation(confederation).firstOrNull() ?: return emptyList()
        return nations.map { it.fifaCode }
    }

    suspend fun getAfricanFifaCodes(): List<String> = getFifaCodesByConfederation(Confederation.CAF.value)
}