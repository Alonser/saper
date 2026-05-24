package com.example.saper.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.saper.data.models.GameState
import com.example.saper.ui.components.CellView
import com.example.saper.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun GameScreen(viewModel: GameViewModel) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    var customRows by remember { mutableStateOf("9") }
    var customCols by remember { mutableStateOf("9") }
    var customMines by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Отслеживаем состояние загрузки
    LaunchedEffect(viewModel.gameState) {
        if (viewModel.gameState == GameState.PLAYING) {
            isLoading = false
        }
    }

    // Анимация для статуса игры
    val animatedColor by animateColorAsState(
        targetValue = when (viewModel.gameState) {
            GameState.PLAYING -> Color(0xFF607D8B)
            GameState.WON -> Color(0xFF4CAF50)
            GameState.LOST -> Color(0xFFF44336)
        },
        animationSpec = tween(durationMillis = 500)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    "Сапёр",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            actions = {
                IconButton(onClick = { viewModel.toggleTheme() }) {
                    Icon(
                        if (viewModel.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Сменить тему",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                IconButton(onClick = {
                    isLoading = true
                    viewModel.restartGame()
                }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Новая игра",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Настройки",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        )

        // Статус игры с анимацией
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = animatedColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AnimatedContent(
                targetState = viewModel.gameState,
                transitionSpec = {
                    fadeIn() with fadeOut() using SizeTransform(clip = false)
                }
            ) { state ->
                Text(
                    text = when (state) {
                        GameState.PLAYING -> "🎮 Игра идёт"
                        GameState.WON -> "🏆 Победа! 🎉"
                        GameState.LOST -> "💀 Поражение! Все мины показаны"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }

        // Информация о размере поля и количестве мин
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "📏 ${viewModel.rows}x${viewModel.cols}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "💣 ${viewModel.mines} мин",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Индикатор загрузки
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            // Игровое поле с анимацией
            LazyVerticalGrid(
                columns = GridCells.Fixed(viewModel.cols),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                items(
                    items = viewModel.field.flatten(),
                    key = { item -> "${item.row}_${item.col}" }
                ) { cell ->
                    // Анимация при открытии клетки
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(200)) +
                                scaleIn(initialScale = 0.8f, animationSpec = tween(200)),
                        exit = fadeOut()
                    ) {
                        CellView(
                            cell = cell,
                            onClick = {
                                if (viewModel.gameState == GameState.PLAYING) {
                                    viewModel.openCell(cell)
                                }
                            },
                            onLongClick = {
                                if (viewModel.gameState == GameState.PLAYING) {
                                    viewModel.toggleFlag(cell)
                                }
                            },
                            gameState = viewModel.gameState
                        )
                    }
                }
            }
        }
    }

    // Диалог настроек
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    "Настройки игры",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Предустановленные уровни:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Предустановленные уровни
                    viewModel.getPresets().forEach { preset ->
                        Button(
                            onClick = {
                                isLoading = true
                                viewModel.applyPreset(preset)
                                showSettingsDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                "${preset.name} (${preset.rows}x${preset.cols}, ${preset.mines} мин)",
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    HorizontalDivider()

                    Text(
                        "Пользовательский размер:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        "Количество мин рассчитается автоматически",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = customRows,
                        onValueChange = {
                            customRows = it.filter { char -> char.isDigit() }
                        },
                        label = { Text("Количество строк (1-50)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = customCols,
                        onValueChange = {
                            customCols = it.filter { char -> char.isDigit() }
                        },
                        label = { Text("Количество столбцов (1-50)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = customMines,
                        onValueChange = {
                            customMines = it.filter { char -> char.isDigit() }
                        },
                        label = { Text("Количество мин (опционально, оставьте пустым для авто)" ) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rows = customRows.toIntOrNull()?.coerceIn(1, 50) ?: 9
                        val cols = customCols.toIntOrNull()?.coerceIn(1, 50) ?: 9
                        val mines = if (customMines.isNotEmpty()) {
                            customMines.toIntOrNull()?.coerceIn(1, rows * cols - 1)
                        } else {
                            null
                        }
                        isLoading = true
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