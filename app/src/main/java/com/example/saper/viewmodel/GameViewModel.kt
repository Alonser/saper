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
        mines = mineCount ?: calculateMinesCount(r, c)
        restartGame()
    }

    fun applyPreset(preset: PresetConfig) {
        rows = preset.rows
        cols = preset.cols
        mines = preset.mines
        restartGame()
    }

    private fun calculateMinesCount(rows: Int, cols: Int): Int {
        return ((rows * cols) * 0.15).toInt().coerceIn(1, (rows * cols) - 1)
    }

    fun openCell(cell: Cell) {
        if (cell.isFlagged || cell.isOpened || gameState != GameState.PLAYING) return

        cell.isOpened = true

        if (cell.isMine) {
            gameState = GameState.LOST
            revealAllMines()  // Показываем все мины при проигрыше
            return
        }

        if (cell.nearbyMines == 0) {
            openNearby(cell.row, cell.col)
        }

        checkWin()
        field = field.toList()
    }

    fun toggleFlag(cell: Cell) {
        if (cell.isOpened || gameState != GameState.PLAYING) return
        cell.isFlagged = !cell.isFlagged
        field = field.toList()
    }

    private fun revealAllMines() {
        // Показываем все мины
        field.flatten().forEach { cell ->
            if (cell.isMine) {
                cell.isOpened = true
            }
        }
        field = field.toList()
    }

    private fun checkWin() {
        val won = field.flatten().all { it.isMine || it.isOpened }
        if (won) gameState = GameState.WON
    }

    private fun openNearby(r: Int, c: Int) {
        for (dr in -1..1) {
            for (dc in -1..1) {
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until rows && nc in 0 until cols) {
                    val cell = field[nr][nc]
                    if (!cell.isOpened && !cell.isMine) {
                        cell.isOpened = true
                        if (cell.nearbyMines == 0) {
                            openNearby(nr, nc)
                        }
                    }
                }
            }
        }
    }

    private fun generateField(rows: Int, cols: Int, mines: Int): List<List<Cell>> {
        val field = List(rows) { r -> List(cols) { c -> Cell(r, c) } }

        var placed = 0
        while (placed < mines) {
            val r = Random.nextInt(rows)
            val c = Random.nextInt(cols)
            if (!field[r][c].isMine) {
                field[r][c].isMine = true
                placed++
            }
        }

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
        return field
    }

    fun getPresets() = presetConfigs
}