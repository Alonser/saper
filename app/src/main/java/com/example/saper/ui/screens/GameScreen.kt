package com.example.saper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.data.models.GameState
import com.example.saper.ui.components.CellView
import com.example.saper.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    // Просто читаем значения напрямую, без делегата
    val gameState = viewModel.gameState
    val minesLeft = viewModel.minesLeft
    val elapsedTime = viewModel.elapsedTime

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC0C0C0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Верхняя панель с кнопкой "Назад"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(onClick = onBack) {
                Text("◀ МЕНЮ")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Панель управления (смайлик, мины, таймер)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFC0C0C0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .minesweeperPanelBorder(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Счётчик мин
                RetroCounter(
                    value = minesLeft,
                    modifier = Modifier.weight(1f)
                )

                // Смайлик (кнопка рестарта)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { viewModel.restartGame() }
                        .minesweeperSmileBorder(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (gameState) {
                            GameState.WON -> "😎"
                            GameState.LOST -> "💀"
                            else -> "🙂"
                        },
                        fontSize = 32.sp
                    )
                }

                // Таймер
                RetroCounter(
                    value = elapsedTime,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Игровое поле
        if (viewModel.board.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(viewModel.cols),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                items(viewModel.board.flatten()) { cell ->
                    CellView(
                        cell = cell,
                        onClick = { viewModel.openCell(cell.row, cell.col) },
                        onLongClick = { viewModel.toggleFlag(cell.row, cell.col) }
                    )
                }
            }
        }

        // Сообщение о конце игры
        when (gameState) {
            GameState.LOST -> {
                Spacer(modifier = Modifier.height(16.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color.Red)) {
                    Text(
                        text = "💥 ВЗОРВАЛИСЬ! 💥",
                        modifier = Modifier.padding(12.dp),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            GameState.WON -> {
                Spacer(modifier = Modifier.height(16.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color.Green)) {
                    Text(
                        text = "🏆 ПОБЕДА! 🏆",
                        modifier = Modifier.padding(12.dp),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
fun RetroCounter(value: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.Black)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = value.toString().padStart(3, '0'),
            color = Color.Red,
            fontSize = 28.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}

fun Modifier.minesweeperPanelBorder() = this.drawBehind {
    val thickness = 3.dp.toPx()
    drawLine(Color.White, Offset(0f, 0f), Offset(size.width, 0f), thickness)
    drawLine(Color.White, Offset(0f, 0f), Offset(0f, size.height), thickness)
    drawLine(Color.DarkGray, Offset(size.width, 0f), Offset(size.width, size.height), thickness)
    drawLine(Color.DarkGray, Offset(0f, size.height), Offset(size.width, size.height), thickness)
}

fun Modifier.minesweeperSmileBorder() = this.drawBehind {
    val thickness = 2.dp.toPx()
    drawLine(Color.DarkGray, Offset(0f, 0f), Offset(size.width, 0f), thickness)
    drawLine(Color.DarkGray, Offset(0f, 0f), Offset(0f, size.height), thickness)
    drawLine(Color.White, Offset(size.width, 0f), Offset(size.width, size.height), thickness)
    drawLine(Color.White, Offset(0f, size.height), Offset(size.width, size.height), thickness)
}