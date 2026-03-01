package com.pourush.saakh.features

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pourush.saakh.core.utils.LedgerExporter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkListScreen(
    viewModel: WorkViewModel,
    onAddClick: () -> Unit,
    onEntryClick: (String) -> Unit,
    onScanClick: () -> Unit
) {

    val context = LocalContext.current
    val entries by viewModel.workEntries.collectAsState()
    val tofuState by viewModel.showTofuDialog.collectAsState()

    // --- THE TOFU DIALOG UI ---
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
                    enabled = contractorName.isNotBlank()
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
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = {
                        val ledgerText = LedgerExporter.generateShareableText(entries)
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, ledgerText)
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share Ledger via...")
                        context.startActivity(shareIntent)
                    }
                ) {
                    Text("📤") // Export/Share Icon
                }

                Spacer(modifier = Modifier.height(16.dp))
                FloatingActionButton(onClick = onScanClick) {
                    Text("📷")
                }
                Spacer(modifier = Modifier.height(16.dp))
                FloatingActionButton(onClick = onAddClick) {
                    Text("+")
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
                // ADDED KEY: This tells Compose exactly which item is which, preventing animation crashes when deleting.
                items(entries, key = { it.id }) { entry ->

                    // --- SWIPE TO DELETE LOGIC ---
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.deleteEntry(entry) // Call the ViewModel to delete from DB
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false, // Only allow right-to-left swipe
                        backgroundContent = {
                            val color by animateColorAsState(
                                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                    MaterialTheme.colorScheme.errorContainer
                                } else {
                                    Color.Transparent
                                }, label = "DeleteColorAnimation"
                            )

                            // The red background and trash icon that appear behind the card
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color, shape = MaterialTheme.shapes.medium)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Entry",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        },
                        content = {
                            // Existing card with verification display
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEntryClick(entry.id) }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Wage: ₹${entry.wageRate}", style = MaterialTheme.typography.titleMedium)
                                    Text("Hours: ${entry.hoursWorked}", style = MaterialTheme.typography.bodyMedium)

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
                    )
                }
            }
        }
    }
}