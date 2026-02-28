package com.fameafrica.afm2026.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "game_states",
    foreignKeys = [
        ForeignKey(
            entity = ManagersEntity::class,
            parentColumns = ["id"],
            childColumns = ["manager_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamsEntity::class,
            parentColumns = ["id"],
            childColumns = ["team_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["manager_id"], unique = true),
        Index(value = ["team_id"]),
        Index(value = ["is_valid"])
    ]
)
data class GameStatesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "manager_id")
    val managerId: Int,

    @ColumnInfo(name = "manager_name")
    val managerName: String,

    @ColumnInfo(name = "team_id")
    val teamId: Int,

    @ColumnInfo(name = "team_name")
    val teamName: String,

    @ColumnInfo(name = "name")
    val name: String,  // Save game name

    @ColumnInfo(name = "season", defaultValue = "2024/25")
    val season: String = "2024/25",

    @ColumnInfo(name = "week", defaultValue = "1")
    val week: Int = 1,

    @ColumnInfo(name = "last_played")
    val lastPlayed: String? = null,

    @ColumnInfo(name = "is_valid", defaultValue = "1")
    val isValid: Boolean = true,

    @ColumnInfo(name = "game_version")
    val gameVersion: String? = null,

    @ColumnInfo(name = "save_file_size")
    val saveFileSize: Long? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null
) {

    val displayName: String
        get() = "$name - $teamName ($season Week $week)"

    val isActive: Boolean
        get() = isValid
}