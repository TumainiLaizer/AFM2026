package com.fameafrica.afm2026.utils.commentary

import com.fameafrica.afm2026.data.database.entities.ManagersEntity
import com.fameafrica.afm2026.data.database.entities.MatchEventsEntity
import com.fameafrica.afm2026.data.database.entities.RefereesEntity

/**
 * African Football Commentary Generator
 * Fully aligned with MatchEventsEntity event types
 * Captures the passion, drama, and intensity of African football
 */
object AfricanFootballCommentaryGenerator {

    // ============ AFRICAN COMMENTATORS ============

    private val commentators = listOf(
        "Baraka Mpenja (Azam Sports)",
        "Jamal Kivumbi (Citizen TV)",
        "Neema Mwakyusa (Azam Sports)",
        "James Omondi (Citizen TV)",
        "Kwame Asare (SuperSport)",
        "Chidi Obi (BBC Africa)",
        "Mark Fish (SuperSport)",
        "Fatou Diouf (France24 Afrique)",
        "Peter Drury (International)",
        "Martin Tyler (International)"
    )

    // ============ GOAL CELEBRATIONS - AFRICAN STYLE ============

    private val goalCelebrations = listOf(
        "GOOOOOAAAAAL! The net bulges! The crowd erupts like a volcano!",
        "GOOOOOAAAAAL! What a strike! The keeper had no chance!",
        "WALLAHI! That's a goal fit to win any match in Africa!",
        "EHEHEHE! The fans are going crazy in the terraces!",
        "MABROOK! He's done it! The stadium is shaking!",
        "AYEEE! That's why they call him the 'Mbuzi' - the GOAT!",
        "HATIMAYA! He's wheeled away in celebration!",
        "ALHAMDULILLAH! The home crowd are in dreamland!",
        "WAU! WAU! WAU! What have we just witnessed?",
        "KAZI IMEKWISHA! The job is done! It's in the back of the net!",
        "EFFICIENT! Like a leopard stalking its prey, he struck!",
        "BWANA! That's what we came to see! Pure magic!"
    )

    private val freeKickCelebrations = listOf(
        "WHAT A FREE KICK! It's like Beckham reincarnated!",
        "He's bent it like a banana! The wall was useless!",
        "FREE KICK SPECIALIST! He practices this in training every day!",
        "The keeper didn't even move! Absolute thunderbolt!",
        "DIP AND SWERVE! That ball had eyes!",
        "MAGISTERIAL! That's a goal of the season contender!"
    )

    private val penaltyCelebrations = listOf(
        "ICE COLD! He sends the keeper the wrong way!",
        "BOTTLE! That takes nerves of steel in this atmosphere!",
        "He's slotted it home! The keeper had no chance!",
        "CALM AS YOU LIKE! Under pressure, he delivers!",
        "The keeper guessed right but the power was too much!"
    )

    private val ownGoalCelebrations = listOf(
        "OH DEAR! He's put it into his own net! The crowd can't believe it!",
        "UNFORTUNATE! That's a nightmare moment for the defender!",
        "OWN GOAL! The home fans are in disbelief!",
        "He's turned it in! The keeper is left stranded!",
        "What a cruel twist of fate! That's going to haunt him!"
    )

    // ============ SHOT COMMENTARY ============

    private val shotCommentaries = listOf(
        "He's pulled the trigger! But it's straight at the keeper!",
        "SO CLOSE! It whistles just wide of the post!",
        "The keeper makes a comfortable save... nothing too dramatic.",
        "He's gone for goal from distance! It's... just over the bar!",
        "Thunderous strike! But the keeper gets down well to save!",
        "He should have done better there! Straight at the keeper.",
        "That's a speculative effort... and it's not troubling anyone."
    )

    private val shotOnTargetCommentaries = listOf(
        "ON TARGET! The keeper is forced into action!",
        "Good save! The keeper gets down quickly!",
        "HE'S DENIED! What a stop from the goalkeeper!",
        "The keeper palms it away! Corner kick!",
        "PARRIED! It falls to... no, cleared away!"
    )

    private val shotOffTargetCommentaries = listOf(
        "WIDE! That's a waste of a good opportunity!",
        "OVER THE BAR! The fans groan in disappointment!",
        "That's a poor effort... skies it into the stands!",
        "Off target... the goalkeeper won't mind that!",
        "He's snatched at it! That's a frustrated effort!"
    )

    // ============ SAVE COMMENTARY ============

    private val saveCommentaries = listOf(
        "WHAT A SAVE! The keeper flies through the air!",
        "Denied! The goalkeeper stands tall!",
        "MIRACULOUS STOP! How did he keep that out?!",
        "The keeper is having a blinder today!",
        "SAFE HANDS! He catches it comfortably.",
        "PUNCHED AWAY! Under pressure, he deals with it!",
        "The keeper is unbeatable at the moment!"
    )

    // ============ CORNER COMMENTARY ============

    private val cornerCommentaries = listOf(
        "Corner kick... let's see what they can create!",
        "The tall men come forward... this could be dangerous!",
        "Swinging in... cleared!",
        "Near post... flicked on... claimed by the keeper!",
        "They're packing the box! This is a real test of nerve!"
    )

    // ============ FOUL COMMENTARY ============

    private val foulCommentaries = listOf(
        "That's a foul! The referee blows his whistle.",
        "He's been caught late! The crowd want a card!",
        "Cyclical challenge! The referee has a word...",
        "Professional foul! He stops the counter-attack.",
        "That's dangerous play! The referee reaches for his pocket...",
        "He's cleaned him out! That's a definite yellow!"
    )

    // ============ OFFSIDE COMMENTARY ============

    private val offsideCommentaries = listOf(
        "The flag is up! Offside! The crowd groan.",
        "He was in behind... but the linesman raises his flag!",
        "Offside! That's a let-off for the defense!",
        "He was a yard off! Good decision by the assistant!",
        "The flag stays down... no, it's up! Offside!"
    )

    // ============ CONTROVERSIAL MOMENTS ============

    private val controversialMoments = listOf(
        "The referee is surrounded by angry players! This is getting heated!",
        "The fans are throwing objects onto the pitch! The tension is unbearable!",
        "VAR check... and it's taking FOREVER! The crowd is whistling loudly!",
        "The linesman's flag is up! But was he offside? The defender claims no!",
        "PENALTY?! The whole stadium is in uproar! The referee stands by his decision!",
        "Red card! The player can't believe it! His teammates are protesting!",
        "The referee is consulting with the fourth official... this could go either way!",
        "The away bench is on their feet! They're livid with that decision!",
        "A melee breaks out in the penalty area! Players are pushing and shoving!",
        "The home fans are lighting flares in protest! The smoke is covering the pitch!",
        "The referee has called both captains over for a word... calm down, gentlemen!",
        "This is absolute DRAMA here in the stadium! You can't write this script!"
    )

    // ============ FAN REACTIONS - AFRICAN STYLE ============

    private val fanReactions = listOf(
        "The 'vuvuzelas' are BLARING! The atmosphere is electric!",
        "The 'ting' is ringing! Fans are dancing in the aisles!",
        "A wave of sound erupts from the 'Sektor' - the ultras are in full voice!",
        "The drummers are setting the rhythm! The whole stadium is bouncing!",
        "Flares are lit! The smoke creates a mystical atmosphere!",
        "The fans are doing the 'shaku shaku' dance in celebration!",
        "A deafening roar echoes around the stadium! The 12th man is making a difference!",
        "The away fans are silenced! The home faithful are jubilant!",
        "Children are waving flags, elders are cheering - football unites the generations!",
        "The stadium announcer calls the scorer's name... and the crowd ROARS back!",
        "OLE OLE OLE! The fans are in full voice!",
        "The Mexican wave sweeps around the stadium!"
    )

    // ============ INJURY DRAMA ============

    private val injuryDramas = listOf(
        "He's down and he's not getting up! The physio is rushing onto the pitch!",
        "This looks serious... they're calling for the stretcher!",
        "The player is in visible pain. The home fans are concerned.",
        "He's trying to walk it off... but he's limping heavily.",
        "The medical team is spraying and taping... he might continue.",
        "He's waving the physio away... he wants to continue! What a warrior!",
        "That's a nasty challenge! The referee shows a yellow card... could have been red!",
        "The player is stretchered off to a standing ovation from all corners.",
        "He's back on his feet! The crowd CHEERS! What a recovery!"
    )

    // ============ SUBSTITUTION DRAMA ============

    private val substitutionDramas = listOf(
        "The number is up on the board... and the crowd is BOOING the decision!",
        "He's taking his time leaving the pitch... milking the applause!",
        "The substitute is stripped and ready... he's about to make his mark!",
        "The manager is giving final instructions... this change could be tactical genius!",
        "The player coming off is not happy! He storms straight down the tunnel!",
        "The crowd gives a standing ovation to a departing legend!",
        "Fresh legs coming on! The tired defender is replaced.",
        "A like-for-like change... both teams are going for it!"
    )

    // ============ CARD COMMENTARY ============

    private val yellowCardComments = listOf(
        "He's in the book! That's a yellow card!",
        "The referee reaches for his pocket... yellow!",
        "He's been booked for that challenge!",
        "First yellow of the match! The referee is being firm!",
        "He'll have to be careful now... one more and he's off!",
        "Dissent! He talks himself into the book!"
    )

    private val redCardComments = listOf(
        "RED CARD! He's sent off! The crowd are in uproar!",
        "That's a straight red! No arguments there!",
        "SECOND YELLOW! He's walking! His team are down to ten!",
        "The referee has no choice! That's a red card offense!",
        "He's gone! The home fans can't believe it!",
        "A reckless challenge! The referee sends him off!"
    )

    // ============ VAR COMMENTARY ============ 

    private val varComments = listOf(
        "VAR CHECK! The referee is going to the monitor...",
        "This is taking forever! The fans are getting restless!",
        "The referee is looking at the screen... what will he decide?",
        "VAR says... NO GOAL! The crowd erupts in celebration/anger!",
        "After a lengthy review... PENALTY!",
        "The decision stands! The referee points to the center circle!",
        "OVERTURNED! The original decision is reversed!"
    )

    // ============ TACTICAL COMMENTARY ============ 

    private val tacticalComments = listOf(
        "They're sitting deep now, protecting their lead like lions guarding their territory.",
        "The manager is gesturing wildly from the touchline! He wants more urgency!",
        "They're hitting on the counter-attack - lightning quick!",
        "Possession football now... they're running down the clock.",
        "The formation has shifted... they're now playing with three at the back!",
        "High press! They're not giving the defenders a moment's peace!",
        "The goalkeeper is taking his time... gamesmanship at its finest.",
        "They're exploiting the space behind the fullback... intelligent play.",
        "The manager has seen something... he's passing instructions to his captain!",
        "They've switched to a more defensive shape... protecting what they have."
    )

    // ============ MANAGER REACTIONS ============ 

    private val managerReactions = listOf(
        "The manager is PUMPING his fists on the touchline!",
        "He's animated! Constantly shouting instructions!",
        "The coach is deep in thought... analyzing every move.",
        "He's not happy with that decision! Arguing with the fourth official!",
        "Celebration! He knows how important this moment is!",
        "He's waving his players forward! Go for the kill!",
        "He's gesturing to calm down... keep possession.",
        "The assistant manager is on his feet too! The whole bench is involved!"
    )

    // ============ AFRICAN DERBY SPECIALS ============ 

    private val derbySpecials = mapOf(
        "Simba SC vs Young Africans" to listOf(
            "It's the DAR DERBY! The Kariakoo market has been buzzing all week!",
            "The rivalry between Msimbazi and Jangwani streets boils over!",
            "This is more than a game - it's a matter of pride for Dar es Salaam!",
            "The 'Wekundu wa Msimbazi' face the 'Jangwani giants' - HISTORY in the making!"
        ),
        "Al Ahly vs Zamalek SC" to listOf(
            "The Cairo Derby! The biggest club match in Africa!",
            "Red vs White - the entire nation of Egypt stops for this one!",
            "A century of rivalry boils over in the Cairo International Stadium!",
            "The 'Red Devils' against the 'White Knights' - this is WAR!"
        ),
        "Kaizer Chiefs vs Orlando Pirates" to listOf(
            "The Soweto Derby! South Africa comes to a standstill!",
            "Amakhosi vs Buccaneers - the biggest rivalry in Southern Africa!",
            "FNB Stadium is PACKED! 90,000 voices waiting to explode!",
            "This is the people's derby! The heartbeat of South African football!"
        ),
        "Hearts of Oak vs Asante Kotoko" to listOf(
            "The Ghanaian Super Clash! Two giants collide!",
            "Phobia vs Porcupine Warriors - tradition and history collide!",
            "Accra Sports Stadium is ELECTRIC! This is West African football at its finest!"
        )
    )

    // ============ MAIN COMMENTARY GENERATION FROM EVENTS ============

    /**
     * Generate commentary from a match event
     */
    fun generateCommentaryFromEvent(
        event: MatchEventsEntity,
        homeTeam: String,
        awayTeam: String,
        homeManager: ManagersEntity?,
        awayManager: ManagersEntity?,
        referee: RefereesEntity?
    ): String {
        return when (event.eventType) {
            "GOAL" -> generateGoalText(event, homeTeam, awayTeam)
            "PENALTY_SCORED" -> generatePenaltyScoredText(event, homeTeam, awayTeam)
            "PENALTY_MISSED" -> generatePenaltyMissedText(event)
            "FREEKICK_SCORED" -> generateFreeKickScoredText(event)
            "FREEKICK_MISSED" -> generateFreeKickMissedText(event)
            "OWN_GOAL" -> generateOwnGoalText(event)
            "ASSIST" -> generateAssistText(event)
            "YELLOW_CARD" -> generateYellowCardText(event, referee)
            "RED_CARD" -> generateRedCardText(event, referee)
            "SUBSTITUTION" -> generateSubstitutionText(event)
            "INJURY" -> generateInjuryText(event)
            "VAR" -> generateVarText(event, referee)
            "SHOT" -> generateShotText(event)
            "SHOT_ON_TARGET" -> generateShotOnTargetText(event)
            "SHOT_OFF_TARGET" -> generateShotOffTargetText(event)
            "SAVE" -> generateSaveText(event)
            "CORNER" -> generateCornerText(event)
            "FOUL" -> generateFoulText(event, referee)
            "OFFSIDE" -> generateOffsideText(event)
            else -> generateGenericEventText(event)
        }
    }

    private fun generateGoalText(event: MatchEventsEntity, homeTeam: String, awayTeam: String): String {
        val scorer = event.playerName.split(" ").last()
        val assist = event.assistPlayerName?.let { it.split(" ").last() }
        val assistPhrase = if (assist != null) " assisted by $assist," else ""
        val scoreline = "${event.homeScore ?: 0}-${event.awayScore ?: 0}"
        val celebration = goalCelebrations.random()

        val isLastMinute = event.minute >= 90 || (event.stoppageTime ?: 0) > 0
        val isHomeTeam = event.teamName == homeTeam
        val shotTypePhrase = event.shotType?.let { " with a ${it.replace('_', ' ').lowercase()}!" } ?: "!"

        return when {
            isLastMinute && isHomeTeam -> "${event.displayMinute}' GOAL! $scorer$assistPhrase scores in STOPPAGE TIME! $scoreline $celebration"
            isLastMinute -> "${event.displayMinute}' GOAL! $scorer$assistPhrase scores deep into injury time! $scoreline Heartbreak!"
            else -> "${event.displayMinute}' GOAL! $scorer$assistPhrase puts ${event.teamName} ahead! $scoreline$shotTypePhrase $celebration"
        }
    }

    private fun generatePenaltyScoredText(event: MatchEventsEntity, homeTeam: String, awayTeam: String): String {
        val scorer = event.playerName.split(" ").last()
        val celebration = penaltyCelebrations.random()
        val isHomeTeam = event.teamName == homeTeam
        val fanReaction = if (isHomeTeam) "The home fans ERUPT!" else "The away fans go WILD!"

        return "${event.displayMinute}' PENALTY SCORED! $scorer sends the keeper the wrong way! ${event.teamName} take the lead! $fanReaction $celebration"
    }

    private fun generatePenaltyMissedText(event: MatchEventsEntity): String {
        val taker = event.playerName.split(" ").last()
        val missType = when {
            event.penaltySaved -> "SAVED! The keeper guesses right and keeps it out!"
            event.penaltyPost -> "HITS THE POST! Unlucky!"
            else -> "WIDE! He's put it wide of the goal!"
        }

        return "${event.displayMinute}' PENALTY MISSED! $taker $missType The crowd can't believe it!"
    }

    private fun generateFreeKickScoredText(event: MatchEventsEntity): String {
        val taker = event.playerName.split(" ").last()
        val distance = event.shotDistance?.let { " from ${it} meters" } ?: ""
        val celebration = freeKickCelebrations.random()

        return "${event.displayMinute}' FREE KICK GOAL! $taker curls it beautifully into the top corner$distance! $celebration"
    }

    private fun generateFreeKickMissedText(event: MatchEventsEntity): String {
        val taker = event.playerName.split(" ").last()
        val distance = event.shotDistance?.let { " from ${it} meters" } ?: ""

        return "${event.displayMinute}' FREE KICK... over the wall... and just wide! $taker$distance, but not enough curl."
    }

    private fun generateOwnGoalText(event: MatchEventsEntity): String {
        val scorer = event.playerName.split(" ").last()
        val celebration = ownGoalCelebrations.random()

        return "${event.displayMinute}' OWN GOAL! $scorer turns it into his own net! $celebration"
    }

    private fun generateAssistText(event: MatchEventsEntity): String {
        val assister = event.playerName.split(" ").last()
        return "${event.displayMinute}' ASSIST! Brilliant vision from $assister, picking out the run!"
    }

    private fun generateYellowCardText(event: MatchEventsEntity, referee: RefereesEntity?): String {
        val player = event.playerName.split(" ").last()
        val refName = referee?.name?.split(" ")?.last() ?: "the referee"
        val comment = yellowCardComments.random()

        return "${event.displayMinute}' YELLOW CARD! $player (${event.teamName}) is booked by $refName. $comment"
    }

    private fun generateRedCardText(event: MatchEventsEntity, referee: RefereesEntity?): String {
        val player = event.playerName.split(" ").last()
        val refName = referee?.name?.split(" ")?.last() ?: "the referee"
        val isSecondYellow = event.description?.contains("second yellow", ignoreCase = true) ?: false
        val comment = if (isSecondYellow) "SECOND YELLOW! He's walking!" else redCardComments.random()

        return "${event.displayMinute}' RED CARD! $player (${event.teamName}) is SENT OFF by $refName! $comment"
    }

    private fun generateSubstitutionText(event: MatchEventsEntity): String {
        val playerOff = event.playerName.split(" ").last()
        val playerOn = event.substitutionInPlayer?.split(" ")?.last() ?: "a substitute"
        val comment = substitutionDramas.random()

        return "${event.displayMinute}' SUBSTITUTION: $playerOn replaces $playerOff for ${event.teamName}. $comment"
    }

    private fun generateInjuryText(event: MatchEventsEntity): String {
        val player = event.playerName.split(" ").last()
        val severity = when (event.injuryType) {
            "MINOR" -> "minor injury"
            "MODERATE" -> "moderate injury"
            "SEVERE" -> "SEVERE injury"
            else -> "injury"
        }
        val minutes = event.injuryMinutes?.let { " Estimated recovery: ${it} minutes." } ?: ""
        val comment = injuryDramas.random()

        return "${event.displayMinute}' INJURY! $player (${event.teamName}) is down with a $severity. $comment$minutes"
    }

    private fun generateVarText(event: MatchEventsEntity, referee: RefereesEntity?): String {
        val refName = referee?.name?.split(" ")?.last() ?: "the referee"
        val outcome = if (event.varOverturned) "OVERTURNED!" else "UPHELD!"
        val comment = varComments.random()

        return "${event.displayMinute}' VAR CHECK! $refName is at the monitor... $outcome $comment"
    }

    private fun generateShotText(event: MatchEventsEntity): String {
        val player = event.playerName.split(" ").last()
        val distance = event.shotDistance?.let { " from ${it}m" } ?: ""
        val xg = event.expectedGoals?.let { " (xG: ${String.format("%.2f", it)})" } ?: ""
        val comment = shotCommentaries.random()

        return "${event.displayMinute}' SHOT! $player pulls the trigger$distance$xg. $comment"
    }

    private fun generateShotOnTargetText(event: MatchEventsEntity): String {
        val player = event.playerName.split(" ").last()
        val distance = event.shotDistance?.let { " from ${it}m" } ?: ""
        val xg = event.expectedGoals?.let { " (xG: ${String.format("%.2f", it)})" } ?: ""
        val comment = shotOnTargetCommentaries.random()

        return "${event.displayMinute}' SHOT ON TARGET! $player tests the keeper$distance$xg. $comment"
    }

    private fun generateShotOffTargetText(event: MatchEventsEntity): String {
        val player = event.playerName.split(" ").last()
        val distance = event.shotDistance?.let { " from ${it}m" } ?: ""
        val comment = shotOffTargetCommentaries.random()

        return "${event.displayMinute}' SHOT OFF TARGET! $player$distance. $comment"
    }

    private fun generateSaveText(event: MatchEventsEntity): String {
        val keeper = event.playerName.split(" ").last()
        val comment = saveCommentaries.random()

        return "${event.displayMinute}' SAVE! $keeper denies the attacker! $comment"
    }

    private fun generateCornerText(event: MatchEventsEntity): String {
        val comment = cornerCommentaries.random()
        val team = event.teamName

        return "${event.displayMinute}' CORNER! $team with a chance to create something. $comment"
    }

    private fun generateFoulText(event: MatchEventsEntity, referee: RefereesEntity?): String {
        val player = event.playerName.split(" ").last()
        val refName = referee?.name?.split(" ")?.last() ?: "the referee"
        val comment = foulCommentaries.random()

        return "${event.displayMinute}' FOUL! $player (${event.teamName}) penalized by $refName. $comment"
    }

    private fun generateOffsideText(event: MatchEventsEntity): String {
        val player = event.playerName.split(" ").last()
        val comment = offsideCommentaries.random()

        return "${event.displayMinute}' OFFSIDE! $player caught offside. $comment"
    }

    private fun generateGenericEventText(event: MatchEventsEntity): String {
        return "${event.displayMinute}' ${event.eventType.replace('_', ' ').lowercase()} - ${event.playerName} (${event.teamName})"
    }

    // ============ NON-EVENT COMMENTARY ============

    fun generateFanReactionCommentary(
        minute: Int,
        stoppageTime: Int?,
        period: String,
        homeTeam: String,
        crowdNoise: Int,
        reaction: String
    ): String {
        val noiseLevel = when (crowdNoise) {
            in 8..10 -> "VOLCANIC!"
            in 5..7 -> "DEAFENING!"
            in 3..4 -> "LIVELY!"
            else -> "MURMURING"
        }
        val fanComment = fanReactions.random()

        return "${displayMinute(minute, stoppageTime)}' FANS: $fanComment The crowd is $noiseLevel"
    }

    fun generateDerbyCommentary(
        minute: Int,
        stoppageTime: Int?,
        period: String,
        derbyName: String,
        homeTeam: String,
        awayTeam: String
    ): String {
        val derbyPhrases = derbySpecials[derbyName] ?: listOf(
            "The intensity is UNBELIEVABLE! This is what derby football is all about!",
            "Tackles flying in! This is passionate, committed football!",
            "The atmosphere is ELECTRIC! Both sets of fans in full voice!"
        )

        return "${displayMinute(minute, stoppageTime)}' DERBY DRAMA! ${derbyPhrases.random()}"
    }

    fun generateManagerReaction(
        minute: Int,
        stoppageTime: Int?,
        period: String,
        manager: ManagersEntity,
        team: String,
        isCelebrating: Boolean
    ): String {
        val managerName = manager.name.split(" ").last()
        val reaction = if (isCelebrating) {
            managerReactions.filter { it.contains("PUMPING") || it.contains("Celebration") }.random()
        } else {
            managerReactions.filter { !it.contains("PUMPING") && !it.contains("Celebration") }.random()
        }

        return "${displayMinute(minute, stoppageTime)}' MANAGER: $team's $managerName $reaction"
    }

    fun generateControversialMoment(
        minute: Int,
        stoppageTime: Int?,
        period: String,
        referee: RefereesEntity?,
        homeTeam: String,
        awayTeam: String,
        isVar: Boolean = false
    ): String {
        val refName = referee?.name?.split(" ")?.last() ?: "the referee"
        val controversy = controversialMoments.random()

        return if (isVar) {
            "${displayMinute(minute, stoppageTime)}' VAR CHECK! $controversy Referee $refName is reviewing the monitor..."
        } else {
            "${displayMinute(minute, stoppageTime)}' CONTROVERSY! $controversy Referee $refName stands by his decision."
        }
    }

    fun generateTacticalCommentary(
        minute: Int,
        stoppageTime: Int?,
        period: String,
        team: String,
        situation: String
    ): String {
        val tactical = tacticalComments.random()
        return "${displayMinute(minute, stoppageTime)}' TACTICAL: $team $tactical"
    }

    fun generateHalfTimeCommentary(
        homeTeam: String,
        awayTeam: String,
        homeScore: Int,
        awayScore: Int,
        homePossession: Int,
        homeShots: Int,
        awayShots: Int,
        homeCorners: Int,
        awayCorners: Int
    ): String {
        val verdict = when {
            homeScore > awayScore -> "$homeTeam lead at the break!"
            awayScore > homeScore -> "$awayTeam ahead going into the tunnel!"
            else -> "It's all square at halftime!"
        }

        return "⏱️ HALF TIME: $homeTeam $homeScore - $awayScore $awayScore. $verdict Possession: $homePossession%-${100 - homePossession}%, Shots: $homeShots-$awayShots, Corners: $homeCorners-$awayCorners."
    }

    fun generateFullTimeCommentary(
        homeTeam: String,
        awayTeam: String,
        homeScore: Int,
        awayScore: Int,
        isUpset: Boolean,
        winner: String?,
        attendance: Int
    ): String {
        val result = when {
            homeScore > awayScore -> "$homeTeam WIN!"
            awayScore > homeScore -> "$awayTeam WIN!"
            else -> "It's a DRAW!"
        }

        val upsetPhrase = if (isUpset) {
            "⚡ MAJOR UPSET! The underdogs have triumphed! "
        } else ""

        val attendanceFormatted = String.format("%,d", attendance)

        return "🏁 FULL TIME: $homeTeam $homeScore - $awayScore $awayScore. $upsetPhrase$result Attendance: $attendanceFormatted at the stadium."
    }

    fun generateCrowdWave(
        minute: Int,
        stoppageTime: Int?,
        period: String,
        stadium: String,
        noiseLevel: Int
    ): String {
        val wave = when (noiseLevel) {
            in 8..10 -> "The famous 'Mexican wave' sweeps around the stadium!"
            in 5..7 -> "The crowd starts a rhythmic clapping... ba-ba-ba-BAAAA!"
            in 3..4 -> "A chant builds from the stands: 'OLE, OLE, OLE!'"
            else -> "Silence falls as the players prepare for a set piece."
        }

        return "${displayMinute(minute, stoppageTime)}' ATMOSPHERE at $stadium: $wave"
    }

    fun generateAfricanProverb(minute: Int, stoppageTime: Int?, period: String): String {
        val proverbs = listOf(
            "As we say in Africa, 'A roaring lion kills no game' - stay calm and finish strong!",
            "There's an African proverb: 'Smooth seas do not make skillful sailors' - this team is being tested!",
            "They say 'The best time to plant a tree was 20 years ago, the second best time is now' - and now is the time to score!",
            "An African elder once said: 'If you want to go fast, go alone. If you want to go far, go together' - and this team is going TOGETHER!",
            "As the elders say, 'However long the night, the dawn will break' - and for these fans, the dawn is coming!"
        )

        return "${displayMinute(minute, stoppageTime)}' WISDOM: ${proverbs.random()}"
    }

    // ============ UTILITY ============

    private val MatchEventsEntity.displayMinute: String
        get() = if (stoppageTime != null && stoppageTime > 0) {
            "$minute+$stoppageTime"
        } else {
            minute.toString()
        }

    private fun displayMinute(minute: Int, stoppageTime: Int?): String {
        return if (stoppageTime != null && stoppageTime > 0) {
            "$minute+$stoppageTime"
        } else {
            minute.toString()
        }
    }
}
