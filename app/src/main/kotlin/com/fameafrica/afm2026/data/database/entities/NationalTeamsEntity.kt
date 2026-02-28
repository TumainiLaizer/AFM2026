package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "national_teams",
    foreignKeys = [
        ForeignKey(
            entity = NationalitiesEntity::class,
            parentColumns = ["fifa_code"],
            childColumns = ["fifa_code"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ManagersEntity::class,
            parentColumns = ["id"],
            childColumns = ["manager_id"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = NationalitiesEntity::class,
            parentColumns = ["fifa_code"],
            childColumns = ["rival_fifa_code"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["fifa_code"], unique = true),
        Index(value = ["confederation"]),
        Index(value = ["manager_id"]),
        Index(value = ["elo_rating"]),
        Index(value = ["reputation"]),
        Index(value = ["rival_fifa_code"]),
        Index(value = ["world_ranking"]),
        Index(value = ["continental_ranking"])
    ]
)
data class NationalTeamsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "confederation")
    val confederation: String,

    @ColumnInfo(name = "fifa_code")
    val fifaCode: String,

    @ColumnInfo(name = "nationality_id")
    val nationalityId: Int,  // References nationalities.id

    @ColumnInfo(name = "elo_rating")
    val eloRating: Int = 1500,

    @ColumnInfo(name = "fifa_ranking")
    val fifaRanking: Int? = null,

    @ColumnInfo(name = "world_ranking")
    val worldRanking: Int? = null,

    @ColumnInfo(name = "continental_ranking")
    val continentalRanking: Int? = null,

    @ColumnInfo(name = "reputation", defaultValue = "50")
    val reputation: Int = 50,

    @ColumnInfo(name = "continental_titles", defaultValue = "0")
    val continentalTitles: Int = 0,

    @ColumnInfo(name = "world_cup_appearances", defaultValue = "0")
    val worldCupAppearances: Int = 0,

    @ColumnInfo(name = "best_finish")
    val bestFinish: String? = null,

    @ColumnInfo(name = "home_stadium")
    val homeStadium: String? = null,

    @ColumnInfo(name = "stadium_capacity")
    val stadiumCapacity: Int? = null,

    @ColumnInfo(name = "fan_loyalty", defaultValue = "50")
    val fanLoyalty: Int = 50,

    @ColumnInfo(name = "rival_fifa_code")
    val rivalFifaCode: String? = null,  // References nationalities.fifa_code

    @ColumnInfo(name = "rival_team")
    val rivalTeam: String? = null,  // Keep for backward compatibility

    @ColumnInfo(name = "avg_attacking_ability")
    val avgAttackingAbility: Double? = null,

    @ColumnInfo(name = "avg_defence_ability")
    val avgDefenceAbility: Double? = null,

    @ColumnInfo(name = "avg_playmaking_ability")
    val avgPlaymakingAbility: Double? = null,

    @ColumnInfo(name = "crowdSupport", defaultValue = "30")
    val crowdSupport: Int = 30,

    @ColumnInfo(name = "sponsorships")
    val sponsorships: String? = null,

    @ColumnInfo(name = "continental_competition")
    val continentalCompetition: String? = null,

    @ColumnInfo(name = "recent_form")
    val recentForm: String? = null,

    @ColumnInfo(name = "manager_id")
    val managerId: Int? = null,

    @ColumnInfo(name = "squad_size")
    val squadSize: Int = 0,

    @ColumnInfo(name = "average_age")
    val averageAge: Double? = null,

    @ColumnInfo(name = "captain_id")
    val captainId: Int? = null,

    @ColumnInfo(name = "top_scorer_id")
    val topScorerId: Int? = null,

    @ColumnInfo(name = "most_capped_id")
    val mostCappedId: Int? = null,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val rankingDisplay: String
        get() = when {
            fifaRanking != null -> "FIFA: $fifaRanking"
            worldRanking != null -> "World: $worldRanking"
            else -> "Unranked"
        }

    val continentalRankingDisplay: String
        get() = continentalRanking?.let { "Continental: $it" } ?: "Unranked"

    val strengthLevel: String
        get() = when {
            eloRating >= 1800 -> "World Class"
            eloRating >= 1700 -> "Elite"
            eloRating >= 1600 -> "Strong"
            eloRating >= 1500 -> "Good"
            eloRating >= 1400 -> "Average"
            else -> "Developing"
        }

    val isCAF: Boolean
        get() = confederation == "CAF"

    val isUEFA: Boolean
        get() = confederation == "UEFA"

    val isCONMEBOL: Boolean
        get() = confederation == "CONMEBOL"

    val isCONCACAF: Boolean
        get() = confederation == "CONCACAF"

    val isAFC: Boolean
        get() = confederation == "AFC"

    val isOFC: Boolean
        get() = confederation == "OFC"

    val hasManager: Boolean
        get() = managerId != null
}

// ============ ENUMS ============
