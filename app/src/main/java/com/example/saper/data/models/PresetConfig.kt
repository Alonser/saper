package com.example.saper.data.models

data class PresetConfig(
    val width: Int,
    val height: Int,
    val mines: Int
) {
    companion object {
        val EASY = PresetConfig(9, 9, 10)
        val NORMAL = PresetConfig(16, 16, 40)
        // Для больших сеток LazyVerticalGrid отлично справится без зависаний
        val HARD = PresetConfig(30, 16, 99)
    }
}