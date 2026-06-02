package com.example.saper.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.saper.viewmodel.GameViewModel

@Composable
fun SettingsScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFC0C0C0)).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("НАСТРОЙКИ", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(16.dp))

        val btnMod = Modifier.fillMaxWidth().padding(8.dp).background(Color(0xFFC0C0C0)).border(2.dp, Color.White).border(2.dp, Color.Gray).padding(20.dp)

        Box(btnMod.clickable { viewModel.setDifficulty("EASY") }) { Text("ЛЁГКИЙ (9x9, 10 мин)") }
        Box(btnMod.clickable { viewModel.setDifficulty("NORMAL") }) { Text("СРЕДНИЙ (16x16, 40 мин)") }
        Box(btnMod.clickable { viewModel.setDifficulty("HARD") }) { Text("СЛОЖНЫЙ (30x16, 99 мин)") }

        Spacer(Modifier.height(24.dp))

        Text("ПРАВИЛА ИГРЫ", fontWeight = FontWeight.Bold)
        Column(modifier = Modifier.background(Color(0xFFF5F5F5)).border(1.dp, Color.Gray).padding(12.dp)) {
            Text("• Нажмите на ячейку - открыть")
            Text("• Долгое нажатие - флаг 🚩")
            Text("• Цифра - количество мин вокруг")
        }

        Spacer(Modifier.height(32.dp))
        Box(btnMod.clickable { onBack() }) { Text("НАЗАД В МЕНЮ") }
    }
}