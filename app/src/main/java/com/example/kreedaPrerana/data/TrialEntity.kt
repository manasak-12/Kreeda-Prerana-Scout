package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trials",
    foreignKeys = [
        ForeignKey(
            entity = AthleteEntity::class,
            parentColumns = ["id"],
            childColumns = ["athleteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["athleteId"])]
)
data class TrialEntity(
    @PrimaryKey(autoGenerate = true)
    val trialId: Int = 0,
    val athleteId: Int,
    val sportType: String,
    val score: Double,
    val timestamp: Long
)
