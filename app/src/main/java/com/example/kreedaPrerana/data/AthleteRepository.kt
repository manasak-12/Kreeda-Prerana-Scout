package com.example.myapplication.data

import kotlinx.coroutines.flow.Flow

class AthleteRepository(private val appDao: AppDao) {

    fun getAllAthletes(): Flow<List<AthleteEntity>> {
        return appDao.getAllAthletes()
    }

    suspend fun insertAthlete(athlete: AthleteEntity) {
        appDao.insertAthlete(athlete)
    }

    suspend fun insertAthletes(athletes: List<AthleteEntity>) {
        appDao.insertAthletes(athletes)
    }

    suspend fun logTrial(trial: TrialEntity) {
        appDao.insertTrial(trial)
    }

    fun getTopTrials(sportType: String): Flow<List<TrialEntity>> {
        return appDao.getTopTrials(sportType)
    }

    fun getAllTrials(): Flow<List<TrialEntity>> {
        return appDao.getAllTrials()
    }
}
