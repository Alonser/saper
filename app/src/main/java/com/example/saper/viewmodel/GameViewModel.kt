package com.example.saper.viewmodel

import androidx.lifecycle.ViewModel
import com.example.saper.data.models.Cell
import com.example.saper.data.models.GameState
import com.example.saper.data.models.PresetConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class GameViewModel : ViewModel() {
    private val _config = MutableStateFlow(PresetConfig.EASY)
    val config = _config.asStateFlow()

    private val _gameState = MutableStateFlow(GameState.INITIAL)
    val gameState = _gameState.asStateFlow()

    private val _grid = MutableStateFlow<List<List<Cell>>>(emptyList())
    val grid = _grid.asStateFlow()

    private val _flagsRemaining = MutableStateFlow(0)
    val flagsRemaining = _flagsRemaining.asStateFlow()

    init {
        resetGame(PresetConfig.EASY)
    }

    fun resetGame(newConfig: PresetConfig = _config.value) {
        _config.value = newConfig
        _gameState.value = GameState.INITIAL
        _flagsRemaining.value = newConfig.mines
        _grid.value = List(newConfig.height) { y ->
            List(newConfig.width) { x -> Cell(x, y) }
        }
    }

    private fun placeMines(firstX: Int, firstY: Int) {
        val currentConfig = _config.value
        val newGrid = _grid.value.map { it.toMutableList() }.toMutableList()
        var minesPlaced = 0

        while (minesPlaced < currentConfig.mines) {
            val rx = Random.nextInt(currentConfig.width)
            val ry = Random.nextInt(currentConfig.height)

            // Защита первого клика: вокруг него не должно быть мин
            if (kotlin.math.abs(rx - firstX) <= 1 && kotlin.math.abs(ry - firstY) <= 1) continue

            if (!newGrid[ry][rx].isMine) {
                newGrid[ry][rx] = newGrid[ry][rx].copy(isMine = true)
                minesPlaced++
            }
        }

        // Подсчет мин вокруг каждой клетки
        for (y in 0 until currentConfig.height) {
            for (x in 0 until currentConfig.width) {
                if (!newGrid[y][x].isMine) {
                    var count = 0
                    for (dy in -1..1) {
                        for (dx in -1..1) {
                            val ny = y + dy
                            val nx = x + dx
                            if (ny in 0 until currentConfig.height && nx in 0 until currentConfig.width && newGrid[ny][nx].isMine) {
                                count++
                            }
                        }
                    }
                    newGrid[y][x] = newGrid[y][x].copy(minesAround = count)
                }
            }
        }
        _grid.value = newGrid
    }

    fun onCellClicked(x: Int, y: Int) {
        if (_gameState.value == GameState.WON || _gameState.value == GameState.LOST) return

        val cell = _grid.value[y][x]
        if (cell.isFlagged || cell.isRevealed) return

        if (_gameState.value == GameState.INITIAL) {
            placeMines(x, y)
            _gameState.value = GameState.PLAYING
        }

        if (_grid.value[y][x].isMine) {
            revealAllMines()
            _gameState.value = GameState.LOST
            return
        }

        revealCell(x, y)
        checkWin()
    }

    fun onCellLongClicked(x: Int, y: Int) {
        if (_gameState.value != GameState.PLAYING && _gameState.value != GameState.INITIAL) return

        val newGrid = _grid.value.map { it.toMutableList() }.toMutableList()
        val cell = newGrid[y][x]

        if (!cell.isRevealed) {
            val wasFlagged = cell.isFlagged
            newGrid[y][x] = cell.copy(isFlagged = !wasFlagged)
            _grid.value = newGrid
            _flagsRemaining.update { it + if (wasFlagged) 1 else -1 }
        }
    }

    private fun revealCell(x: Int, y: Int) {
        val currentConfig = _config.value
        val newGrid = _grid.value.map { it.toMutableList() }.toMutableList()
        val queue = mutableListOf(Pair(x, y))

        // ИСПРАВЛЕНИЕ: Храним координаты клеток, которые уже добавлены в очередь
        val visited = mutableSetOf(Pair(x, y))

        while (queue.isNotEmpty()) {
            val (cx, cy) = queue.removeAt(0)
            val cell = newGrid[cy][cx]

            if (cell.isRevealed || cell.isFlagged) continue

            newGrid[cy][cx] = cell.copy(isRevealed = true)

            // Если вокруг нет мин, открываем соседние клетки
            if (cell.minesAround == 0) {
                for (dy in -1..1) {
                    for (dx in -1..1) {
                        val nx = cx + dx
                        val ny = cy + dy
                        if (ny in 0 until currentConfig.height && nx in 0 until currentConfig.width) {
                            val neighborPos = Pair(nx, ny)
                            // ИСПРАВЛЕНИЕ: Проверяем по visited, чтобы не добавлять в очередь бесконечно
                            if (!visited.contains(neighborPos) && !newGrid[ny][nx].isRevealed) {
                                visited.add(neighborPos)
                                queue.add(neighborPos)
                            }
                        }
                    }
                }
            }
        }
        _grid.value = newGrid
    }

    private fun revealAllMines() {
        _grid.value = _grid.value.map { row ->
            row.map { cell ->
                if (cell.isMine) cell.copy(isRevealed = true) else cell
            }
        }
    }

    private fun checkWin() {
        val safeUnrevealed = _grid.value.sumOf { row ->
            row.count { cell -> !cell.isMine && !cell.isRevealed }
        }
        if (safeUnrevealed == 0) {
            _gameState.value = GameState.WON
        }
    }
}