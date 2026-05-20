package com.example.myapplication.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.AthleteEntity
import com.example.myapplication.ui.theme.ScoutScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {
    val state by viewModel.state.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    ScoutScaffold(title = stringResource(R.string.scout_analytics)) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Sport Filter Dropdown
                Text(
                    text = stringResource(R.string.filter_by_sport),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(state.selectedSport)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        state.sportsList.forEach { sport ->
                            DropdownMenuItem(
                                text = { Text(sport) },
                                onClick = {
                                    viewModel.selectSport(sport)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Talent Curve Placeholder / Visualization
                Text(
                    text = stringResource(R.string.talent_curve),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    TalentCurveCanvas(state)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Diamonds in the Rough List Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Diamond,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.diamonds_in_rough),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = stringResource(R.string.cleared_benchmarks),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (state.diamondsInTheRough.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.no_athletes_identified),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(state.diamondsInTheRough) { athlete ->
                    DiamondAthleteItem(athlete)
                }
            }
        }
    }
}

@Composable
fun TalentCurveCanvas(state: AnalyticsState) {
    val trials = if (state.selectedSport == "All") state.trials else state.trials.filter { it.sportType == state.selectedSport }
    
    if (trials.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_trials_recorded), style = MaterialTheme.typography.bodySmall)
        }
        return
    }

    val maxScore = trials.maxOf { it.score }.coerceAtLeast(1.0)

    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val width = size.width
        val height = size.height
        
        // Simple visualization: Bar distribution
        val barCount = trials.size.coerceAtMost(20)
        val barWidth = width / (barCount * 1.5f)
        val spacing = (width - (barWidth * barCount)) / (barCount + 1)

        trials.take(barCount).sortedByDescending { it.score }.forEachIndexed { index, trial ->
            val barHeight = (trial.score / maxScore) * height
            val x = spacing + index * (barWidth + spacing)
            val y = height - barHeight.toFloat()

            drawRect(
                color = if (trial.score > (maxScore * 0.8)) Color(0xFFFFD700) else Color(0xFF6200EE),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight.toFloat())
            )
        }
    }
}

@Composable
fun DiamondAthleteItem(athlete: AthleteEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = athlete.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${athlete.primarySport} | Age: ${athlete.age}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Surface(
                color = Color(0xFFFFD700),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    stringResource(R.string.district_ready),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}
