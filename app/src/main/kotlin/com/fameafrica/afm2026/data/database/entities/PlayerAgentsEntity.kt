package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "player_agents",
    foreignKeys = [
        ForeignKey(
            entity = PlayersEntity::class,
            parentColumns = ["name"],
            childColumns = ["player_name"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["agent_name"]),
        Index(value = ["player_name"], unique = true),
        Index(value = ["agency"]),
        Index(value = ["commission_rate"]),
        Index(value = ["reputation"])
    ]
)
data class PlayerAgentsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "agent_name")
    val agentName: String,

    @ColumnInfo(name = "agency")
    val agency: String? = null,

    @ColumnInfo(name = "player_name")
    val playerName: String,

    @ColumnInfo(name = "negotiation_power", defaultValue = "50")
    val negotiationPower: Int = 50,

    @ColumnInfo(name = "commission_rate")
    val commissionRate: Int = 10,  // Percentage (5-20%)

    @ColumnInfo(name = "reputation")
    val reputation: Int = 50,  // 0-100

    @ColumnInfo(name = "nationality")
    val nationality: String? = null,

    @ColumnInfo(name = "languages")
    val languages: String? = null,  // Comma-separated

    @ColumnInfo(name = "specialization")
    val specialization: String? = null,  // "YOUNG_TALENT", "STARS", "LOCAL", "INTERNATIONAL"

    @ColumnInfo(name = "years_experience")
    val yearsExperience: Int = 0,

    @ColumnInfo(name = "active_clients")
    val activeClients: Int = 1,

    @ColumnInfo(name = "successful_deals")
    val successfulDeals: Int = 0,

    @ColumnInfo(name = "total_deal_value")
    val totalDealValue: Long = 0L,

    @ColumnInfo(name = "email")
    val email: String? = null,

    @ColumnInfo(name = "phone")
    val phone: String? = null,

    @ColumnInfo(name = "photo_url")
    val photoUrl: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val negotiationLevel: String
        get() = when {
            negotiationPower >= 90 -> "Legendary Negotiator"
            negotiationPower >= 80 -> "Elite Agent"
            negotiationPower >= 70 -> "Experienced"
            negotiationPower >= 60 -> "Competent"
            negotiationPower >= 50 -> "Average"
            else -> "Inexperienced"
        }

    val commissionPercentage: String
        get() = "$commissionRate%"

    val reputationLevel: String
        get() = when {
            reputation >= 90 -> "World Class"
            reputation >= 80 -> "Respected"
            reputation >= 70 -> "Well-Known"
            reputation >= 60 -> "Emerging"
            else -> "Unknown"
        }

    val isSuperAgent: Boolean
        get() = reputation >= 85 && successfulDeals >= 50

    val languagesList: List<String>
        get() = languages?.split(",")?.map { it.trim() } ?: emptyList()
}

// ============ ENUMS ============

enum class AgentSpecialization(val value: String) {
    YOUNG_TALENT("YOUNG_TALENT"),
    STARS("STARS"),
    LOCAL("LOCAL"),
    INTERNATIONAL("INTERNATIONAL"),
    ALL_ROUNDER("ALL_ROUNDER")
}