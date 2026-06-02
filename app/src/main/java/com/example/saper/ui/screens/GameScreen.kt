package com.example.saper.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.saper.viewmodel.GameViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    val board = viewModel.board.value
    val cols = viewModel.cols

    // Логика смайлика
    val smiley = when {
        viewModel.isGameOver.value -> "💀"
        viewModel.isGameWon.value -> "😎"
        else -> "🙂"
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFC0C0C0)).padding(8.dp)) {
        // Кнопка МЕНЮ
        Box(modifier = Modifier.padding(bottom = 8.dp).border(2.dp, Color.White).border(2.dp, Color.Gray).clickable { onBack() }.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("МЕНЮ", fontWeight = FontWeight.Bold)
        }

        // HUD (растянут)
        Row(modifier = Modifier.fillMaxWidth().border(2.dp, Color.Gray).border(2.dp, Color.White).padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(viewModel.minesLeft.intValue.toString().padStart(3, '0'), color = Color.Red, fontSize = 32.sp, modifier = Modifier.background(Color.Black).padding(8.dp))
            Box(modifier = Modifier.background(Color(0xFFC0C0C0)).border(2.dp, Color.White).border(2.dp, Color.Gray).padding(8.dp).clickable { viewModel.restartGame() }) {
                Text(smiley, fontSize = 24.sp)
            }
            Text(viewModel.timeSeconds.intValue.toString().padStart(3, '0'), color = Color.Red, fontSize = 32.sp, modifier = Modifier.background(Color.Black).padding(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Поле
        LazyVerticalGrid(columns = GridCells.Fixed(cols), modifier = Modifier.weight(1f).border(2.dp, Color.Gray)) {
            items(board.size * cols) { index ->
                val cell = board[index / cols][index % cols]
                Box(modifier = Modifier.size(32.dp)
                    .background(Color(0xFFC0C0C0))
                    .border(2.dp, if (cell.isOpened) Color.Gray else Color.White)
                    .border(2.dp, if (cell.isOpened) Color.Transparent else Color.Gray)
                    .combinedClickable(onClick = { viewModel.openCell(index / cols, index % cols) }, onLongClick = { viewModel.toggleFlag(index / cols, index % cols) }),
                    contentAlignment = Alignment.Center) {
                    if (cell.isOpened && !cell.isMine && cell.adjacentMines > 0) Text(cell.adjacentMines.toString(), fontWeight = FontWeight.Bold, color = if(cell.adjacentMines == 1) Color.Blue else Color.Red)
                    else if (cell.isFlagged) Text("🚩")
                    else if (cell.isOpened && cell.isMine) Text("💣")
                }
            }
        }

        // Статус игры
        if (viewModel.isGameOver.value || viewModel.isGameWon.value) {
            Text(
                text = if (viewModel.isGameWon.value) "ПОБЕДА!" else "ПОРАЖЕНИЕ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (viewModel.isGameWon.value) Color(0xFF008000) else Color.Red,
                modifier = Modifier.fillMaxWidth().padding(8.dp).border(2.dp, Color.Gray).padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}