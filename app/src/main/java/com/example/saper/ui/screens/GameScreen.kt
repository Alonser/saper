package com.example.saper.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.saper.data.models.GameState
import com.example.saper.ui.components.CellView
import com.example.saper.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(viewModel: GameViewModel) {

    var showSettingsDialog by remember {
        mutableStateOf(false)
    }

    val animatedColor by animateColorAsState(

        targetValue = when (viewModel.gameState) {

            GameState.PLAYING ->
                Color(0xFF607D8B)

            GameState.WON ->
                Color(0xFF4CAF50)

            GameState.LOST ->
                Color(0xFFF44336)
        },

        animationSpec = tween(400),

        label = ""
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopAppBar(

            title = {
                Text("Сапёр")
            },

            actions = {

                IconButton(
                    onClick = {
                        viewModel.restartGame()
                    }
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null
                    )
                }

                IconButton(
                    onClick = {
                        showSettingsDialog = true
                    }
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null
                    )
                }
            }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),

            colors = CardDefaults.cardColors(
                containerColor = animatedColor
            )
        ) {

            Text(

                text = when (viewModel.gameState) {

                    GameState.PLAYING ->
                        "🎮 Игра идёт"

                    GameState.WON ->
                        "🏆 Победа"

                    GameState.LOST ->
                        "💀 Поражение"
                },

                modifier = Modifier.padding(16.dp),

                color = Color.White
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),

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

                Text("📏 ${viewModel.rows}x${viewModel.cols}")

                Text("💣 ${viewModel.mines}")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(

            columns = GridCells.Fixed(viewModel.cols),

            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {

            items(
                items = viewModel.field.flatten(),
                key = { "${it.row}_${it.col}" }
            ) { cell ->

                CellView(

                    rows = viewModel.rows,

                    cell = cell,

                    gameState = viewModel.gameState,

                    onClick = {
                        viewModel.openCell(cell)
                    },

                    onLongClick = {
                        viewModel.toggleFlag(cell)
                    }
                )
            }
        }
    }

    if (showSettingsDialog) {

        AlertDialog(

            onDismissRequest = {
                showSettingsDialog = false
            },

            title = {
                Text("Выбор сложности")
            },

            text = {

                Column {

                    viewModel.getPresets().forEach { preset ->

                        Button(

                            onClick = {

                                viewModel.applyPreset(preset)

                                showSettingsDialog = false
                            },

                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                "${preset.name} (${preset.rows}x${preset.cols}, ${preset.mines} мин)"
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },

            confirmButton = {

                Button(
                    onClick = {
                        showSettingsDialog = false
                    }
                ) {
                    Text("Закрыть")
                }
            }
        )
    }
}