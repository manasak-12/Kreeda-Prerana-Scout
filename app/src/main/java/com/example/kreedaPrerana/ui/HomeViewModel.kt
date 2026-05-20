package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AthleteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeSummary(
    val totalAthletes: Int = 0,
    val totalTrials: Int = 0
)

class HomeViewModel(private val repository: AthleteRepository) : ViewModel() {

    val summary: StateFlow<HomeSummary> = combine(
        repository.getAllAthletes(),
        repository.getAllTrials()
    ) { athletes, trials ->
        HomeSummary(totalAthletes = athletes.size, totalTrials = trials.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeSummary())

    fun refreshSummary() {
        // No longer needed as it's reactive, but kept for compatibility if called
    }
}

class HomeViewModelFactory(private val repository: AthleteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
