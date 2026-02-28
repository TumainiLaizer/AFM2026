package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "settings_history",
    foreignKeys = [
        ForeignKey(
            entity = GameSettingsEntity::class,
            parentColumns = ["id"],
            childColumns = ["settings_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["settings_id"]),
        Index(value = ["changed_at"])
    ]
)
data class SettingsHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "settings_id")
    val settingsId: Int,

    @ColumnInfo(name = "changed_field")
    val changedField: String,

    @ColumnInfo(name = "old_value")
    val oldValue: String? = null,

    @ColumnInfo(name = "new_value")
    val newValue: String? = null,

    @ColumnInfo(name = "changed_by", defaultValue = "user")
    val changedBy: String = "user",

    @ColumnInfo(name = "changed_at")
    val changedAt: Long,

    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: String? = null
) {

    val changeSummary: String
        get() = "$changedField: ${oldValue ?: "null"} → ${newValue ?: "null"}"
}