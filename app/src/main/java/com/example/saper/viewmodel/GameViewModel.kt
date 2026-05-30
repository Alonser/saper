package com.example.saper.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import com.example.saper.data.models.Cell
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("saper_prefs", Context.MODE_PRIVATE)

    var rows by mutableIntStateOf(9)
    var cols by mutableIntStateOf(9)
    var minesCount by mutableIntStateOf(10)
    var board = mutableStateOf(emptyList<List<Cell>>())
    var minesLeft = mutableStateOf(10)
    var isGameOver = mutableStateOf(false)
    var bestTime = mutableStateOf(0)

    init {
        restartGame()
    }

    fun updateDifficulty(r: Int, c: Int) {
        rows = r
        cols = c
        minesCount = (r * c * 0.15).toInt().coerceAtLeast(1)
        restartGame()
    }

    fun saveWin(time: Int) {
        val key = "best_${rows}x${cols}"
        val currentBest = prefs.getInt(key, 0)
        if (currentBest == 0 || time < currentBest) {
            prefs.edit().putInt(key, time).apply()
            bestTime.value = time
        }
    }

    fun restartGame() {
        val newBoard = MutableList(rows) { r -> MutableList(cols) { c -> Cell(r, c) } }
        var placed = 0
        while (placed < minesCount) {
            val r = Random.nextInt(rows)
            val c = Random.nextInt(cols)
            if (!newBoard[r][c].isMine) {
                newBoard[r][c].isMine = true
                placed++
            }
        }
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (!newBoard[r][c].isMine) newBoard[r][c].adjacentMines = countMines(newBoard, r, c)
            }
        }
        board.value = newBoard
        minesLeft.value = minesCount
        isGameOver.value = false
        bestTime.value = prefs.getInt("best_${rows}x${cols}", 0)
    }

    private fun countMines(b: List<List<Cell>>, r: Int, c: Int): Int {
        var count = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                if ((r + dr) in 0 until rows && (c + dc) in 0 until cols && b[r + dr][c + dc].isMine) count++
            }
        }
        return count
    }

    fun openCell(r: Int, c: Int) {
        if (isGameOver.value || board.value[r][c].isOpened || board.value[r][c].isFlagged) return
        board.value[r][c].isOpened = true
        if (board.value[r][c].isMine) {
            isGameOver.value = true
        } else if (board.value[r][c].adjacentMines == 0) {
            for (dr in -1..1) {
                for (dc in -1..1) {
                    if ((r + dr) in 0 until rows && (c + dc) in 0 until cols) openCell(r + dr, c + dc)
                }
            }
        }
    }

    fun toggleFlag(r: Int, c: Int) {
        val cell = board.value[r][c]
        if (!cell.isOpened) {
            cell.isFlagged = !cell.isFlagged
            minesLeft.value += if (cell.isFlagged) -1 else 1
        }
    }
}