package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "user_preferences",
    indices = [
        Index(value = ["preference_key"], unique = true)
    ]
)
data class UserPreferencesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "preference_key")
    val preferenceKey: String,

    @ColumnInfo(name = "preference_value")
    val preferenceValue: String,

    @ColumnInfo(name = "preference_type", defaultValue = "string")
    val preferenceType: String = "string",  // string, boolean, integer, float

    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: String? = null,

    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
    val updatedAt: String? = null
)