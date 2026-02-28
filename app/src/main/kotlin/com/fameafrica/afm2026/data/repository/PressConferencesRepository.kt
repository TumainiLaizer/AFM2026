package com.fameafrica.afm2026.data.repository

import com.fameafrica.afm2026.data.database.dao.ManagerPressStats
import com.fameafrica.afm2026.data.database.dao.PressConferencesDao
import com.fameafrica.afm2026.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PressConferencesRepository @Inject constructor(
    private val pressConferencesDao: PressConferencesDao,
    private val journalistsRepository: JournalistsRepository,
    private val managersRepository: ManagersRepository,
    private val newsRepository: NewsRepository
) {

    // ============ BASIC CRUD ============

    fun getAllPressConferences(): Flow<List<PressConferencesEntity>> = pressConferencesDao.getAll()

    suspend fun getPressConferenceById(id: Int): PressConferencesEntity? = pressConferencesDao.getById(id)

    suspend fun insertPressConference(pressConference: PressConferencesEntity) = pressConferencesDao.insert(pressConference)

    suspend fun updatePressConference(pressConference: PressConferencesEntity) = pressConferencesDao.update(pressConference)

    suspend fun deletePressConference(pressConference: PressConferencesEntity) = pressConferencesDao.delete(pressConference)

    // ============ MANAGER-BASED ============

    fun getPressConferencesByManager(managerId: Int): Flow<List<PressConferencesEntity>> =
        pressConferencesDao.getByManager(managerId)

    fun getPendingPressConferences(managerId: Int): Flow<List<PressConferencesEntity>> =
        pressConferencesDao.getPendingPressConferences(managerId)

    // ============ PRESS CONFERENCE GENERATION - MANAGER ONLY ============

    /**
     * Generate a press conference with THREE randomized response options
     * The options are shuffled so the user doesn't know which is positive/neutral/negative
     * This creates a TRICKY decision-making experience
     */
    suspend fun generatePressConference(
        managerId: Int,
        context: String,
        category: QuestionCategory,
        customQuestion: String? = null
    ): PressConferencesEntity? {

        val manager = managersRepository.getManagerById(managerId) ?: return null
        val journalist = journalistsRepository.getRandomJournalist() ?: return null

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())

        // Get question and response options based on category and journalist personality
        val questionData = getQuestionWithResponses(category, journalist.personality, context, manager.name)

        // Create three response options with different impacts
        val options = listOf(
            Triple(questionData.positiveResponse, ResponseType.POSITIVE.value, questionData.positiveImpact),
            Triple(questionData.neutralResponse, ResponseType.NEUTRAL.value, questionData.neutralImpact),
            Triple(questionData.negativeResponse, ResponseType.NEGATIVE.value, questionData.negativeImpact)
        )

        // 🔥 RANDOMIZE THE ORDER - This makes the game TRICKY
        // User cannot just pick the first option - they must read carefully!
        val shuffledOptions = options.shuffled(Random(System.currentTimeMillis()))

        val pressConference = PressConferencesEntity(
            managerId = managerId,
            journalistName = journalist.name,
            journalistPersonality = journalist.personality,
            questionCategory = category.value,
            question = customQuestion ?: questionData.question,
            optionA = shuffledOptions[0].first,
            optionB = shuffledOptions[1].first,
            optionC = shuffledOptions[2].first,
            responseTypeA = shuffledOptions[0].second,
            responseTypeB = shuffledOptions[1].second,
            responseTypeC = shuffledOptions[2].second,
            timestamp = timestamp,
            isPublished = false
        )

        pressConferencesDao.insert(pressConference)
        return pressConference
    }

    /**
     * Submit answer to press conference
     * Calculates impact on team morale and manager reputation
     * Generates news article about the press conference
     */
    suspend fun submitResponse(
        pressConferenceId: Int,
        selectedOption: String
    ): PressConferencesEntity? {

        val pressConference = pressConferencesDao.getById(pressConferenceId) ?: return null

        // Helper data class for clarity
        data class ResponseData(
            val text: String,
            val type: String,
            val impact: Int,
            val reputationChange: Int
        )

        // Determine which option was selected and get its data
        val responseData = when (selectedOption) {
            pressConference.optionA -> ResponseData(
                text = pressConference.optionA,
                type = pressConference.responseTypeA,
                impact = getImpactValue(pressConference.responseTypeA),
                reputationChange = getReputationChange(pressConference.responseTypeA)
            )
            pressConference.optionB -> ResponseData(
                text = pressConference.optionB,
                type = pressConference.responseTypeB,
                impact = getImpactValue(pressConference.responseTypeB),
                reputationChange = getReputationChange(pressConference.responseTypeB)
            )
            pressConference.optionC -> ResponseData(
                text = pressConference.optionC,
                type = pressConference.responseTypeC,
                impact = getImpactValue(pressConference.responseTypeC),
                reputationChange = getReputationChange(pressConference.responseTypeC)
            )
            else -> return null
        }

        // Update press conference using the clear properties from our data class
        val updated = pressConference.copy(
            selectedResponse = selectedOption,
            responseText = responseData.text,
            // The rest of your responseType properties are fine
            responseTypeA = pressConference.responseTypeA,
            responseTypeB = pressConference.responseTypeB,
            responseTypeC = pressConference.responseTypeC,
            impactOnTeam = responseData.impact,
            reputationChange = responseData.reputationChange,
            isPublished = true
        )

        pressConferencesDao.update(updated)

        // Update manager reputation
        managersRepository.updateReputation(pressConference.managerId, responseData.reputationChange)

        // Generate news article about the press conference
        generatePressConferenceNews(updated)

        return updated
    }

    /**
     * Calculate impact on team morale (-10 to +10)
     */
    private fun getImpactValue(responseType: String): Int {
        return when (responseType) {
            ResponseType.POSITIVE.value -> Random.nextInt(3, 8)  // +3 to +7
            ResponseType.NEUTRAL.value -> Random.nextInt(-2, 3) // -2 to +2
            ResponseType.NEGATIVE.value -> Random.nextInt(-8, -2) // -8 to -3
            else -> 0
        }
    }

    /**
     * Calculate reputation change (-5 to +5)
     */
    private fun getReputationChange(responseType: String): Int {
        return when (responseType) {
            ResponseType.POSITIVE.value -> Random.nextInt(1, 4)  // +1 to +3
            ResponseType.NEUTRAL.value -> Random.nextInt(-1, 2) // -1 to +1
            ResponseType.NEGATIVE.value -> Random.nextInt(-4, -1) // -4 to -2
            else -> 0
        }
    }

    /**
     * Generate news article from press conference
     */
    private suspend fun generatePressConferenceNews(pressConference: PressConferencesEntity) {
        val manager = managersRepository.getManagerById(pressConference.managerId) ?: return
        val journalist = journalistsRepository.getJournalistByName(pressConference.journalistName) ?: return

        val headline = when (pressConference.responseTypeA) { // Using selected response type
            ResponseType.POSITIVE.value -> "${manager.name} confident about ${pressConference.questionCategory.lowercase()}"
            ResponseType.NEGATIVE.value -> "${manager.name} concerned: \"${pressConference.responseText?.take(30)}...\""
            else -> "${manager.name} speaks on ${pressConference.questionCategory.lowercase()}"
        }

        val content = buildString {
            appendLine("In today's press conference, ${manager.name} faced questions from ${journalist.name} of ${journalist.mediaCompany}.")
            appendLine()
            appendLine("Q: ${pressConference.question}")
            appendLine()
            appendLine("A: \"${pressConference.responseText}\"")
            appendLine()

            when (pressConference.impactOnTeam) {
                in 1..10 -> appendLine("The manager's words have lifted team morale.")
                in -10..-1 -> appendLine("The manager's comments have unsettled the dressing room.")
                else -> appendLine("The manager kept a balanced tone.")
            }
        }

        val category = when (pressConference.questionCategory) {
            "Transfer Rumors" -> "TRANSFER"
            "Match Performance" -> "MATCH"
            else -> "PRESS_CONFERENCE"
        }

        newsRepository.createNewsArticle(
            headline = headline,
            content = content,
            category = category,
            journalistName = journalist.name,
            journalistLogo = journalist.logo,
            isTopNews = pressConference.impactOnTeam >= 5 || pressConference.impactOnTeam <= -5
        )
    }

    /**
     * AFRICAN DRAMA - Context-aware questions with personality-based responses
     */
    private fun getQuestionWithResponses(
        category: QuestionCategory,
        journalistPersonality: String,
        context: String,
        managerName: String
    ): QuestionData {
        return when (category) {
            QuestionCategory.MATCH_PERFORMANCE -> getMatchPerformanceQuestion(journalistPersonality, context)
            QuestionCategory.TRANSFER_RUMORS -> getTransferRumorQuestion(journalistPersonality, context, managerName)
            QuestionCategory.PLAYER_FORM -> getPlayerFormQuestion(journalistPersonality, context)
            QuestionCategory.TACTICS -> getTacticsQuestion(journalistPersonality)
            QuestionCategory.BOARD -> getBoardQuestion(journalistPersonality)
            QuestionCategory.FANS -> getFanQuestion(journalistPersonality)
            QuestionCategory.RIVALS -> getRivalryQuestion(journalistPersonality, context)
            QuestionCategory.INJURY -> getInjuryQuestion(journalistPersonality, context)
            QuestionCategory.CONTRACT -> getContractQuestion(journalistPersonality, context, managerName)
            QuestionCategory.FUTURE -> getFutureQuestion(journalistPersonality, context, managerName)
        }
    }

    // ============ AFRICAN FOOTBALL DRAMA - QUESTION SETS ============

    private fun getMatchPerformanceQuestion(personality: String, context: String): QuestionData {
        val question = when (personality) {
            JournalistPersonality.HOSTILE.value -> "Your team looked completely disorganized out there. Do you even have a game plan?"
            JournalistPersonality.SENSATIONALIST.value -> "Fans are calling this the WORST performance in club history. Are you feeling the pressure?"
            JournalistPersonality.FRIENDLY.value -> "Tough result today. What positives can you take from the match?"
            else -> "How do you assess your team's performance today?"
        }

        return QuestionData(
            question = question,
            positiveResponse = "The lads gave everything. We created chances and showed character. We'll learn from this and come back stronger.",
            neutralResponse = "Mixed performance. Some good moments, but we need to be more consistent. Back to training tomorrow.",
            negativeResponse = "That was unacceptable. We didn't follow the game plan. I take full responsibility for this result.",
            positiveImpact = 5,
            neutralImpact = 0,
            negativeImpact = -5
        )
    }

    private fun getTransferRumorQuestion(personality: String, context: String, managerName: String): QuestionData {
        val question = when (personality) {
            JournalistPersonality.SENSATIONALIST.value -> "EXCLUSIVE: Is it true that ${context} has handed in a transfer request? Our sources say he wants out!"
            JournalistPersonality.HOSTILE.value -> "There are rumors you've lost the dressing room and players want to leave. Any comment?"
            JournalistPersonality.ANALYST.value -> "With ${context}'s contract expiring, what's the club's position on his future?"
            else -> "Can you clarify the situation regarding ${context} and the recent transfer speculation?"
        }

        return QuestionData(
            question = question,
            positiveResponse = "${context} is fully committed to this club. We're in positive contract talks and expect him to stay.",
            neutralResponse = "These are just rumors. We don't comment on speculation. ${context} is focused on training.",
            negativeResponse = "Look, in football these things happen. If a player wants to leave and the offer is right, we'll consider it.",
            positiveImpact = 6,
            neutralImpact = 1,
            negativeImpact = -7
        )
    }

    private fun getPlayerFormQuestion(personality: String, context: String): QuestionData {
        val question = when (personality) {
            JournalistPersonality.HOSTILE.value -> "${context} has been a shadow of himself this season. Is he past it?"
            JournalistPersonality.SENSATIONALIST.value -> "Fans are BOOING ${context} off the pitch. Will you drop him?"
            JournalistPersonality.FRIENDLY.value -> "${context} seems to be struggling for form. How are you supporting him?"
            else -> "What's your assessment of ${context}'s recent performances?"
        }

        return QuestionData(
            question = question,
            positiveResponse = "I have absolute faith in ${context}. Class is permanent. He's working hard in training and will come good.",
            neutralResponse = "He's working through a difficult period. We're giving him support and competition for places is healthy.",
            negativeResponse = "His form has been below the standards we expect. He needs to show more in training to earn his spot.",
            positiveImpact = 4,
            neutralImpact = 0,
            negativeImpact = -6
        )
    }

    private fun getTacticsQuestion(personality: String): QuestionData {
        val question = when (personality) {
            JournalistPersonality.ANALYST.value -> "You've switched formations three times this season. Are you still searching for your best XI?"
            JournalistPersonality.HOSTILE.value -> "Critics say your tactics are outdated and predictable. How do you respond?"
            JournalistPersonality.SENSATIONALIST.value -> "Is it true players are confused by your tactical instructions?"
            else -> "Can you explain your tactical approach for the upcoming match?"
        }

        return QuestionData(
            question = question,
            positiveResponse = "We have a clear philosophy and identity. The system evolves based on the players at my disposal and the opposition.",
            neutralResponse = "We analyze every opponent and set up accordingly. The players understand their roles.",
            negativeResponse = "Look, sometimes you have to adapt. We've tried different approaches to get results.",
            positiveImpact = 3,
            neutralImpact = 0,
            negativeImpact = -4
        )
    }

    private fun getBoardQuestion(personality: String): QuestionData {
        val question = when (personality) {
            JournalistPersonality.SENSATIONALIST.value -> "Rumors are swirling that the board has lost confidence in you. Do you fear for your job?"
            JournalistPersonality.HOSTILE.value -> "With the team underperforming, are you still getting full backing from upstairs?"
            JournalistPersonality.FRIENDLY.value -> "How is your relationship with the board and do you feel supported?"
            else -> "Can you update us on your discussions with the board regarding the transfer window?"
        }

        return QuestionData(
            question = question,
            positiveResponse = "The board has been incredibly supportive. We're aligned on the vision and working together to move the club forward.",
            neutralResponse = "I have regular communication with the board. We discuss all aspects of the club professionally.",
            negativeResponse = "That's a question for the board, not for me. My focus is on the training ground and the next match.",
            positiveImpact = 5,
            neutralImpact = -1,
            negativeImpact = -8
        )
    }

    private fun getFanQuestion(personality: String): QuestionData {
        val question = when (personality) {
            JournalistPersonality.SENSATIONALIST.value -> "Fans are organizing a protest against the team's performance. What's your message to them?"
            JournalistPersonality.HOSTILE.value -> "The atmosphere at the stadium has turned toxic. Have you lost the fans?"
            JournalistPersonality.FRIENDLY.value -> "What would you say to the fans who traveled long distance to support the team?"
            else -> "How important is the support of the fans to this team?"
        }

        return QuestionData(
            question = question,
            positiveResponse = "Our fans are the heartbeat of this club. Their support drives us forward and we'll give everything to make them proud.",
            neutralResponse = "We appreciate the fans' support. We know we need to give them more to cheer about.",
            negativeResponse = "We understand their frustration because we're frustrated too. We need to stick together through difficult moments.",
            positiveImpact = 6,
            neutralImpact = 1,
            negativeImpact = -3
        )
    }

    private fun getRivalryQuestion(personality: String, context: String): QuestionData {
        val question = when (personality) {
            JournalistPersonality.SENSATIONALIST.value -> "This derby means EVERYTHING to the fans. Is this the biggest match of your career?"
            JournalistPersonality.HOSTILE.value -> "You haven't beaten ${context} in five attempts. Why do you always struggle against them?"
            JournalistPersonality.ANALYST.value -> "What's the key tactical battle you're focusing on for the derby?"
            else -> "How are you preparing for the match against your rivals?"
        }

        return QuestionData(
            question = question,
            positiveResponse = "These are the games you want to be involved in. We're relishing the challenge and believe in our ability to get the result.",
            neutralResponse = "It's an important game like any other. We prepare the same way and focus on our own performance.",
            negativeResponse = "Past results don't matter. This is a new game and we're determined to put things right.",
            positiveImpact = 7,
            neutralImpact = 0,
            negativeImpact = -4
        )
    }

    private fun getInjuryQuestion(personality: String, context: String): QuestionData {
        val question = when (personality) {
            JournalistPersonality.SENSATIONALIST.value -> "Is ${context}'s injury worse than the club is letting on? Will he be out for the season?"
            JournalistPersonality.HOSTILE.value -> "Your medical team has a poor record with injuries. Is ${context} being rushed back?"
            JournalistPersonality.FRIENDLY.value -> "How is ${context} progressing in his recovery?"
            else -> "Can you give us an update on ${context}'s injury status?"
        }

        return QuestionData(
            question = question,
            positiveResponse = "${context} is ahead of schedule in his recovery. We're optimistic he'll return sooner than expected.",
            neutralResponse = "He's making steady progress. We'll assess him week by week and won't take any risks.",
            negativeResponse = "It's a complicated injury. We're being cautious and he'll be out longer than initially thought.",
            positiveImpact = 4,
            neutralImpact = 0,
            negativeImpact = -5
        )
    }

    private fun getContractQuestion(personality: String, context: String, managerName: String): QuestionData {
        val question = when (personality) {
            JournalistPersonality.SENSATIONALIST.value -> "Your own contract expires at the end of the season. Are you planning to stay or are you eyeing other opportunities?"
            JournalistPersonality.HOSTILE.value -> "There's been no announcement on your contract. Should fans be worried you're leaving?"
            JournalistPersonality.FRIENDLY.value -> "Are you close to agreeing a new deal with the club?"
            else -> "Can you clarify your contract situation?"
        }

        return QuestionData(
            question = question,
            positiveResponse = "I'm committed to this project. Talks are positive and I see my long-term future here.",
            neutralResponse = "My focus is entirely on the upcoming matches. Contract discussions are between me and the board.",
            negativeResponse = "Now isn't the right time to discuss my personal situation. I have a job to do here.",
            positiveImpact = 5,
            neutralImpact = -2,
            negativeImpact = -7
        )
    }

    private fun getFutureQuestion(personality: String, context: String, managerName: String): QuestionData {
        val question = when (personality) {
            JournalistPersonality.SENSATIONALIST.value -> "There are rumors linking you with the ${context} job. Would you be interested?"
            JournalistPersonality.HOSTILE.value -> "Are you already planning your exit? Some big clubs are reportedly monitoring you."
            JournalistPersonality.FRIENDLY.value -> "Where do you see this team in the next 3-5 years?"
            else -> "What are your long-term ambitions with this club?"
        }

        return QuestionData(
            question = question,
            positiveResponse = "I'm 100% focused on building something special here. We're creating a legacy and I want to be part of it.",
            neutralResponse = "I have a contract and I respect it. My job is to get the best out of this squad.",
            negativeResponse = "I don't speculate on rumors. I'm ambitious and we'll see what the future brings.",
            positiveImpact = 6,
            neutralImpact = -2,
            negativeImpact = -8
        )
    }

    // ============ STATISTICS ============

    suspend fun getManagerPressStats(managerId: Int): ManagerPressStats? =
        pressConferencesDao.getManagerPressStats(managerId)

    // ============ DASHBOARD ============

    suspend fun getPressConferenceDashboard(managerId: Int): PressConferenceDashboard {
        val allPress = pressConferencesDao.getByManager(managerId).firstOrNull() ?: emptyList()
        val pending = allPress.filter { it.selectedResponse == null }
        val published = allPress.filter { it.isPublished }

        val totalImpact = published.sumOf { it.impactOnTeam }
        val totalRepChange = published.sumOf { it.reputationChange }
        val avgImpact = if (published.isNotEmpty()) totalImpact.toDouble() / published.size else 0.0
        val avgRepChange = if (published.isNotEmpty()) totalRepChange.toDouble() / published.size else 0.0

        val positiveResponses = published.count { it.selectedResponseType == ResponseType.POSITIVE.value }
        val neutralResponses = published.count { it.selectedResponseType == ResponseType.NEUTRAL.value }
        val negativeResponses = published.count { it.selectedResponseType == ResponseType.NEGATIVE.value }

        return PressConferenceDashboard(
            totalPressConferences = allPress.size,
            pendingResponses = pending.size,
            publishedResponses = published.size,
            averageImpact = avgImpact,
            averageReputationChange = avgRepChange,
            positiveResponseCount = positiveResponses,
            neutralResponseCount = neutralResponses,
            negativeResponseCount = negativeResponses,
            recentPressConferences = published.sortedByDescending { it.timestamp }.take(10),
            pendingPressConferences = pending.sortedBy { it.timestamp }
        )
    }
}

// ============ DATA CLASSES ============

data class QuestionData(
    val question: String,
    val positiveResponse: String,
    val neutralResponse: String,
    val negativeResponse: String,
    val positiveImpact: Int,
    val neutralImpact: Int,
    val negativeImpact: Int
)

data class PressConferenceDashboard(
    val totalPressConferences: Int,
    val pendingResponses: Int,
    val publishedResponses: Int,
    val averageImpact: Double,
    val averageReputationChange: Double,
    val positiveResponseCount: Int,
    val neutralResponseCount: Int,
    val negativeResponseCount: Int,
    val recentPressConferences: List<PressConferencesEntity>,
    val pendingPressConferences: List<PressConferencesEntity>
)