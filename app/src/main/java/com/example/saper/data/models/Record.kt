package com.example.saper.data.models

data class Record(
    val id: Long = 0,
    val difficulty: String,      // "EASY", "NORMAL", "HARD"
    val timeSeconds: Int,        // Время в секундах
    val date: Long = System.currentTimeMillis(),
    val boardSize: String,       // "9x9", "16x16", "30x16"
    val minesCount: Int
)