package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.GameStatesDao
import com.fameafrica.afm2026.data.database.dao.SaveGameSummary
import com.fameafrica.afm2026.data.database.dao.SeasonHistoryDao
import com.fameafrica.afm2026.data.database.entities.GameStatesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameStatesRepository @Inject constructor(
    private val gameStatesDao: GameStatesDao,
    seasonHistoryDao: SeasonHistoryDao
) {

    // ============ BASIC CRUD ============

    fun getAllGameStates(): Flow<List<GameStatesEntity>> = gameStatesDao.getAll()

    suspend fun getGameStateById(id: Int): GameStatesEntity? = gameStatesDao.getById(id)

    suspend fun getGameStateByManagerId(managerId: Int): GameStatesEntity? =
        gameStatesDao.getByManagerId(managerId)

    fun getValidSaveGames(): Flow<List<GameStatesEntity>> = gameStatesDao.getValidSaveGames()

    suspend fun insertGameState(gameState: GameStatesEntity) = gameStatesDao.insert(gameState)

    suspend fun updateGameState(gameState: GameStatesEntity) = gameStatesDao.update(gameState)

    suspend fun deleteGameState(gameState: GameStatesEntity) = gameStatesDao.delete(gameState)

    suspend fun deleteGameStateById(id: Int) = gameStatesDao.deleteById(id)

    suspend fun deleteAllGameStates() = gameStatesDao.deleteAll()

    suspend fun getValidSaveCount(): Int = gameStatesDao.getValidSaveCount()

    // ============ SAVE GAME MANAGEMENT ============

    suspend fun createNewSave(
        managerId: Int,
        managerName: String,
        teamId: Int,
        teamName: String,
        saveName: String,
        season: String = "2024/25",
        week: Int = 1
    ): GameStatesEntity {
        // Check if manager already has a save
        val existing = gameStatesDao.getByManagerId(managerId)
        if (existing != null) {
            // Invalidate old save
            invalidateSave(existing.id)
        }

        val now = getCurrentDateTime()

        val gameState = GameStatesEntity(
            managerId = managerId,
            managerName = managerName,
            teamId = teamId,
            teamName = teamName,
            name = saveName,
            season = season,
            week = week,
            lastPlayed = now,
            isValid = true,
            gameVersion = getAppVersion()
        )

        gameStatesDao.insert(gameState)
        return gameState
    }

    suspend fun saveGame(gameStateId: Int, week: Int): GameStatesEntity? {
        val gameState = gameStatesDao.getById(gameStateId) ?: return null

        val updated = gameState.copy(
            week = week,
            lastPlayed = getCurrentDateTime()
        )

        gameStatesDao.update(updated)
        return updated
    }

    suspend fun loadGame(gameStateId: Int): GameStatesEntity? {
        val gameState = gameStatesDao.getById(gameStateId) ?: return null

        // Update last played time
        val updated = gameState.copy(
            lastPlayed = getCurrentDateTime()
        )

        gameStatesDao.update(updated)
        return updated
    }

    suspend fun invalidateSave(gameStateId: Int): Boolean {
        val gameState = gameStatesDao.getById(gameStateId) ?: return false
        gameStatesDao.invalidateSave(gameStateId)
        return true
    }

    suspend fun validateSave(gameStateId: Int): Boolean {
        val gameState = gameStatesDao.getById(gameStateId) ?: return false
        gameStatesDao.validateSave(gameStateId)
        return true
    }

    // ============ TEAM-BASED ============

    fun getSaveGamesForTeam(teamId: Int): Flow<List<GameStatesEntity>> =
        gameStatesDao.getSaveGamesForTeam(teamId)

    fun getSaveGamesForTeamName(teamName: String): Flow<List<GameStatesEntity>> =
        gameStatesDao.getSaveGamesForTeamName(teamName)

    suspend fun getLatestSaveForTeam(teamId: Int): GameStatesEntity? {
        return gameStatesDao.getSaveGamesForTeam(teamId)
            .firstOrNull()
            ?.maxByOrNull { it.lastPlayed ?: "" }
    }

    // ============ SEASON-BASED ============

    fun getSaveGamesForSeason(season: String): Flow<List<GameStatesEntity>> =
        gameStatesDao.getSaveGamesForSeason(season)

    fun getSeasonsWithSaves(): Flow<List<String>> = gameStatesDao.getSeasonsWithSaves()

    // ============ SEARCH ============

    fun searchSaveGames(searchQuery: String): Flow<List<GameStatesEntity>> =
        gameStatesDao.searchSaveGames(searchQuery)

    // ============ STATISTICS ============

    suspend fun getTotalSaveCount(): Int = gameStatesDao.getTotalSaveCount()

    suspend fun getAverageWeek(): Double? = gameStatesDao.getAverageWeek()

    suspend fun getMaxWeek(): Int? = gameStatesDao.getMaxWeek()

    suspend fun getSaveGameSummary(): SaveGameSummary {
        val totalSaves = gameStatesDao.getTotalSaveCount()
        val validSaves = gameStatesDao.getValidSaveCount()
        val allSaves = gameStatesDao.getAll().firstOrNull() ?: emptyList()
        val latestSave = allSaves.maxByOrNull { it.lastPlayed ?: "" }
        val oldestSave = allSaves.minByOrNull { it.lastPlayed ?: "" }
        val averageWeek = gameStatesDao.getAverageWeek()

        return SaveGameSummary(
            totalSaves = totalSaves,
            validSaves = validSaves,
            latestSave = latestSave,
            oldestSave = oldestSave,
            averageWeek = averageWeek ?: 0.0
        )
    }

    // ============ BACKUP ============

    suspend fun exportSaveGames(): List<GameStatesEntity> {
        return gameStatesDao.getAll().firstOrNull() ?: emptyList()
    }

    suspend fun importSaveGames(saves: List<GameStatesEntity>) {
        gameStatesDao.deleteAll()
        gameStatesDao.insertAll(saves)
    }

    // ============ UTILITY ============

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun getAppVersion(): String {
        return "1.0.0" // This should come from BuildConfig
    }

    // ============ DASHBOARD ============

    suspend fun getGameStatesDashboard(): GameStatesDashboard {
        val allSaves = gameStatesDao.getAll().firstOrNull() ?: emptyList()
        val validSaves = allSaves.filter { it.isValid }
        val invalidSaves = allSaves.filter { !it.isValid }

        val savesBySeason = allSaves.groupBy { it.season }
            .map { (season, saves) ->
                SeasonSaves(
                    season = season,
                    saveCount = saves.size,
                    latestSave = saves.maxByOrNull { it.lastPlayed ?: "" }
                )
            }
            .sortedByDescending { it.season }

        val savesByTeam = allSaves.groupBy { it.teamName }
            .map { (team, saves) ->
                TeamSaves(
                    teamName = team,
                    saveCount = saves.size,
                    latestSave = saves.maxByOrNull { it.lastPlayed ?: "" }
                )
            }
            .sortedByDescending { it.saveCount }
            .take(10)

        return GameStatesDashboard(
            totalSaves = allSaves.size,
            validSaves = validSaves.size,
            invalidSaves = invalidSaves.size,
            latestSave = allSaves.maxByOrNull { it.lastPlayed ?: "" },
            savesBySeason = savesBySeason,
            topTeamsBySaves = savesByTeam,
            allSaves = allSaves
        )
    }
}

// ============ DATA CLASSES ============

data class SeasonSaves(
    val season: String,
    val saveCount: Int,
    val latestSave: GameStatesEntity?
)

data class TeamSaves(
    val teamName: String,
    val saveCount: Int,
    val latestSave: GameStatesEntity?
)

data class GameStatesDashboard(
    val totalSaves: Int,
    val validSaves: Int,
    val invalidSaves: Int,
    val latestSave: GameStatesEntity?,
    val savesBySeason: List<SeasonSaves>,
    val topTeamsBySaves: List<TeamSaves>,
    val allSaves: List<GameStatesEntity>
)