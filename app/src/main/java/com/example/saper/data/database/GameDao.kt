package com.example.saper.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert
    suspend fun insertScore(score: GameScore)

    @Query("SELECT * FROM scores ORDER BY timeSeconds ASC")
    fun getAllScores(): Flow<List<GameScore>>
}