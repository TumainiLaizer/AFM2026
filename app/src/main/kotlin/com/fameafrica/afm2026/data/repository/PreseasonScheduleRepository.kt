package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.PreseasonScheduleDao
import com.fameafrica.afm2026.data.database.dao.PreseasonStats
import com.fameafrica.afm2026.data.database.entities.PreseasonScheduleEntity
import com.fameafrica.afm2026.data.database.entities.PreseasonStatus
import com.fameafrica.afm2026.data.database.entities.TourLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreseasonScheduleRepository @Inject constructor(
    private val preseasonScheduleDao: PreseasonScheduleDao,
    private val teamsRepository: TeamsRepository
) {

    // ============ BASIC CRUD ============

    fun getAllPreseasonMatches(): Flow<List<PreseasonScheduleEntity>> = preseasonScheduleDao.getAll()

    suspend fun getPreseasonMatchById(id: Int): PreseasonScheduleEntity? = preseasonScheduleDao.getById(id)

    suspend fun insertPreseasonMatch(match: PreseasonScheduleEntity) = preseasonScheduleDao.insert(match)

    suspend fun updatePreseasonMatch(match: PreseasonScheduleEntity) = preseasonScheduleDao.update(match)

    suspend fun deletePreseasonMatch(match: PreseasonScheduleEntity) = preseasonScheduleDao.delete(match)

    suspend fun deleteBySeason(season: String) = preseasonScheduleDao.deleteBySeason(season)

    // ============ TEAM-BASED ============

    fun getTeamPreseasonSchedule(teamName: String, season: String): Flow<List<PreseasonScheduleEntity>> =
        preseasonScheduleDao.getTeamPreseasonSchedule(teamName, season)

    fun getUpcomingPreseasonMatches(teamName: String, season: String): Flow<List<PreseasonScheduleEntity>> =
        preseasonScheduleDao.getUpcomingPreseasonMatches(teamName, season)

    fun getCompletedPreseasonMatches(teamName: String, season: String): Flow<List<PreseasonScheduleEntity>> =
        preseasonScheduleDao.getCompletedPreseasonMatches(teamName, season)

    // ============ USER TEAM (MANAGER'S TEAM) ============

    suspend fun generateUserPreseasonTour(
        userTeamName: String,
        season: String,
        tourLocation: String = TourLocation.TANZANIA.value
    ): List<PreseasonScheduleEntity> {
        // Delete any existing preseason matches for this team and season
        preseasonScheduleDao.deleteBySeason(season)

        val matches = mutableListOf<PreseasonScheduleEntity>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        // Set start date to July 1st of the season year
        val seasonYear = season.split("/").first().toInt()
        calendar.set(seasonYear, Calendar.JULY, 1, 15, 0)

        // Generate 4 preseason matches
        val opponents = when (tourLocation) {
            TourLocation.TANZANIA.value -> listOf("Young Africans", "Simba SC", "Azam FC", "Singida Black Stars")
            TourLocation.KENYA.value -> listOf("Gor Mahia", "AFC Leopards", "KCB", "Bandari")
            TourLocation.UGANDA.value -> listOf("Vipers SC", "KCCA", "Express FC", "URA SC")
            TourLocation.EGYPT.value -> listOf("Al Ahly", "Zamalek SC", "Pyramids FC", "Future FC")
            TourLocation.GHANA.value -> listOf("Asante Kotoko", "Hearts of Oak", "Medeama", "Aduana Stars")
            TourLocation.NIGERIA.value -> listOf("Enyimba", "Kano Pillars", "Rangers", "Shooting Stars")
            TourLocation.SOUTH_AFRICA.value -> listOf("Kaizer Chiefs", "Orlando Pirates", "Mamelodi Sundowns", "SuperSport United")
            TourLocation.RWANDA.value -> listOf("APR FC", "Rayon Sports", "Police FC Rwanda", "AS Kigali")
            TourLocation.ZAMBIA.value -> listOf("Power Dynamos FC", "ZESCO United FC", "Nkana FC", "Red Arrows FC")
            TourLocation.ZIMBABWE.value -> listOf("Dynamos FC", "Highlanders FC", "CAPS United", "FC Platinum")
            else -> listOf("Young Africans", "Simba SC", "Azam FC", "Singida Black Stars")
        }

        opponents.forEachIndexed { index, opponent ->
            val match = PreseasonScheduleEntity(
                teamName = userTeamName,
                season = season,
                matchDate = dateFormat.format(calendar.time),
                opponent = opponent,
                location = if (index % 2 == 0) "Home" else "Away",
                stadium = if (index % 2 == 0) getTeamStadium(userTeamName) else getTeamStadium(opponent),
                status = PreseasonStatus.SCHEDULED.value,
                isUserTeam = true,
                tourLocation = tourLocation
            )
            matches.add(match)

            // Next match in 4 days
            calendar.add(Calendar.DAY_OF_YEAR, 4)
        }

        preseasonScheduleDao.insertAll(matches)

        // Generate opponent preseason friendlies (other teams also arrange friendlies)
        generateOpponentPreseasonFriendlies(season, tourLocation, opponents)

        return matches
    }

    /**
     * Generate preseason friendlies for other teams (not the user's team)
     * This makes the game world feel alive - other teams are also preparing for the season
     */
    private suspend fun generateOpponentPreseasonFriendlies(
        season: String,
        tourLocation: String,
        localOpponents: List<String>
    ) {
        val allTeams = teamsRepository.getAllTeams().firstOrNull() ?: emptyList()
        val otherTeams = allTeams.filter { it.name !in localOpponents }.shuffled().take(10)

        val calendar = Calendar.getInstance()
        val seasonYear = season.split("/").first().toInt()
        calendar.set(seasonYear, Calendar.JULY, 1, 15, 0)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        val friendlies = mutableListOf<PreseasonScheduleEntity>()

        // Generate friendlies for other teams
        otherTeams.forEachIndexed { index, team ->
            if (index % 2 == 0) {
                val opponent = otherTeams.getOrNull(index + 1) ?: return@forEachIndexed

                val friendly = PreseasonScheduleEntity(
                    teamName = team.name,
                    season = season,
                    matchDate = dateFormat.format(calendar.time),
                    opponent = opponent.name,
                    location = "Home",
                    stadium = team.homeStadium,
                    status = PreseasonStatus.SCHEDULED.value,
                    isUserTeam = false,
                    tourLocation = tourLocation
                )
                friendlies.add(friendly)

                calendar.add(Calendar.HOUR_OF_DAY, 2)
            }
        }

        preseasonScheduleDao.insertAll(friendlies)
    }

    // ============ MATCH MANAGEMENT ============

    suspend fun completePreseasonMatch(
        matchId: Int,
        homeScore: Int,
        opponentScore: Int
    ): PreseasonScheduleEntity? {
        val match = preseasonScheduleDao.getById(matchId) ?: return null

        val updated = match.copy(
            status = PreseasonStatus.COMPLETED.value,
            homeScore = homeScore,
            opponentScore = opponentScore
        )

        preseasonScheduleDao.update(updated)
        return updated
    }

    suspend fun cancelPreseasonMatch(matchId: Int): PreseasonScheduleEntity? {
        val match = preseasonScheduleDao.getById(matchId) ?: return null

        val updated = match.copy(
            status = PreseasonStatus.CANCELLED.value
        )

        preseasonScheduleDao.update(updated)
        return updated
    }

    // ============ OPPONENT REQUEST SYSTEM ============

    /**
     * Other teams can request preseason friendlies with the user's team
     * This adds African drama - smaller teams wanting to play bigger teams
     */
    suspend fun requestPreseasonFriendly(
        requestingTeam: String,
        userTeam: String,
        season: String,
        proposedDate: String
    ): PreseasonScheduleEntity? {

        // Check if date is available
        val existingMatches = preseasonScheduleDao.getTeamPreseasonSchedule(userTeam, season)
            .firstOrNull() ?: emptyList()

        val isDateAvailable = existingMatches.none { it.matchDate == proposedDate }

        if (!isDateAvailable) return null

        val match = PreseasonScheduleEntity(
            teamName = userTeam,
            season = season,
            matchDate = proposedDate,
            opponent = requestingTeam,
            location = "Home",
            stadium = getTeamStadium(userTeam),
            status = PreseasonStatus.SCHEDULED.value,
            isUserTeam = true,
            tourLocation = "Friendly Request"
        )

        preseasonScheduleDao.insert(match)
        return match
    }

    // ============ UTILITY ============

    private suspend fun getTeamStadium(teamName: String): String? {
        return teamsRepository.getTeamByName(teamName)?.homeStadium
    }

    suspend fun getPreseasonStats(teamName: String, season: String): PreseasonStats? =
        preseasonScheduleDao.getPreseasonStats(teamName, season)

    // ============ DASHBOARD ============

    suspend fun getPreseasonDashboard(teamName: String, season: String): PreseasonDashboard {
        val allMatches = preseasonScheduleDao.getTeamPreseasonSchedule(teamName, season)
            .firstOrNull() ?: emptyList()

        val scheduled = allMatches.filter { it.status == PreseasonStatus.SCHEDULED.value }
        val completed = allMatches.filter { it.status == PreseasonStatus.COMPLETED.value }

        val wins = completed.count { it.didWin }
        val draws = completed.count { it.isDraw }
        val losses = completed.count { it.didLose }
        val goalsFor = completed.sumOf { it.homeScore ?: 0 }
        val goalsAgainst = completed.sumOf { it.opponentScore ?: 0 }

        return PreseasonDashboard(
            totalMatches = allMatches.size,
            scheduled = scheduled.size,
            completed = completed.size,
            wins = wins,
            draws = draws,
            losses = losses,
            goalsFor = goalsFor,
            goalsAgainst = goalsAgainst,
            goalDifference = goalsFor - goalsAgainst,
            upcomingMatches = scheduled.sortedBy { it.matchDate },
            recentResults = completed.sortedByDescending { it.matchDate }.take(5)
        )
    }
}

// ============ DATA CLASSES ============

data class PreseasonDashboard(
    val totalMatches: Int,
    val scheduled: Int,
    val completed: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int,
    val upcomingMatches: List<PreseasonScheduleEntity>,
    val recentResults: List<PreseasonScheduleEntity>
)