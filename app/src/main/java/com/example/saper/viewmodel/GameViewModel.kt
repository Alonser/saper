package com.example.saper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.saper.data.models.Cell
import com.example.saper.data.models.GameState
import com.example.saper.data.models.PresetConfig
import kotlin.random.Random

class GameViewModel : ViewModel() {
    var isDarkTheme by mutableStateOf(false)
        private set

    var rows by mutableIntStateOf(9)
    var cols by mutableIntStateOf(9)
    var mines by mutableIntStateOf(10)
    var gameState by mutableStateOf(GameState.PLAYING)
    var field by mutableStateOf(generateField(rows, cols, mines))

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
        field = generateField(rows, cols, mines)
    }

    fun setFieldSize(r: Int, c: Int, mineCount: Int? = null) {
        rows = r
        cols = c
        mines = mineCount ?: calculateOptimalMinesCount(r, c)
        restartGame()
    }

    fun applyPreset(preset: PresetConfig) {
        rows = preset.rows
        cols = preset.cols
        mines = preset.mines
        restartGame()
    }

    private fun calculateOptimalMinesCount(rows: Int, cols: Int): Int {
        val totalCells = rows * cols
        return when {
            totalCells <= 81 -> (totalCells * 0.12).toInt()
            totalCells <= 256 -> (totalCells * 0.15).toInt()
            totalCells <= 480 -> (totalCells * 0.18).toInt()
            totalCells <= 900 -> (totalCells * 0.20).toInt()
            else -> (totalCells * 0.22).toInt()
        }.coerceIn(1, totalCells - 1)
    }

    fun openCell(cell: Cell) {
        if (cell.isFlagged || cell.isOpened || gameState != GameState.PLAYING) return

        if (cell.isMine) {
            cell.isOpened = true
            field = field.toList()
            gameState = GameState.LOST
            revealAllMines()
            return
        }

        // Используем итеративный подход вместо рекурсивного
        openAreaIterative(cell.row, cell.col)
        checkWin()
    }

    private fun openAreaIterative(startRow: Int, startCol: Int) {
        val queue = ArrayDeque<Pair<Int, Int>>()
        val visited = mutableSetOf<Pair<Int, Int>>()

        queue.addLast(Pair(startRow, startCol))
        visited.add(Pair(startRow, startCol))

        while (queue.isNotEmpty()) {
            val (row, col) = queue.removeFirst()
            val currentCell = field[row][col]

            // Пропускаем если это мина или флаг
            if (currentCell.isMine || currentCell.isFlagged) continue

            // Открываем клетку
            if (!currentCell.isOpened) {
                currentCell.isOpened = true
            }

            // Если клетка пустая (нет соседних мин), добавляем соседей в очередь
            if (currentCell.nearbyMines == 0) {
                for (dr in -1..1) {
                    for (dc in -1..1) {
                        if (dr == 0 && dc == 0) continue

                        val nr = row + dr
                        val nc = col + dc
                        val pair = Pair(nr, nc)

                        if (nr in 0 until rows &&
                            nc in 0 until cols &&
                            !visited.contains(pair)) {
                            val neighbor = field[nr][nc]
                            if (!neighbor.isMine && !neighbor.isFlagged) {
                                visited.add(pair)
                                queue.addLast(pair)
                            }
                        }
                    }
                }
            }
        }

        // Обновляем UI один раз после всех изменений
        field = field.toList()
    }

    fun toggleFlag(cell: Cell) {
        if (cell.isOpened || gameState != GameState.PLAYING) return
        cell.isFlagged = !cell.isFlagged
        field = field.toList()
    }

    private fun revealAllMines() {
        field.flatten().forEach { cell ->
            if (cell.isMine && !cell.isOpened) {
                cell.isOpened = true
            }
        }
        field = field.toList()
    }

    private fun checkWin() {
        val won = field.flatten().all { it.isMine || it.isOpened }
        if (won && gameState == GameState.PLAYING) {
            gameState = GameState.WON
            field = field.toList()
        }
    }

    private fun generateField(rows: Int, cols: Int, mines: Int): List<List<Cell>> {
        val field = MutableList(rows) { r ->
            MutableList(cols) { c ->
                Cell(r, c)
            }
        }

        // Размещаем мины
        var placed = 0
        while (placed < mines) {
            val r = Random.nextInt(rows)
            val c = Random.nextInt(cols)
            if (!field[r][c].isMine) {
                field[r][c].isMine = true
                placed++
            }
        }

        // Вычисляем количество соседних мин
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (field[r][c].isMine) continue

                var count = 0
                for (dr in -1..1) {
                    for (dc in -1..1) {
                        val nr = r + dr
                        val nc = c + dc
                        if (nr in 0 until rows && nc in 0 until cols && field[nr][nc].isMine) {
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