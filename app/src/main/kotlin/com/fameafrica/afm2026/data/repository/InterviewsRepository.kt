package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.InterviewsDao
import com.fameafrica.afm2026.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class InterviewsRepository @Inject constructor(
    private val interviewsDao: InterviewsDao,
    private val journalistsRepository: JournalistsRepository,
    private val managersRepository: ManagersRepository,
    private val playersRepository: PlayersRepository,
    private val newsRepository: NewsRepository
) {

    // ============ BASIC CRUD ============

    fun getAllInterviews(): Flow<List<InterviewsEntity>> = interviewsDao.getAll()

    suspend fun getInterviewById(id: Int): InterviewsEntity? = interviewsDao.getById(id)

    suspend fun insertInterview(interview: InterviewsEntity) = interviewsDao.insert(interview)

    suspend fun updateInterview(interview: InterviewsEntity) = interviewsDao.update(interview)

    suspend fun deleteInterview(interview: InterviewsEntity) = interviewsDao.delete(interview)

    // ============ MANAGER INTERVIEWS ============

    fun getManagerInterviews(managerId: Int): Flow<List<InterviewsEntity>> =
        interviewsDao.getManagerInterviews(managerId)

    fun getPendingManagerInterviews(managerId: Int): Flow<List<InterviewsEntity>> =
        interviewsDao.getPendingManagerInterviews(managerId)

    // ============ PLAYER INTERVIEWS ============

    fun getPlayerInterviews(playerId: Int): Flow<List<InterviewsEntity>> =
        interviewsDao.getPlayerInterviews(playerId)

    fun getPendingPlayerInterviews(playerId: Int): Flow<List<InterviewsEntity>> =
        interviewsDao.getPendingPlayerInterviews(playerId)

    // ============ PLAYER INTERVIEW GENERATION ============

    /**
     * Generate interview for a player based on their:
     * - Performance (recent goals/assists)
     * - Happiness (morale)
     * - Contract situation
     * - Transfer rumors
     * - Loan speculation
     * - Unsettled status
     */
    suspend fun generatePlayerInterview(playerId: Int): InterviewsEntity? {
        val player = playersRepository.getPlayerById(playerId) ?: return null
        val journalist = journalistsRepository.getRandomJournalist() ?: return null

        // Player must be active
        if (player.retired) return null

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val today = dateFormat.format(Date())

        // Determine interview type based on player's situation
        val interviewData = determinePlayerInterviewContext(player)

        val interview = InterviewsEntity(
            playerId = playerId,
            intervieweeName = player.name,
            intervieweeType = IntervieweeType.PLAYER.value,
            journalistName = journalist.name,
            journalistPersonality = journalist.personality,
            dateRequested = today,
            status = InterviewStatus.PENDING.value,
            interviewType = interviewData.type.value,
            topic = interviewData.topic,
            question = interviewData.question,
            notes = "Player interview requested by ${journalist.name}"
        )

        interviewsDao.insert(interview)
        return interview
    }

    /**
     * Generate interview for manager
     */
    suspend fun generateManagerInterview(managerId: Int, context: String): InterviewsEntity? {
        val manager = managersRepository.getManagerById(managerId) ?: return null
        val journalist = journalistsRepository.getRandomJournalist() ?: return null

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val today = dateFormat.format(Date())

        val interview = InterviewsEntity(
            intervieweeId = managerId,
            intervieweeName = manager.name,
            intervieweeType = IntervieweeType.MANAGER.value,
            journalistName = journalist.name,
            journalistPersonality = journalist.personality,
            dateRequested = today,
            status = InterviewStatus.PENDING.value,
            interviewType = InterviewType.EXCLUSIVE.value,
            topic = "Manager Interview",
            question = "We'd like to sit down with you for an exclusive interview about your time at the club and your future ambitions.",
            notes = "Exclusive interview requested by ${journalist.name}"
        )

        interviewsDao.insert(interview)
        return interview
    }

    /**
     * Determine player interview context based on their current situation
     * AFRICAN DRAMA - Players can be:
     * - Unsettled and wanting to leave
     * - Angry about being benched
     * - Thrilled about scoring form
     * - Frustrated with contract delays
     * - Linked with European moves
     */
    private fun determinePlayerInterviewContext(player: PlayersEntity): InterviewContext {
        val random = Random(System.currentTimeMillis())

        // Check if player is unhappy (low morale)
        if (player.morale <= 40) {
            val reasons = listOf(
                "lack of playing time",
                "not feeling valued",
                "broken promises",
                "disagreement with manager",
                "homesickness"
            )
            val reason = reasons.random()

            return InterviewContext(
                type = InterviewType.UNSETTLED,
                topic = "Player Unhappiness",
                question = "There are rumors you're unhappy at the club due to ${reason}. Can you tell us your side of the story?"
            )
        }

        // Check if player is in excellent form
        if (player.currentForm >= 75 && player.goals > 0) {
            return InterviewContext(
                type = InterviewType.FORM,
                topic = "Scoring Form",
                question = "You've been in incredible form lately with ${player.goals} goals this season. What's the secret to your success?"
            )
        }

        // Check contract situation
        if (player.contractExpiry <= "2025") {
            return InterviewContext(
                type = InterviewType.CONTRACT,
                topic = "Contract Situation",
                question = "Your contract expires in ${player.contractExpiry}. Are you close to agreeing a new deal or are you considering your options?"
            )
        }

        // Check if player is transfer listed
        if (player.transferListStatus == "AVAILABLE") {
            val destinations = listOf(
                "Europe", "South Africa", "Egypt", "Morocco", "Tunisia"
            )
            val destination = destinations.random()

            return InterviewContext(
                type = InterviewType.TRANSFER_RUMOR,
                topic = "Transfer Speculation",
                question = "You've been transfer listed. There's strong interest from ${destination}. Would you be open to a move?"
            )
        }

        // Check if player is loan listed
        if (player.transferListStatus == "LOAN_LISTED") {
            return InterviewContext(
                type = InterviewType.LOAN,
                topic = "Loan Move",
                question = "The club is willing to let you leave on loan. What kind of move are you looking for?"
            )
        }

        // Check if player is captain or vice-captain
        if (player.isCaptain || player.isViceCaptain) {
            return InterviewContext(
                type = InterviewType.EXCLUSIVE,
                topic = "Leadership Role",
                question = "As team captain, how do you assess the current squad morale and what needs to change?"
            )
        }

        // Check if player is young talent
        if (player.age <= 21 && player.rating >= 70) {
            return InterviewContext(
                type = InterviewType.EXCLUSIVE,
                topic = "Young Talent",
                question = "You're considered one of Africa's brightest young talents. How do you handle the pressure and expectations?"
            )
        }

        // Default interview
        return InterviewContext(
            type = InterviewType.EXCLUSIVE,
            topic = "Player Profile",
            question = "Tell us about your journey in football and your ambitions with this club."
        )
    }

    /**
     * Schedule an interview
     */
    suspend fun scheduleInterview(interviewId: Int, interviewDate: String): Boolean {
        val interview = interviewsDao.getById(interviewId) ?: return false

        val updated = interview.copy(
            status = InterviewStatus.SCHEDULED.value,
            interviewDate = interviewDate
        )

        interviewsDao.update(updated)
        return true
    }

    /**
     * Complete an interview with response
     */
    suspend fun completeInterview(
        interviewId: Int,
        response: String,
        responseType: String,
        impactOnMorale: Int,
        reputationChange: Int,
        fanPopularityChange: Int
    ): Boolean {
        val interview = interviewsDao.getById(interviewId) ?: return false

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val today = dateFormat.format(Date())

        val updated = interview.copy(
            status = InterviewStatus.COMPLETED.value,
            interviewDate = today,
            response = response,
            responseType = responseType,
            impactOnMorale = impactOnMorale,
            reputationChange = reputationChange,
            fanPopularityChange = fanPopularityChange,
            isPublished = true
        )

        interviewsDao.update(updated)

        // Update player or manager based on interview type
        if (interview.isPlayerInterview) {
            interview.playerId?.let { playerId ->
                playersRepository.updatePlayerMorale(playerId, impactOnMorale)
            }
        } else {
            interview.intervieweeId?.let { managerId ->
                managersRepository.updateReputation(managerId, reputationChange)
            }
        }

        // Generate news article from interview
        generateInterviewNews(updated)

        return true
    }

    /**
     * Decline an interview
     */
    suspend fun declineInterview(interviewId: Int): Boolean {
        val interview = interviewsDao.getById(interviewId) ?: return false

        val updated = interview.copy(
            status = InterviewStatus.DECLINED.value
        )

        interviewsDao.update(updated)

        // Negative impact for declining
        if (interview.isPlayerInterview) {
            interview.playerId?.let { playerId ->
                playersRepository.updatePlayerMorale(playerId, -2)
            }
        } else {
            interview.intervieweeId?.let { managerId ->
                managersRepository.updateReputation(managerId, -1)
            }
        }

        return true
    }

    /**
     * Generate news article from interview
     */
    private suspend fun generateInterviewNews(interview: InterviewsEntity) {
        val journalist = journalistsRepository.getJournalistByName(interview.journalistName) ?: return

        val headline = when (interview.interviewType) {
            "Transfer Rumor" -> "EXCLUSIVE: ${interview.intervieweeName} speaks on transfer speculation"
            "Contract" -> "${interview.intervieweeName} opens up on contract future"
            "Form" -> "${interview.intervieweeName}: \"I'm enjoying my football\""
            "Unsettled" -> "${interview.intervieweeName} admits frustration at club"
            "Loan" -> "${interview.intervieweeName} considering loan move"
            else -> "EXCLUSIVE: ${interview.intervieweeName} sits down for interview"
        }

        val content = buildString {
            appendLine("${journalist.name} of ${journalist.mediaCompany} sat down with ${interview.intervieweeName} for an exclusive interview.")
            appendLine()
            appendLine("Q: ${interview.question}")
            appendLine()
            appendLine("A: \"${interview.response}\"")
            appendLine()

            when {
                interview.impactOnMorale > 0 -> appendLine("The player's comments have boosted morale at the club.")
                interview.impactOnMorale < 0 -> appendLine("These comments may cause concern among the coaching staff.")
            }
        }

        newsRepository.createNewsArticle(
            headline = headline,
            content = content,
            category = "INTERVIEW",
            journalistName = journalist.name,
            journalistLogo = journalist.logo,
            isTopNews = interview.impactOnMorale <= -5 || interview.impactOnMorale >= 5
        )
    }

    /**
     * Generate player interviews based on performance triggers
     */
    suspend fun generatePerformanceBasedInterviews(): List<InterviewsEntity> {
        val interviews = mutableListOf<InterviewsEntity>()

        // Get all active players
        val allPlayers = playersRepository.getAllPlayers().firstOrNull() ?: return emptyList()

        // Players who scored a hat-trick or more (3+ goals)
        val hatTrickScorers = allPlayers.filter { it.goals >= 3 }
        hatTrickScorers.take(2).forEach { player ->
            generatePlayerInterview(player.id)?.let { interviews.add(it) }
        }

        // Players with very low morale (unsettled)
        val unhappyPlayers = allPlayers.filter { it.morale <= 30 }
        unhappyPlayers.take(3).forEach { player ->
            generatePlayerInterview(player.id)?.let { interviews.add(it) }
        }

        // Players with expiring contracts
        val expiringContracts = allPlayers.filter { it.contractExpiry <= "2025" }
        expiringContracts.take(2).forEach { player ->
            generatePlayerInterview(player.id)?.let { interviews.add(it) }
        }

        return interviews
    }

    // ============ DASHBOARD ============

    suspend fun getManagerInterviewsDashboard(managerId: Int): InterviewsDashboard {
        val allInterviews = interviewsDao.getManagerInterviews(managerId).firstOrNull() ?: emptyList()
        val pending = allInterviews.filter { it.status == InterviewStatus.PENDING.value }
        val scheduled = allInterviews.filter { it.status == InterviewStatus.SCHEDULED.value }
        val completed = allInterviews.filter { it.status == InterviewStatus.COMPLETED.value }

        return InterviewsDashboard(
            totalInterviews = allInterviews.size,
            pendingInterviews = pending.size,
            scheduledInterviews = scheduled.size,
            completedInterviews = completed.size,
            declinedInterviews = allInterviews.count { it.status == InterviewStatus.DECLINED.value },
            pendingList = pending,
            scheduledList = scheduled,
            recentCompleted = completed.sortedByDescending { it.interviewDate }.take(5)
        )
    }

    suspend fun getPlayerInterviewsDashboard(playerId: Int): InterviewsDashboard {
        val allInterviews = interviewsDao.getPlayerInterviews(playerId).firstOrNull() ?: emptyList()
        val pending = allInterviews.filter { it.status == InterviewStatus.PENDING.value }
        val scheduled = allInterviews.filter { it.status == InterviewStatus.SCHEDULED.value }
        val completed = allInterviews.filter { it.status == InterviewStatus.COMPLETED.value }

        return InterviewsDashboard(
            totalInterviews = allInterviews.size,
            pendingInterviews = pending.size,
            scheduledInterviews = scheduled.size,
            completedInterviews = completed.size,
            declinedInterviews = allInterviews.count { it.status == InterviewStatus.DECLINED.value },
            pendingList = pending,
            scheduledList = scheduled,
            recentCompleted = completed.sortedByDescending { it.interviewDate }.take(5)
        )
    }
}

// ============ DATA CLASSES ============

data class InterviewContext(
    val type: InterviewType,
    val topic: String,
    val question: String
)

data class InterviewsDashboard(
    val totalInterviews: Int,
    val pendingInterviews: Int,
    val scheduledInterviews: Int,
    val completedInterviews: Int,
    val declinedInterviews: Int,
    val pendingList: List<InterviewsEntity>,
    val scheduledList: List<InterviewsEntity>,
    val recentCompleted: List<InterviewsEntity>
)