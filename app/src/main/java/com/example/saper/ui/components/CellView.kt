package com.example.saper.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.data.models.Cell
import com.example.saper.data.models.GameState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CellView(
    cell: Cell,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    gameState: GameState,
    rows: Int
) {

    val cellSize = when {
        rows >= 30 -> 18.dp
        rows >= 20 -> 24.dp
        rows >= 15 -> 30.dp
        else -> 38.dp
    }

    val backgroundColor = when {

        gameState == GameState.LOST && cell.isMine ->
            Color.Red

        cell.isOpened ->
            Color(0xFF616161)

        else ->
            Color(0xFF2C2C2C)
    }

    val textColor = when (cell.nearbyMines) {
        1 -> Color(0xFF64B5F6)
        2 -> Color(0xFF81C784)
        3 -> Color(0xFFE57373)
        4 -> Color(0xFFBA68C8)
        5 -> Color(0xFFFFB74D)
        6 -> Color(0xFF4DD0E1)
        7 -> Color.White
        8 -> Color.LightGray
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(cellSize)
            .background(backgroundColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),

        contentAlignment = Alignment.Center
    ) {

        when {

            cell.isFlagged &&
                    !cell.isOpened &&
                    gameState == GameState.PLAYING -> {

                Text("🚩", fontSize = 14.sp)
            }

            cell.isOpened && cell.isMine -> {

                Text("💣", fontSize = 14.sp)
            }

            cell.isOpened &&
                    cell.nearbyMines > 0 -> {

                Text(
                    text = cell.nearbyMines.toString(),
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}