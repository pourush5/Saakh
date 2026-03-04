package com.pourush.saakh.features

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pourush.saakh.R
import com.pourush.saakh.core.datastore.UserRole
import com.pourush.saakh.ui.theme.OnSaakhOrange
import com.pourush.saakh.ui.theme.SaakhOrange

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(2f))
        Image(painter= painterResource(id = R.drawable.saakh_logo),
            contentDescription = "Kangto_logo",modifier=Modifier.size(150.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Welcome!\nसाख में आपका स्वागत है!",
            style = MaterialTheme.typography.headlineMedium,
            color = OnSaakhOrange,
            fontFamily = FontFamily.Cursive,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Choose your role.\nकृपया अपना रोल चुनें",
            fontSize = 20.sp,

            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))

        // Laborer Button (Primary)
        Button(
            onClick = { onRoleSelected(UserRole.LABORER) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OnSaakhOrange,
                contentColor = Color.White
            )
        ) {
            Text("Labourer\nमैं काम दर्ज कर रहा हूँ - मज़दूर", style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contractor Button (Secondary Outline)
        OutlinedButton(
            onClick = { onRoleSelected(UserRole.CONTRACTOR) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Text("Contractor\nमैं काम की जाँच कर रहा हूँ - ठेकेदार", style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center, color = OnSaakhOrange
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        A2ZFooter()
    }
}