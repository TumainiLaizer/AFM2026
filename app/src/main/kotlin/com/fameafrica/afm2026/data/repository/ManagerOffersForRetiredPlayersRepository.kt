package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.ManagerOffersForRetiredPlayersDao
import com.fameafrica.afm2026.data.database.dao.PlayersDao
import com.fameafrica.afm2026.data.database.dao.TeamsDao
import com.fameafrica.afm2026.data.database.dao.LeaguesDao
import com.fameafrica.afm2026.data.database.dao.RetiredPlayerOfferWithDetails
import com.fameafrica.afm2026.data.database.dao.RoleTypeDistribution
import com.fameafrica.afm2026.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Calendar

@Singleton
class ManagerOffersForRetiredPlayersRepository @Inject constructor(
    private val retiredPlayerOffersDao: ManagerOffersForRetiredPlayersDao,
    private val playersDao: PlayersDao,
    private val teamsDao: TeamsDao,
    private val leaguesDao: LeaguesDao
) {

    // ============ BASIC CRUD ============

    fun getAllOffers(): Flow<List<ManagerOffersForRetiredPlayersEntity>> =
        retiredPlayerOffersDao.getAll()

    suspend fun getOfferById(id: Int): ManagerOffersForRetiredPlayersEntity? =
        retiredPlayerOffersDao.getById(id)

    suspend fun insertOffer(offer: ManagerOffersForRetiredPlayersEntity) =
        retiredPlayerOffersDao.insert(offer)

    suspend fun insertAllOffers(offers: List<ManagerOffersForRetiredPlayersEntity>) =
        retiredPlayerOffersDao.insertAll(offers)

    suspend fun updateOffer(offer: ManagerOffersForRetiredPlayersEntity) =
        retiredPlayerOffersDao.update(offer)

    suspend fun deleteOffer(offer: ManagerOffersForRetiredPlayersEntity) =
        retiredPlayerOffersDao.delete(offer)

    // ============ ATTRIBUTE-BASED ROLE DETERMINATION ============

    /**
     * Determine the appropriate staff role for a retired player based on their attributes
     *
     * LEADERSHIP ≥ 80 → SPORTING_DIRECTOR, TECHNICAL_DIRECTOR, ASSISTANT_MANAGER
     * MEDIA_HANDLING ≥ 70 & LEADERSHIP < 70 → CLUB_MEDIA_OFFICER
     * POSITION = 'GK' & LEADERSHIP ≥ 60 → GOALKEEPER_COACH
     * POSITION in ('CB', 'LB', 'RB', 'SW') & LEADERSHIP ≥ 60 → DEFENSIVE_COACH
     * POSITION in ('CDM', 'CM', 'CAM') & LEADERSHIP ≥ 60 → MIDFIELD_COACH
     * POSITION in ('ST', 'CF', 'LW', 'RW') & LEADERSHIP ≥ 60 → ATTACKING_COACH
     * YOUTH_DEVELOPMENT_FOCUS (via player age/experience) → YOUTH_COACH
     * HIGH INTELLIGENCE/DECISIONS → SCOUT, CHIEF_SCOUT
     * DEFAULT → SCOUT
     */
    fun determineStaffRoleForRetiredPlayer(player: PlayersEntity): RetiredPlayerRoleWithDescription {
        val leadership = player.leadership
        val mediaHandling = player.mediaHandling
        val position = player.position
        val positionCategory = player.positionCategory
        val age = player.age
        val experience = player.experience
        val decisions = player.decisions
        val anticipation = player.anticipation

        return when {
            // ============ SENIOR MANAGEMENT ROLES ============
            leadership >= 85 -> {
                // Elite leaders become Sporting Directors or Technical Directors
                val role = if (experience >= 300)
                    RetiredPlayerRoleType.SPORTING_DIRECTOR
                else
                    RetiredPlayerRoleType.TECHNICAL_DIRECTOR

                RetiredPlayerRoleWithDescription(
                    roleType = role.value,
                    description = "With exceptional leadership qualities, this player is suited for a senior management role overseeing club strategy."
                )
            }

            leadership >= 80 && mediaHandling >= 60 -> {
                // Strong leaders with good media presence become Assistant Managers
                RetiredPlayerRoleWithDescription(
                    roleType = RetiredPlayerRoleType.ASSISTANT_MANAGER.value,
                    description = "Natural leader with good communication skills - ideal as right-hand to the first team manager."
                )
            }

            // ============ MEDIA ROLES ============
            mediaHandling >= 70 && leadership < 70 -> {
                // Media-friendly but not strong leaders become Club Media Officers
                RetiredPlayerRoleWithDescription(
                    roleType = RetiredPlayerRoleType.CLUB_MEDIA_OFFICER.value,
                    description = "Comfortable in front of cameras and microphones - perfect for handling club communications."
                )
            }

            // ============ POSITION-SPECIFIC COACHING ROLES ============
            position == "GK" && leadership >= 60 -> {
                RetiredPlayerRoleWithDescription(
                    roleType = RetiredPlayerRoleType.GOALKEEPER_COACH.value,
                    description = "Experienced goalkeeper with ability to train the next generation of shot-stoppers."
                )
            }

            positionCategory == "DEFENDER" && leadership >= 60 -> {
                RetiredPlayerRoleWithDescription(
                    roleType = RetiredPlayerRoleType.YOUTH_COACH.value, // Specialized defensive coach
                    description = "Solid defensive understanding - can organize backlines and develop young defenders."
                )
            }

            positionCategory == "MIDFIELDER" && leadership >= 60 -> {
                RetiredPlayerRoleWithDescription(
                    roleType = RetiredPlayerRoleType.YOUTH_COACH.value,
                    description = "Tactically aware midfielder who can teach passing, positioning, and game management."
                )
            }

            positionCategory == "FORWARD" && leadership >= 60 -> {
                RetiredPlayerRoleWithDescription(
                    roleType = RetiredPlayerRoleType.YOUTH_COACH.value,
                    description = "Natural goalscorer who can mentor young forwards and improve their finishing."
                )
            }

            // ============ PLAYER-COACH (Hybrid Role) ============
            age >= 32 && leadership >= 65 && experience >= 200 -> {
                RetiredPlayerRoleWithDescription(
                    roleType = RetiredPlayerRoleType.PLAYER_COACH.value,
                    description = "Veteran player who can contribute on the pitch while developing coaching skills."
                )
            }

            // ============ SCOUTING ROLES ============
            decisions >= 70 && anticipation >= 70 -> {
                // High football intelligence becomes Chief Scout
                val role = if (experience >= 250)
                    RetiredPlayerRoleType.CHIEF_SCOUT
                else
                    RetiredPlayerRoleType.SCOUT

                RetiredPlayerRoleWithDescription(
                    roleType = role.value,
                    description = "Excellent judge of talent - can identify and evaluate players for the club."
                )
            }

            // ============ MEDICAL ROLES ============
            position == "GK" && experience >= 200 -> {
                // Goalkeepers often become physios (lots of falling experience!)
                RetiredPlayerRoleWithDescription(
                    roleType = RetiredPlayerRoleType.PHYSIOTHERAPIST.value,
                    description = "Understanding of player fitness and rehabilitation from years of professional experience."
                )
            }

            // ============ YOUTH DEVELOPMENT ============
            age >= 30 && player.potential - player.rating >= 10 -> {
                // Players who overachieved their potential can develop youth
                RetiredPlayerRoleWithDescription(
                    roleType = RetiredPlayerRoleType.ACADEMY_MANAGER.value,
                    description = "Knows what it takes to maximize potential - ideal for academy management."
                )
            }

            // ============ DEFAULT ROLE ============
            else -> {
                RetiredPlayerRoleWithDescription(
                    roleType = RetiredPlayerRoleType.SCOUT.value,
                    description = "General football knowledge makes this player a valuable scouting asset."
                )
            }
        }
    }

    /**
     * GENERATE OFFER FOR RETIRED PLAYER
     *
     * Called when a player retires
     * Determines appropriate role based on attributes and generates an offer
     */
    suspend fun generateOfferForRetiredPlayer(
        playerId: Int,
        preferredTeamId: Int? = null
    ): ManagerOffersForRetiredPlayersEntity? {
        val player = playersDao.getById(playerId) ?: return null

        // Player must be retired
        if (!player.retired) return null

        // Determine role based on attributes
        val roleWithDescription = determineStaffRoleForRetiredPlayer(player)

        // Find offering team
        val offeringTeam = if (preferredTeamId != null) {
            teamsDao.getById(preferredTeamId)
        } else {
            // Try to find player's last team
            val lastTeam = teamsDao.getById(player.teamId)

            if (lastTeam != null) {
                lastTeam
            } else {
                // Find a team in an appropriate league level
                val targetLevel = when (player.rating) {
                    in 80..99 -> 1..2
                    in 70..79 -> 2..3
                    else -> 3..4
                }

                val leagues = targetLevel.flatMap { level ->
                    leaguesDao.getLeaguesByLevel(level).firstOrNull() ?: emptyList()
                }

                if (leagues.isNotEmpty()) {
                    val selectedLeague = leagues.random()
                    teamsDao.getTeamsByLeague(selectedLeague.name).firstOrNull()?.firstOrNull()
                } else null
            }
        } ?: return null

        val league = leaguesDao.getByName(offeringTeam.league)
        val leagueLevel = league?.level ?: 3

        // Calculate salary based on role and player rating
        val baseSalary = when (roleWithDescription.roleType) {
            RetiredPlayerRoleType.SPORTING_DIRECTOR.value -> 3000000
            RetiredPlayerRoleType.TECHNICAL_DIRECTOR.value -> 2500000
            RetiredPlayerRoleType.ASSISTANT_MANAGER.value -> 2000000
            RetiredPlayerRoleType.CHIEF_SCOUT.value -> 1800000
            RetiredPlayerRoleType.ACADEMY_MANAGER.value -> 1500000
            RetiredPlayerRoleType.GOALKEEPER_COACH.value -> 1200000
            RetiredPlayerRoleType.YOUTH_COACH.value -> 1000000
            RetiredPlayerRoleType.PLAYER_COACH.value -> 900000
            RetiredPlayerRoleType.SCOUT.value -> 800000
            RetiredPlayerRoleType.PHYSIOTHERAPIST.value -> 700000
            RetiredPlayerRoleType.CLUB_MEDIA_OFFICER.value -> 600000
            else -> 500000
        }

        val ratingBonus = (player.rating - 50) * 10000
        val offeredSalary = (baseSalary + ratingBonus).coerceAtLeast(500000)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 10)
        val expiryDate = calendar.timeInMillis

        val offer = ManagerOffersForRetiredPlayersEntity(
            playerId = player.id,
            playerName = player.name,
            offeredTeam = offeringTeam.name,
            offeredTeamId = offeringTeam.id,
            leagueName = offeringTeam.league,
            leagueLevel = leagueLevel,
            offeredSalary = offeredSalary,
            contractYears = 2,
            status = "Pending",
            roleType = roleWithDescription.roleType,
            roleDescription = roleWithDescription.description,
            offerDate = System.currentTimeMillis(),
            expiryDate = expiryDate,
            message = buildRetiredPlayerOfferMessage(player, roleWithDescription, offeringTeam)
        )

        retiredPlayerOffersDao.insert(offer)
        return offer
    }

    /**
     * GENERATE OFFERS FOR ALL NEWLY RETIRED PLAYERS
     * Called at the end of each season
     */
    suspend fun generateOffersForAllRetiredPlayers(): List<ManagerOffersForRetiredPlayersEntity> {
        val retiredPlayers = playersDao.getRetiredPlayers().firstOrNull() ?: emptyList()
        val offers = mutableListOf<ManagerOffersForRetiredPlayersEntity>()

        retiredPlayers.forEach { player ->
            // Check if player already has any offers
            val existingOffers = retiredPlayerOffersDao.getOffersByPlayer(player.id)
                .firstOrNull() ?: emptyList()

            if (existingOffers.isEmpty()) {
                generateOfferForRetiredPlayer(player.id)?.let { offers.add(it) }
            }
        }

        return offers
    }

    private fun buildRetiredPlayerOfferMessage(
        player: PlayersEntity,
        role: RetiredPlayerRoleWithDescription,
        team: TeamsEntity
    ): String {
        return "Dear ${player.name},\n\n" +
                "We've been following your illustrious career and are impressed by your football intelligence. " +
                "We believe you have the qualities to become an excellent ${role.roleType.replace('_', ' ').lowercase()}.\n\n" +
                "${role.description}\n\n" +
                "Join us at ${team.name} and start your new career in football management.\n\n" +
                "Best regards,\n" +
                "${team.name} Board"
    }

    // ============ OFFER RESPONSE HANDLING ============

    suspend fun acceptOffer(offerId: Int): Boolean {
        val offer = retiredPlayerOffersDao.getById(offerId) ?: return false
        if (offer.status != "Pending" || offer.isExpired) return false

        val updatedOffer = offer.copy(status = "Accepted")
        retiredPlayerOffersDao.update(updatedOffer)

        // Here you would create a staff member from the retired player
        // This would be implemented in the StaffRepository

        // Reject all other pending offers for this player
        val otherOffers = retiredPlayerOffersDao.getPendingOffersByPlayer(offer.playerId)
            .firstOrNull() ?: emptyList()

        otherOffers.forEach { otherOffer ->
            if (otherOffer.id != offerId) {
                retiredPlayerOffersDao.update(otherOffer.copy(status = "Rejected"))
            }
        }

        return true
    }

    suspend fun rejectOffer(offerId: Int): Boolean {
        val offer = retiredPlayerOffersDao.getById(offerId) ?: return false
        if (offer.status != "Pending") return false

        val updatedOffer = offer.copy(status = "Rejected")
        retiredPlayerOffersDao.update(updatedOffer)
        return true
    }

    // ============ QUERIES ============

    fun getOffersByPlayer(playerId: Int): Flow<List<ManagerOffersForRetiredPlayersEntity>> =
        retiredPlayerOffersDao.getOffersByPlayer(playerId)

    fun getPendingOffersByPlayer(playerId: Int): Flow<List<ManagerOffersForRetiredPlayersEntity>> =
        retiredPlayerOffersDao.getPendingOffersByPlayer(playerId)

    fun getCoachingOffers(): Flow<List<ManagerOffersForRetiredPlayersEntity>> =
        retiredPlayerOffersDao.getCoachingOffers()

    fun getScoutingOffers(): Flow<List<ManagerOffersForRetiredPlayersEntity>> =
        retiredPlayerOffersDao.getScoutingOffers()

    fun getDirectorOffers(): Flow<List<ManagerOffersForRetiredPlayersEntity>> =
        retiredPlayerOffersDao.getDirectorOffers()

    fun getMediaOffers(): Flow<List<ManagerOffersForRetiredPlayersEntity>> =
        retiredPlayerOffersDao.getMediaOffers()

    fun getMedicalOffers(): Flow<List<ManagerOffersForRetiredPlayersEntity>> =
        retiredPlayerOffersDao.getMedicalOffers()

    fun getRoleTypeDistribution(): Flow<List<RoleTypeDistribution>> =
        retiredPlayerOffersDao.getRoleTypeDistribution()

    suspend fun getRetiredPlayerOfferWithDetails(offerId: Int): RetiredPlayerOfferWithDetails? =
        retiredPlayerOffersDao.getRetiredPlayerOfferWithDetails(offerId)
}

// ============ DATA CLASSES ============

data class RetiredPlayerRoleWithDescription(
    val roleType: String,
    val description: String
)