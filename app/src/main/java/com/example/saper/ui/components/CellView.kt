package com.example.saper.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
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
fun CellView(cell: Cell, onClick: () -> Unit, onLongClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .border(width = 1.dp, Color.DarkGray)
            .background(color = if (cell.isOpened) Color(0xFFE0E0E0) else Color(0xFFC0C0C0))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            cell.isOpened && cell.isMine -> {
                Text(text = "💣", fontSize = 20.sp)
            }
            cell.isOpened && !cell.isMine && cell.adjacentMines > 0 -> {
                Text(
                    text = cell.adjacentMines.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = getNumberColor(cell.adjacentMines)
                )
            }
            !cell.isOpened && cell.isFlagged -> {
                Text(text = "🚩", fontSize = 20.sp)
            }
        }
    }
}

fun getNumberColor(number: Int): Color = when (number) {
    1 -> Color.Blue
    2 -> Color(0xFF008000)
    3 -> Color.Red
    4 -> Color(0xFF000080)
    5 -> Color(0xFF800000)
    6 -> Color(0xFF008080)
    7 -> Color.Black
    8 -> Color.DarkGray
    else -> Color.Black
}