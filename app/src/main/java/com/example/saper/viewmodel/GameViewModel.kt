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

    var rows by mutableIntStateOf(9)
    var cols by mutableIntStateOf(9)
    var mines by mutableIntStateOf(10)

    var gameState by mutableStateOf(GameState.PLAYING)

    var field by mutableStateOf(
        generateField(rows, cols, mines)
    )

    private val presetConfigs = listOf(

        PresetConfig(
            "Новичок",
            9,
            9,
            10
        ),

        PresetConfig(
            "Любитель",
            16,
            16,
            40
        ),

        PresetConfig(
            "Эксперт",
            30,
            16,
            99
        )
    )

    fun restartGame() {

        gameState = GameState.PLAYING

        field = generateField(
            rows,
            cols,
            mines
        )
    }

    fun applyPreset(
        preset: PresetConfig
    ) {

        rows = preset.rows
        cols = preset.cols
        mines = preset.mines

        restartGame()
    }

    fun getPresets() = presetConfigs

    fun toggleFlag(cell: Cell) {

        if (
            cell.isOpened ||
            gameState != GameState.PLAYING
        ) return

        cell.isFlagged = !cell.isFlagged

        field = field.toList()
    }

    fun openCell(cell: Cell) {

        if (
            cell.isOpened ||
            cell.isFlagged ||
            gameState != GameState.PLAYING
        ) return

        if (cell.isMine) {

            cell.isOpened = true

            gameState = GameState.LOST

            revealAllMines()

            field = field.toList()

            return
        }

        val mutableField = field
            .map { it.toMutableList() }
            .toMutableList()

        openAreaFast(
            mutableField,
            cell.row,
            cell.col
        )

        field = mutableField

        checkWin()
    }

    private fun openAreaFast(
        field: MutableList<MutableList<Cell>>,
        startRow: Int,
        startCol: Int
    ) {

        val queue = ArrayDeque<Pair<Int, Int>>()

        queue.add(Pair(startRow, startCol))

        while (queue.isNotEmpty()) {

            val (row, col) = queue.removeFirst()

            if (
                row !in 0 until rows ||
                col !in 0 until cols
            ) continue

            val current = field[row][col]

            if (
                current.isOpened ||
                current.isMine ||
                current.isFlagged
            ) continue

            current.isOpened = true

            if (current.nearbyMines == 0) {

                for (dr in -1..1) {
                    for (dc in -1..1) {

                        if (dr == 0 && dc == 0)
                            continue

                        queue.add(
                            Pair(
                                row + dr,
                                col + dc
                            )
                        )
                    }
                }
            }
        }
    }

    private fun revealAllMines() {

        field.flatten().forEach {

            if (it.isMine) {

                it.isOpened = true
            }
        }

        field = field.toList()
    }

    private fun checkWin() {

        val won = field.flatten().all {

            it.isMine || it.isOpened
        }

        if (won) {

            gameState = GameState.WON
        }
    }

    private fun generateField(
        rows: Int,
        cols: Int,
        mines: Int
    ): List<List<Cell>> {

        val field =
            MutableList(rows) { r ->

                MutableList(cols) { c ->

                    Cell(r, c)
                }
            }

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

                if (field[r][c].isMine)
                    continue

                var count = 0

                for (dr in -1..1) {
                    for (dc in -1..1) {

                        val nr = r + dr
                        val nc = c + dc

                        if (
                            nr in 0 until rows &&
                            nc in 0 until cols &&
                            field[nr][nc].isMine
                        ) {

                            count++
                        }
                    }
                }

                field[r][c].nearbyMines = count
            }
        }

        return field
    }
}