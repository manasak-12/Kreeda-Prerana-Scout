package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AthleteEntity
import com.example.myapplication.data.AthleteRepository
import com.example.myapplication.data.TrialEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AthleteViewModel(private val repository: AthleteRepository) : ViewModel() {

    val allAthletes: StateFlow<List<AthleteEntity>> = repository.getAllAthletes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSport = MutableStateFlow("")
    
    val topTrials: StateFlow<List<TrialEntity>> = _selectedSport
        .flatMapLatest { sport ->
            repository.getTopTrials(sport)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertAthlete(name: String, age: Int, primarySport: String) {
        viewModelScope.launch {
            val athlete = AthleteEntity(name = name, age = age, primarySport = primarySport)
            repository.insertAthlete(athlete)
        }
    }

    fun insertAthletes(athletes: List<AthleteEntity>) {
        viewModelScope.launch {
            repository.insertAthletes(athletes)
        }
    }

    fun logTrial(athleteId: Int, sportType: String, score: Double) {
        viewModelScope.launch {
            val trial = TrialEntity(
                athleteId = athleteId,
                sportType = sportType,
                score = score,
                timestamp = System.currentTimeMillis()
            )
            repository.logTrial(trial)
        }
    }

    fun loadLeaderboard(sportType: String) {
        _selectedSport.value = sportType
    }
}
