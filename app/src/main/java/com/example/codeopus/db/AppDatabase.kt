package com.example.codeopus.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.codeopus.db.quiz.QuizHistory
import com.example.codeopus.db.quiz.QuizHistoryDao
import com.example.codeopus.db.translation.TranslationHistory
import com.example.codeopus.db.translation.TranslationHistoryDao


@Database(entities = [QuizHistory::class, TranslationHistory::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizHistoryDao(): QuizHistoryDao
    abstract fun translationHistoryDao(): TranslationHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, userId: String): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database_$userId"
                )
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE translation_history ADD COLUMN sequenceNumber INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration logic from version 4 to 5
                // For example:
                // database.execSQL("ALTER TABLE translation_history ADD COLUMN newColumn TEXT")
            }
        }
    }
}
