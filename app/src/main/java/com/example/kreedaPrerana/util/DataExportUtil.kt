package com.example.myapplication.util

import android.content.Context
import android.content.Intent
import com.example.myapplication.data.AthleteEntity
import com.example.myapplication.data.TrialEntity
import java.text.SimpleDateFormat
import java.util.*

object DataExportUtils {

    /**
     * Converts athlete and trial data into a CSV formatted string.
     */
    fun generateTrialsCsv(athletes: List<AthleteEntity>, trials: List<TrialEntity>): String {
        val athleteMap = athletes.associateBy { it.id }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        
        val csvBuilder = StringBuilder()
        // CSV Header
        csvBuilder.append("Athlete Name,Age,Primary Sport,Trial Sport,Score,Date Recorded\n")
        
        // CSV Rows
        trials.forEach { trial ->
            val athlete = athleteMap[trial.athleteId]
            val date = dateFormat.format(Date(trial.timestamp))
            
            csvBuilder.append("${athlete?.name ?: "Unknown"},")
            csvBuilder.append("${athlete?.age ?: "N/A"},")
            csvBuilder.append("${athlete?.primarySport ?: "N/A"},")
            csvBuilder.append("${trial.sportType},")
            csvBuilder.append("${trial.score},")
            csvBuilder.append("$date\n")
        }
        
        return csvBuilder.toString()
    }

    /**
     * Uses an Intent to share the CSV string via email or messaging apps.
     */
    fun shareCsv(context: Context, csvString: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Kreeda-Prerana Scout: Talent Data Export")
            putExtra(Intent.EXTRA_TEXT, csvString)
        }
        
        val chooser = Intent.createChooser(shareIntent, "Share Talent Data via")
        context.startActivity(chooser)
    }
}
