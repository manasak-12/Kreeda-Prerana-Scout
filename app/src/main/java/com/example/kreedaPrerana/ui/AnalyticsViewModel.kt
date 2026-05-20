package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AthleteEntity
import com.example.myapplication.data.AthleteRepository
import com.example.myapplication.data.TrialEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AnalyticsState(
    val selectedSport: String = "All",
    val athletes: List<AthleteEntity> = emptyList(),
    val trials: List<TrialEntity> = emptyList(),
    val diamondsInTheRough: List<AthleteEntity> = emptyList(),
    val sportsList: List<String> = emptyList()
)

class AnalyticsViewModel(private val repository: AthleteRepository) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsState())
    val state: StateFlow<AnalyticsState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val athletes = repository.getAllAthletes().first()
            val trials = repository.getAllTrials().first()
            val sports = trials.map { it.sportType }.distinct().toMutableList().apply {
                add(0, "All")
            }
            
            _state.value = _state.value.copy(
                athletes = athletes,
                trials = trials,
                sportsList = sports
            )
            updateAnalytics()
        }
    }

    fun selectSport(sport: String) {
        _state.value = _state.value.copy(selectedSport = sport)
        updateAnalytics()
    }

    private fun updateAnalytics() {
        val currentState = _state.value
        val filteredTrials = if (currentState.selectedSport == "All") {
            currentState.trials
        } else {
            currentState.trials.filter { it.sportType == currentState.selectedSport }
        }

        // Identify "Diamonds in the Rough" (District Level Ready)
        val achievers = filteredTrials.filter { trial ->
            checkMilestone(trial.score, trial.sportType)
        }.map { it.athleteId }.distinct()

        val diamonds = currentState.athletes.filter { it.id in achievers }

        _state.value = _state.value.copy(diamondsInTheRough = diamonds)
    }

    private fun checkMilestone(score: Double, sport: String): Boolean {
        return when {
            sport.contains("100m Sprint", ignoreCase = true) && score < 12.0 -> true
            sport.contains("Long Jump", ignoreCase = true) && score > 5.0 -> true
            sport.contains("Kabaddi", ignoreCase = true) && score > 50.0 -> true
            else -> false
        }
    }
}

class AnalyticsViewModelFactory(private val repository: AthleteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalyticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
