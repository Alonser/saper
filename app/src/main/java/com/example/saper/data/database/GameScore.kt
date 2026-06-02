package com.example.saper.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class GameScore(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val difficulty: String,
    val timeSeconds: Long,
    val isWin: Boolean
)