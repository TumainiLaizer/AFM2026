package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "teams",
    foreignKeys = [
        ForeignKey(
            entity = LeaguesEntity::class,
            parentColumns = ["name"],
            childColumns = ["league"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CupsEntity::class,
            parentColumns = ["name"],
            childColumns = ["cup_name"],
            onDelete = ForeignKey.SET_NULL,
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
            entity = TeamsEntity::class,
            parentColumns = ["name"],
            childColumns = ["rival_team"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SponsorsEntity::class,
            parentColumns = ["name"],
            childColumns = ["sponsorships"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["league"]),
        Index(value = ["manager_id"]),
        Index(value = ["elo_rating"]),
        Index(value = ["reputation"]),
        Index(value = ["cup_name"]),
        Index(value = ["rival_team"])
    ]
)
data class TeamsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "league")
    val league: String,

    @ColumnInfo(name = "elo_rating", defaultValue = "1500")
    val eloRating: Int = 1500,

    @ColumnInfo(name = "reputation", defaultValue = "50")
    val reputation: Int = 50,

    @ColumnInfo(name = "points", defaultValue = "0")
    val points: Int = 0,

    @ColumnInfo(name = "revenue", defaultValue = "10000000")
    val revenue: Double = 10000000.0,

    @ColumnInfo(name = "morale", defaultValue = "50")
    val morale: Int = 50,

    @ColumnInfo(name = "logo_path")
    val logoPath: String?,

    @ColumnInfo(name = "home_stadium", defaultValue = "FAME Stadium")
    val homeStadium: String = "FAME Stadium",

    @ColumnInfo(name = "stadium_capacity", defaultValue = "3000")
    val stadiumCapacity: Int = 3000,

    @ColumnInfo(name = "fan_loyalty", defaultValue = "50")
    val fanLoyalty: Int = 50,

    @ColumnInfo(name = "rival_team")
    val rivalTeam: String?,

    @ColumnInfo(name = "formation")
    val formation: String?,

    @ColumnInfo(name = "cup_qualification")
    val cupQualification: String?,

    @ColumnInfo(name = "cup_winner", defaultValue = "0")
    val cupWinner: Int = 0,

    @ColumnInfo(name = "cup_stage")
    val cupStage: String?,

    @ColumnInfo(name = "cup_name")
    val cupName: String?,

    @ColumnInfo(name = "crowdSupport", defaultValue = "30")
    val crowdSupport: Int = 30,

    @ColumnInfo(name = "sponsorships", defaultValue = "Visit Tanzania")
    val sponsorships: String = "Visit Tanzania",

    @ColumnInfo(name = "cup_status")
    val cupStatus: String?,

    @ColumnInfo(name = "manager_id")
    val managerId: Int?,

    @ColumnInfo(name = "avg_attacking_ability")
    val avgAttackingAbility: Double?,

    @ColumnInfo(name = "avg_defence_ability")
    val avgDefenceAbility: Double?,

    @ColumnInfo(name = "avg_playmaking_ability")
    val avgPlaymakingAbility: Double?
) {

    // ============ COMPUTED PROPERTIES ============

    val overallRating: Double
        get() {
            val attack = avgAttackingAbility ?: 50.0
            val defence = avgDefenceAbility ?: 50.0
            val playmaking = avgPlaymakingAbility ?: 50.0
            return (attack + defence + playmaking) / 3.0
        }

    val tier: String
        get() = when {
            eloRating >= 1700 -> "Elite"
            eloRating >= 1600 -> "Championship"
            eloRating >= 1500 -> "Professional"
            eloRating >= 1400 -> "Semi-Professional"
            else -> "Amateur"
        }

    val fanSupport: String
        get() = when {
            fanLoyalty >= 80 -> "Passionate"
            fanLoyalty >= 60 -> "Loyal"
            fanLoyalty >= 40 -> "Supportive"
            fanLoyalty >= 20 -> "Fair-weather"
            else -> "Indifferent"
        }

    val financialHealth: String
        get() = when {
            revenue >= 50000000 -> "Rich"
            revenue >= 20000000 -> "Healthy"
            revenue >= 10000000 -> "Stable"
            revenue >= 5000000 -> "Breaking Even"
            else -> "In Debt"
        }

    val formDescription: String
        get() = when {
            morale >= 80 -> "Excellent"
            morale >= 60 -> "Good"
            morale >= 50 -> "Average"
            morale >= 30 -> "Poor"
            else -> "Very Poor"
        }

    // ============ BUSINESS METHODS ============

    fun updateAfterMatch(result: FixturesResultsEntity): TeamsEntity {
        val pointsEarned = when {
            result.homeTeam == name && result.homeTeamWin -> 3
            result.awayTeam == name && result.awayTeamWin -> 3
            result.isDraw -> 1
            else -> 0
        }

        val moraleChange = when {
            pointsEarned == 3 -> +5
            pointsEarned == 1 -> +1
            else -> -3
        }

        return this.copy(
            points = this.points + pointsEarned,
            morale = (this.morale + moraleChange).coerceIn(0, 100)
        )
    }

    fun updateElo(newElo: Int): TeamsEntity {
        return this.copy(eloRating = newElo)
    }

    fun updateRevenue(amount: Double): TeamsEntity {
        return this.copy(revenue = this.revenue + amount)
    }

    fun updateMorale(change: Int): TeamsEntity {
        return this.copy(morale = (this.morale + change).coerceIn(0, 100))
    }

    fun updateFanLoyalty(change: Int): TeamsEntity {
        return this.copy(fanLoyalty = (this.fanLoyalty + change).coerceIn(0, 100))
    }

    fun assignManager(managerId: Int?): TeamsEntity {
        return this.copy(managerId = managerId)
    }

    fun updateCupProgress(cupName: String, stage: String, status: String): TeamsEntity {
        return this.copy(
            cupName = cupName,
            cupStage = stage,
            cupStatus = status
        )
    }

    fun winCup(cupName: String): TeamsEntity {
        return this.copy(
            cupWinner = this.cupWinner + 1,
            cupStage = "Winner",
            cupStatus = "Completed"
        )
    }

    fun calculateAverageAbilities(players: List<PlayersEntity>): TeamsEntity {
        if (players.isEmpty()) return this

        val attackPlayers = players.filter { it.positionCategory == "FORWARD" }
        val defencePlayers = players.filter { it.positionCategory == "DEFENDER" }
        val midfieldPlayers = players.filter { it.positionCategory == "MIDFIELDER" }

        val avgAttack = if (attackPlayers.isNotEmpty())
            attackPlayers.map { it.rating }.average() else 50.0

        val avgDefence = if (defencePlayers.isNotEmpty())
            defencePlayers.map { it.rating }.average() else 50.0

        val avgPlaymaking = if (midfieldPlayers.isNotEmpty())
            midfieldPlayers.map { it.rating }.average() else 50.0

        return this.copy(
            avgAttackingAbility = avgAttack,
            avgDefenceAbility = avgDefence,
            avgPlaymakingAbility = avgPlaymaking
        )
    }
}