package com.pourush.saakh.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.pourush.saakh.ui.theme.OnSaakhOrange
import com.pourush.saakh.ui.theme.Purple40
import com.pourush.saakh.ui.theme.Purple80

@Composable
fun AddWorkScreen(
    viewModel: WorkViewModel,
    onSaveClick: () -> Unit
) {
    var hours by rememberSaveable { mutableStateOf("") }
    var wage by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Today's Work\n(आज का काम)",
            style = MaterialTheme.typography.headlineMedium,
            color = OnSaakhOrange,
            textAlign = TextAlign.Center
        )

        // Hours Input
        OutlinedTextField(
            value = hours,
            onValueChange = { hours = it },
            label = { Text("Hours Worked (कितने घंटे काम किया ?)",color = Purple40,
                fontSize = 20.sp)},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.titleLarge
        )

        // Wage Input
        OutlinedTextField(
            value = wage,
            onValueChange = { wage = it },
            label = { Text("Daily Wage. दिहाड़ी (रोज़ की मज़दूरी)",color = Purple40,fontSize = 20.sp
            ) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.titleLarge
        )

        // Save Button
        Button(
            onClick = {
                val h = hours.toFloatOrNull() ?: 0f
                val w = wage.toDoubleOrNull() ?: 0.0
                viewModel.addEntry(System.currentTimeMillis(), h, w, notes)
                onSaveClick() // Navigate back
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Taller button for easier tapping
            ,
            colors = ButtonDefaults.buttonColors(
                containerColor = OnSaakhOrange,
                contentColor = Color.White
            )
        ) {
            Text("Save Entry (सेव करें)",style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center)
        }
    }
}