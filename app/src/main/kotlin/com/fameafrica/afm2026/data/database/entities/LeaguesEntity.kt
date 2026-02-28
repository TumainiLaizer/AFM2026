package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "leagues",
    foreignKeys = [
        ForeignKey(
            entity = NationalitiesEntity::class,
            parentColumns = ["id"],
            childColumns = ["country_id"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SponsorsEntity::class,
            parentColumns = ["name"],
            childColumns = ["sponsor"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["country_id"]),
        Index(value = ["sponsor"]),
        Index(value = ["level"]),
        Index(value = ["prize_money"]),
        Index(value = ["country_id", "level"])
    ]
)
data class LeaguesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "country_id")
    val countryId: Int?,  // Foreign key to nationalities

    @ColumnInfo(name = "country")
    val country: String?,  // Keep for backward compatibility

    @ColumnInfo(name = "level")
    val level: Int,  // 1 = Top Division, 2 = Second Division, etc.

    @ColumnInfo(name = "sponsor")
    val sponsor: String?,

    @ColumnInfo(name = "prize_money")
    val prizeMoney: Int,

    @ColumnInfo(name = "logo")
    val logo: String?
) {

    // ============ COMPUTED PROPERTIES ============

    val prizeMoneyInMillions: Double
        get() = prizeMoney / 1_000_000.0

    val tierName: String
        get() = when (level) {
            1 -> "Premier Division"
            2 -> "Championship"
            3 -> "League One"
            4 -> "League Two"
            5 -> "Regional League"
            else -> "Division $level"
        }

    val fullName: String
        get() = if (country != null) {
            "$country $name"
        } else {
            name
        }

    val displayName: String
        get() = name.replace("League", "").trim()

    val isTopDivision: Boolean
        get() = level == 1

    val isSecondDivision: Boolean
        get() = level == 2

    val isDomestic: Boolean
        get() = countryId != null

    val leagueQuality: String
        get() = when {
            prizeMoney >= 1_000_000 -> "Elite"
            prizeMoney >= 500_000 -> "High"
            prizeMoney >= 200_000 -> "Good"
            prizeMoney >= 100_000 -> "Average"
            prizeMoney >= 50_000 -> "Low"
            else -> "Amateur"
        }

    /**
     * Get maximum foreign players allowed in this league
     * Based on country-specific regulations
     */
    fun getMaxForeignPlayers(): Int {
        return ForeignPlayerRules.getMaxForeignPlayersByCountry(
            countryId = countryId ?: 0,
            leagueName = name
        )
    }

    /**
     * Get promotion spots available
     */
    val promotionSpots: Int
        get() = when (level) {
            1 -> 0  // Top division doesn't promote
            2 -> 3  // Second division promotes 3 teams
            3 -> 4  // Third division promotes 4 teams
            4 -> 4  // Fourth division promotes 4 teams
            else -> 2
        }

    /**
     * Get relegation spots
     */
    val relegationSpots: Int
        get() = when (level) {
            1 -> 3  // Top division relegates 3
            2 -> 4  // Second division relegates 4
            3 -> 4  // Third division relegates 4
            4 -> 4  // Fourth division relegates 4
            else -> 2
        }

    /**
     * Get playoff spots
     */
    val playoffSpots: Int
        get() = when (level) {
            1 -> 0
            2 -> 2  // Positions 3-4 go to playoffs
            3 -> 2
            4 -> 2
            else -> 0
        }
}

// ============ ENUMS ============

enum class LeagueLevel(val value: Int) {
    PREMIER(1),
    CHAMPIONSHIP(2),
    LEAGUE_ONE(3),
    LEAGUE_TWO(4),
    REGIONAL(5)
}

enum class LeagueTier(val value: String) {
    ELITE("Elite"),
    HIGH("High"),
    GOOD("Good"),
    AVERAGE("Average"),
    LOW("Low"),
    AMATEUR("Amateur")
}