package com.example.saper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.ui.components.MinesweeperButton
import com.example.saper.viewmodel.GameViewModel

@Composable
fun SettingsScreen(onBack: () -> Unit, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC0C0C0))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("НАСТРОЙКИ", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(24.dp))

        // Выбор размера поля
        Text("Размер поля:", fontSize = 18.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MinesweeperButton(text = "9x9", onClick = { viewModel.rows = 9; viewModel.cols = 9; viewModel.restartGame() })
            MinesweeperButton(text = "16x16", onClick = { viewModel.rows = 16; viewModel.cols = 16; viewModel.restartGame() })
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Правила игры
        Text("ПРАВИЛА ИГРЫ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "1. Открывайте ячейки, избегая мин.\n" +
                    "2. Цифра показывает, сколько мин вокруг ячейки.\n" +
                    "3. Долгое нажатие ставит флажок на подозрительное место.\n" +
                    "4. Ваша цель — открыть все безопасные ячейки!",
            fontSize = 14.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.weight(1f))
        MinesweeperButton(text = "НАЗАД", modifier = Modifier.fillMaxWidth(), onClick = onBack)
    }
}