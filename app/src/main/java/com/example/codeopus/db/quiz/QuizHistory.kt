package com.example.codeopus.db.quiz

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Locale

@Entity(tableName = "quiz_history")
data class QuizHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val language: String,
    val level: String,
    val date: String,
    val score: Int,
    val total: Int,
    val category: String
)
 {
    fun getDateInMillis(): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Ubah format sesuai dengan data yang disimpan
        return sdf.parse(date.toString())?.time ?: 0
    }
}
