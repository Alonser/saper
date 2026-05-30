package com.example.saper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Модификатор для 3D-рамки
fun Modifier.minesweeper3DBorder(
    thickness: Dp = 3.dp,
    highlightColor: Color = Color(0xFFFFFFFF), // Белый блик
    shadowColor: Color = Color(0xFF808080)     // Темно-серая тень
) = this.drawBehind {
    val pxThickness = thickness.toPx()
    drawLine(color = highlightColor, start = Offset(0f, 0f), end = Offset(size.width, 0f), strokeWidth = pxThickness)
    drawLine(color = highlightColor, start = Offset(0f, 0f), end = Offset(0f, size.height), strokeWidth = pxThickness)
    drawLine(color = shadowColor, start = Offset(size.width, 0f), end = Offset(size.width, size.height), strokeWidth = pxThickness)
    drawLine(color = shadowColor, start = Offset(0f, size.height), end = Offset(size.width, size.height), strokeWidth = pxThickness)
}

// 2. Ретро-кнопка (теперь с onClick!)
@Composable
fun MinesweeperButton(
    icon: ImageVector? = null,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit // Обработчик нажатия
) {
    Box(
        modifier = modifier
            .background(Color(0xFFC0C0C0))
            .minesweeper3DBorder()
            .clickable(onClick = onClick), // Делаем кнопку кликабельной
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).padding(end = 8.dp),
                    tint = Color.Black
                )
            }
            Text(
                text = text,
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 3. Ретро-плитка (можно будет использовать вместо CellView позже)
@Composable
fun MinesweeperTile(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(Color(0xFFC0C0C0))
            .minesweeper3DBorder(),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}