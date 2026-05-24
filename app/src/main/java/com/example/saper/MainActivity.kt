package com.example.saper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saper.ui.screens.GameScreen
import com.example.saper.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaperApp()
        }
    }
}

@Composable
fun SaperApp() {
    val viewModel: GameViewModel = viewModel()
    val darkTheme = viewModel.isDarkTheme

    // Создаем серую светлую тему
    val lightGrayColorScheme = lightColorScheme(
        primary = Color(0xFF616161), // Серый
        onPrimary = Color.White,
        primaryContainer = Color(0xFFBDBDBD), // Светло-серый
        onPrimaryContainer = Color.Black,
        secondary = Color(0xFF757575), // Серый
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE0E0E0), // Очень светлый серый
        onSecondaryContainer = Color.Black,
        surface = Color(0xFFF5F5F5), // Светло-серый фон
        onSurface = Color.Black,
        surfaceVariant = Color(0xFFEEEEEE),
        onSurfaceVariant = Color.Black,
        error = Color(0xFFD32F2F),
        onError = Color.White,
        errorContainer = Color(0xFFFFCDD2),
        onErrorContainer = Color.Black,
        background = Color(0xFFFAFAFA), // Почти белый фон
        onBackground = Color.Black,
        outline = Color(0xFFBDBDBD)
    )

    // Темная тема (оставляем стандартную)
    val darkColorScheme = darkColorScheme()

    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme else lightGrayColorScheme,
        typography = Typography(
            titleLarge = MaterialTheme.typography.titleLarge.copy(
                color = if (darkTheme) Color.White else Color.Black
            ),
            titleMedium = MaterialTheme.typography.titleMedium.copy(
                color = if (darkTheme) Color.White else Color.Black
            ),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                color = if (darkTheme) Color.White else Color.Black
            )
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            GameScreen(viewModel)
        }
    }
}