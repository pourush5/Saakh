package com.pourush.saakh.features

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pourush.saakh.core.crypto.SaakhCryptoManager
import com.pourush.saakh.core.database.Contractor
import com.pourush.saakh.core.database.WorkEntry
import com.pourush.saakh.core.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Temporary state to hold data while we wait for the user to type the contractor's name
data class PendingTofu(
    val date: Long,
    val hours: Float,
    val wage: Double,
    val signature: String,
    val publicKey: String
)

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val repository: WorkRepository,
    private val cryptoManager: SaakhCryptoManager
) : ViewModel() {

    // The UI observes this list. When the DB changes, this updates automatically.
    val workEntries: StateFlow<List<WorkEntry>> = repository.allWorkEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // State to trigger the TOFU popup dialog in the UI
    private val _showTofuDialog = MutableStateFlow<PendingTofu?>(null)
    val showTofuDialog: StateFlow<PendingTofu?> = _showTofuDialog.asStateFlow()

    fun addEntry(date: Long, hours: Float, wage: Double, notes: String) {
        viewModelScope.launch {
            val entry = WorkEntry(
                date = date,
                hoursWorked = hours,
                wageRate = wage,
                notes = notes
            )
            repository.addWorkEntry(entry)
        }
    }

    fun deleteEntry(entry: WorkEntry) {
        viewModelScope.launch {
            repository.deleteWorkEntry(entry)
        }
    }

    fun processScannedQrCode(scannedData: String) {
        viewModelScope.launch {
            try {
                // 1. Split the string into 3 parts
                val parts = scannedData.split("|")
                if (parts.size != 3) {
                    Log.e("SAAKH_ERROR", "Invalid QR format. Expected 3 parts.")
                    return@launch
                }

                // Extract the pieces dynamically
                val dataPart = parts.find { it.startsWith("DATA:") }?.removePrefix("DATA:") ?: return@launch
                val sigPart = parts.find { it.startsWith("SIG:") }?.removePrefix("SIG:") ?: return@launch
                val pubPart = parts.find { it.startsWith("PUB:") }?.removePrefix("PUB:") ?: return@launch

                // 2. Verify the Math using the SENDER'S Public Key (pubPart)
                val isValid = cryptoManager.verifySignature(
                    data = dataPart,
                    signatureBase64 = sigPart,
                    publicKeyBase64 = pubPart
                )

                if (isValid) {
                    val dataValues = dataPart.split(",")
                    val date = dataValues[0].toLong()
                    val hours = dataValues[1].toFloat()
                    val wage = dataValues[2].toDouble()

                    // 3. TOFU LOGIC: Check if we recognize this Public Key
                    val knownContractor = repository.getContractor(pubPart)

                    if (knownContractor != null) {
                        // We know them! Save the entry immediately using their known name.
                        saveVerifiedEntry(date, hours, wage, sigPart, pubPart, knownContractor.name)
                    } else {
                        // New Key! Pause and trigger the UI to ask for a name.
                        _showTofuDialog.value = PendingTofu(date, hours, wage, sigPart, pubPart)
                    }

                } else {
                    Log.e("SAAKH_SECURITY", "Signature Verification Failed! Data was tampered with.")
                }
            } catch (e: Exception) {
                Log.e("SAAKH_ERROR", "Failed to parse QR code", e)
            }
        }
    }

    // Called by the UI when the user types a name and clicks "Save"
    fun onNewContractorNamed(contractorName: String) {
        val pendingData = _showTofuDialog.value ?: return

        viewModelScope.launch {
            // 1. Save the new identity to the Contractor table (Address Book)
            repository.addContractor(Contractor(pendingData.publicKey, contractorName))

            // 2. Save the actual work entry with the new name
            saveVerifiedEntry(
                pendingData.date,
                pendingData.hours,
                pendingData.wage,
                pendingData.signature,
                pendingData.publicKey,
                contractorName
            )

            // 3. Close the dialog
            _showTofuDialog.value = null
        }
    }

    // Called if the user cancels the popup
    fun dismissTofuDialog() {
        _showTofuDialog.value = null
    }

    // Helper function to keep the code DRY
    private suspend fun saveVerifiedEntry(
        date: Long, hours: Float, wage: Double, sig: String, pubKey: String, contractorName: String
    ) {
        val verifiedEntry = WorkEntry(
            date = date,
            hoursWorked = hours,
            wageRate = wage,
            isVerified = true,
            digitalSignature = sig,
            contractorPublicKey = pubKey,
            contractorName = contractorName // Ensure this field exists in WorkEntry!
        )

        repository.addWorkEntry(verifiedEntry)
        Log.d("SAAKH_SUCCESS", "Verified entry saved to database!")
    }
}