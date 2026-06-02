package com.example.saper.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saper.data.models.Cell
import com.example.saper.database.AppDatabase
import com.example.saper.data.repository.RecordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    // Инициализация базы данных и репозитория рекордов
    private val repository: RecordRepository

    init {
        val dao = AppDatabase.getDatabase(application).recordDao()
        repository = RecordRepository(dao)
    }

    // Настройки игры по умолчанию (Лёгкий)
    var currentDifficulty by mutableStateOf("EASY")
    var rows by mutableIntStateOf(9)
    var cols by mutableIntStateOf(9)
    var minesCount by mutableIntStateOf(10)

    // Состояния интерфейса
    var board = mutableStateOf(emptyList<List<Cell>>())
    var minesLeft = mutableIntStateOf(10)
    var isGameOver = mutableStateOf(false)
    var isGameWon = mutableStateOf(false)

    // Таймер (Int для совместимости с RecordsScreen)
    var timeSeconds = mutableIntStateOf(0)
    private var timerJob: Job? = null

    init {
        restartGame()
    }

    // Обновленная функция установки сложности
    fun setDifficulty(difficulty: String) {
        currentDifficulty = difficulty
        when (difficulty) {
            "EASY" -> { rows = 9; cols = 9; minesCount = 10 }
            "NORMAL" -> { rows = 16; cols = 16; minesCount = 40 }
            "HARD" -> { rows = 30; cols = 16; minesCount = 99 } // 30 строк, 16 столбцов
        }
        restartGame()
    }

    fun restartGame() {
        // Сброс таймера
        timerJob?.cancel()
        timeSeconds.intValue = 0

        // Создаем пустое поле
        val newBoard = MutableList(rows) { r -> MutableList(cols) { c -> Cell(r, c) } }

        // Расставляем мины
        var placed = 0
        while (placed < minesCount) {
            val r = Random.nextInt(rows)
            val c = Random.nextInt(cols)
            if (!newBoard[r][c].isMine) {
                newBoard[r][c].isMine = true
                placed++
            }
        }

        // Считаем мины вокруг
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (!newBoard[r][c].isMine) {
                    newBoard[r][c].adjacentMines = countMines(newBoard, r, c)
                }
            }
        }

        // Обновляем состояния
        board.value = newBoard
        minesLeft.intValue = minesCount
        isGameOver.value = false
        isGameWon.value = false

        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (!isGameOver.value && !isGameWon.value) {
                delay(1000L)
                timeSeconds.intValue += 1
            }
        }
    }

    private fun countMines(b: List<List<Cell>>, r: Int, c: Int): Int {
        var count = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until rows && nc in 0 until cols && b[nr][nc].isMine) count++
            }
        }
        return count
    }

    fun openCell(r: Int, c: Int) {
        if (isGameOver.value || isGameWon.value) return
        val cell = board.value[r][c]
        if (cell.isOpened || cell.isFlagged) return

        cell.isOpened = true

        if (cell.isMine) {
            // Поражение
            isGameOver.value = true
            revealAllMines()
            timerJob?.cancel() // Останавливаем таймер, но ничего не сохраняем
        } else {
            // Открываем соседние, если пусто
            if (cell.adjacentMines == 0) {
                for (dr in -1..1) {
                    for (dc in -1..1) {
                        val nr = r + dr
                        val nc = c + dc
                        if (nr in 0 until rows && nc in 0 until cols) {
                            openCell(nr, nc)
                        }
                    }
                }
            }
            checkWinCondition()
        }

        // Принудительно обновляем UI
        board.value = board.value.toList()
    }

    fun toggleFlag(r: Int, c: Int) {
        if (isGameOver.value || isGameWon.value) return
        val cell = board.value[r][c]
        if (!cell.isOpened) {
            cell.isFlagged = !cell.isFlagged
            minesLeft.intValue += if (cell.isFlagged) -1 else 1
            // Принудительно обновляем UI для отображения флажка
            board.value = board.value.toList()
        }
    }

    private fun checkWinCondition() {
        var unrevealedSafeCells = 0
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cell = board.value[r][c]
                if (!cell.isMine && !cell.isOpened) {
                    unrevealedSafeCells++
                }
            }
        }

        // Победа: все клетки без мин открыты
        if (unrevealedSafeCells == 0) {
            isGameWon.value = true
            isGameOver.value = true
            timerJob?.cancel()

            // Сохраняем рекорд только при победе
            viewModelScope.launch {
                repository.saveRecord(
                    difficulty = currentDifficulty,
                    timeSeconds = timeSeconds.intValue
                )
            }
        }
    }

    private fun revealAllMines() {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (board.value[r][c].isMine) {
                    board.value[r][c].isOpened = true
                }
            }
        }
    }
}