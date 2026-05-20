package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AthleteEntity
import com.example.myapplication.data.AthleteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BatchEntryViewModel(private val repository: AthleteRepository) : ViewModel() {

    // Initialize with 30 "empty" athletes
    private val _athletes = MutableStateFlow(List(30) { 
        AthleteEntity(name = "", age = 0, primarySport = "") 
    })
    val athletes: StateFlow<List<AthleteEntity>> = _athletes.asStateFlow()

    /**
     * Updates a specific athlete in the list at [index].
     */
    fun updateAthlete(index: Int, name: String? = null, age: Int? = null, sport: String? = null) {
        val currentList = _athletes.value.toMutableList()
        val athlete = currentList[index]
        
        currentList[index] = athlete.copy(
            name = name ?: athlete.name,
            age = age ?: athlete.age,
            primarySport = sport ?: athlete.primarySport
        )
        _athletes.value = currentList
    }

    /**
     * Filters out empty entries and saves the rest to the database.
     */
    fun saveAll() {
        viewModelScope.launch {
            // Only save athletes that have at least a name
            val validAthletes = _athletes.value.filter { it.name.isNotBlank() }
            if (validAthletes.isNotEmpty()) {
                repository.insertAthletes(validAthletes)
            }
        }
    }
}

class BatchEntryViewModelFactory(private val repository: AthleteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BatchEntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BatchEntryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
