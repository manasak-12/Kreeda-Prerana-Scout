package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AthleteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn

data class RankedAthlete(
    val athleteName: String,
    val score: Double,
    val rank: Int,
    val hasBadge: Boolean
)

class LeaderboardViewModel(private val repository: AthleteRepository) : ViewModel() {

    val leaderboard: StateFlow<Map<String, List<RankedAthlete>>> = combine(
        repository.getAllAthletes(),
        repository.getAllTrials()
    ) { athletes, trials ->
        val athleteMap = athletes.associateBy { it.id }
        
        // Group trials by sport
        val trialsBySport = trials.groupBy { it.sportType }

        trialsBySport.mapValues { (sport, sportTrials) ->
            // For each athlete in this sport, find their best trial
            val bestTrialsByAthlete = sportTrials.groupBy { it.athleteId }
                .mapValues { (_, athleteTrials) ->
                    if (isLowerScoreBetter(sport)) {
                        athleteTrials.minByOrNull { it.score }
                    } else {
                        athleteTrials.maxByOrNull { it.score }
                    }
                }

            // Sort the best trials
            val sortedBestTrials = bestTrialsByAthlete.values.filterNotNull()
                .let { list ->
                    if (isLowerScoreBetter(sport)) {
                        list.sortedBy { it.score }
                    } else {
                        list.sortedByDescending { it.score }
                    }
                }

            // Map to RankedAthlete objects
            sortedBestTrials.mapIndexed { index, trial ->
                val athlete = athleteMap[trial.athleteId]
                RankedAthlete(
                    athleteName = athlete?.name ?: "Unknown",
                    score = trial.score,
                    rank = index + 1,
                    hasBadge = checkMilestone(trial.score, sport)
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refreshLeaderboard() {
        // Reactive now, but kept for compatibility.
    }

    private fun isLowerScoreBetter(sport: String): Boolean {
        return sport.contains("Sprint", ignoreCase = true) || 
               sport.contains("Time", ignoreCase = true) || 
               sport.contains("Race", ignoreCase = true)
    }

    private fun checkMilestone(score: Double, sport: String): Boolean {
        return when {
            sport.contains("100m Sprint", ignoreCase = true) && score < 12.0 -> true
            sport.contains("Long Jump", ignoreCase = true) && score > 5.0 -> true
            sport.contains("Kabaddi", ignoreCase = true) && score > 50.0 -> true
            else -> false
        }
    }

    suspend fun checkMilestoneForAthlete(athleteId: Int): Boolean {
        val allTrials = repository.getAllTrials().first()
        val athleteTrials = allTrials.filter { it.athleteId == athleteId }
        return athleteTrials.any { checkMilestone(it.score, it.sportType) }
    }

    suspend fun getCsvData(): String {
        val athletes = repository.getAllAthletes().first()
        val trials = repository.getAllTrials().first()
        return com.example.myapplication.util.DataExportUtils.generateTrialsCsv(athletes, trials)
    }
}

class LeaderboardViewModelFactory(private val repository: AthleteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeaderboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
