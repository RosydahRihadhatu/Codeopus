package com.example.codeopus.db.quiz

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface QuizHistoryDao {

    @Insert
    suspend fun insertQuizHistory(quizHistory: QuizHistory)

    @Update
    suspend fun updateQuizHistory(quizHistory: QuizHistory)

    @Query("SELECT * FROM quiz_history ORDER BY date DESC")
    suspend fun getAllQuizHistory(): List<QuizHistory>
}
