package com.pourush.saakh.features

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pourush.saakh.core.datastore.UserRole
import com.pourush.saakh.core.utils.LedgerExporter
import com.pourush.saakh.ui.theme.OnSaakhOrange
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkListScreen(
    viewModel: WorkViewModel,
    userRole: UserRole,
    onAddClick: () -> Unit,
    onEntryClick: (String) -> Unit,
    onScanClick: () -> Unit
) {

    val context = LocalContext.current
    val entries by viewModel.workEntries.collectAsState()
    val tofuState by viewModel.showTofuDialog.collectAsState()
    var showInfoDialog by remember { mutableStateOf(false) }

    // --- THE TOFU DIALOG UI ---
    if (tofuState != null) {
        var contractorName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.dismissTofuDialog() },
            title = { Text("New Signature Detected \n(नई डिजिटल पहचान मिली)🛡️ ",style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center, color=OnSaakhOrange) },
            text = {
                Column {
                    Text("This is a valid signature, but it's from an unknown device. Who is this contractor? नयी पहचान । ठेकेदार का नाम? ",style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        textStyle = MaterialTheme.typography.titleLarge,
                        value = contractorName,
                        onValueChange = { contractorName = it },
                        label = { Text("Enter Contractor Name (ठेकेदार का नाम अंकित करें)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onNewContractorNamed(contractorName) },
                    enabled = contractorName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OnSaakhOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save & Trust (भरोसा एवं सेव करें)",style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissTofuDialog() }) {
                    Text("Cancel (रद्द करें)",style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center)
                }
            }
        )
    }
// --- THE NEW INFO/HELP DIALOG ---
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    "How to Use Saakh (Labourer-centric guide)\n(साख का उपयोग कैसे करें) ℹ️",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = OnSaakhOrange
                )
            },
            text = {
                // Make it scrollable in case the text gets long on small screens
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (userRole == UserRole.LABORER) {
                        // Guide for Laborers
                        Text("1. Add Work (+)", fontWeight = FontWeight.Bold, color = OnSaakhOrange)
                        Text("Tap '+' to log your daily hours and wages. This creates a 'Pending' entry.\n(+ दबाकर अपनी दिहाड़ी और घंटे दर्ज करें।)")
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("2. Get Verified", fontWeight = FontWeight.Bold, color = OnSaakhOrange)
                        Text("Tap your pending card to show a QR code to the Contractor.\n(अपने असत्यापित काम पर क्लिक करके ठेकेदार को QR कोड दिखाएं।)")
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("3. Scan Receipt (📷)", fontWeight = FontWeight.Bold, color = OnSaakhOrange)
                        Text("After the Contractor scans QR code of labourer, use the Camera to scan contractor's QR code for complete verification by getting verified digital receipt.\n(ठेकेदार के सत्यापन (QR code स्कैन) करने के बाद, उनका QR कोड स्कैन करके पूर्ण सत्यापन की रसीद प्राप्त करें।)")
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("4. Export (📤)", fontWeight = FontWeight.Bold, color = OnSaakhOrange)
                        Text("Share your full ledger across different platforms.\n(अपना खाता शेयर करें।)")
                    } else {
                        // Guide for Contractors
                        Text("1. Scan Worker's Code (📷)", fontWeight = FontWeight.Bold, color = OnSaakhOrange)
                        Text("Use the Camera to scan a worker's pending entry.\n(मज़दूर का QR कोड स्कैन करें।)")
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("2. Approve Work", fontWeight = FontWeight.Bold, color = OnSaakhOrange)
                        Text("The app will automatically verify and sign the entry, generating a new QR Code.\n(ऐप काम को पक्का करके एक नया QR कोड बनाएगा।)")
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("3. Handshake", fontWeight = FontWeight.Bold, color = OnSaakhOrange)
                        Text("Show your new QR code back to the worker so they get their receipt.\n(मज़दूर को यह नया QR कोड दिखाएं ताकि उन्हें पक्की रसीद मिल सके।)")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OnSaakhOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text("Ok")
                }
            }
        )
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(), // Ensure Scaffold takes full height
        floatingActionButton = {
            // Apply navigationBarsPadding so the FABs don't hide behind system buttons
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.navigationBarsPadding()
            ) {

                // --- NEW INFO BUTTON FOR EVERYONE ---
                FloatingActionButton(onClick = { showInfoDialog = true }) {
                    Text("ℹ️")
                }

                if (userRole == UserRole.LABORER) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FloatingActionButton(
                        onClick = {
                            val ledgerText = LedgerExporter.generateShareableText(entries)
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, ledgerText)
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Ledger via...(मज़दूरी खाता साझा करें)")
                            context.startActivity(shareIntent)
                        }
                    ) {
                        Text("📤")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // BRING BACK THE SCANNER FOR THE RETURN TRIP!
                    FloatingActionButton(onClick = onScanClick) {
                        Text("📷")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FloatingActionButton(onClick = onAddClick) {
                        Text("+")
                    }

                } else if (userRole == UserRole.CONTRACTOR) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // 2. Contractors STILL only need the scanner to approve work
                    FloatingActionButton(onClick = onScanClick) {
                        Text("📷")
                    }
                }
            }
        },
        bottomBar = {
            // Wrap custom footer to avoid the bottom system navigation area
            Box(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                A2ZFooter()
            }
        }
    ) { paddingValues ->
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), // Vital for top/bottom system bar avoidance
                contentAlignment = Alignment.Center
            ) {
                Text("No work logged yet. Tap + to start.\nअभी तक कोई काम दर्ज नहीं किया गया है | + दबाएं)",
                    color= OnSaakhOrange)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Protects from top status bar and bottomBar overlaps
                    .consumeWindowInsets(paddingValues), // Tells Compose these insets are handled
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
                                    imageVector = Icons.Filled.Delete,
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
                                    Text("Wage (मज़दूरी): ₹${entry.wageRate}", style = MaterialTheme.typography.titleMedium)
                                    Text("Hours (घंटे): ${entry.hoursWorked}", style = MaterialTheme.typography.bodyMedium)

                                    if (entry.isVerified) {
                                        Text(
                                            text = "Verified by (द्वारा सत्यापित): ${entry.contractorName ?: "Unknown"} ✅",
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.labelLarge,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                        Text(
                                            text = "Key ID: ${com.pourush.saakh.core.utils.CryptoUtils.generateKeyFingerprint(entry.contractorPublicKey)}",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "Status (स्थिति): Pending (बाकी) ⏳",
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