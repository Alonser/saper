package com.example.saper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saper.data.models.ScreenState
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
                SaperApp()
            }
        }
    }
}

@Composable
fun SaperApp(viewModel: GameViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Scaffold создает базовый каркас приложения с местом для верхней и нижней панели
    Scaffold(
        bottomBar = {
            NavigationBar {
                // Кнопка "Меню"
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Меню") },
                    label = { Text("Меню") },
                    selected = currentScreen == ScreenState.MENU,
                    onClick = { viewModel.navigateTo(ScreenState.MENU) }
                )
                // Кнопка "Игра"
                NavigationBarItem(
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Игра") },
                    label = { Text("Игра") },
                    selected = currentScreen == ScreenState.GAME,
                    onClick = { viewModel.navigateTo(ScreenState.GAME) }
                )
                // Кнопка "Настройки"
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Настройки") },
                    label = { Text("Настройки") },
                    selected = currentScreen == ScreenState.SETTINGS,
                    onClick = { viewModel.navigateTo(ScreenState.SETTINGS) }
                )
            }
        }
    ) { innerPadding ->
        // Surface содержит сами экраны и отступает от нижней панели (innerPadding)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // Это важно, чтобы панель не перекрывала игру
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                ScreenState.MENU -> MainMenuScreen(viewModel)
                ScreenState.SETTINGS -> SettingsScreen(viewModel)
                ScreenState.GAME -> GameScreen(viewModel)
            }
        }
    }
}