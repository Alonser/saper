package com.example.saper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.ui.components.MinesweeperButton

@Composable
fun MainMenuScreen(
    onStartGame: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC0C0C0))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Сапер",
            fontSize = 56.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        MinesweeperButton(
            text = "GO",
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            onClick = onStartGame
        )

        MinesweeperButton(
            text = "НАСТРОЙКИ",
            modifier = Modifier.fillMaxWidth(),
            onClick = onSettings
        )
    }
}