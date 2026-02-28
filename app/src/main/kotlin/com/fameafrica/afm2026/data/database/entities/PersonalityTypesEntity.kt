package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "personality_types",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class PersonalityTypesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String, // e.g. PROFESSIONAL, AGGRESSIVE

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "positive_effects")
    val positiveEffects: String, // Comma-separated list

    @ColumnInfo(name = "negative_effects")
    val negativeEffects: String, // Comma-separated list

    @ColumnInfo(name = "icon_name")
    val iconName: String? = null
)
