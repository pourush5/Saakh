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
    val id: String,
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
                val parts = scannedData.split("|")
                if (parts.size != 3) {
                    Log.e("SAAKH_ERROR", "Invalid QR format. Expected 3 parts.")
                    return@launch
                }

                val dataPart = parts.find { it.startsWith("DATA:") }?.removePrefix("DATA:") ?: return@launch
                val sigPart = parts.find { it.startsWith("SIG:") }?.removePrefix("SIG:") ?: return@launch
                val pubPart = parts.find { it.startsWith("PUB:") }?.removePrefix("PUB:") ?: return@launch

                val isValid = cryptoManager.verifySignature(dataPart, sigPart, pubPart)

                if (isValid) {
                    // NEW: Parse 4 items (ID, Date, Hours, Wage)
                    val dataValues = dataPart.split(",")
                    if (dataValues.size != 4) {
                        Log.e("SAAKH_ERROR", "Invalid payload format. Missing ID.")
                        return@launch
                    }

                    val entryId = dataValues[0] // Extract the ID
                    val date = dataValues[1].toLong()
                    val hours = dataValues[2].toFloat()
                    val wage = dataValues[3].toDouble()

                    val knownContractor = repository.getContractor(pubPart)

                    if (knownContractor != null) {
                        // We know them! Save using the exact ID.
                        saveVerifiedEntry(entryId, date, hours, wage, sigPart, pubPart, knownContractor.name)
                    } else {
                        // Unknown key! Pass the ID to the TOFU popup.
                        _showTofuDialog.value = PendingTofu(entryId, date, hours, wage, sigPart, pubPart)
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
            repository.addContractor(Contractor(pendingData.publicKey, contractorName))

            saveVerifiedEntry(
                pendingData.id, // Pass the ID!
                pendingData.date,
                pendingData.hours,
                pendingData.wage,
                pendingData.signature,
                pendingData.publicKey,
                contractorName
            )

            _showTofuDialog.value = null
        }
    }

    // Called if the user cancels the popup
    fun dismissTofuDialog() {
        _showTofuDialog.value = null
    }

    // Helper function to keep the code DRY
    private suspend fun saveVerifiedEntry(
        id: String, // Add ID parameter
        date: Long, hours: Float, wage: Double, sig: String, pubKey: String, contractorName: String
    ) {
        val verifiedEntry = WorkEntry(
            id = id, // Force Room to use this specific ID (Triggers REPLACE)
            date = date,
            hoursWorked = hours,
            wageRate = wage,
            isVerified = true,
            digitalSignature = sig,
            contractorPublicKey = pubKey,
            contractorName = contractorName
        )

        repository.addWorkEntry(verifiedEntry)
        Log.d("SAAKH_SUCCESS", "Verified entry updated in database!")
    }
}