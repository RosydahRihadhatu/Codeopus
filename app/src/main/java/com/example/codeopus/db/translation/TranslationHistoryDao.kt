package com.example.codeopus.db.translation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TranslationHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(translationHistory: TranslationHistory)

    @Query("SELECT * FROM translation_history WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllHistoryByUser(userId: String): List<TranslationHistory>

    @Query("SELECT * FROM translation_history ORDER BY timestamp DESC LIMIT 3")
    suspend fun getAllHistory(): List<TranslationHistory>

    @Query("SELECT MAX(sequenceNumber) FROM translation_history WHERE userId = :userId")
    suspend fun getMaxSequenceNumberByUser(userId: String): Int?
}
