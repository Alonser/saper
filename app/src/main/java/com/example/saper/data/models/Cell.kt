package com.example.saper.data.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Cell(
    val row: Int,
    val col: Int
) {
    var isMine by mutableStateOf(false)
    var isOpened by mutableStateOf(false)
    var isFlagged by mutableStateOf(false)
    var adjacentMines by mutableStateOf(0)
}