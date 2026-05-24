package com.example.saper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.saper.data.models.GameState
import com.example.saper.ui.components.CellView
import com.example.saper.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(viewModel: GameViewModel) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    var customRows by remember { mutableStateOf("9") }
    var customCols by remember { mutableStateOf("9") }
    var customMines by remember { mutableStateOf("10") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Сапёр", style = MaterialTheme.typography.titleLarge) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            actions = {
                IconButton(onClick = { viewModel.toggleTheme() }) {
                    Icon(
                        if (viewModel.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Сменить тему"
                    )
                }
                IconButton(onClick = { viewModel.restartGame() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Новая игра")
                }
                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Настройки")
                }
            }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (viewModel.gameState) {
                    GameState.PLAYING -> MaterialTheme.colorScheme.secondaryContainer
                    GameState.WON -> MaterialTheme.colorScheme.tertiaryContainer
                    GameState.LOST -> MaterialTheme.colorScheme.errorContainer
                }
            )
        ) {
            Text(
                text = when (viewModel.gameState) {
                    GameState.PLAYING -> "🎮 Игра идёт"
                    GameState.WON -> "🏆 Победа!"
                    GameState.LOST -> "💀 Поражение!"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                color = when (viewModel.gameState) {
                    GameState.PLAYING -> MaterialTheme.colorScheme.onSecondaryContainer
                    GameState.WON -> MaterialTheme.colorScheme.onTertiaryContainer
                    GameState.LOST -> MaterialTheme.colorScheme.onErrorContainer
                }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(viewModel.cols),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            items(viewModel.field.flatten()) { cell ->
                CellView(
                    cell = cell,
                    onClick = { viewModel.openCell(cell) },
                    onLongClick = { viewModel.toggleFlag(cell) }
                )
            }
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Настройки игры", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Предустановленные уровни:", style = MaterialTheme.typography.titleSmall)

                    viewModel.getPresets().forEach { preset ->
                        Button(
                            onClick = {
                                viewModel.applyPreset(preset)
                                showSettingsDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text("${preset.name} (${preset.rows}x${preset.cols}, ${preset.mines} мин)")
                        }
                    }

                    HorizontalDivider()

                    Text("Пользовательский размер:", style = MaterialTheme.typography.titleSmall)

                    OutlinedTextField(
                        value = customRows,
                        onValueChange = { customRows = it.filter { char -> char.isDigit() } },
                        label = { Text("Количество строк") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customCols,
                        onValueChange = { customCols = it.filter { char -> char.isDigit() } },
                        label = { Text("Количество столбцов") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customMines,
                        onValueChange = { customMines = it.filter { char -> char.isDigit() } },
                        label = { Text("Количество мин (опционально)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rows = customRows.toIntOrNull()?.coerceIn(1, 30) ?: 9
                        val cols = customCols.toIntOrNull()?.coerceIn(1, 30) ?: 9
                        val mines = customMines.toIntOrNull()?.coerceIn(1, rows * cols - 1)
                        viewModel.setFieldSize(rows, cols, mines)
                        showSettingsDialog = false
                    }
                ) {
                    Text("Применить")
                }
            },
            dismissButton = {
                Button(onClick = { showSettingsDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}