package com.example.saper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import com.example.saper.ui.components.MinesweeperButton

fun Modifier.retroBorder(
    thickness: Dp = 4.dp,
    lightColor: Color = Color(0xFFFFFFFF),
    darkColor: Color = Color(0xFF808080)
) = this.drawBehind {
    val t = thickness.toPx()
    drawLine(lightColor, Offset(0f, 0f), Offset(size.width, 0f), t)
    drawLine(lightColor, Offset(0f, 0f), Offset(0f, size.height), t)
    drawLine(darkColor, Offset(size.width, 0f), Offset(size.width, size.height), t)
    drawLine(darkColor, Offset(0f, size.height), Offset(size.width, size.height), t)
}

@Composable
fun MainMenuScreen(
    onStartGame: () -> Unit,
    onSettings: () -> Unit,
    onRecords: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC0C0C0))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Заголовок с 3D рамкой
        Box(
            modifier = Modifier
                .padding(bottom = 64.dp)
                .retroBorder(thickness = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "САПЁР",
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
            )
        }

        // Кнопка GO
        MinesweeperButton(
            text = "GO",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            onClick = onStartGame
        )

        // Кнопка РЕКОРДЫ
        MinesweeperButton(
            text = "РЕКОРДЫ",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            onClick = onRecords
        )

        // Кнопка НАСТРОЙКИ
        MinesweeperButton(
            text = "НАСТРОЙКИ",
            modifier = Modifier.fillMaxWidth(0.7f),
            onClick = onSettings
        )
    }
}