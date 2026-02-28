package com.fameafrica.afm2026

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.fameafrica.afm2026.data.database.AFMDatabase
import com.fameafrica.afm2026.data.initializer.GameInitializer
import com.fameafrica.afm2026.di.DatabaseModule
import com.google.firebase.components.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AFM2026Application : Application(), Configuration.Provider {

    @Inject
    lateinit var database: AFMDatabase

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var gameInitializer: GameInitializer

    override fun onCreate() {
        super.onCreate()

        // DEVELOPMENT ONLY - Verify your prepopulated database
        if (BuildConfig.DEBUG) {
            verifyDatabase()
        }

        // Initialize game data
        CoroutineScope(Dispatchers.IO).launch {
            gameInitializer.initializeGameData()
        }
    }

    private fun verifyDatabase() {
        CoroutineScope(Dispatchers.IO).launch {

            // 1. Verify Foreign Key constraints are enabled
            DatabaseModule.verifyForeignKeyConstraints(database)
            DatabaseModule.testForeignKeyConstraint(database)

            // 2. Verify your prepopulated data exists
            verifyPrepopulatedData()
        }
    }

    private suspend fun verifyPrepopulatedData() {
        try {
            // Check Nationalities
            val nationalitiesCount = database.nationalitiesDao().getCount()
            android.util.Log.i("AFM2026", "📊 Prepopulated nationalities: $nationalitiesCount")

            // Check Referees
            val refereesCount = database.refereesDao().getAll().first().size
            android.util.Log.i("AFM2026", "📊 Prepopulated referees: $refereesCount")

            // Check Leagues
            val leaguesCount = database.leaguesDao().getAll().first().size
            android.util.Log.i("AFM2026", "📊 Prepopulated leagues: $leaguesCount")

            // Check Cups
            val cupsCount = database.cupsDao().getAll().first().size
            android.util.Log.i("AFM2026", "📊 Prepopulated cups: $cupsCount")

            // Check Teams
            val teamsCount = database.teamsDao().getAll().first().size
            android.util.Log.i("AFM2026", "📊 Prepopulated teams: $teamsCount")

            // Check Players
            val playersCount = database.playersDao().getAll().first().size
            android.util.Log.i("AFM2026", "📊 Prepopulated players: $playersCount")

            // Verify specific data integrity
            verifySpecificData()

        } catch (e: Exception) {
            android.util.Log.e("AFM2026", "❌ Database verification failed", e)
        }
    }

    private suspend fun verifySpecificData() {
        // 1. Verify Ahmed Arajiga (Tanzanian referee) exists
        val ahmedArajiga = database.refereesDao().getByName("Ahmed Arajiga")
        android.util.Log.i("AFM2026",
            "👨‍⚖️ Ahmed Arajiga (Tanzania): ${if (ahmedArajiga != null) "✅ FOUND" else "❌ MISSING"}"
        )

        // 2. Verify Tanzanian Premier League exists
        val tanzaniaLeague = database.leaguesDao().getByName("Tanzania Premier League")
        android.util.Log.i("AFM2026",
            "🏆 Tanzanian Premier League: ${if (tanzaniaLeague != null) "✅ FOUND" else "❌ MISSING"}"
        )

        // 3. Verify CAF Champions League exists
        val cafCL = database.cupsDao().getByName("CAF Champions League")
        android.util.Log.i("AFM2026",
            "🏆 CAF Champions League: ${if (cafCL != null) "✅ FOUND" else "❌ MISSING"}"
        )

        // 4. Verify CRDB Federations Cup exists
        val tanzaniaCup = database.cupsDao().getByName("CRDB Federations Cup")
        android.util.Log.i("AFM2026",
            "🏆 CRDB Federations Cup: ${if (tanzaniaCup != null) "✅ FOUND" else "❌ MISSING"}"
        )

        // 5. Verify foreign key relationships
        if (ahmedArajiga != null && tanzaniaLeague != null) {
            val refereeNation = database.nationalitiesDao()
                .getById(ahmedArajiga.nationalityId)

            android.util.Log.i("AFM2026",
                "🔗 Referee ${ahmedArajiga.name} nationality: ${refereeNation?.nationality ?: "UNKNOWN"}"
            )

            // Ahmed Arajiga SHOULD be Tanzanian (nationality_id = 1)
            val isTanzanian = refereeNation?.fifaCode == "TAN"
            android.util.Log.i("AFM2026",
                "🔗 Ahmed Arajiga is Tanzanian: ${if (isTanzanian) "✅ CORRECT" else "❌ INCORRECT"}"
            )
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}