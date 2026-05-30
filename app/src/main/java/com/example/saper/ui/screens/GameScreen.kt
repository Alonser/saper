package com.example.saper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.ui.components.*
import com.example.saper.viewmodel.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFC0C0C0)).padding(8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            MinesweeperButton(text = "МЕНЮ", onClick = onBack)
            Box(modifier = Modifier.size(40.dp).minesweeper3DBorder().clickable { viewModel.restartGame() }, contentAlignment = Alignment.Center) {
                Text(text = if (viewModel.isGameOver.value) "😵" else "🙂", fontSize = 20.sp)
            }
            Text(text = String.format("%03d", viewModel.minesLeft.value), fontSize = 28.sp, color = Color.Red,
                modifier = Modifier.background(Color.Black).padding(horizontal = 8.dp))
        }

        Column(modifier = Modifier.background(Color.Gray).padding(2.dp)) {
            for (r in 0 until viewModel.rows) {
                Row {
                    for (c in 0 until viewModel.cols) {
                        CellView(
                            cell = viewModel.board.value[r][c],
                            onClick = { viewModel.openCell(r, c) },
                            onLongClick = { viewModel.toggleFlag(r, c) }
                        )
                    }
                }
            }
        }
    }
}