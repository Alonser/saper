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
    var minesLeft = mutableStateOf(10)
    var isGameOver = mutableStateOf(false)

    init { restartGame() }

    fun updateDifficulty(r: Int, c: Int) {
        rows = r; cols = c; minesCount = (r * c * 0.15).toInt().coerceAtLeast(1)
        restartGame()
    }

    fun restartGame() {
        val newBoard = MutableList(rows) { r -> MutableList(cols) { c -> Cell(r, c) } }
        var placed = 0
        while (placed < minesCount) {
            val r = Random.nextInt(rows); val c = Random.nextInt(cols)
            if (!newBoard[r][c].isMine) { newBoard[r][c].isMine = true; placed++ }
        }
        for (r in 0 until rows) for (c in 0 until cols)
            if (!newBoard[r][c].isMine) newBoard[r][c].adjacentMines = countMines(newBoard, r, c)

        board.value = newBoard; minesLeft.value = minesCount; isGameOver.value = false
    }

    private fun countMines(b: List<List<Cell>>, r: Int, c: Int): Int {
        var count = 0
        for (dr in -1..1) for (dc in -1..1)
            if ((r+dr) in 0 until rows && (c+dc) in 0 until cols && b[r+dr][c+dc].isMine) count++
        return count
    }

    fun openCell(r: Int, c: Int) {
        if (isGameOver.value || board.value[r][c].isOpened || board.value[r][c].isFlagged) return
        val cell = board.value[r][c]
        cell.isOpened = true
        if (cell.isMine) { isGameOver.value = true; revealAllMines() }
        else if (cell.adjacentMines == 0) {
            for (dr in -1..1) for (dc in -1..1)
                if ((r+dr) in 0 until rows && (c+dc) in 0 until cols) openCell(r+dr, c+dc)
        }
    }

    private fun revealAllMines() {
        for (row in board.value) for (cell in row) if (cell.isMine) cell.isOpened = true
    }

    fun toggleFlag(r: Int, c: Int) {
        val cell = board.value[r][c]
        if (!cell.isOpened) { cell.isFlagged = !cell.isFlagged; minesLeft.value += if (cell.isFlagged) -1 else 1 }
    }
}