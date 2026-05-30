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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.data.models.Cell

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CellView(cell: Cell, onClick: () -> Unit, onLongClick: () -> Unit) {
    Box(
        modifier = Modifier.size(36.dp)
            .background(if (cell.isOpened) Color(0xFFD0D0D0) else Color(0xFFC0C0C0))
            .then(if (!cell.isOpened) Modifier.minesweeper3DBorder() else Modifier.border(0.5.dp, Color.Gray))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        contentAlignment = Alignment.Center
    ) {
        if (cell.isOpened) {
            if (cell.isMine) Text("💣", fontSize = 20.sp)
            else if (cell.adjacentMines > 0) Text(cell.adjacentMines.toString(), fontSize = 22.sp)
        } else if (cell.isFlagged) {
            Text("🚩", fontSize = 18.sp)
        }
    }
}