package com.example.saper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.data.models.ScreenState
import com.example.saper.viewmodel.GameViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(viewModel: GameViewModel) {
    val width by viewModel.customWidth.collectAsState()
    val height by viewModel.customHeight.collectAsState()
    val mines by viewModel.customMines.collectAsState()

    // Максимальное количество мин зависит от размера поля (оставляем запас для 1-го клика)
    val maxPossibleMines = ((width * height) * 0.8f).coerceAtLeast(1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Своя игра", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        // Ширина
        Text("Ширина поля: ${width.roundToInt()}", fontSize = 18.sp)
        Slider(
            value = width,
            onValueChange = { viewModel.customWidth.value = it },
            valueRange = 5f..30f,
            steps = 25
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Высота
        Text("Высота поля: ${height.roundToInt()}", fontSize = 18.sp)
        Slider(
            value = height,
            onValueChange = { viewModel.customHeight.value = it },
            valueRange = 5f..30f,
            steps = 25
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Мины (динамически обновляем ползунок, если поле стало меньше)
        val currentMines = if (mines > maxPossibleMines) maxPossibleMines else mines
        LaunchedEffect(maxPossibleMines) { viewModel.customMines.value = currentMines }

        Text("Количество мин: ${currentMines.roundToInt()}", fontSize = 18.sp)
        Slider(
            value = currentMines,
            onValueChange = { viewModel.customMines.value = it },
            valueRange = 1f..maxPossibleMines
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { viewModel.startGameWithCustomSettings() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Начать игру", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { viewModel.navigateTo(ScreenState.MENU) }) {
            Text("Назад в меню", fontSize = 18.sp)
        }
    }
}