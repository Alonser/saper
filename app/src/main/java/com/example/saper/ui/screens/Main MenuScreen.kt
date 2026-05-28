package com.example.saper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.data.models.PresetConfig
import com.example.saper.data.models.ScreenState
import com.example.saper.viewmodel.GameViewModel

@Composable
fun MainMenuScreen(viewModel: GameViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "САПЁР",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Button(
            onClick = {
                viewModel.resetGame(PresetConfig.EASY)
                viewModel.navigateTo(ScreenState.GAME)
            },
            modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)
        ) {
            Text("Новичок (9x9)", fontSize = 18.sp)
        }

        Button(
            onClick = {
                viewModel.resetGame(PresetConfig.NORMAL)
                viewModel.navigateTo(ScreenState.GAME)
            },
            modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)
        ) {
            Text("Любитель (16x16)", fontSize = 18.sp)
        }

        Button(
            onClick = {
                viewModel.resetGame(PresetConfig.HARD)
                viewModel.navigateTo(ScreenState.GAME)
            },
            modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)
        ) {
            Text("Профи (30x16)", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = { viewModel.navigateTo(ScreenState.SETTINGS) },
            modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)
        ) {
            Text("Своя игра (Настройки)", fontSize = 18.sp)
        }
    }
}