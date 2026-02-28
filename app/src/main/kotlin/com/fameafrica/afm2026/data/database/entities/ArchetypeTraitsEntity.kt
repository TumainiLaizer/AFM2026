package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "archetype_traits",
    indices = [
        Index(value = ["archetype_name"], unique = true)
    ]
)
data class ArchetypeTraitsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "archetype_name")
    val archetypeName: String,

    @ColumnInfo(name = "primary_trait")
    val primaryTrait: String,

    @ColumnInfo(name = "secondary_trait")
    val secondaryTrait: String?,

    @ColumnInfo(name = "gameplay_focus")
    val gameplayFocus: String,

    @ColumnInfo(name = "attribute_boost")  // JSON with attribute boosts
    val attributeBoost: String?,

    @ColumnInfo(name = "description")
    val description: String?
) {

    // ============ COMPUTED PROPERTIES ============

    val fullName: String
        get() = archetypeName.replace('_', ' ').split(' ').joinToString(" ") {
            it.lowercase().replaceFirstChar { char -> char.uppercase() }
        }

    val focusAreas: List<String>
        get() = gameplayFocus.split(',').map { it.trim() }

    val traitCombination: String
        get() = if (secondaryTrait != null) {
            "$primaryTrait + $secondaryTrait"
        } else {
            primaryTrait
        }
}

// ============ ENUMS ============

