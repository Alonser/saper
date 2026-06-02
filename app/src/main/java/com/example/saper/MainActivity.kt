package com.example.saper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.data.repository.RecordRepository
import com.example.saper.database.AppDatabase
import com.example.saper.ui.components.MinesweeperButton
import com.example.saper.ui.screens.GameScreen
import com.example.saper.ui.screens.RecordsScreen
import com.example.saper.ui.screens.SettingsScreen
import com.example.saper.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    // Инициализируем ViewModel один раз на уровне Activity
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Инициализация базы данных Room
        val database = AppDatabase.getDatabase(this)

        // 2. Создание репозитория и передача в него корректного объекта DAO
        val recordRepository = RecordRepository(database.recordDao())

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Переменная состояния для управления экранами
                    var currentScreen by remember { mutableStateOf("MENU") }

                    // Простая и надежная логика переключения экранов
                    when (currentScreen) {
                        "MENU" -> {
                            MainMenuScreen(
                                onStartGame = {
                                    gameViewModel.restartGame() // Сбрасываем поле перед новой игрой
                                    currentScreen = "GAME"
                                },
                                onOpenRecords = { currentScreen = "RECORDS" },
                                onOpenSettings = { currentScreen = "SETTINGS" }
                            )
                        }
                        "GAME" -> {
                            GameScreen(
                                viewModel = gameViewModel,
                                onBack = { currentScreen = "MENU" }
                            )
                        }
                        "RECORDS" -> {
                            // Передаем репозиторий, который теперь инициализирован правильно
                            RecordsScreen(
                                recordRepository = recordRepository,
                                onBack = { currentScreen = "MENU" }
                            )
                        }
                        "SETTINGS" -> {
                            SettingsScreen(
                                viewModel = gameViewModel,
                                onBack = { currentScreen = "MENU" }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Компонент Главного Меню игры (в стиле Сапера)
@Composable
fun MainMenuScreen(
    onStartGame: () -> Unit,
    onOpenRecords: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC0C0C0)) // Ретро серый фон
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = " САПЁР ",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        MinesweeperButton(
            text = "ИГРАТЬ",
            modifier = Modifier.fillMaxWidth(0.7f).padding(vertical = 8.dp),
            onClick = onStartGame
        )

        MinesweeperButton(
            text = "РЕКОРДЫ",
            modifier = Modifier.fillMaxWidth(0.7f).padding(vertical = 8.dp),
            onClick = onOpenRecords
        )

        MinesweeperButton(
            text = "НАСТРОЙКИ",
            modifier = Modifier.fillMaxWidth(0.7f).padding(vertical = 8.dp),
            onClick = onOpenSettings
        )
    }
}