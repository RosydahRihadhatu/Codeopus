package com.example.codeopus.db.quiz

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QuizHistory::class], version = 1)
abstract class QuizHistoryDatabase : RoomDatabase() {
    abstract fun quizHistoryDao(): QuizHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: QuizHistoryDatabase? = null

        fun getDatabase(context: Context, userId: String): QuizHistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizHistoryDatabase::class.java,
                    "quiz_history_database_$userId" // Create a separate database for each user
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
