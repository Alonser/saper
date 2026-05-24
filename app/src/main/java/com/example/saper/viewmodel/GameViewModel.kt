package com.example.saper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saper.data.models.Cell
import com.example.saper.data.models.GameState
import com.example.saper.data.models.PresetConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {
    var isDarkTheme by mutableStateOf(false)
        private set

    var rows by mutableIntStateOf(9)
    var cols by mutableIntStateOf(9)
    var mines by mutableIntStateOf(10)
    var gameState by mutableStateOf(GameState.PLAYING)
    var field by mutableStateOf(generateField(rows, cols, mines))

    private var isOpeningArea = false // Флаг для предотвращения множественных вызовов

    private val presetConfigs = listOf(
        PresetConfig("Новичок", 9, 9, 10),
        PresetConfig("Любитель", 16, 16, 40),
        PresetConfig("Эксперт", 30, 16, 99)
    )

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }

    fun restartGame() {
        gameState = GameState.PLAYING
        isOpeningArea = false
        field = generateField(rows, cols, mines)
    }

    fun setFieldSize(r: Int, c: Int, mineCount: Int? = null) {
        rows = r
        cols = c
        // Автоматический расчет количества мин
        mines = mineCount ?: calculateOptimalMinesCount(r, c)
        restartGame()
    }

    fun applyPreset(preset: PresetConfig) {
        rows = preset.rows
        cols = preset.cols
        mines = preset.mines
        restartGame()
    }

    // Умный расчет количества мин в зависимости от размера поля
    private fun calculateOptimalMinesCount(rows: Int, cols: Int): Int {
        val totalCells = rows * cols
        return when {
            totalCells <= 81 -> (totalCells * 0.12).toInt() // 9x9 -> ~10 мин
            totalCells <= 256 -> (totalCells * 0.15).toInt() // 16x16 -> ~38 мин
            totalCells <= 480 -> (totalCells * 0.18).toInt() // 30x16 -> ~86 мин
            totalCells <= 900 -> (totalCells * 0.20).toInt() // 30x30 -> ~180 мин
            else -> (totalCells * 0.22).toInt() // Очень большие поля -> больше мин
        }.coerceIn(1, totalCells - 1) // Минимум 1 мина, не больше всех клеток
    }

    fun openCell(cell: Cell) {
        if (cell.isFlagged || cell.isOpened || gameState != GameState.PLAYING || isOpeningArea) return

        if (cell.isMine) {
            // Открываем мину
            cell.isOpened = true
            field = field.toList()
            gameState = GameState.LOST
            revealAllMines()
            return
        }

        // Открываем клетку
        cell.isOpened = true
        field = field.toList()

        if (cell.nearbyMines == 0) {
            // Асинхронно открываем соседние клетки, чтобы не блокировать UI
            openNearbyAsync(cell.row, cell.col)
        } else {
            checkWin()
        }
    }

    private fun openNearbyAsync(startRow: Int, startCol: Int) {
        if (isOpeningArea) return
        isOpeningArea = true

        viewModelScope.launch {
            val cellsToOpen = mutableListOf<Cell>()
            val visited = mutableSetOf<Pair<Int, Int>>()
            val queue = ArrayDeque<Pair<Int, Int>>()

            queue.addLast(Pair(startRow, startCol))
            visited.add(Pair(startRow, startCol))

            // Собираем все клетки для открытия
            while (queue.isNotEmpty()) {
                val (row, col) = queue.removeFirst()
                val currentCell = field[row][col]

                if (!currentCell.isOpened && !currentCell.isMine) {
                    cellsToOpen.add(currentCell)
                }

                if (currentCell.nearbyMines == 0 && !currentCell.isMine) {
                    for (dr in -1..1) {
                        for (dc in -1..1) {
                            val nr = row + dr
                            val nc = col + dc
                            val pair = Pair(nr, nc)

                            if (nr in 0 until rows &&
                                nc in 0 until cols &&
                                !visited.contains(pair)) {
                                visited.add(pair)
                                queue.addLast(pair)
                            }
                        }
                    }
                }
            }

            // Открываем клетки порциями, чтобы UI не зависал
            val batchSize = 50
            for (i in cellsToOpen.indices step batchSize) {
                val end = minOf(i + batchSize, cellsToOpen.size)
                for (j in i until end) {
                    val cellToOpen = cellsToOpen[j]
                    if (!cellToOpen.isOpened && !cellToOpen.isMine && gameState == GameState.PLAYING) {
                        cellToOpen.isOpened = true
                    }
                }
                field = field.toList()
                delay(16) // Небольшая задержка для плавности (примерно 60 FPS)
            }

            isOpeningArea = false
            checkWin()
        }
    }

    fun toggleFlag(cell: Cell) {
        if (cell.isOpened || gameState != GameState.PLAYING) return
        cell.isFlagged = !cell.isFlagged
        field = field.toList()
    }

    private fun revealAllMines() {
        viewModelScope.launch {
            // Показываем мины с анимацией
            val mineCells = field.flatten().filter { it.isMine }

            for (cell in mineCells) {
                if (!cell.isOpened) {
                    cell.isOpened = true
                    field = field.toList()
                    delay(30) // Плавное появление мин
                }
            }
        }
    }

    private fun checkWin() {
        if (!isOpeningArea) {
            val won = field.flatten().all { it.isMine || it.isOpened }
            if (won && gameState == GameState.PLAYING) {
                gameState = GameState.WON
                field = field.toList()
            }
        }
    }

    private fun generateField(rows: Int, cols: Int, mines: Int): List<List<Cell>> {
        // Генерируем поле с оптимизацией
        val field = MutableList(rows) { r ->
            MutableList(cols) { c ->
                Cell(r, c)
            }
        }

        // Размещение мин
        var placed = 0
        val totalCells = rows * cols
        val maxAttempts = totalCells * 2
        var attempts = 0

        while (placed < mines && attempts < maxAttempts) {
            val r = Random.nextInt(rows)
            val c = Random.nextInt(cols)
            if (!field[r][c].isMine) {
                field[r][c].isMine = true
                placed++
            }
            attempts++
        }

        // Если не удалось разместить все мины, размещаем оставшиеся последовательно
        if (placed < mines) {
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if (placed < mines && !field[r][c].isMine) {
                        field[r][c].isMine = true
                        placed++
                    }
                }
            }
        }

        // Подсчёт соседних мин (оптимизированная версия)
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (field[r][c].isMine) continue
                var count = 0
                val minRow = maxOf(0, r - 1)
                val maxRow = minOf(rows - 1, r + 1)
                val minCol = maxOf(0, c - 1)
                val maxCol = minOf(cols - 1, c + 1)

                for (nr in minRow..maxRow) {
                    for (nc in minCol..maxCol) {
                        if (field[nr][nc].isMine) {
                            count++
                        }
                    }
                }
                field[r][c].nearbyMines = count
            }
        }

        return field.map { it.toList() }
    }

    fun getPresets() = presetConfigs
}