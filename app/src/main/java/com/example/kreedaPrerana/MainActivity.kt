package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.AthleteRepository
import com.example.myapplication.ui.*
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AthleteRepository(database.appDao())
        
        val homeFactory = HomeViewModelFactory(repository)
        val batchFactory = BatchEntryViewModelFactory(repository)
        val timerFactory = TimerViewModelFactory(repository)
        val leaderboardFactory = LeaderboardViewModelFactory(repository)
        val analyticsFactory = AnalyticsViewModelFactory(repository)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf("home") }
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                label = { Text(stringResource(R.string.nav_home)) },
                                selected = currentScreen == "home",
                                onClick = { currentScreen = "home" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                                label = { Text(stringResource(R.string.nav_batch)) },
                                selected = currentScreen == "batch",
                                onClick = { currentScreen = "batch" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Timer, contentDescription = null) },
                                label = { Text(stringResource(R.string.nav_timer)) },
                                selected = currentScreen == "timer",
                                onClick = { currentScreen = "timer" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                                label = { Text(stringResource(R.string.nav_insights)) },
                                selected = currentScreen == "analytics",
                                onClick = { currentScreen = "analytics" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Leaderboard, contentDescription = null) },
                                label = { Text(stringResource(R.string.nav_leaderboard)) },
                                selected = currentScreen == "leaderboard",
                                onClick = { currentScreen = "leaderboard" }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "home" -> {
                                val viewModel: HomeViewModel = viewModel(factory = homeFactory)
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToBatch = { currentScreen = "batch" },
                                    onNavigateToTimer = { currentScreen = "timer" },
                                    onNavigateToLeaderboard = { currentScreen = "leaderboard" }
                                )
                            }
                            "batch" -> {
                                val viewModel: BatchEntryViewModel = viewModel(factory = batchFactory)
                                BatchEntryScreen(viewModel = viewModel)
                            }
                            "timer" -> {
                                val viewModel: TimerViewModel = viewModel(factory = timerFactory)
                                TimerScreen(viewModel = viewModel)
                            }
                            "analytics" -> {
                                val viewModel: AnalyticsViewModel = viewModel(factory = analyticsFactory)
                                AnalyticsScreen(viewModel = viewModel)
                            }
                            "leaderboard" -> {
                                val viewModel: LeaderboardViewModel = viewModel(factory = leaderboardFactory)
                                LeaderboardScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
