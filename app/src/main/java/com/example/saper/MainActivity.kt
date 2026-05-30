package com.example.saper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saper.ui.screens.GameScreen
import com.example.saper.ui.screens.MainMenuScreen
import com.example.saper.ui.screens.SettingsScreen
import com.example.saper.ui.theme.SaperTheme
import com.example.saper.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaperTheme {
                // Создаем один экземпляр ViewModel для всего приложения
                val gameViewModel: GameViewModel = viewModel()

                // Состояние текущего экрана
                var currentScreen by remember { mutableStateOf("MENU") }

                // Переключение между экранами
                when (currentScreen) {
                    "MENU" -> MainMenuScreen(
                        onStartGame = {
                            gameViewModel.restartGame() // Сбрасываем игру при старте
                            currentScreen = "GAME"
                        },
                        onSettings = { currentScreen = "SETTINGS" }
                    )

                    "GAME" -> GameScreen(
                        viewModel = gameViewModel,
                        onBack = { currentScreen = "MENU" }
                    )

                    "SETTINGS" -> SettingsScreen(
                        onBack = { currentScreen = "MENU" },
                        viewModel = gameViewModel // Передаем viewModel для изменения размера поля
                    )
                }
            }
        }
    }
}