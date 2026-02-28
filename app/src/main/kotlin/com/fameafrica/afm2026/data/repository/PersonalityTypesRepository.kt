package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.PersonalityStatistics
import com.fameafrica.afm2026.data.database.dao.PersonalityTypesDao
import com.fameafrica.afm2026.data.database.entities.PersonalityTypesEntity
import com.fameafrica.afm2026.data.database.entities.PlayerPersonality
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonalityTypesRepository @Inject constructor(
    private val personalityTypesDao: PersonalityTypesDao
) {

    // ============ BASIC CRUD ============

    fun getAllPersonalities(): Flow<List<PersonalityTypesEntity>> = personalityTypesDao.getAll()

    suspend fun getPersonalityById(id: Int): PersonalityTypesEntity? = personalityTypesDao.getById(id)

    suspend fun getPersonalityByName(name: String): PersonalityTypesEntity? = personalityTypesDao.getByName(name)

    suspend fun insertPersonality(personality: PersonalityTypesEntity) = personalityTypesDao.insert(personality)

    suspend fun insertAllPersonalities(personalities: List<PersonalityTypesEntity>) = personalityTypesDao.insertAll(personalities)

    suspend fun updatePersonality(personality: PersonalityTypesEntity) = personalityTypesDao.update(personality)

    suspend fun deletePersonality(personality: PersonalityTypesEntity) = personalityTypesDao.delete(personality)

    // ============ INITIALIZATION ============

    suspend fun initializeDefaultPersonalities() {
        if (personalityTypesDao.getCount() == 0) {
            val defaultPersonalities = listOf(
                PersonalityTypesEntity(
                    name = PlayerPersonality.PROFESSIONAL.value,
                    description = "Takes career seriously, maintains consistent form, rarely misses training.",
                    moraleEffect = 1.1,
                    formConsistency = 1.2
                ),
                PersonalityTypesEntity(
                    name = PlayerPersonality.AGGRESSIVE.value,
                    description = "Plays with high intensity, commits more fouls, can be a discipline risk.",
                    moraleEffect = 1.0,
                    formConsistency = 0.9
                ),
                PersonalityTypesEntity(
                    name = PlayerPersonality.LOYAL.value,
                    description = "Deeply committed to the club, unlikely to request transfer, good mentor.",
                    moraleEffect = 1.1,
                    formConsistency = 1.0
                ),
                PersonalityTypesEntity(
                    name = PlayerPersonality.MEDIA_FRIENDLY.value,
                    description = "Handles press well, boosts club reputation, good for merchandise.",
                    moraleEffect = 1.05,
                    formConsistency = 1.0
                ),
                PersonalityTypesEntity(
                    name = PlayerPersonality.MEDIA_HOSTILE.value,
                    description = "Avoids interviews, can create negative headlines, causes media drama.",
                    moraleEffect = 0.9,
                    formConsistency = 0.95
                ),
                PersonalityTypesEntity(
                    name = PlayerPersonality.NATURAL_LEADER.value,
                    description = "Inspires teammates, helps younger players develop, captain material.",
                    moraleEffect = 1.15,
                    formConsistency = 1.1
                ),
                PersonalityTypesEntity(
                    name = PlayerPersonality.AMBITIOUS.value,
                    description = "Driven to succeed, may request moves to bigger clubs, works hard.",
                    moraleEffect = 1.1,
                    formConsistency = 1.05
                ),
                PersonalityTypesEntity(
                    name = PlayerPersonality.TEMPERAMENTAL.value,
                    description = "Inconsistent performer, can be brilliant or invisible, emotional.",
                    moraleEffect = 0.85,
                    formConsistency = 0.7
                ),
                PersonalityTypesEntity(
                    name = PlayerPersonality.TEAM_PLAYER.value,
                    description = "Puts team first, unselfish, good chemistry with all teammates.",
                    moraleEffect = 1.05,
                    formConsistency = 1.05
                ),
                PersonalityTypesEntity(
                    name = PlayerPersonality.INDIVIDUALIST.value,
                    description = "Prefers individual glory, can be selfish, high risk high reward.",
                    moraleEffect = 0.95,
                    formConsistency = 0.85
                )
            )

            personalityTypesDao.insertAll(defaultPersonalities)
        }
    }

    // ============ UTILITY ============

    suspend fun getMoraleEffect(personalityName: String): Double {
        return personalityTypesDao.getByName(personalityName)?.moraleEffect ?: 1.0
    }

    suspend fun getFormConsistency(personalityName: String): Double {
        return personalityTypesDao.getByName(personalityName)?.formConsistency ?: 1.0
    }

    fun getPersonalityStatistics(): Flow<List<PersonalityStatistics>> =
        personalityTypesDao.getPersonalityStatistics()
}