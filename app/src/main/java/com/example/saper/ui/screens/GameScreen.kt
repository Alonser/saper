package com.example.saper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.ui.components.CellView
import com.example.saper.ui.components.MinesweeperButton
import com.example.saper.ui.components.minesweeper3DBorder
import com.example.saper.viewmodel.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFC0C0C0)).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // Верхняя панель
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            MinesweeperButton(text = "МЕНЮ", onClick = onBack, modifier = Modifier.height(40.dp))

            // Смайл-кнопка (Рестарт)
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFC0C0C0)).minesweeper3DBorder().clickable { viewModel.restartGame() }, contentAlignment = Alignment.Center) {
                Text(text = if (viewModel.isGameOver.value) "😵" else "🙂", fontSize = 20.sp)
            }

            Text(text = String.format("%03d", viewModel.minesLeft.value), fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.Red, modifier = Modifier.background(Color.Black).padding(horizontal = 8.dp))
        }

        // Поле
        Box(modifier = Modifier.background(Color.Gray).padding(2.dp)) {
            LazyColumn {
                items(viewModel.rows) { r ->
                    LazyRow {
                        items(viewModel.cols) { c ->
                            CellView(cell = viewModel.board.value[r][c], onClick = { viewModel.openCell(r, c) }, onLongClick = { viewModel.toggleFlag(r, c) })
                        }
                    }
                }
            }
        }
    }
}