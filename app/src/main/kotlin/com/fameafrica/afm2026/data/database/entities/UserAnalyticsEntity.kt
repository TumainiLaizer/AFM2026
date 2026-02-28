package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "user_analytics",
    indices = [
        Index(value = ["event_type", "created_at"]),
        Index(value = ["user_id"]),
        Index(value = ["session_id"])
    ]
)
data class UserAnalyticsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "event_type")
    val eventType: String,  // GAME_START, SETTINGS_CHANGE, MATCH_PLAYED, TRANSFER_MADE, etc.

    @ColumnInfo(name = "event_data")
    val eventData: String? = null,  // JSON data

    @ColumnInfo(name = "user_id")
    val userId: String? = null,  // Anonymous user ID

    @ColumnInfo(name = "session_id")
    val sessionId: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "device_info")
    val deviceInfo: String? = null,

    @ColumnInfo(name = "app_version")
    val appVersion: String? = null,

    @ColumnInfo(name = "country_code")
    val countryCode: String? = null
)