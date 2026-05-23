package com.example.saper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

import kotlin.random.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val viewModel: GameViewModel = viewModel()

            val darkTheme = viewModel.isDarkTheme.value

            MaterialTheme(
                colorScheme = if (darkTheme)
                    darkColorScheme()
                else
                    lightColorScheme()
            ) {

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {

                    GameScreen(viewModel)
                }
            }
        }
    }
}

data class Cell(
    val row: Int,
    val col: Int,
    var isMine: Boolean = false,
    var isOpened: Boolean = false,
    var isFlagged: Boolean = false,
    var nearbyMines: Int = 0
)

enum class GameState {
    PLAYING,
    WON,
    LOST
}

class GameViewModel : ViewModel() {

    var isDarkTheme = mutableStateOf(false)
        private set

    var rows by mutableIntStateOf(9)
    var cols by mutableIntStateOf(9)
    var mines by mutableIntStateOf(10)

    var gameState by mutableStateOf(GameState.PLAYING)

    var field by mutableStateOf(generateField(rows, cols, mines))

    fun toggleTheme() {

        isDarkTheme.value = !isDarkTheme.value
    }

    fun restartGame() {

        gameState = GameState.PLAYING

        field = generateField(rows, cols, mines)
    }

    fun setFieldSize(r: Int, c: Int) {

        rows = r
        cols = c

        mines = ((rows * cols) * 0.15).toInt()

        restartGame()
    }

    fun openCell(cell: Cell) {

        if (cell.isFlagged || cell.isOpened) return

        cell.isOpened = true

        if (cell.isMine) {

            gameState = GameState.LOST

            revealAll()

            return
        }

        if (cell.nearbyMines == 0) {

            openNearby(cell.row, cell.col)
        }

        checkWin()

        field = field.toList()
    }

    fun toggleFlag(cell: Cell) {

        if (cell.isOpened) return

        cell.isFlagged = !cell.isFlagged

        field = field.toList()
    }

    private fun revealAll() {

        field.flatten().forEach {

            it.isOpened = true
        }

        field = field.toList()
    }

    private fun checkWin() {

        val won = field.flatten().all {

            it.isMine || it.isOpened
        }

        if (won) {

            gameState = GameState.WON
        }
    }

    private fun openNearby(r: Int, c: Int) {

        for (dr in -1..1) {
            for (dc in -1..1) {

                val nr = r + dr
                val nc = c + dc

                if (nr in 0 until rows &&
                    nc in 0 until cols
                ) {

                    val cell = field[nr][nc]

                    if (!cell.isOpened &&
                        !cell.isMine
                    ) {

                        cell.isOpened = true

                        if (cell.nearbyMines == 0) {

                            openNearby(nr, nc)
                        }
                    }
                }
            }
        }
    }

    private fun generateField(
        rows: Int,
        cols: Int,
        mines: Int
    ): List<List<Cell>> {

        val field = List(rows) { r ->

            List(cols) { c ->

                Cell(r, c)
            }
        }

        var placed = 0

        while (placed < mines) {

            val r = Random.nextInt(rows)
            val c = Random.nextInt(cols)

            if (!field[r][c].isMine) {

                field[r][c].isMine = true

                placed++
            }
        }

        for (r in 0 until rows) {
            for (c in 0 until cols) {

                if (field[r][c].isMine) continue

                var count = 0

                for (dr in -1..1) {
                    for (dc in -1..1) {

                        val nr = r + dr
                        val nc = c + dc

                        if (nr in 0 until rows &&
                            nc in 0 until cols &&
                            field[nr][nc].isMine
                        ) {

                            count++
                        }
                    }
                }

                field[r][c].nearbyMines = count
            }
        }

        return field
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(viewModel: GameViewModel) {

    var showDialog by remember {

        mutableStateOf(false)
    }

    var customRows by remember {

        mutableStateOf("9")
    }

    var customCols by remember {

        mutableStateOf("9")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        TopAppBar(

            title = {

                Text("Сапёр")
            },

            actions = {

                IconButton(
                    onClick = {

                        viewModel.toggleTheme()
                    }
                ) {

                    Icon(
                        Icons.Default.DarkMode,
                        contentDescription = null
                    )
                }

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

                        showDialog = true
                    }
                ) {

                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(

            text = when (viewModel.gameState) {

                GameState.PLAYING -> "Игра идёт"
                GameState.WON -> "Победа!"
                GameState.LOST -> "Поражение!"
            },

            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(

            columns = GridCells.Fixed(viewModel.cols),

            modifier = Modifier.fillMaxSize()
        ) {

            items(viewModel.field.flatten()) { cell ->

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
                    }
                )
            }
        }
    }

    if (showDialog) {

        AlertDialog(

            onDismissRequest = {

                showDialog = false
            },

            confirmButton = {

                Button(
                    onClick = {

                        val r = customRows.toIntOrNull() ?: 9
                        val c = customCols.toIntOrNull() ?: 9

                        viewModel.setFieldSize(r, c)

                        showDialog = false
                    }
                ) {

                    Text("Применить")
                }
            },

            dismissButton = {

                Button(
                    onClick = {

                        showDialog = false
                    }
                ) {

                    Text("Закрыть")
                }
            },

            title = {

                Text("Настройки")
            },

            text = {

                Column {

                    Button(
                        onClick = {

                            viewModel.setFieldSize(9, 9)

                            showDialog = false
                        }
                    ) {

                        Text("Лёгкий 9x9")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {

                            viewModel.setFieldSize(16, 16)

                            showDialog = false
                        }
                    ) {

                        Text("Средний 16x16")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {

                            viewModel.setFieldSize(30, 16)

                            showDialog = false
                        }
                    ) {

                        Text("Сложный 30x16")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(

                        value = customRows,

                        onValueChange = {

                            customRows = it
                        },

                        label = {

                            Text("Строки")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(

                        value = customCols,

                        onValueChange = {

                            customCols = it
                        },

                        label = {

                            Text("Колонки")
                        }
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CellView(
    cell: Cell,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {

    Box(

        modifier = Modifier
            .padding(1.dp)
            .size(34.dp)

            .background(

                when {

                    cell.isOpened && cell.isMine ->
                        Color.Red

                    cell.isOpened ->
                        Color.LightGray

                    else ->
                        Color.Gray
                }
            )

            .combinedClickable(

                onClick = onClick,

                onLongClick = onLongClick
            ),

        contentAlignment = Alignment.Center
    ) {

        Text(

            text = when {

                cell.isFlagged ->
                    "🚩"

                !cell.isOpened ->
                    ""

                cell.isMine ->
                    "💣"

                cell.nearbyMines > 0 ->
                    cell.nearbyMines.toString()

                else ->
                    ""
            }
        )
    }
}