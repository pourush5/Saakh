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

    fun processScannedQrCode(
        scannedData: String,
        onContractorSigned: (String) -> Unit, // Callback to show QR back to Laborer
        onLaborerVerified: () -> Unit,        // Callback to return to list
        onError: (String) -> Unit = {}        // NEW: Callback for UX error bubbling
    ) {
        viewModelScope.launch {
            try {
                if (scannedData.startsWith("REQ|")) {
                    // --- CONTRACTOR SCANNED A REQUEST ---
                    val dataPart = scannedData.removePrefix("REQ|")
                    val dataValues = dataPart.split(",")
                    val entryId = dataValues[0]
                    val date = dataValues[1].toLong()
                    val hours = dataValues[2].toFloat()
                    val wage = dataValues[3].toDouble()

                    // Sign it immediately
                    val signature = cryptoManager.signData(dataPart)
                    val pubKey = cryptoManager.getMyPublicKey()

                    // Save to Contractor's DB as Verified
                    val verifiedEntry = WorkEntry(
                        id = entryId, date = date, hoursWorked = hours, wageRate = wage,
                        isVerified = true, digitalSignature = signature,
                        contractorPublicKey = pubKey, contractorName = "My Signature"
                    )
                    repository.addWorkEntry(verifiedEntry)

                    // Tell UI to navigate to HandshakeScreen to show the signed QR
                    onContractorSigned(entryId)

                } else if (scannedData.startsWith("RES|")) {
                    // --- LABORER SCANNED A RESPONSE ---
                    val parts = scannedData.split("|")
                    val dataPart = parts[1]
                    val sigPart = parts[2].removePrefix("SIG:")
                    val pubPart = parts[3].removePrefix("PUB:")

                    val isValid = cryptoManager.verifySignature(dataPart, sigPart, pubPart)

                    if (isValid) {
                        val dataValues = dataPart.split(",")
                        val entryId = dataValues[0]
                        val date = dataValues[1].toLong()
                        val hours = dataValues[2].toFloat()
                        val wage = dataValues[3].toDouble()

                        val knownContractor = repository.getContractor(pubPart)

                        if (knownContractor != null) {
                            saveVerifiedEntry(entryId, date, hours, wage, sigPart, pubPart, knownContractor.name)
                            onLaborerVerified() // Close camera, we are done!
                        } else {
                            _showTofuDialog.value = PendingTofu(entryId, date, hours, wage, sigPart, pubPart)
                            onLaborerVerified()
                        }
                    } else {
                        Log.e("SAAKH_SECURITY", "Signature Verification Failed!")
                        onError("Signature Verification Failed!")
                    }
                }
            } catch (e: Exception) {
                Log.e("SAAKH_ERROR", "Failed to parse QR code: ${e.message}", e)
                onError("Scanner Error: ${e.localizedMessage ?: "Failed to process QR"}")
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