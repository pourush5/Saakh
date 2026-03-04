package com.pourush.saakh.features

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pourush.saakh.core.datastore.UserRole
import com.pourush.saakh.core.utils.QrCodeGenerator
import com.pourush.saakh.ui.theme.OnSaakhOrange

@Composable
fun HandshakeScreen(
    userRole: UserRole,
    viewModel: HandshakeViewModel = hiltViewModel(), // Inject ViewModel
    modifier: Modifier = Modifier
) {
    val state by viewModel.qrState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val currentState = state) {
            is QrState.Loading -> {
                CircularProgressIndicator()
                Text("Generating secure code... सुरक्षित कोड बनाया जा रहा है... ", modifier = Modifier.padding(top = 16.dp))
            }
            is QrState.Error -> {
                Text(text = "Error (त्रुटि) : ${currentState.message}", color = MaterialTheme.colorScheme.error)
            }
            is QrState.Success -> {
                // 2. DYNAMICALLY CHOOSE THE TEXT BASED ON ROLE AND QR TYPE
                val headerText = when {
                    currentState.qrContent.startsWith("REQ") -> "Show this to the Contractor.\nइसे ठेकेदार को दिखाएँ |"
                    userRole == UserRole.CONTRACTOR -> "Show this back to the Laborer.\nइसे मज़दूर को दिखाएँ |"
                    else -> "Verified Digital Receipt (सत्यापित डिजिटल रसीद) ✅" // For the Laborer viewing their own proof!
                }

                Text(
                    text = headerText,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 32.dp),
                    color= OnSaakhOrange
                )

                val qrBitmap = remember(currentState.qrContent) {
                    QrCodeGenerator.generateQrBitmap(currentState.qrContent)
                }

                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap,
                        contentDescription = "Cryptographic QR Code",
                        modifier = Modifier.size(300.dp)
                    )
                } else {
                    Text("Failed to render QR Code. QR कोड दिखाने में समस्या हुई।")
                }
            }
        }
    }
}