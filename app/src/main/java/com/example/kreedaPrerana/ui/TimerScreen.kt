package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.ScoutScaffold

@Composable
fun TimerScreen(viewModel: TimerViewModel) {
    val athletes by viewModel.athletes.collectAsState()
    val selectedAthlete by viewModel.selectedAthlete.collectAsState()
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val laps by viewModel.laps.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    ScoutScaffold(title = stringResource(R.string.track_trial)) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Athlete Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedAthlete?.name ?: stringResource(R.string.select_athlete))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        athletes.forEach { athlete ->
                            DropdownMenuItem(
                                text = { Text(athlete.name) },
                                onClick = {
                                    viewModel.selectAthlete(athlete)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Timer Display
                Text(
                    text = viewModel.formatTime(elapsedTime),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { if (isRunning) viewModel.stopTimer() else viewModel.startTimer() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRunning) Color.Red else Color.Green,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).padding(8.dp)
                    ) {
                        Text(if (isRunning) stringResource(R.string.stop) else stringResource(R.string.start))
                    }

                    Button(
                        onClick = { viewModel.recordLap() },
                        enabled = isRunning || elapsedTime > 0,
                        modifier = Modifier.weight(1f).padding(8.dp)
                    ) {
                        Text(stringResource(R.string.lap))
                    }

                    OutlinedButton(
                        onClick = { viewModel.resetTimer() },
                        modifier = Modifier.weight(1f).padding(8.dp)
                    ) {
                        Text(stringResource(R.string.reset))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Laps List Header
                Text(
                    text = stringResource(R.string.laps_header),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }

            itemsIndexed(laps) { index, lapTime ->
                ListItem(
                    headlineContent = { Text(stringResource(R.string.lap_label, laps.size - index)) },
                    trailingContent = {
                        Text(
                            text = viewModel.formatTime(lapTime),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
