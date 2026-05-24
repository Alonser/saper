package com.example.saper.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.data.models.Cell
import com.example.saper.data.models.GameState

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun CellView(
    cell: Cell,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    gameState: GameState
) {
    var isOpening by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isOpening) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { isOpening = false }
    )

    // Адаптивный размер клеток
    val cellSize = 40.dp

    // Цвет фона клетки
    val backgroundColor = when {
        // Если игра проиграна и это мина
        gameState == GameState.LOST && cell.isMine -> Color(0xFFFF4444)
        // Если клетка открыта
        cell.isOpened -> {
            when {
                cell.isMine -> Color(0xFFFF0000)
                cell.nearbyMines > 0 -> Color(0xFFE0E0E0)
                else -> Color(0xFFF5F5F5)
            }
        }
        // Закрытая клетка
        else -> Color(0xFF9E9E9E)
    }

    // Цвет текста для цифр
    val textColor = when (cell.nearbyMines) {
        1 -> Color(0xFF1976D2) // Синий
        2 -> Color(0xFF388E3C) // Зеленый
        3 -> Color(0xFFD32F2F) // Красный
        4 -> Color(0xFF7B1FA2) // Фиолетовый
        5 -> Color(0xFFF57C00) // Оранжевый
        6 -> Color(0xFF00BCD4) // Бирюзовый
        7 -> Color.Black
        8 -> Color(0xFF757575) // Серый
        else -> Color.Black
    }

    // Размер шрифта
    val fontSize = when {
        cell.nearbyMines > 0 -> 18.sp
        else -> 16.sp
    }

    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(cellSize)
            .background(backgroundColor)
            .scale(scale)
            .combinedClickable(
                onClick = {
                    // Открываем клетку только если игра активна, клетка не открыта и не отмечена флагом
                    if (gameState == GameState.PLAYING && !cell.isOpened && !cell.isFlagged) {
                        isOpening = true
                        onClick()
                    }
                    // Если игра проиграна и это мина - показываем анимацию
                    else if (gameState != GameState.PLAYING && cell.isMine && !cell.isOpened) {
                        isOpening = true
                    }
                },
                onLongClick = {
                    // Устанавливаем/снимаем флаг только если игра активна и клетка не открыта
                    if (gameState == GameState.PLAYING && !cell.isOpened) {
                        onLongClick()
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Анимированное содержимое клетки
        AnimatedContent(
            targetState = Triple(cell.isOpened, cell.isFlagged, gameState),
            transitionSpec = {
                fadeIn(animationSpec = tween(150)) +
                        scaleIn(initialScale = 0.8f, animationSpec = tween(150)) with
                        fadeOut(animationSpec = tween(100))
            }
        ) { (isOpened, isFlagged, state) ->
            when {
                // Показываем флаг (только если игра активна и клетка не открыта)
                isFlagged && state == GameState.PLAYING && !isOpened -> {
                    Text(
                        text = "🚩",
                        fontSize = fontSize,
                        modifier = Modifier.scale(if (isOpening) 1.2f else 1f)
                    )
                }
                // При проигрыше показываем все мины
                state == GameState.LOST && cell.isMine -> {
                    Text(
                        text = "💣",
                        fontSize = fontSize,
                        color = Color.White,
                        modifier = Modifier.scale(if (isOpening) 1.3f else 1f)
                    )
                }
                // Открытая клетка с миной
                isOpened && cell.isMine -> {
                    Text(
                        text = "💣",
                        fontSize = fontSize,
                        color = Color.White
                    )
                }
                // Открытая клетка с цифрой
                isOpened && cell.nearbyMines > 0 -> {
                    Text(
                        text = cell.nearbyMines.toString(),
                        color = textColor,
                        fontSize = fontSize,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                // Открытая пустая клетка (не показываем ничего)
                isOpened -> {
                    Text(
                        text = "",
                        fontSize = fontSize
                    )
                }
                // Закрытая клетка (не показываем ничего)
                else -> {
                    Text(
                        text = "",
                        fontSize = fontSize
                    )
                }
            }
        }
    }
}