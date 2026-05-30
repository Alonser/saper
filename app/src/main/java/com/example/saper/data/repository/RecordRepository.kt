package com.example.saper.data.repository

import android.content.Context
import com.example.saper.data.database.DatabaseHelper
import com.example.saper.data.models.Record
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecordRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _easyRecords = MutableStateFlow<List<Record>>(emptyList())
    val easyRecords = _easyRecords.asStateFlow()

    private val _normalRecords = MutableStateFlow<List<Record>>(emptyList())
    val normalRecords = _normalRecords.asStateFlow()

    private val _hardRecords = MutableStateFlow<List<Record>>(emptyList())
    val hardRecords = _hardRecords.asStateFlow()

    init {
        scope.launch {
            refreshAllRecords()
        }
    }

    private suspend fun refreshAllRecords() {
        _easyRecords.update { dbHelper.getTopRecords("EASY") }
        _normalRecords.update { dbHelper.getTopRecords("NORMAL") }
        _hardRecords.update { dbHelper.getTopRecords("HARD") }
    }

    suspend fun saveRecordIfBetter(difficulty: String, timeSeconds: Int, boardSize: String, minesCount: Int): Boolean {
        val bestTime = dbHelper.getBestTime(difficulty)

        if (bestTime == null || timeSeconds < bestTime) {
            val record = Record(
                difficulty = difficulty,
                timeSeconds = timeSeconds,
                boardSize = boardSize,
                minesCount = minesCount
            )
            dbHelper.insertRecord(record)
            refreshAllRecords()
            return true
        }
        return false
    }

    suspend fun clearRecords(difficulty: String) {
        dbHelper.clearRecords(difficulty)
        refreshAllRecords()
    }
}