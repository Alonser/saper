package com.example.saper.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saper.data.models.Cell
import com.example.saper.data.models.GameState
import com.example.saper.data.repository.RecordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("saper_prefs", Context.MODE_PRIVATE)
    private val recordRepository = RecordRepository(application)

    var rows by mutableIntStateOf(9)
    var cols by mutableIntStateOf(9)
    var minesCount by mutableIntStateOf(10)

    var board by mutableStateOf(emptyList<List<Cell>>())
    var minesLeft by mutableIntStateOf(10)
    var gameState by mutableStateOf(GameState.INITIAL)
    var elapsedTime by mutableIntStateOf(0)
    var isNewRecord by mutableStateOf(false)

    private var isFirstMove by mutableStateOf(true)
    private var timerJob: Job? = null
    private var isTimerRunning = false

    init {
        loadSettings()
        restartGame()
    }

    private fun loadSettings() {
        rows = prefs.getInt("rows", 9)
        cols = prefs.getInt("cols", 9)
        minesCount = prefs.getInt("mines", 10)
    }

    private fun saveSettings() {
        prefs.edit().apply {
            putInt("rows", rows)
            putInt("cols", cols)
            putInt("mines", minesCount)
            apply()
        }
    }

    fun updateDifficulty(r: Int, c: Int) {
        rows = r
        cols = c
        minesCount = when {
            r == 9 && c == 9 -> 10
            r == 16 && c == 16 -> 40
            else -> 99
        }
        saveSettings()
        restartGame()
    }

    fun restartGame() {
        stopTimer()
        val newBoard = MutableList(rows) { r -> MutableList(cols) { c -> Cell(r, c) } }
        board = newBoard
        minesLeft = minesCount
        gameState = GameState.PLAYING
        isFirstMove = true
        elapsedTime = 0
        isTimerRunning = false
        isNewRecord = false
    }

    private fun startTimer() {
        if (timerJob?.isActive == true) return
        isTimerRunning = true
        timerJob = viewModelScope.launch {
            while (isTimerRunning && gameState == GameState.PLAYING) {
                delay(1000)
                if (gameState == GameState.PLAYING) {
                    elapsedTime++
                }
            }
        }
    }

    private fun stopTimer() {
        isTimerRunning = false
        timerJob?.cancel()
        timerJob = null
    }

    private fun placeMines(firstRow: Int, firstCol: Int) {
        val newBoard = board.toMutableList()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                newBoard[r][c].isMine = false
                newBoard[r][c].adjacentMines = 0
                newBoard[r][c].isOpened = false
                newBoard[r][c].isFlagged = false
            }
        }
        val safeCells = mutableSetOf<Pair<Int, Int>>()
        for (dr in -1..1) {
            for (dc in -1..1) {
                val nr = firstRow + dr
                val nc = firstCol + dc
                if (nr in 0 until rows && nc in 0 until cols) {
                    safeCells.add(Pair(nr, nc))
                }
            }
        }
        var placed = 0
        while (placed < minesCount) {
            val r = Random.nextInt(rows)
            val c = Random.nextInt(cols)
            val isSafe = safeCells.contains(Pair(r, c))
            if (!newBoard[r][c].isMine && !isSafe) {
                newBoard[r][c].isMine = true
                placed++
            }
            if (placed > rows * cols) break
        }
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (!newBoard[r][c].isMine) {
                    newBoard[r][c].adjacentMines = countMines(newBoard, r, c)
                }
            }
        }
        board = newBoard
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
        if (gameState != GameState.PLAYING) return
        if (board[r][c].isOpened || board[r][c].isFlagged) return

        if (isFirstMove) {
            placeMines(r, c)
            isFirstMove = false
            startTimer()
        }

        val cell = board[r][c]

        if (cell.isMine) {
            cell.isOpened = true
            gameState = GameState.LOST
            revealAllMines()
            stopTimer()
            return
        }

        openCellRecursive(r, c)
        checkWinCondition()
    }

    private fun openCellRecursive(r: Int, c: Int) {
        if (r !in 0 until rows || c !in 0 until cols) return
        val cell = board[r][c]
        if (cell.isOpened || cell.isFlagged) return
        cell.isOpened = true
        if (cell.adjacentMines == 0 && !cell.isMine) {
            for (dr in -1..1) {
                for (dc in -1..1) {
                    if (dr == 0 && dc == 0) continue
                    openCellRecursive(r + dr, c + dc)
                }
            }
        }
    }

    private fun checkWinCondition() {
        var openedCount = 0
        for (row in board) {
            for (cell in row) {
                if (cell.isOpened) openedCount++
            }
        }
        if (openedCount == (rows * cols - minesCount)) {
            gameState = GameState.WON
            stopTimer()
            saveRecord()
        }
    }

    private fun saveRecord() {
        viewModelScope.launch {
            val difficulty = when {
                rows == 9 && cols == 9 -> "EASY"
                rows == 16 && cols == 16 -> "NORMAL"
                else -> "HARD"
            }
            val boardSize = "${rows}x${cols}"
            isNewRecord = recordRepository.saveRecordIfBetter(difficulty, elapsedTime, boardSize, minesCount)
        }
    }

    private fun revealAllMines() {
        for (row in board) {
            for (cell in row) {
                if (cell.isMine) {
                    cell.isOpened = true
                }
            }
        }
    }

    fun toggleFlag(r: Int, c: Int) {
        if (gameState != GameState.PLAYING) return
        val cell = board[r][c]
        if (!cell.isOpened) {
            cell.isFlagged = !cell.isFlagged
            minesLeft += if (cell.isFlagged) -1 else 1
        }
    }

    fun getCurrentDifficulty(): String {
        return when {
            rows == 9 && cols == 9 -> "EASY"
            rows == 16 && cols == 16 -> "NORMAL"
            else -> "HARD"
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}