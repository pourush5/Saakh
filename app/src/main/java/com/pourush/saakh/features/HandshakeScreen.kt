package com.pourush.saakh.features

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pourush.saakh.core.utils.QrCodeGenerator

@Composable
fun HandshakeScreen(
    workDataPayload: String, // Passing data here
    signature: String?,      // The cryptographic proof
    modifier: Modifier = Modifier
) {
    // Combine data and signature into one string for the QR code
    val finalPayload = "DATA:$workDataPayload|SIG:$signature"

    // Generate the QR code only when the payload changes
    val qrBitmap = remember(finalPayload) {
        QrCodeGenerator.generateQrBitmap(finalPayload)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Show this to the Contractor",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (qrBitmap != null) {
            Image(
                bitmap = qrBitmap,
                contentDescription = "Cryptographic QR Code",
                modifier = Modifier.size(300.dp)
            )
        } else {
            Text("Failed to generate secure code.")
        }
    }
}