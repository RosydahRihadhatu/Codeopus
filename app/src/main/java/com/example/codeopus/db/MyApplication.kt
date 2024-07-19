package com.example.codeopus.db

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MyApplication : Application() {

    companion object {
        private lateinit var database: AppDatabase
        private lateinit var instance: MyApplication

        fun getDatabase(): AppDatabase {
            return database
        }

        fun initializeDatabase(userId: String, context: Context) {
            database = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "quiz_database_$userId"
            ).addMigrations(MIGRATION_1_2)
                .build()
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE quiz_history ADD COLUMN language TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE quiz_history ADD COLUMN level TEXT NOT NULL DEFAULT ''")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app_database"
        ).build()
    }

        fun getDatabase(): AppDatabase {
            return database
        }
}

