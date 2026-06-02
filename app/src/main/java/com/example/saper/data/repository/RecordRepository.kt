package com.example.saper.data.repository

import com.example.saper.database.GameRecord
import com.example.saper.database.RecordDao

class RecordRepository(private val recordDao: RecordDao) {

    // Эти переменные уже ждет твой RecordsScreen
    val easyRecords = recordDao.getRecordsByDifficulty("EASY")
    val normalRecords = recordDao.getRecordsByDifficulty("NORMAL")
    val hardRecords = recordDao.getRecordsByDifficulty("HARD")

    suspend fun saveRecord(difficulty: String, timeSeconds: Int) {
        val record = GameRecord(
            difficulty = difficulty,
            timeSeconds = timeSeconds,
            date = System.currentTimeMillis() // Текущая дата
        )
        recordDao.insertRecord(record)
    }

    suspend fun clearRecords(difficulty: String) {
        recordDao.clearRecords(difficulty)
    }
}