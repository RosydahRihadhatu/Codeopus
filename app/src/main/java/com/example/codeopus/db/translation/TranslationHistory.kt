package com.example.codeopus.db.translation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_history")
data class TranslationHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val inputText: String,
    val translatedText: String,
    val timestamp: Long,
    val userId: String,
    val sequenceNumber: Int// Pastikan kolom ini ditambahkan
)
