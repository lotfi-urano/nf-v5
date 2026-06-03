package com.aistudio.nasheet.app

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.aistudio.nasheet.app.data.database.AppDatabase
import com.aistudio.nasheet.app.data.database.NasheetRepository

class NasheetApplication : Application() {

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "nasheet_db"
        )
        .addMigrations(
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6,
            MIGRATION_6_7,
            MIGRATION_7_8
        )
        .fallbackToDestructiveMigration()
        .fallbackToDestructiveMigrationOnDowngrade()
        .build()
    }

    val repository: NasheetRepository by lazy {
        NasheetRepository(database.appDao())
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Load saved API URL from preferences
        initApiClient()
    }

    private fun initApiClient() {
        try {
            val savedUrl = com.aistudio.nasheet.app.data.api.NasheetPreferenceHelper.getBaseUrl(this)
            com.aistudio.nasheet.app.data.api.NasheetApiClient.updateBaseUrl(savedUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load saved API URL, using default.", e)
        }
    }

    companion object {
        private const val TAG = "NasheetApplication"

        lateinit var instance: NasheetApplication
            private set

        private val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS educational_content")
            }
        }

        private val MIGRATION_4_5 = object : androidx.room.migration.Migration(4, 5) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                safeAddColumn(db, "child_profiles", "parentName", "TEXT NOT NULL DEFAULT ''")
                safeAddColumn(db, "child_profiles", "parentPhone", "TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_5_6 = object : androidx.room.migration.Migration(5, 6) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                safeAddColumn(db, "child_profiles", "parentCode", "TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_6_7 = object : androidx.room.migration.Migration(6, 7) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                safeAddColumn(db, "child_profiles", "teacherCode", "TEXT NOT NULL DEFAULT ''")
                safeAddColumn(db, "child_profiles", "parentEmail", "TEXT NOT NULL DEFAULT ''")
                safeAddColumn(db, "child_profiles", "levelProgress", "INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_7_8 = object : androidx.room.migration.Migration(7, 8) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                safeAddColumn(db, "activity_logs", "starsEarned", "INTEGER NOT NULL DEFAULT 0")
            }
        }

        private fun safeAddColumn(
            db: androidx.sqlite.db.SupportSQLiteDatabase,
            tableName: String,
            columnName: String,
            typeAndDefault: String
        ) {
            try {
                db.query(
                    androidx.sqlite.db.SimpleSQLiteQuery("PRAGMA table_info($tableName)")
                ).use { cursor ->
                    var exists = false
                    val nameIndex = cursor.getColumnIndex("name")
                    if (nameIndex != -1) {
                        while (cursor.moveToNext()) {
                            if (cursor.getString(nameIndex) == columnName) {
                                exists = true
                                break
                            }
                        }
                    }
                    if (!exists) {
                        db.execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $typeAndDefault")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding column $columnName to $tableName", e)
            }
        }
    }
}
