package com.pourush.saakh.features
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.pourush.saakh.R

@Composable
fun A2ZFooter(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background) // Ensures it blends in
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pourushpandey.vercel.app"))
                context.startActivity(intent)
            }
            .padding(vertical = 16.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.a2z_nobg),
            contentDescription = "Bottom_A2Z_graphic",
            modifier = Modifier.size(50.dp)
        )
        Text(
            text = "An initiative by Pourush Pandey",
            fontFamily = FontFamily.Cursive,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}