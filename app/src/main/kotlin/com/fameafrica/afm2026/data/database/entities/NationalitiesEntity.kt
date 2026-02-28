package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "nationalities",
    indices = [
        Index(value = ["nationality"], unique = true),
        Index(value = ["fifa_code"], unique = true),
        Index(value = ["flag_path"], unique = true),
        Index(value = ["confederation"]),
        Index(value = ["region"]),
        Index(value = ["is_african"])
    ]
)
data class NationalitiesEntity(
    @PrimaryKey(autoGenerate = false)  // Manual ID from your data
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "nationality")
    val nationality: String,

    @ColumnInfo(name = "fifa_code")
    val fifaCode: String,

    @ColumnInfo(name = "flag_path")
    val flagPath: String?,

    @ColumnInfo(name = "confederation")
    val confederation: String? = null,  // CAF, UEFA, CONMEBOL, etc.

    @ColumnInfo(name = "region")
    val region: String? = null,  // West Africa, East Africa, Southern Africa, etc.

    @ColumnInfo(name = "is_african")
    val isAfrican: Boolean = false,

    @ColumnInfo(name = "population")
    val population: Long? = null,

    @ColumnInfo(name = "capital_city")
    val capitalCity: String? = null,

    @ColumnInfo(name = "currency")
    val currency: String? = null,

    @ColumnInfo(name = "language")
    val language: String? = null
) {

    // ============ COMPUTED PROPERTIES ============

    val displayName: String
        get() = nationality

    val flagUrl: String
        get() = flagPath ?: "flags/default.png"

    val isCAFMember: Boolean
        get() = confederation == "CAF"

    val isUEFAMember: Boolean
        get() = confederation == "UEFA"

    val isCONMEBOLMember: Boolean
        get() = confederation == "CONMEBOL"

    val isCONCACAFMember: Boolean
        get() = confederation == "CONCACAF"

    val isAFCMember: Boolean
        get() = confederation == "AFC"

    val isOFCMember: Boolean
        get() = confederation == "OFC"
}

// ============ ENUMS ============

enum class Confederation(val value: String) {
    CAF("CAF"),
    UEFA("UEFA"),
    CONMEBOL("CONMEBOL"),
    CONCACAF("CONCACAF"),
    AFC("AFC"),
    OFC("OFC")
}
