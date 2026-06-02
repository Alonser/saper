package com.example.saper.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class GameRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val difficulty: String, // "EASY", "NORMAL" или "HARD"
    val timeSeconds: Int,   // Твой экран использует Int для времени
    val date: Long          // Timestamp для отображения даты
)