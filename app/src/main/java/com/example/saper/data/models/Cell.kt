package com.example.saper.data.models

data class Cell(
    val x: Int,
    val y: Int,
    val isMine: Boolean = false,
    val isRevealed: Boolean = false,
    val isFlagged: Boolean = false,
    val minesAround: Int = 0
)