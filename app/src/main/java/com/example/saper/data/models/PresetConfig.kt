package com.example.saper.data.models

data class PresetConfig(
    val width: Int,
    val height: Int,
    val mines: Int
) {
    companion object {
        val EASY = PresetConfig(width = 9, height = 9, mines = 10)
        val NORMAL = PresetConfig(width = 16, height = 16, mines = 40)
        val HARD = PresetConfig(width = 30, height = 16, mines = 99)
    }
}