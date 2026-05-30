package com.example.saper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.saper.data.models.Cell
import kotlin.random.Random

class GameViewModel : ViewModel() {
    var rows by mutableStateOf(9)
    var cols by mutableStateOf(9)
    var minesCount by mutableStateOf(10)

    var board = mutableStateOf(emptyList<List<Cell>>())
    var minesLeft = mutableStateOf(minesCount)
    var isGameOver = mutableStateOf(false)
    var isGameWon = mutableStateOf(false)

    init {
        restartGame()
    }

    fun restartGame() {
        // Создаем новую сетку ячеек
        val newBoard = MutableList(rows) { r ->
            MutableList(cols) { c -> Cell(r, c) }
        }

        // Расставляем мины
        var placedMines = 0
        while (placedMines < minesCount) {
            val r = Random.nextInt(rows)
            val c = Random.nextInt(cols)
            if (!newBoard[r][c].isMine) {
                newBoard[r][c].isMine = true
                placedMines++
            }
        }

        // Считаем соседние мины для каждой ячейки
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (!newBoard[r][c].isMine) {
                    newBoard[r][c].adjacentMines = countAdjacentMines(newBoard, r, c)
                }
            }
        }

        board.value = newBoard
        minesLeft.value = minesCount
        isGameOver.value = false
        isGameWon.value = false
    }

    private fun countAdjacentMines(currentBoard: MutableList<MutableList<Cell>>, r: Int, c: Int): Int {
        var count = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until rows && nc in 0 until cols && currentBoard[nr][nc].isMine) {
                    count++
                }
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
            isGameOver.value = true
            revealAllMines()
        } else if (cell.adjacentMines == 0) {
            // Рекурсивное открытие пустых ячеек
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

    fun toggleFlag(r: Int, c: Int) {
        if (isGameOver.value || isGameWon.value) return
        val cell = board.value[r][c]
        if (!cell.isOpened) {
            cell.isFlagged = !cell.isFlagged
            minesLeft.value += if (cell.isFlagged) -1 else 1
        }
    }

    private fun checkWinCondition() {
        var closedSafeCells = 0
        for (row in board.value) {
            for (cell in row) {
                if (!cell.isMine && !cell.isOpened) {
                    closedSafeCells++
                }
            }
        }
        if (closedSafeCells == 0 && !isGameOver.value) {
            isGameWon.value = true
        }
    }

    private fun revealAllMines() {
        for (row in board.value) {
            for (cell in row) {
                if (cell.isMine) {
                    cell.isOpened = true
                }
            }
        }
    }
}