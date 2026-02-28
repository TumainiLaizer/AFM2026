package com.fameafrica.afm2026.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Converters {
    
    // String List converters
    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }
    
    @TypeConverter
    fun toStringList(list: List<String>?): String? {
        return list?.joinToString(",")
    }
    
    // Int List converters
    @TypeConverter
    fun fromIntList(value: String?): List<Int>? {
        return value?.split(",")?.mapNotNull { it.trim().toIntOrNull() }
    }
    
    @TypeConverter
    fun toIntList(list: List<Int>?): String? {
        return list?.joinToString(",")
    }
    
    // JSON converters
    @TypeConverter
    fun fromJson(value: String?): Map<String, Any>? {
        return value?.let { 
            try {
                Json.decodeFromString<Map<String, Any>>(it)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    @TypeConverter
    fun toJson(map: Map<String, Any>?): String? {
        return map?.let { 
            try {
                Json.encodeToString(it)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    // Boolean converters (SQLite stores as 0/1)
    @TypeConverter
    fun fromBoolean(value: Int?): Boolean? = value?.let { it == 1 }
    
    @TypeConverter
    fun toBoolean(value: Boolean?): Int? = value?.let { if (it) 1 else 0 }
    
    // Timestamp converters (using Long for milliseconds)
    @TypeConverter
    fun fromTimestamp(value: Long?): java.util.Date? {
        return value?.let { java.util.Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: java.util.Date?): Long? {
        return date?.time
    }
}
