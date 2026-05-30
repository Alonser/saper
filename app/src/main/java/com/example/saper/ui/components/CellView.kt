package com.example.saper.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.data.models.Cell

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CellView(
    cell: Cell,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val isOpened = cell.isOpened
    val isFlagged = cell.isFlagged
    val isMine = cell.isMine

    // ВНИМАНИЕ: Если горит красным, поменяй adjacentMines на то, как у тебя называется количество мин в Cell.kt (например, minesAround)
    val adjacentMines = cell.adjacentMines

    Box(
        modifier = Modifier
            .size(36.dp)
            .background(if (isOpened) Color(0xFFD0D0D0) else Color(0xFFC0C0C0))
            .then(
                if (!isOpened) {
                    Modifier.minesweeper3DBorder(thickness = 3.dp)
                } else {
                    Modifier.border(0.5.dp, Color.Gray)
                }
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isOpened) {
            if (isMine) {
                Text(text = "💣", fontSize = 20.sp)
            } else if (adjacentMines > 0) {
                val numberColor = when (adjacentMines) {
                    1 -> Color.Blue
                    2 -> Color(0xFF008000)
                    3 -> Color.Red
                    4 -> Color(0xFF000080)
                    5 -> Color(0xFF800000)
                    6 -> Color(0xFF008080)
                    7 -> Color.Black
                    else -> Color.Gray
                }
                Text(
                    text = adjacentMines.toString(),
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = numberColor
                )
            }
        } else if (isFlagged) {
            Text(text = "🚩", fontSize = 18.sp)
        }
    }
}