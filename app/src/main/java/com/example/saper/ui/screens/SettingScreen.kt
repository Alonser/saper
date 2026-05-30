package com.example.saper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.data.models.PresetConfig
import com.example.saper.ui.components.MinesweeperButton
import com.example.saper.viewmodel.GameViewModel

@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC0C0C0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "НАСТРОЙКИ",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "СЛОЖНОСТЬ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    MinesweeperButton(
                        text = "ЛЁГКИЙ (9x9, 10 мин)",
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        onClick = {
                            viewModel.updateDifficulty(PresetConfig.EASY.width, PresetConfig.EASY.height)
                        }
                    )

                    MinesweeperButton(
                        text = "СРЕДНИЙ (16x16, 40 мин)",
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        onClick = {
                            viewModel.updateDifficulty(PresetConfig.NORMAL.width, PresetConfig.NORMAL.height)
                        }
                    )

                    MinesweeperButton(
                        text = "СЛОЖНЫЙ (30x16, 99 мин)",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.updateDifficulty(PresetConfig.HARD.width, PresetConfig.HARD.height)
                        }
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📖 ПРАВИЛА ИГРЫ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "• Нажмите на ячейку, чтобы открыть её",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = "• Долгое нажатие ставит флаг 🚩",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = "• Цифра показывает количество мин вокруг",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = "• Откройте все ячейки без мин, чтобы победить",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = "• Первый ход всегда безопасный!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF008000),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "💡 Совет: Если открылась пустая ячейка, все соседние откроются автоматически!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF000080),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        item {
            MinesweeperButton(
                text = "НАЗАД В МЕНЮ",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(vertical = 16.dp),
                onClick = onBack
            )
        }
    }
}