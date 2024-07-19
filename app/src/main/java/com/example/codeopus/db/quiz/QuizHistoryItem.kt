package com.example.codeopus.db.quiz

sealed class QuizHistoryItem {
    data class DateHeader(val date: String) : QuizHistoryItem()
    data class QuizHistory(val history: com.example.codeopus.db.quiz.QuizHistory) : QuizHistoryItem()
}
