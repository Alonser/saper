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

    // Адаптивный размер клеток в зависимости от количества
    val cellSize = 38.dp

    val backgroundColor = when {
        gameState == GameState.LOST && cell.isMine -> Color(0xFFFF4444)
        cell.isOpened -> {
            when {
                cell.isMine -> Color(0xFFFF0000)
                cell.nearbyMines > 0 -> Color(0xFFE0E0E0)
                else -> Color(0xFFF5F5F5)
            }
        }
        else -> Color(0xFF9E9E9E)
    }

    val textColor = when (cell.nearbyMines) {
        1 -> Color(0xFF1976D2)
        2 -> Color(0xFF388E3C)
        3 -> Color(0xFFD32F2F)
        4 -> Color(0xFF7B1FA2)
        5 -> Color(0xFFF57C00)
        6 -> Color(0xFF00BCD4)
        7 -> Color.Black
        8 -> Color(0xFF757575)
        else -> Color.Black
    }

    val fontSize = when {
        cell.nearbyMines > 0 -> 16.sp
        else -> 14.sp
    }

    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(cellSize)
            .background(backgroundColor)
            .scale(scale)
            .combinedClickable(
                onClick = {
                    if (gameState == GameState.PLAYING && !cell.isOpened) {
                        isOpening = true
                        onClick()
                    } else if (gameState != GameState.PLAYING && cell.isMine && !cell.isOpened) {
                        isOpening = true
                    }
                },
                onLongClick = {
                    if (gameState == GameState.PLAYING && !cell.isOpened) {
                        onLongClick()
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = Triple(cell.isOpened, cell.isFlagged, gameState),
            transitionSpec = {
                fadeIn(animationSpec = tween(150)) +
                        scaleIn(initialScale = 0.8f, animationSpec = tween(150)) with
                        fadeOut(animationSpec = tween(100))
            }
        ) { (isOpened, isFlagged, state) ->
            when {
                isFlagged && state == GameState.PLAYING && !isOpened -> {
                    Text(
                        text = "🚩",
                        fontSize = fontSize,
                        modifier = Modifier.scale(if (isOpening) 1.2f else 1f)
                    )
                }
                state == GameState.LOST && cell.isMine -> {
                    Text(
                        text = "💣",
                        fontSize = fontSize,
                        color = Color.White,
                        modifier = Modifier.scale(if (isOpening) 1.3f else 1f)
                    )
                }
                isOpened && cell.isMine -> {
                    Text(
                        text = "💣",
                        fontSize = fontSize,
                        color = Color.White
                    )
                }
                isOpened && cell.nearbyMines > 0 -> {
                    Text(
                        text = cell.nearbyMines.toString(),
                        color = textColor,
                        fontSize = fontSize,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                else -> {}
            }
        }
    }
}