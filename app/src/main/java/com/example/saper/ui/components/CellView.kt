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
import com.example.saper.data.models.Cell
import com.example.saper.data.models.GameState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CellView(
    cell: Cell,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    gameState: GameState
) {
    val backgroundColor = when {
        // Если игра проиграна и это мина - показываем красным
        gameState == GameState.LOST && cell.isMine -> Color.Red
        // Если клетка открыта и это мина
        cell.isOpened && cell.isMine -> Color.Red
        // Обычная открытая клетка
        cell.isOpened -> MaterialTheme.colorScheme.surfaceVariant
        // Закрытая клетка
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(38.dp)
            .background(backgroundColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when {
                // Показываем флаг если он установлен и игра не проиграна (или проиграна но не мина)
                cell.isFlagged && gameState != GameState.LOST -> "🚩"
                // При проигрыше показываем мины
                gameState == GameState.LOST && cell.isMine -> "💣"
                // Если клетка открыта и это мина
                cell.isOpened && cell.isMine -> "💣"
                // Обычная открытая клетка с цифрой
                cell.isOpened && cell.nearbyMines > 0 -> cell.nearbyMines.toString()
                // Пустая открытая клетка
                cell.isOpened -> ""
                // Закрытая клетка
                else -> ""
            },
            color = when (cell.nearbyMines) {
                1 -> Color.Blue
                2 -> Color.Green
                3 -> Color.Red
                4 -> Color(0xFF000080)
                5 -> Color(0xFF8B4513)
                6 -> Color.Cyan
                7 -> Color.Black
                8 -> Color.Gray
                else -> MaterialTheme.colorScheme.onSurface
            },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}