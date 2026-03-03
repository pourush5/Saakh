package com.pourush.saakh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pourush.saakh.core.datastore.UserRole
import com.pourush.saakh.features.AddWorkScreen
import com.pourush.saakh.features.HandshakeScreen
import com.pourush.saakh.features.RoleSelectionScreen
import com.pourush.saakh.features.RoleViewModel
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
                    // 1. Grab the RoleViewModel
                    val roleViewModel: RoleViewModel = hiltViewModel()
                    val userRole by roleViewModel.userRole.collectAsState()

                    // 2. The Gatekeeper Logic
                    when (userRole) {
                        null -> {
                            // DataStore is still loading from disk
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        UserRole.UNASSIGNED -> {
                            // First time opening the app!
                            RoleSelectionScreen(
                                onRoleSelected = { selectedRole ->
                                    roleViewModel.saveRole(selectedRole)
                                }
                            )
                        }
                        else -> {
                            // User is assigned. Launch the main app!
                            val navController = rememberNavController()
                            val viewModel: WorkViewModel = hiltViewModel()

                            NavHost(navController = navController, startDestination = "work_list") {
                                composable("work_list") {
                                    WorkListScreen(
                                        viewModel = viewModel,
                                        userRole = userRole!!, // PASS THE ROLE DOWN!
                                        onAddClick = { navController.navigate("add_work") },
                                        onEntryClick = { entryId -> navController.navigate("handshake/$entryId") },
                                        onScanClick = { navController.navigate("scanner") }
                                    )
                                }
                                composable("add_work") {
                                    AddWorkScreen(
                                        viewModel = viewModel,
                                        onSaveClick = { navController.popBackStack() }
                                    )
                                }
                                composable("handshake/{entryId}") {
                                    // Pass the userRole parameter down!
                                    HandshakeScreen(userRole = userRole!!)
                                }
                                composable("scanner") {
                                    ScannerScreen(
                                        onQrScanned = { scannedData ->
                                            viewModel.processScannedQrCode(
                                                scannedData = scannedData,
                                                onContractorSigned = { signedEntryId ->
                                                    // 1. The Contractor just signed it. Go show the QR Code!
                                                    navController.navigate("handshake/$signedEntryId") {
                                                        popUpTo("work_list") // Remove the scanner from the back button history
                                                    }
                                                },
                                                onLaborerVerified = {
                                                    // 2. The Laborer just got verified. Go back to the main list!
                                                    navController.popBackStack()
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}