package com.pourush.saakh

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pourush.saakh.features.AddWorkScreen
import com.pourush.saakh.features.HandshakeScreen
import com.pourush.saakh.features.ScannerScreen
import com.pourush.saakh.features.WorkListScreen
import com.pourush.saakh.features.WorkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Hilt automatically provides the ViewModel here
                    val viewModel: WorkViewModel = hiltViewModel()

                    NavHost(navController = navController, startDestination = "work_list") {

                    // Screen 1: The History
                        composable("work_list") {
                            WorkListScreen(
                                viewModel = viewModel,
                                onAddClick = { navController.navigate("add_work") },
                                onEntryClick = { entryId -> navController.navigate("handshake/$entryId") },
                                onScanClick = { navController.navigate("scanner") } // Handles Camera Click
                            )
                        }

                        // Screen 2: The Input Form
                        composable("add_work") {
                            AddWorkScreen(
                                viewModel = viewModel,
                                onSaveClick = { navController.popBackStack() }
                            )
                        }

                        // Screen 3: The QR Handshake Screen
                        composable("handshake/{entryId}") { backStackEntry ->
                            // The HandshakeViewModel automatically intercepts {entryId}
                            HandshakeScreen()
                        }

                        composable("scanner") {
                            // Showing a Toast popup using Android context
                            val context = LocalContext.current

                            ScannerScreen(
                                onQrScanned = { scannedData ->
                                    // 1. Use Android's native logger
                                    Log.d("SAAKH_SCANNER", "Successfully read QR: $scannedData")

                                    // 2. Show a popup on the phone screen itself
                                    Toast.makeText(context, "Scanned: $scannedData", Toast.LENGTH_LONG).show()

                                    // 3. Close the camera
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}