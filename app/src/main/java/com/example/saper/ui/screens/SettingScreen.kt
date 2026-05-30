package com.example.saper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.saper.viewmodel.GameViewModel

@Composable
fun SettingsScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Настройки поля", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { viewModel.updateDifficulty(9, 9) }, modifier = Modifier.fillMaxWidth(0.7f)) {
            Text("Легко (9x9)")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.updateDifficulty(16, 16) }, modifier = Modifier.fillMaxWidth(0.7f)) {
            Text("Сложно (16x16)")
        }
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(onClick = onBack) {
            Text("Назад")
        }
    }
}