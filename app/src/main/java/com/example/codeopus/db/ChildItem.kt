package com.example.codeopus.db

data class ChildItem(
    val title: String,
    val level: String,
    val isDone: Boolean = false,
    val score: Int? = null,
    val totalScore: Int? = null
)