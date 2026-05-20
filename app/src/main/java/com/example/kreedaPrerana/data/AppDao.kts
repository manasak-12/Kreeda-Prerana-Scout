package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAthlete(athlete: AthleteEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAthletes(athletes: List<AthleteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrial(trial: TrialEntity)

    @Query("SELECT * FROM athletes")
    fun getAllAthletes(): Flow<List<AthleteEntity>>

    @Query("""
        SELECT * FROM trials 
        WHERE sportType = :sportType 
        ORDER BY score DESC 
        LIMIT 10
    """)
    fun getTopTrials(sportType: String): Flow<List<TrialEntity>>

    @Query("SELECT * FROM trials")
    fun getAllTrials(): Flow<List<TrialEntity>>
}
