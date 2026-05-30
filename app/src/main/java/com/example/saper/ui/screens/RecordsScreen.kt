package com.example.saper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saper.data.repository.RecordRepository
import com.example.saper.ui.components.MinesweeperButton
import kotlinx.coroutines.launch

@Composable
fun RecordsScreen(
    recordRepository: RecordRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val easyRecords by recordRepository.easyRecords.collectAsState()
    val normalRecords by recordRepository.normalRecords.collectAsState()
    val hardRecords by recordRepository.hardRecords.collectAsState()

    var selectedDifficulty by remember { mutableStateOf("EASY") }

    val currentRecords = when (selectedDifficulty) {
        "EASY" -> easyRecords
        "NORMAL" -> normalRecords
        else -> hardRecords
    }

    val difficultyName = when (selectedDifficulty) {
        "EASY" -> "ЛЁГКИЙ (9x9, 10 мин)"
        "NORMAL" -> "СРЕДНИЙ (16x16, 40 мин)"
        else -> "СЛОЖНЫЙ (30x16, 99 мин)"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC0C0C0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏆 РЕКОРДЫ 🏆",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = selectedDifficulty == "EASY",
                onClick = { selectedDifficulty = "EASY" },
                label = { Text("Лёгкий") }
            )
            FilterChip(
                selected = selectedDifficulty == "NORMAL",
                onClick = { selectedDifficulty = "NORMAL" },
                label = { Text("Средний") }
            )
            FilterChip(
                selected = selectedDifficulty == "HARD",
                onClick = { selectedDifficulty = "HARD" },
                label = { Text("Сложный") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = difficultyName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (currentRecords.isNotEmpty()) {
            Button(
                onClick = {
                    scope.launch {
                        recordRepository.clearRecords(selectedDifficulty)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800000))
            ) {
                Text("Очистить рекорды", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("№", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
                    Text("Время", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
                    Text("Дата", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                if (currentRecords.isEmpty()) {
                    Text(
                        text = "Пока нет рекордов\nСыграйте и установите рекорд!",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                } else {
                    LazyColumn {
                        items(currentRecords) { record ->
                            val position = currentRecords.indexOf(record) + 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$position.",
                                    fontSize = 14.sp,
                                    modifier = Modifier.width(40.dp),
                                    color = if (position == 1) Color(0xFFFFD700) else Color.Black,
                                    fontWeight = if (position == 1) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = formatTime(record.timeSeconds),
                                    fontSize = 14.sp,
                                    modifier = Modifier.width(80.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF008000)
                                )
                                Text(
                                    text = formatDate(record.date),
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f),
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MinesweeperButton(
            text = "НАЗАД",
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = onBack
        )
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (minutes > 0) {
        String.format("%d:%02d", minutes, remainingSeconds)
    } else {
        String.format("%d сек", remainingSeconds)
    }
}

fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("dd.MM HH:mm", java.util.Locale.getDefault())
    return format.format(date)
}