package com.example.codeopus.db.quiz

sealed class ProgrammingQuizHistoryItem {
    data class QuizHistory(val history: com.example.codeopus.db.quiz.QuizHistory, val day: String, val month: String) : ProgrammingQuizHistoryItem()
}
