package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "athletes")
data class AthleteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val age: Int,
    val primarySport: String
)

data class AthleteWithScore(
    val id: Int,
    val name: String,
    val primarySport: String,
    val bestScore: Double
)
