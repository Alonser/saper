package com.example.saper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saper.ui.screens.*
import com.example.saper.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Инициализируем ViewModel, она сама подхватит контекст приложения
            val gameViewModel: GameViewModel = viewModel()

            // Состояние экрана (MENU, GAME, SETTINGS)
            var currentScreen by remember { mutableStateOf("MENU") }

            // Навигация
            when (currentScreen) {
                "MENU" -> MainMenuScreen(
                    onStartGame = {
                        gameViewModel.restartGame()
                        currentScreen = "GAME"
                    },
                    onSettings = {
                        currentScreen = "SETTINGS"
                    }
                )
                "GAME" -> GameScreen(
                    viewModel = gameViewModel,
                    onBack = { currentScreen = "MENU" }
                )
                "SETTINGS" -> SettingsScreen(
                    viewModel = gameViewModel,
                    onBack = { currentScreen = "MENU" }
                )
            }
        }
    }
}