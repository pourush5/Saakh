package com.pourush.saakh.features

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer // ADD THIS
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height // ADD THIS
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WorkListScreen(
    viewModel: WorkViewModel,
    onAddClick: () -> Unit,
    onEntryClick: (String) -> Unit,
    onScanClick: () -> Unit
) {
    // Collect the StateFlow from the ViewModel
    val entries by viewModel.workEntries.collectAsState()

    Scaffold(
        floatingActionButton = {
            // 2. WRAP BUTTONS IN A COLUMN
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(onClick = onScanClick) {
                    Text("📷") // The Scan Button
                }

                Spacer(modifier = Modifier.height(16.dp)) // Adds space between buttons

                FloatingActionButton(onClick = onAddClick) {
                    Text("+") // Add Button
                }
            }
        }
    ) { paddingValues ->
        if (entries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No work logged yet. Tap + to start.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries) { entry ->
                    // A simple card for each work day
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEntryClick(entry.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Wage: ₹${entry.wageRate}", style = MaterialTheme.typography.titleMedium)
                            Text("Hours: ${entry.hoursWorked}", style = MaterialTheme.typography.bodyMedium)
                            Text("Status: ${if (entry.isVerified) "Verified ✅" else "Pending ⏳"}")
                        }
                    }
                }
            }
        }
    }
}