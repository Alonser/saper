package com.example.saper.data.models

data class Cell(
    val row: Int,
    val col: Int,
    var isMine: Boolean = false,
    var isOpened: Boolean = false,
    var isFlagged: Boolean = false,
    var nearbyMines: Int = 0
)