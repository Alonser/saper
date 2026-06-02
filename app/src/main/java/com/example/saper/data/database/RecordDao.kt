package com.example.saper.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Insert
    suspend fun insertRecord(record: GameRecord)

    // Получаем рекорды по сложности, сортируем по времени (лучшие сверху), берем топ-10
    @Query("SELECT * FROM records WHERE difficulty = :difficulty ORDER BY timeSeconds ASC LIMIT 10")
    fun getRecordsByDifficulty(difficulty: String): Flow<List<GameRecord>>

    @Query("DELETE FROM records WHERE difficulty = :difficulty")
    suspend fun clearRecords(difficulty: String)
}