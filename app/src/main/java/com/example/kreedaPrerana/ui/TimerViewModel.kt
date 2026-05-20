package com.example.myapplication.ui

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AthleteEntity
import com.example.myapplication.data.AthleteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimerViewModel(private val repository: AthleteRepository) : ViewModel() {

    val athletes: StateFlow<List<AthleteEntity>> = repository.getAllAthletes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedAthlete = MutableStateFlow<AthleteEntity?>(null)
    val selectedAthlete: StateFlow<AthleteEntity?> = _selectedAthlete.asStateFlow()

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _laps = MutableStateFlow<List<Long>>(emptyList())
    val laps: StateFlow<List<Long>> = _laps.asStateFlow()

    private var timerJob: Job? = null
    private var startTime = 0L
    private var timePausedAt = 0L

    fun selectAthlete(athlete: AthleteEntity) {
        _selectedAthlete.value = athlete
    }

    fun startTimer() {
        if (_isRunning.value) return
        
        _isRunning.value = true
        startTime = SystemClock.elapsedRealtime() - timePausedAt
        
        timerJob = viewModelScope.launch {
            while (_isRunning.value) {
                _elapsedTime.value = SystemClock.elapsedRealtime() - startTime
                delay(10) // Update approx every 10ms for hundredths
            }
        }
    }

    fun stopTimer() {
        if (!_isRunning.value) return
        _isRunning.value = false
        timePausedAt = SystemClock.elapsedRealtime() - startTime
        timerJob?.cancel()
    }

    fun resetTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        _elapsedTime.value = 0L
        timePausedAt = 0L
        _laps.value = emptyList()
    }

    fun recordLap() {
        if (_isRunning.value || _elapsedTime.value > 0) {
            val currentLaps = _laps.value.toMutableList()
            currentLaps.add(0, _elapsedTime.value)
            _laps.value = currentLaps
        }
    }
    
    fun formatTime(ms: Long): String {
        val hundredths = (ms % 1000) / 10
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        return "%02d:%02d.%02d".format(minutes, seconds, hundredths)
    }
}

class TimerViewModelFactory(private val repository: AthleteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
