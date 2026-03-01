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
import com.pourush.saakh.core.utils.QrCodeGenerator

@Composable
fun HandshakeScreen(
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
                Text("Generating secure code...", modifier = Modifier.padding(top = 16.dp))
            }
            is QrState.Error -> {
                Text(text = "Error: ${currentState.message}", color = MaterialTheme.colorScheme.error)
            }
            is QrState.Success -> {
                Text(
                    text = "Show this to the Contractor",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Format: DATA:timestamp,hours,wage|SIG:base64string
                val finalPayload = "DATA:${currentState.payload}|SIG:${currentState.signature}"

                val qrBitmap = remember(finalPayload) {
                    QrCodeGenerator.generateQrBitmap(finalPayload)
                }

                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap,
                        contentDescription = "Cryptographic QR Code",
                        modifier = Modifier.size(300.dp)
                    )
                } else {
                    Text("Failed to render QR Code.")
                }
            }
        }
    }
}