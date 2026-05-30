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
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFC0C0C0)).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("НАСТРОЙКИ", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Размер поля:")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MinesweeperButton(text = "9x9", onClick = { viewModel.updateDifficulty(9, 9) })
            MinesweeperButton(text = "16x16", onClick = { viewModel.updateDifficulty(16, 16) })
        }

        Spacer(modifier = Modifier.height(40.dp))
        Text("ПРАВИЛА:", fontWeight = FontWeight.Bold)
        Text("1. Избегай мин. \n2. Цифра = количество мин вокруг. \n3. Долгое нажатие = флаг.", color = Color.DarkGray)

        Spacer(modifier = Modifier.weight(1f))
        MinesweeperButton(text = "НАЗАД", modifier = Modifier.fillMaxWidth(), onClick = onBack)
    }
}