package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.ScoutScaffold

@Composable
fun BatchEntryScreen(viewModel: BatchEntryViewModel) {
    val athletes by viewModel.athletes.collectAsState()

    ScoutScaffold(
        title = stringResource(R.string.batch_entry_full_title),
        bottomBar = {
            Button(
                onClick = { viewModel.saveAll() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(stringResource(R.string.submit_class), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            itemsIndexed(athletes) { index, athlete ->
                AthleteEntryRow(
                    index = index,
                    name = athlete.name,
                    age = if (athlete.age == 0) "" else athlete.age.toString(),
                    onNameChange = { newName ->
                        viewModel.updateAthlete(index = index, name = newName)
                    },
                    onAgeChange = { newAge ->
                        // Only allow numeric input
                        if (newAge.isEmpty() || newAge.all { it.isDigit() }) {
                            viewModel.updateAthlete(
                                index = index,
                                age = newAge.toIntOrNull() ?: 0
                            )
                        }
                    }
                )
                if (index < athletes.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

@Composable
fun AthleteEntryRow(
    index: Int,
    name: String,
    age: String,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.student_label, index + 1),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.name)) },
                modifier = Modifier.weight(0.7f),
                singleLine = true
            )
            OutlinedTextField(
                value = age,
                onValueChange = onAgeChange,
                label = { Text(stringResource(R.string.age)) },
                modifier = Modifier.weight(0.3f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
    }
}
