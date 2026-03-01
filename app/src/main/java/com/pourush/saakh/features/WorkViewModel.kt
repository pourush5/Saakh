package com.pourush.saakh.features
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pourush.saakh.core.crypto.SaakhCryptoManager
import com.pourush.saakh.core.database.WorkEntry
import com.pourush.saakh.core.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                    publicKeyBase64 = pubPart // <--- THIS WAS THE FIX
                )

                if (isValid) {
                    val dataValues = dataPart.split(",")
                    val date = dataValues[0].toLong()
                    val hours = dataValues[1].toFloat()
                    val wage = dataValues[2].toDouble()

                    val verifiedEntry = WorkEntry(
                        date = date,
                        hoursWorked = hours,
                        wageRate = wage,
                        isVerified = true,
                        digitalSignature = sigPart,
                        contractorPublicKey = pubPart // Save the contractor's identity!
                    )

                    repository.addWorkEntry(verifiedEntry)
                    Log.d("SAAKH_SUCCESS", "Verified entry saved to database!")

                } else {
                    Log.e("SAAKH_SECURITY", "Signature Verification Failed! Data was tampered with.")
                }
            } catch (e: Exception) {
                Log.e("SAAKH_ERROR", "Failed to parse QR code", e)
            }
        }
    }
}