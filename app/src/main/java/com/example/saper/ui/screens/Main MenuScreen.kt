package com.example.saper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Функция ретро-рамки добавлена прямо сюда, чтобы исправить ошибку Unresolved reference
fun Modifier.retroBorder(thickness: Dp = 2.dp) = this.drawBehind {
    val t = thickness.toPx()
    val light = Color(0xFFFFFFFF)
    val dark = Color(0xFF808080)

    drawLine(light, Offset(0f, 0f), Offset(size.width, 0f), t)
    drawLine(light, Offset(0f, 0f), Offset(0f, size.height), t)
    drawLine(dark, Offset(size.width, 0f), Offset(size.width, size.height), t)
    drawLine(dark, Offset(0f, size.height), Offset(size.width, size.height), t)
}

@Composable
fun MainMenuScreen(onStart: () -> Unit, onSettings: () -> Unit, onRecords: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC0C0C0))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("САПЁР", modifier = Modifier.padding(bottom = 64.dp))

        // Кнопки с использованием retroBorder
        val btnMod = Modifier
            .fillMaxWidth(0.7f)
            .background(Color(0xFFC0C0C0))
            .retroBorder(thickness = 3.dp)
            .padding(16.dp)

        Box(btnMod.clickable { onStart() }, contentAlignment = Alignment.Center) {
            Text("GO")
        }
        Spacer(Modifier.height(16.dp))

        Box(btnMod.clickable { onRecords() }, contentAlignment = Alignment.Center) {
            Text("РЕКОРДЫ")
        }
        Spacer(Modifier.height(16.dp))

        Box(btnMod.clickable { onSettings() }, contentAlignment = Alignment.Center) {
            Text("НАСТРОЙКИ")
        }
    }
}