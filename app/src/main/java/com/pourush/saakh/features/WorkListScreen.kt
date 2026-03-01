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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val tofuState by viewModel.showTofuDialog.collectAsState() // 1. COLLECT TOFU STATE

    // 2. THE TOFU DIALOG UI
    if (tofuState != null) {
        var contractorName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.dismissTofuDialog() },
            title = { Text("New Signature Detected 🛡️") },
            text = {
                Column {
                    Text("This is a mathematically valid signature, but it's from an unknown device. Who is this contractor?")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = contractorName,
                        onValueChange = { contractorName = it },
                        label = { Text("Enter Contractor Name") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onNewContractorNamed(contractorName) },
                    enabled = contractorName.isNotBlank() // Prevent saving an empty name
                ) {
                    Text("Save & Trust")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissTofuDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
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

                            // THE NEW UI LOGIC
                            if (entry.isVerified) {
                                Text(
                                    text = "Verified by: ${entry.contractorName ?: "Unknown"} ✅",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            } else {
                                Text(
                                    text = "Status: Pending ⏳",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}