package com.example.myapplication.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.ScoutScaffold
import com.example.myapplication.ui.theme.StarShape
import com.example.myapplication.util.DataExportUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel) {
    val leaderboardData by viewModel.leaderboard.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val sports = leaderboardData.keys.toList()
    var selectedSportIndex by remember { mutableIntStateOf(0) }

    // Update selected index if it's out of bounds after a refresh
    if (selectedSportIndex >= sports.size && sports.isNotEmpty()) {
        selectedSportIndex = 0
    }

    ScoutScaffold(
        title = stringResource(R.string.nav_leaderboard),
        actions = {
            IconButton(onClick = {
                scope.launch {
                    val csv = viewModel.getCsvData()
                    DataExportUtils.shareCsv(context, csv)
                }
            }) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = stringResource(R.string.export_csv),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (sports.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = selectedSportIndex,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    sports.forEachIndexed { index, sport ->
                        Tab(
                            selected = selectedSportIndex == index,
                            onClick = { selectedSportIndex = index },
                            text = { Text(sport) }
                        )
                    }
                }

                val currentSport = sports[selectedSportIndex]
                val athletes = leaderboardData[currentSport]?.take(10) ?: emptyList()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.talent_curve_label, currentSport),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        TalentCurve(athletes = athletes)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.top_10_performers),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(athletes) { ranked ->
                        LeaderboardItem(ranked)
                    }
                }
            } else if (!isRefreshing) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_trials_recorded), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun TalentCurve(athletes: List<RankedAthlete>) {
    if (athletes.isEmpty()) return

    val maxScore = athletes.maxOf { it.score }.coerceAtLeast(1.0)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barWidth = canvasWidth / (athletes.size * 1.5f)
                val spacing = (canvasWidth - (barWidth * athletes.size)) / (athletes.size + 1)

                athletes.forEachIndexed { index, athlete ->
                    val barHeight = (athlete.score / maxScore) * canvasHeight
                    val x = spacing + index * (barWidth + spacing)
                    val y = canvasHeight - barHeight.toFloat()

                    drawRect(
                        color = if (athlete.hasBadge) Color(0xFFFFD700) else Color(0xFF6200EE),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight.toFloat())
                    )
                    
                    // Optional: Draw a line for the curve
                    if (index < athletes.size - 1) {
                        val nextBarHeight = (athletes[index + 1].score / maxScore) * canvasHeight
                        val nextX = spacing + (index + 1) * (barWidth + spacing)
                        val nextY = canvasHeight - nextBarHeight.toFloat()
                        
                        drawLine(
                            color = Color.Gray,
                            start = Offset(x + barWidth / 2, y),
                            end = Offset(nextX + barWidth / 2, nextY),
                            strokeWidth = 2f
                        )
                    }
                }
                
                // Draw Baseline
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, canvasHeight),
                    end = Offset(canvasWidth, canvasHeight),
                    strokeWidth = 4f
                )
            }
        }
    }
}

@Composable
fun LeaderboardItem(ranked: RankedAthlete) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${ranked.rank}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(36.dp)
                )
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = ranked.athleteName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (ranked.hasBadge) {
                            Surface(
                                color = Color(0xFFFFD700), // Gold
                                shape = StarShape(),
                                modifier = Modifier.padding(start = 8.dp).size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.EmojiEvents,
                                    contentDescription = stringResource(R.string.district_level_ready),
                                    tint = Color.Black,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                    if (ranked.hasBadge) {
                        Text(
                            stringResource(R.string.district_level_ready),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFB8860B)
                        )
                    }
                }
            }
            Text(
                text = "%.2f".format(ranked.score),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
