package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.*
import com.fameafrica.afm2026.data.database.entities.PlayersEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayersRepository @Inject constructor(
    private val playersDao: PlayersDao
) {

    // ============ BASIC CRUD ============

    fun getAllPlayers(): Flow<List<PlayersEntity>> = playersDao.getAll()

    suspend fun getPlayerById(id: Int): PlayersEntity? = playersDao.getById(id)

    suspend fun getPlayerByName(name: String): PlayersEntity? = playersDao.getByName(name)

    suspend fun insertPlayer(player: PlayersEntity) = playersDao.insert(player)

    suspend fun insertAllPlayers(players: List<PlayersEntity>) = playersDao.insertAll(players)

    suspend fun updatePlayer(player: PlayersEntity) = playersDao.update(player)

    suspend fun deletePlayer(player: PlayersEntity) = playersDao.delete(player)

    suspend fun deletePlayerById(playerId: Int) = playersDao.deleteById(playerId)

    suspend fun getPlayersCount(): Int = playersDao.getCount()

    suspend fun getActivePlayersCount(): Int = playersDao.getActiveCount()

    // ============ TEAM-BASED ============

    fun getPlayersByTeamId(teamId: Int): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByTeamId(teamId)

    fun getPlayersByTeamName(teamName: String): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByTeamName(teamName)

    fun getPlayersByTeamAndCategory(teamId: Int, category: String): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByTeamAndCategory(teamId, category)

    fun getStartingXI(teamId: Int): Flow<List<PlayersEntity>> =
        playersDao.getStartingXI(teamId)

    suspend fun getTeamCaptain(teamId: Int): PlayersEntity? =
        playersDao.getTeamCaptain(teamId)

    suspend fun getTeamViceCaptain(teamId: Int): PlayersEntity? =
        playersDao.getTeamViceCaptain(teamId)

    suspend fun getInjuredCountByTeam(teamId: Int): Int =
        playersDao.getInjuredCountByTeam(teamId)

    // ============ POSITION-BASED ============

    fun getPlayersByPosition(position: String): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByPosition(position)

    fun getPlayersByCategory(category: String): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByCategory(category)

    fun getPlayersByCategoryAndNationality(category: String, nationality: String): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByCategoryAndNationality(category, nationality)

    // ============ NATIONALITY-BASED ============

    fun getPlayersByNationality(nationality: String): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByNationality(nationality)

    fun getActivePlayersByNationality(nationality: String): Flow<List<PlayersEntity>> =
        playersDao.getActivePlayersByNationality(nationality)

    fun getDistinctNationalities(): Flow<List<String>> =
        playersDao.getDistinctNationalities()

    suspend fun getPlayerCountByNationality(nationality: String): Int =
        playersDao.getPlayerCountByNationality(nationality)

    // ============ RATING-BASED ============

    fun getPlayersByMinRating(minRating: Int): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByMinRating(minRating)

    fun getPlayersByRatingRange(minRating: Int, maxRating: Int): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByRatingRange(minRating, maxRating)

    fun getTopRatedPlayers(limit: Int): Flow<List<PlayersEntity>> =
        playersDao.getTopRatedPlayers(limit)

    fun getTopRatedByPosition(position: String, limit: Int): Flow<List<PlayersEntity>> =
        playersDao.getTopRatedByPosition(position, limit)

    // ============ POTENTIAL-BASED ============

    fun getTopYoungPlayers(minPotential: Int): Flow<List<PlayersEntity>> =
        playersDao.getTopYoungPlayers(minPotential)

    fun getBiggestPotential(limit: Int): Flow<List<PlayersEntity>> =
        playersDao.getBiggestPotential(limit)

    // ============ AGE-BASED ============

    fun getYouthPlayers(): Flow<List<PlayersEntity>> = playersDao.getYouthPlayers()

    fun getVeteranPlayers(): Flow<List<PlayersEntity>> = playersDao.getVeteranPlayers()

    fun getPlayersByAgeRange(minAge: Int, maxAge: Int): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByAgeRange(minAge, maxAge)

    // ============ HEIGHT & PHYSICAL ============

    fun getTallestPlayers(minHeight: Int): Flow<List<PlayersEntity>> =
        playersDao.getTallestPlayers(minHeight)

    fun getPlayersByPreferredFoot(foot: String): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByPreferredFoot(foot)

    // ============ INJURY & STATUS ============

    fun getInjuredPlayers(): Flow<List<PlayersEntity>> = playersDao.getInjuredPlayers()

    fun getSuspendedPlayers(): Flow<List<PlayersEntity>> = playersDao.getSuspendedPlayers()

    fun getRetiredPlayers(): Flow<List<PlayersEntity>> = playersDao.getRetiredPlayers()

    // ============ CONTRACT & TRANSFER ============

    fun getFreeAgents(): Flow<List<PlayersEntity>> = playersDao.getFreeAgents()

    fun getTransferListed(): Flow<List<PlayersEntity>> = playersDao.getTransferListed()

    fun getLoanListed(): Flow<List<PlayersEntity>> = playersDao.getLoanListed()

    fun getPlayersByMarketValueRange(minValue: Int, maxValue: Int): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByMarketValueRange(minValue, maxValue)

    fun getPlayersWithExpiringContracts(): Flow<List<PlayersEntity>> =
        playersDao.getPlayersWithExpiringContracts()

    // ============ PERSONALITY, TRAITS & ARCHETYPE ============

    fun getPlayersByPersonality(personality: String): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByPersonality(personality)

    fun getPlayersByArchetype(archetype: String): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByArchetype(archetype)

    fun getPlayersByTrait(trait: String): Flow<List<PlayersEntity>> =
        playersDao.getPlayersByTrait(trait)

    fun getPotentialCaptains(): Flow<List<PlayersEntity>> = playersDao.getPotentialCaptains()

    fun getTeamLeaders(): Flow<List<PlayersEntity>> = playersDao.getTeamLeaders()

    fun getMediaFriendlyPlayers(): Flow<List<PlayersEntity>> = playersDao.getMediaFriendlyPlayers()

    fun getFanFavorites(): Flow<List<PlayersEntity>> = playersDao.getFanFavorites()

    // ============ SEARCH ============

    fun searchPlayers(searchQuery: String): Flow<List<PlayersEntity>> =
        playersDao.searchPlayers(searchQuery)

    fun advancedSearch(searchQuery: String): Flow<List<PlayersEntity>> =
        playersDao.advancedSearch(searchQuery)

    // ============ STATISTICS ============

    suspend fun getAverageRatingByTeam(teamId: Int): Double? =
        playersDao.getAverageRatingByTeam(teamId)

    suspend fun getTotalMarketValueByTeam(teamId: Int): Long? =
        playersDao.getTotalMarketValueByTeam(teamId)

    suspend fun getAverageAgeByTeam(teamId: Int): Double? =
        playersDao.getAverageAgeByTeam(teamId)

    suspend fun getAverageHeightByTeam(teamId: Int): Double? =
        playersDao.getAverageHeightByTeam(teamId)

    fun getTeamSquadAnalysis(teamId: Int): Flow<List<SquadAnalysis>> =
        playersDao.getTeamSquadAnalysis(teamId)

    fun getNationalityDistribution(limit: Int = 10): Flow<List<NationalityDistribution>> =
        playersDao.getNationalityDistribution(limit)

    fun getArchetypeDistribution(): Flow<List<ArchetypeDistribution>> =
        playersDao.getArchetypeDistribution()

    fun getPersonalityDistribution(): Flow<List<PersonalityDistribution>> =
        playersDao.getPersonalityDistribution()

    fun getTraitDistribution(): Flow<List<TraitDistribution>> =
        playersDao.getTraitDistribution()

    // ============ NATIONAL TEAM ELIGIBILITY ============

    fun getEligiblePlayersForNationalTeam(nationality: String): Flow<List<PlayersEntity>> =
        playersDao.getEligiblePlayersForNationalTeam(nationality)

    fun getEligibleGoalkeepersForNationalTeam(nationality: String): Flow<List<PlayersEntity>> =
        playersDao.getEligibleGoalkeepersForNationalTeam(nationality)

    fun getEligibleDefendersForNationalTeam(nationality: String): Flow<List<PlayersEntity>> =
        playersDao.getEligibleDefendersForNationalTeam(nationality)

    fun getEligibleMidfieldersForNationalTeam(nationality: String): Flow<List<PlayersEntity>> =
        playersDao.getEligibleMidfieldersForNationalTeam(nationality)

    fun getEligibleForwardsForNationalTeam(nationality: String): Flow<List<PlayersEntity>> =
        playersDao.getEligibleForwardsForNationalTeam(nationality)

    fun getPotentialNationalTeamCaptains(nationality: String): Flow<List<PlayersEntity>> =
        playersDao.getPotentialNationalTeamCaptains(nationality)

    // ============ JOIN QUERIES ============

    suspend fun getPlayerWithDetails(playerId: Int): PlayerWithDetails? =
        playersDao.getPlayerWithDetails(playerId)

    fun getTeamSquadWithDetails(teamId: Int): Flow<List<PlayerWithTeamDetails>> =
        playersDao.getTeamSquadWithDetails(teamId)

    // ============ 🔥 PLAYER ATTRIBUTE UPDATES ============

    /**
     * Update specific player attributes
     * Used by training system, development, injuries, etc.
     *
     * @param playerId The ID of the player to update
     * @param attributeUpdates Map of attribute names to new values
     * @return The updated player entity, or null if player not found
     */
    suspend fun updatePlayerAttributes(
        playerId: Int,
        attributeUpdates: Map<String, Int>
    ): PlayersEntity? {

        // Get current player
        val player = playersDao.getById(playerId) ?: return null

        // Apply all attribute updates
        var updatedPlayer = player

        attributeUpdates.forEach { (attribute, value) ->
            // Ensure value is within valid range (1-99)
            val clampedValue = value.coerceIn(1, 99)

            updatedPlayer = when (attribute.lowercase()) {
                // Technical attributes
                "finishing" -> updatedPlayer.copy(finishing = clampedValue)
                "passing" -> updatedPlayer.copy(passing = clampedValue)
                "dribbling" -> updatedPlayer.copy(dribbling = clampedValue)
                "skill" -> updatedPlayer.copy(skill = clampedValue)
                "crossing" -> updatedPlayer.copy(crossing = clampedValue)
                "defending" -> updatedPlayer.copy(defending = clampedValue)
                "heading" -> updatedPlayer.copy(heading = clampedValue)
                "long_shots", "longshots" -> updatedPlayer.copy(longShots = clampedValue)

                // Physical attributes
                "pace" -> updatedPlayer.copy(pace = clampedValue)
                "stamina" -> updatedPlayer.copy(stamina = clampedValue)
                "strength" -> updatedPlayer.copy(strength = clampedValue)
                "acceleration" -> updatedPlayer.copy(acceleration = clampedValue)
                "agility" -> updatedPlayer.copy(agility = clampedValue)

                // Mental attributes
                "aggression" -> updatedPlayer.copy(aggression = clampedValue)
                "leadership" -> updatedPlayer.copy(leadership = clampedValue)
                "motivation" -> updatedPlayer.copy(motivation = clampedValue)
                "composure" -> updatedPlayer.copy(composure = clampedValue)
                "vision" -> updatedPlayer.copy(vision = clampedValue)
                "positioning" -> updatedPlayer.copy(positioning = clampedValue)
                "anticipation" -> updatedPlayer.copy(anticipation = clampedValue)
                "decisions" -> updatedPlayer.copy(decisions = clampedValue)
                "creativity" -> updatedPlayer.copy(creativity = clampedValue)
                "teamwork" -> updatedPlayer.copy(teamwork = clampedValue)

                // Goalkeeper attributes
                "goalkeeping" -> updatedPlayer.copy(goalkeeping = clampedValue)
                "reflexes" -> updatedPlayer.copy(reflexes = clampedValue)
                "handling" -> updatedPlayer.copy(handling = clampedValue)
                "aerial_ability", "aerialability" -> updatedPlayer.copy(aerialAbility = clampedValue)
                "command_of_area", "commandofarea" -> updatedPlayer.copy(commandOfArea = clampedValue)
                "kicking" -> updatedPlayer.copy(kicking = clampedValue)

                // Overall ratings
                "rating" -> updatedPlayer.copy(rating = clampedValue)
                "potential" -> updatedPlayer.copy(potential = clampedValue)
                "current_form", "form" -> updatedPlayer.copy(currentForm = clampedValue)
                "experience" -> updatedPlayer.copy(experience = clampedValue)
                "morale" -> updatedPlayer.copy(morale = clampedValue.coerceIn(0, 100))

                // Media and influence
                "media_handling", "mediahandling" -> updatedPlayer.copy(mediaHandling = clampedValue.coerceIn(0, 100))
                "fan_popularity", "fanpopularity" -> updatedPlayer.copy(fanPopularity = clampedValue.coerceIn(0, 100))
                "dressing_room_influence", "dressingroominfluence" -> updatedPlayer.copy(dressingRoomInfluence = clampedValue.coerceIn(0, 100))

                // Personality and traits
                "personality_type", "personalitytype" -> updatedPlayer.copy(personalityType = value.toString())
                "archetype" -> updatedPlayer.copy(archetype = value.toString())
                "primary_trait", "primarytrait" -> updatedPlayer.copy(primaryTrait = value.toString())
                "secondary_trait", "secondarytrait" -> updatedPlayer.copy(secondaryTrait = value.toString())
                "gameplay_focus", "gameplayfocus" -> updatedPlayer.copy(gameplayFocus = value.toString())

                // Physical attributes (new)
                "height" -> updatedPlayer.copy(height = clampedValue)
                "preferred_foot", "preferredfoot" -> updatedPlayer.copy(preferredFoot = value.toString())

                // Injury and status
                "injury_risk", "injuryrisk" -> updatedPlayer.copy(injuryRisk = clampedValue.coerceIn(0, 100))
                "injury_status", "injurystatus" -> updatedPlayer.copy(injuryStatus = value.toString())
                "recovery_time", "recoverytime" -> updatedPlayer.copy(recoveryTime = clampedValue)
                "suspended" -> updatedPlayer.copy(suspended = value == 1)

                // Contract
                "market_value", "marketvalue" -> updatedPlayer.copy(marketValue = clampedValue)
                "salary" -> updatedPlayer.copy(salary = clampedValue.toDouble())
                "contract_expiry", "contractexpiry" -> updatedPlayer.copy(contractExpiry = value.toString())
                "free_agent", "freeagent" -> updatedPlayer.copy(freeAgent = value == 1)
                "transfer_list_status", "transferliststatus" -> updatedPlayer.copy(transferListStatus = value.toString())

                // Team role
                "is_starting_xi", "isstartingxi" -> updatedPlayer.copy(isStartingXi = value == 1)
                "is_captain", "iscaptain" -> updatedPlayer.copy(isCaptain = value == 1)
                "is_vice_captain", "isvicecaptain" -> updatedPlayer.copy(isViceCaptain = value == 1)
                "work_rate", "workrate" -> updatedPlayer.copy(workRate = value.toString())

                // Career status
                "retired" -> updatedPlayer.copy(retired = value == 1)
                "future_role", "futurerole" -> updatedPlayer.copy(futureRole = value.toString())
                "player_coach", "playercoach" -> updatedPlayer.copy(playerCoach = value == 1)
                "season" -> updatedPlayer.copy(season = value.toString())

                // If attribute not recognized, return unchanged
                else -> updatedPlayer
            }
        }

        // Update the timestamp
        updatedPlayer = updatedPlayer.copy(
            updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())
        )

        // Save to database
        playersDao.update(updatedPlayer)

        return updatedPlayer
    }

    /**
     * Convenience method to update a single attribute
     */
    suspend fun updatePlayerAttribute(
        playerId: Int,
        attributeName: String,
        newValue: Int
    ): PlayersEntity? {
        return updatePlayerAttributes(playerId, mapOf(attributeName to newValue))
    }

    /**
     * Batch update multiple players' attributes
     * Used for team-wide training effects
     */
    suspend fun batchUpdatePlayerAttributes(
        updates: List<Pair<Int, Map<String, Int>>>
    ): List<PlayersEntity> {
        val updatedPlayers = mutableListOf<PlayersEntity>()

        for ((playerId, attributeMap) in updates) {
            updatePlayerAttributes(playerId, attributeMap)?.let {
                updatedPlayers.add(it)
            }
        }

        return updatedPlayers
    }

    // ============ PLAYER MANAGEMENT ============

    suspend fun updatePlayerAfterMatch(
        playerId: Int,
        goalsScored: Int,
        assistsMade: Int,
        isManOfMatch: Boolean
    ) {
        val player = playersDao.getById(playerId) ?: return
        val updatedPlayer = player.updateAfterMatch(goalsScored, assistsMade, isManOfMatch)
        playersDao.update(updatedPlayer)
    }

    suspend fun incrementPlayerGoals(playerId: Int) {
        val player = playersDao.getById(playerId) ?: return
        val updatedPlayer = player.copy(
            goals = player.goals + 1,
            currentForm = (player.currentForm + 5).coerceIn(1, 100)
        )
        playersDao.update(updatedPlayer)
    }

    suspend fun incrementPlayerAssists(playerId: Int) {
        val player = playersDao.getById(playerId) ?: return
        val updatedPlayer = player.copy(
            assists = player.assists + 1,
            currentForm = (player.currentForm + 3).coerceIn(1, 100)
        )
        playersDao.update(updatedPlayer)
    }

    suspend fun incrementPlayerCleanSheets(playerId: Int) {
        val player = playersDao.getById(playerId) ?: return
        val updatedPlayer = player.copy(
            cleanSheets = player.cleanSheets + 1,
            currentForm = (player.currentForm + 5).coerceIn(1, 100)
        )
        playersDao.update(updatedPlayer)
    }

    suspend fun incrementPlayerYellowCards(playerId: Int) {
        val player = playersDao.getById(playerId) ?: return
        val updatedPlayer = player.copy(
            yellowCards = player.yellowCards + 1,
            currentForm = (player.currentForm - 2).coerceIn(1, 100),
            morale = (player.morale - 2).coerceIn(0, 100)
        )
        playersDao.update(updatedPlayer)
    }

    suspend fun incrementPlayerRedCards(playerId: Int) {
        val player = playersDao.getById(playerId) ?: return
        val updatedPlayer = player.copy(
            redCards = player.redCards + 1,
            currentForm = (player.currentForm - 5).coerceIn(1, 100),
            morale = (player.morale - 5).coerceIn(0, 100),
            suspended = true
        )
        playersDao.update(updatedPlayer)
    }

    suspend fun setPlayerInjury(playerId: Int, injuryType: String, recoveryDays: Int) {
        val player = playersDao.getById(playerId) ?: return
        val updatedPlayer = player.setInjury(injuryType, recoveryDays)
        playersDao.update(updatedPlayer)
    }

    suspend fun recoverPlayerFromInjury(playerId: Int) {
        val player = playersDao.getById(playerId) ?: return
        val updatedPlayer = player.recoverFromInjury()
        playersDao.update(updatedPlayer)
    }

    suspend fun transferPlayer(playerId: Int, newTeamId: Int, newTeamName: String, newMarketValue: Int? = null) {
        val player = playersDao.getById(playerId) ?: return
        val updatedPlayer = player.transferTo(newTeamId, newTeamName, newMarketValue)
        playersDao.update(updatedPlayer)
    }

    suspend fun renewContract(playerId: Int, newSalary: Double, newExpiry: String) {
        val player = playersDao.getById(playerId) ?: return
        val updatedPlayer = player.renewContract(newSalary, newExpiry)
        playersDao.update(updatedPlayer)
    }

    suspend fun retirePlayer(playerId: Int) {
        val player = playersDao.getById(playerId) ?: return
        val updatedPlayer = player.retire()
        playersDao.update(updatedPlayer)
    }

    suspend fun updatePlayerRating(playerId: Int, newRating: Int) {
        val player = playersDao.getById(playerId) ?: return
        val updatedPlayer = player.updateRating(newRating)
        playersDao.update(updatedPlayer)
    }

    suspend fun assignCaptain(teamId: Int, playerId: Int) {
        playersDao.removeCaptain(teamId)
        playersDao.setCaptain(playerId)
    }

    suspend fun assignViceCaptain(teamId: Int, playerId: Int) {
        playersDao.removeViceCaptain(teamId)
        playersDao.setViceCaptain(playerId)
    }

    suspend fun setStartingXI(teamId: Int, playerIds: List<Int>) {
        playersDao.resetStartingXI(teamId)
        playerIds.forEach { playerId ->
            playersDao.setPlayerAsStarter(playerId)
        }
    }

    suspend fun updatePlayerForm(playerId: Int, change: Int) {
        playersDao.updatePlayerForm(playerId, change)
    }

    suspend fun updatePlayerMorale(playerId: Int, change: Int) {
        playersDao.updatePlayerMorale(playerId, change)
    }

    suspend fun incrementYoungPlayerExperience(teamId: Int) {
        playersDao.incrementYoungPlayerExperience(teamId)
    }

    suspend fun suspendPlayer(playerId: Int) {
        playersDao.suspendPlayer(playerId)
    }

    suspend fun unsuspendPlayer(playerId: Int) {
        playersDao.unsuspendPlayer(playerId)
    }

    // ============ PLAYER DEVELOPMENT ============

    suspend fun developPlayer(playerId: Int, trainingEffectiveness: Double = 1.0) {
        val player = playersDao.getById(playerId) ?: return
        if (player.retired || player.age > 30) return

        // Age-based development curve
        val developmentFactor = when {
            player.age <= 21 -> 1.5
            player.age <= 25 -> 1.2
            player.age <= 29 -> 1.0
            else -> 0.7
        }

        val finalFactor = developmentFactor * trainingEffectiveness

        // Improve rating (capped by potential)
        val potentialGap = player.potential - player.rating
        val ratingIncrease = if (potentialGap > 0) {
            (1 * finalFactor).toInt().coerceAtLeast(1).coerceAtMost(potentialGap)
        } else {
            0
        }

        if (ratingIncrease > 0) {
            val updatedPlayer = player.copy(
                rating = player.rating + ratingIncrease,
                experience = player.experience + 1
            )
            playersDao.update(updatedPlayer)
        }
    }

    // ============ DASHBOARD ============

    suspend fun getPlayerDashboard(playerId: Int): PlayerDashboard {
        val player = playersDao.getById(playerId) ?: throw IllegalArgumentException("Player not found")
        val playerWithDetails = playersDao.getPlayerWithDetails(playerId)

        val goalContribution = if (player.matches > 0) {
            (player.goals + player.assists).toDouble() / player.matches
        } else 0.0

        val cardsPerMatch = if (player.matches > 0) {
            (player.yellowCards + player.redCards).toDouble() / player.matches
        } else 0.0

        val potentialAchieved = (player.rating.toDouble() / player.potential * 100).coerceAtMost(100.0)

        return PlayerDashboard(
            player = player,
            playerWithDetails = playerWithDetails,
            goalContributionPerGame = goalContribution,
            cardsPerGame = cardsPerMatch,
            potentialAchieved = potentialAchieved,
            yearsUntilPrime = (27 - player.age).coerceAtLeast(0),
            estimatedValueInMillions = player.marketValue / 1_000_000.0,
            estimatedWageInMillions = player.salary / 1_000_000.0
        )
    }

    // ============ BATCH OPERATIONS ============

    suspend fun getPlayersByIds(playerIds: List<Int>): List<PlayersEntity> {
        return playerIds.mapNotNull { playersDao.getById(it) }
    }

    suspend fun getTeamPlayersCountByPosition(teamId: Int): Map<String, Int> {
        val squad = playersDao.getPlayersByTeamId(teamId).firstOrNull() ?: emptyList()
        return squad.groupingBy { it.positionCategory }.eachCount()
    }

    suspend fun getTeamAverageAttributes(teamId: Int): TeamAverageAttributes {
        val squad = playersDao.getPlayersByTeamId(teamId).firstOrNull() ?: emptyList()

        return TeamAverageAttributes(
            averageRating = squad.map { it.rating }.average(),
            averageAge = squad.map { it.age }.average(),
            averageHeight = squad.map { it.height }.average(),
            averagePace = squad.map { it.pace }.average(),
            averageStamina = squad.map { it.stamina }.average(),
            averageStrength = squad.map { it.strength }.average(),
            averagePassing = squad.map { it.passing }.average(),
            averageFinishing = squad.map { it.finishing }.average(),
            averageDefending = squad.map { it.defending }.average()
        )
    }
}

// ============ ADDITIONAL DATA CLASSES ============

data class PlayerDashboard(
    val player: PlayersEntity,
    val playerWithDetails: PlayerWithDetails?,
    val goalContributionPerGame: Double,
    val cardsPerGame: Double,
    val potentialAchieved: Double,
    val yearsUntilPrime: Int,
    val estimatedValueInMillions: Double,
    val estimatedWageInMillions: Double
)

data class TeamAverageAttributes(
    val averageRating: Double,
    val averageAge: Double,
    val averageHeight: Double,
    val averagePace: Double,
    val averageStamina: Double,
    val averageStrength: Double,
    val averagePassing: Double,
    val averageFinishing: Double,
    val averageDefending: Double
)