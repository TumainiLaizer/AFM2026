package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import kotlin.math.abs

@Entity(
    tableName = "fixtures_results",
    foreignKeys = [
        ForeignKey(
            entity = FixturesEntity::class,
            parentColumns = ["id"],
            childColumns = ["fixture_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["home_team"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["away_team"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RefereesEntity::class,
            parentColumns = ["referee_id"],
            childColumns = ["referee_id"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LeaguesEntity::class,
            parentColumns = ["name"],
            childColumns = ["league_name"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CupsEntity::class,
            parentColumns = ["name"],
            childColumns = ["cup_name"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["fixture_id"], unique = true),
        Index(value = ["match_date"]),
        Index(value = ["home_team", "away_team", "match_date"], unique = true),
        Index(value = ["referee_id"]),
        Index(value = ["league_name"]),
        Index(value = ["cup_name"]),
        Index(value = ["season"]),
        Index(value = ["match_type"]),
        Index(value = ["attendance"]),
        Index(value = ["is_upset"])
    ]
)
data class FixturesResultsEntity(
    @PrimaryKey(autoGenerate = false)  // Use same ID as fixture
    @ColumnInfo(name = "fixture_id")
    val fixtureId: Int,

    // ============ MATCH IDENTIFICATION ============

    @ColumnInfo(name = "match_date")
    val matchDate: String,

    @ColumnInfo(name = "home_team")
    val homeTeam: String,

    @ColumnInfo(name = "away_team")
    val awayTeam: String,

    // ============ SCORES ============

    @ColumnInfo(name = "home_score", defaultValue = "0")
    val homeScore: Int = 0,

    @ColumnInfo(name = "away_score", defaultValue = "0")
    val awayScore: Int = 0,

    @ColumnInfo(name = "home_halftime_score", defaultValue = "0")
    val homeHalftimeScore: Int = 0,

    @ColumnInfo(name = "away_halftime_score", defaultValue = "0")
    val awayHalftimeScore: Int = 0,

    @ColumnInfo(name = "home_penalty_score")
    val homePenaltyScore: Int? = null,  // For cup matches decided by penalties

    @ColumnInfo(name = "away_penalty_score")
    val awayPenaltyScore: Int? = null,  // For cup matches decided by penalties

    // ============ MATCH STATISTICS ============

    @ColumnInfo(name = "possession_home", defaultValue = "50")
    val possessionHome: Int = 50,  // Percentage

    @ColumnInfo(name = "possession_away", defaultValue = "50")
    val possessionAway: Int = 50,  // Percentage

    @ColumnInfo(name = "shots_home", defaultValue = "0")
    val shotsHome: Int = 0,

    @ColumnInfo(name = "shots_away", defaultValue = "0")
    val shotsAway: Int = 0,

    @ColumnInfo(name = "shots_on_target_home", defaultValue = "0")
    val shotsOnTargetHome: Int = 0,

    @ColumnInfo(name = "shots_on_target_away", defaultValue = "0")
    val shotsOnTargetAway: Int = 0,

    @ColumnInfo(name = "corners_home", defaultValue = "0")
    val cornersHome: Int = 0,

    @ColumnInfo(name = "corners_away", defaultValue = "0")
    val cornersAway: Int = 0,

    @ColumnInfo(name = "fouls_home", defaultValue = "0")
    val foulsHome: Int = 0,

    @ColumnInfo(name = "fouls_away", defaultValue = "0")
    val foulsAway: Int = 0,

    @ColumnInfo(name = "yellow_cards_home", defaultValue = "0")
    val yellowCardsHome: Int = 0,

    @ColumnInfo(name = "yellow_cards_away", defaultValue = "0")
    val yellowCardsAway: Int = 0,

    @ColumnInfo(name = "red_cards_home", defaultValue = "0")
    val redCardsHome: Int = 0,

    @ColumnInfo(name = "red_cards_away", defaultValue = "0")
    val redCardsAway: Int = 0,

    @ColumnInfo(name = "offsides_home", defaultValue = "0")
    val offsidesHome: Int = 0,

    @ColumnInfo(name = "offsides_away", defaultValue = "0")
    val offsidesAway: Int = 0,

    // ============ REFEREE ============

    @ColumnInfo(name = "referee_id")
    val refereeId: Int? = null,

    // ============ MATCH DETAILS ============

    @ColumnInfo(name = "attendance", defaultValue = "0")
    val attendance: Int = 0,

    @ColumnInfo(name = "weather_conditions", defaultValue = "Clear")
    val weatherConditions: String = "Clear",

    @ColumnInfo(name = "stadium")
    val stadium: String,

    @ColumnInfo(name = "match_type")
    val matchType: String,  // League, Cup, Friendly, International

    @ColumnInfo(name = "league_name")
    val leagueName: String? = null,

    @ColumnInfo(name = "cup_name")
    val cupName: String? = null,

    @ColumnInfo(name = "cup_round")
    val cupRound: String? = null,

    @ColumnInfo(name = "season")
    val season: String,

    @ColumnInfo(name = "match_status", defaultValue = "COMPLETED")
    val matchStatus: String = "COMPLETED",

    @ColumnInfo(name = "man_of_match")
    val manOfMatch: String? = null,  // Player name

    @ColumnInfo(name = "man_of_match_team")
    val manOfMatchTeam: String? = null,

    @ColumnInfo(name = "man_of_match_rating")
    val manOfMatchRating: Double? = null,

    // ============ UPSET DETECTION ============

    @ColumnInfo(name = "home_team_elo", defaultValue = "1500")
    val homeTeamElo: Int = 1500,

    @ColumnInfo(name = "away_team_elo", defaultValue = "1500")
    val awayTeamElo: Int = 1500,

    @ColumnInfo(name = "elo_change_home", defaultValue = "0")
    val eloChangeHome: Int = 0,

    @ColumnInfo(name = "elo_change_away", defaultValue = "0")
    val eloChangeAway: Int = 0,

    @ColumnInfo(name = "is_upset", defaultValue = "0")
    val isUpset: Boolean = false,

    @ColumnInfo(name = "upset_factor")
    val upsetFactor: Double? = null,  // 1.0 = even, >1.0 = upset

    // ============ TIMESTAMPS ============

    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: String = "",

    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
    val updatedAt: String = ""
) {

    // ============ COMPUTED PROPERTIES ============

    val totalGoals: Int
        get() = homeScore + awayScore

    val totalShots: Int
        get() = shotsHome + shotsAway

    val totalShotsOnTarget: Int
        get() = shotsOnTargetHome + shotsOnTargetAway

    val shotAccuracyHome: Double
        get() = if (shotsHome > 0) (shotsOnTargetHome.toDouble() / shotsHome * 100) else 0.0

    val shotAccuracyAway: Double
        get() = if (shotsAway > 0) (shotsOnTargetAway.toDouble() / shotsAway * 100) else 0.0

    val totalYellowCards: Int
        get() = yellowCardsHome + yellowCardsAway

    val totalRedCards: Int
        get() = redCardsHome + redCardsAway

    val totalFouls: Int
        get() = foulsHome + foulsAway

    val winner: String?
        get() = when {
            homeScore > awayScore -> homeTeam
            awayScore > homeScore -> awayTeam
            homePenaltyScore != null && awayPenaltyScore != null -> {
                if (homePenaltyScore > awayPenaltyScore) homeTeam else awayTeam
            }
            else -> null  // Draw
        }

    val isDraw: Boolean
        get() = homeScore == awayScore && homePenaltyScore == null

    val isPenaltyShootout: Boolean
        get() = homePenaltyScore != null && awayPenaltyScore != null

    val result: String
        get() = when {
            isPenaltyShootout -> {
                val penaltyResult = if (homePenaltyScore!! > awayPenaltyScore!!)
                    "$homeTeam wins on penalties"
                else
                    "$awayTeam wins on penalties"
                "$homeScore-$awayScore ($penaltyResult)"
            }
            homeScore > awayScore -> "$homeTeam Win"
            awayScore > homeScore -> "$awayTeam Win"
            else -> "Draw"
        }

    val scoreline: String
        get() = "$homeScore - $awayScore"

    val halftimeScoreline: String
        get() = "$homeHalftimeScore - $awayHalftimeScore"

    val penaltyScoreline: String?
        get() = if (isPenaltyShootout)
            "${homePenaltyScore ?: 0} - ${awayPenaltyScore ?: 0}"
        else null

    val homeTeamWin: Boolean
        get() = homeScore > awayScore ||
                (isPenaltyShootout && homePenaltyScore!! > awayPenaltyScore!!)

    val awayTeamWin: Boolean
        get() = awayScore > homeScore ||
                (isPenaltyShootout && awayPenaltyScore!! > homePenaltyScore!!)

    val homeTeamPoints: Int
        get() = when {
            homeTeamWin -> 3
            isDraw -> 1
            else -> 0
        }

    val awayTeamPoints: Int
        get() = when {
            awayTeamWin -> 3
            isDraw -> 1
            else -> 0
        }

    val goalDifference: Int
        get() = homeScore - awayScore

    val isHighScoring: Boolean
        get() = totalGoals >= 4

    val isCleanSheet: Pair<Boolean, Boolean>
        get() = Pair(homeScore == 0, awayScore == 0)

    val homeCleanSheet: Boolean
        get() = awayScore == 0

    val awayCleanSheet: Boolean
        get() = homeScore == 0

    val isComeback: Boolean
        get() = (homeHalftimeScore < awayHalftimeScore && homeScore > awayScore) ||
                (awayHalftimeScore < homeHalftimeScore && awayScore > homeScore)

    val isThrashing: Boolean
        get() = abs(homeScore - awayScore) >= 3

    // ============ BUSINESS METHODS ============

    fun calculateUpsetFactor(): Double {
        if (homeTeamElo == awayTeamElo) return 1.0

        val expectedWinProbability = 1.0 / (1.0 + Math.pow(10.0, (awayTeamElo - homeTeamElo) / 400.0))

        return when {
            homeTeamWin -> (1.0 - expectedWinProbability) / expectedWinProbability
            awayTeamWin -> expectedWinProbability / (1.0 - expectedWinProbability)
            else -> 1.0
        }
    }

    fun calculateEloChanges(k: Int = 32): Pair<Int, Int> {
        val expectedHome = 1.0 / (1.0 + Math.pow(10.0, (awayTeamElo - homeTeamElo) / 400.0))
        val expectedAway = 1.0 / (1.0 + Math.pow(10.0, (homeTeamElo - awayTeamElo) / 400.0))

        val actualHome = when {
            homeTeamWin -> 1.0
            isDraw -> 0.5
            else -> 0.0
        }

        val actualAway = when {
            awayTeamWin -> 1.0
            isDraw -> 0.5
            else -> 0.0
        }

        val homeChange = (k * (actualHome - expectedHome)).toInt()
        val awayChange = (k * (actualAway - expectedAway)).toInt()

        return Pair(homeChange, awayChange)
    }

    fun withCalculatedElo(): FixturesResultsEntity {
        val (homeChange, awayChange) = calculateEloChanges()
        val upset = calculateUpsetFactor()

        return this.copy(
            eloChangeHome = homeChange,
            eloChangeAway = awayChange,
            isUpset = upset > 1.5,
            upsetFactor = upset
        )
    }

    fun getMatchReport(): String {
        return buildString {
            appendLine("🏆 $homeTeam vs $awayTeam")
            appendLine("📅 $matchDate")
            appendLine("🏟️ $stadium")
            appendLine("📊 Final Score: $scoreline")

            if (isPenaltyShootout) {
                appendLine("⚽ Penalties: ${penaltyScoreline}")
            }

            appendLine("\n⏱️ Halftime: $halftimeScoreline")
            appendLine("\n📈 Match Statistics:")
            appendLine("   Possession: $possessionHome% - $possessionAway%")
            appendLine("   Shots: $shotsHome - $shotsAway")
            appendLine("   Shots on Target: $shotsOnTargetHome - $shotsOnTargetAway")
            appendLine("   Corners: $cornersHome - $cornersAway")
            appendLine("   Fouls: $foulsHome - $foulsAway")
            appendLine("   Yellow Cards: $yellowCardsHome - $yellowCardsAway")
            appendLine("   Red Cards: $redCardsHome - $redCardsAway")

            if (isUpset) {
                appendLine("\n⚠️ UPSET! Rating: ${upsetFactor?.times(100)?.toInt()}%")
            }

            if (manOfMatch != null) {
                appendLine("\n⭐ Man of the Match: $manOfMatch ($manOfMatchTeam)")
                appendLine("   Rating: ${manOfMatchRating ?: "N/A"}")
            }
        }
    }
}