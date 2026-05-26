package com.example.saper.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
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
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (cell.isRevealed) {
        if (cell.isMine) Color.Red else Color(0xFFE0E0E0)
    } else {
        Color(0xFFBDBDBD)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(1.dp, Color.Gray)
            .background(backgroundColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (cell.isRevealed) {
            if (cell.isMine) {
                Text(text = "💣", fontSize = 16.sp)
            } else if (cell.minesAround > 0) {
                Text(
                    text = cell.minesAround.toString(),
                    color = getNumberColor(cell.minesAround),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        } else if (cell.isFlagged) {
            Text(text = "🚩", fontSize = 16.sp)
        }
    }
}

private fun getNumberColor(count: Int): Color {
    return when (count) {
        1 -> Color.Blue
        2 -> Color(0xFF008000) // Зеленый
        3 -> Color.Red
        4 -> Color(0xFF000080) // Темно-синий
        5 -> Color(0xFF800000) // Бордовый
        6 -> Color(0xFF008080) // Бирюзовый
        7 -> Color.Black
        8 -> Color.DarkGray
        else -> Color.Black
    }
}