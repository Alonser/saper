package com.example.saper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.data.models.GameState
import com.example.saper.data.models.ScreenState
import com.example.saper.ui.components.CellView
import com.example.saper.viewmodel.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val grid by viewModel.grid.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val flagsRemaining by viewModel.flagsRemaining.collectAsState()
    val config by viewModel.config.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Шапка со счетчиком и возвратом
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { viewModel.navigateTo(ScreenState.MENU) }) {
                Text("⬅ Меню", fontSize = 18.sp)
            }

            Text("🚩 $flagsRemaining", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            val emoji = when (gameState) {
                GameState.WON -> "😎"
                GameState.LOST -> "😵"
                else -> "🙂"
            }

            Button(onClick = { viewModel.resetGame() }) {
                Text(emoji, fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Игровая сетка
        LazyVerticalGrid(
            columns = GridCells.Fixed(config.width),
            modifier = Modifier.fillMaxWidth().weight(1f) // Занимает всё доступное место
        ) {
            items(grid.flatten()) { cell ->
                CellView(
                    cell = cell,
                    onClick = { viewModel.onCellClicked(cell.x, cell.y) },
                    onLongClick = { viewModel.onCellLongClicked(cell.x, cell.y) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Статус победы/поражения
        if (gameState == GameState.WON) {
            Text("Победа!", color = MaterialTheme.colorScheme.primary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        } else if (gameState == GameState.LOST) {
            Text("Поражение!", color = MaterialTheme.colorScheme.error, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }
    }
}