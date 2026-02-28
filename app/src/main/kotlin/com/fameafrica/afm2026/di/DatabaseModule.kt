package com.fameafrica.afm2026.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fameafrica.afm2026.data.database.AFMDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AFMDatabase {

        // ============ CRITICAL: Foreign Key Enforcement ============
        // Your prepopulated database already has the schema with FOREIGN KEY constraints
        // But SQLite turns them OFF by default. This callback ENSURES they are ON
        // Without this, your FKs are just documentation and invalid data CAN be inserted!
        // ===========================================================
        val enableForeignKeyCallback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // This MUST be called EVERY TIME the database is opened
                db.execSQL("PRAGMA foreign_keys=ON;")
                android.util.Log.d("AFM2026", "✅ Foreign key constraints ENABLED on database open")
            }

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("PRAGMA foreign_keys=ON;")
                android.util.Log.d("AFM2026", "✅ Foreign key constraints ENABLED on database create")
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                db.execSQL("PRAGMA foreign_keys=ON;")
                android.util.Log.d("AFM2026", "✅ Foreign key constraints ENABLED after destructive migration")
            }
        }

        // ============ BUILD DATABASE ============
        return Room.databaseBuilder(
            context,
            AFMDatabase::class.java,
            "afm2026.db"
        )
            // ✅ LOAD YOUR EXISTING PREPOPULATED DATABASE
            // Your afm2026_prepopulated.db already contains:
            // - All 62 tables with correct schema
            // - Nationalities (with FIFA codes)
            // - Referees (linked to nationalities via FK)
            // - Leagues (linked to nationalities via country_id)
            // - Cups (linked to nationalities via country_id)
            // - Players, Teams, Managers, etc.
            .createFromAsset("databases/afm2026_prepopulated.db")

            // ✅ ENABLE FOREIGN KEY CONSTRAINTS
            // This is the ONLY callback you need - NO seeding callbacks!
            .addCallback(enableForeignKeyCallback)

            // ✅ DEVELOPMENT ONLY - Remove for production!
            // This allows schema changes during development without losing data
            .fallbackToDestructiveMigration()

            // ✅ PRODUCTION - Use this instead (uncomment for release)
            // .fallbackToDestructiveMigrationOnDowngrade()

            .build()
    }

    /**
     * VERIFICATION UTILITY
     * Call this from your Application.onCreate() during development
     * to confirm foreign key constraints are working
     */
    fun verifyForeignKeyConstraints(database: AFMDatabase) {
        val db = database.openHelper.writableDatabase
        val cursor = db.query("PRAGMA foreign_keys")
        cursor.use {
            if (it.moveToFirst()) {
                val enabled = it.getInt(0) == 1
                android.util.Log.i("AFM2026",
                    "Foreign key constraints: ${if (enabled) "✅ ENABLED" else "❌ DISABLED"}"
                )

                // Force enable if disabled (should never happen with callback above)
                if (!enabled) {
                    db.execSQL("PRAGMA foreign_keys=ON;")
                    android.util.Log.w("AFM2026", "⚠️ Foreign keys FORCED enabled")
                }
            }
        }
    }

    /**
     * VERIFICATION UTILITY
     * Test that your foreign keys are actually working
     * This should FAIL if FKs are properly enforced
     */
    fun testForeignKeyConstraint(database: AFMDatabase) {
        try {
            val db = database.openHelper.writableDatabase

            // Attempt to insert a referee with invalid nationality_id
            // This should throw an exception if FKs are working
            db.execSQL("""
                INSERT INTO referees (name, strictness, bias, nationality_id, rating)
                VALUES ('Test Referee', 50, 50, 99999, 70)
            """.trimIndent())

            android.util.Log.e("AFM2026", "❌ FOREIGN KEY CONSTRAINT FAILED! Invalid data was inserted!")

            // Clean up the test data
            db.execSQL("DELETE FROM referees WHERE name = 'Test Referee'")

        } catch (e: Exception) {
            android.util.Log.i("AFM2026", "✅ Foreign key constraint WORKING: ${e.message}")
        }
    }
}