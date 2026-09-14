package com.pourush.saakh.features

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pourush.saakh.core.crypto.SaakhCryptoManager
import com.pourush.saakh.core.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// State class to handle UI loading/success/error cleanly
sealed class QrState {
    object Loading : QrState()
    data class Success(val qrContent: String) : QrState()
    data class Error(val message: String) : QrState()
}

@HiltViewModel
class HandshakeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WorkRepository,
    private val cryptoManager: SaakhCryptoManager
) : ViewModel() {

    private val _qrState = MutableStateFlow<QrState>(QrState.Loading)
    val qrState: StateFlow<QrState> = _qrState.asStateFlow()

    init {
        // Grab the ID passed from the navigation route
        val entryId: String? = savedStateHandle["entryId"]
        if (entryId != null) {
            generateHandshakeData(entryId)
        } else {
            _qrState.value = QrState.Error("No entry ID provided")
        }
    }

    private fun generateHandshakeData(entryId: String) {
        viewModelScope.launch {
            val entry = repository.getWorkEntry(entryId)
            if (entry != null) {
                val payloadData = "${entry.id},${entry.date},${entry.hoursWorked},${entry.wageRate}"

                if (!entry.isVerified) {
                    // LABORER SIDE: Unverified. Generate a Request (REQ)
                    _qrState.value = QrState.Success("REQ|$payloadData")
                } else {
                    // CONTRACTOR SIDE: Verified. Generate a signed Response (RES)
                    try {
                        val signature = entry.digitalSignature ?: cryptoManager.signData(payloadData)
                        val publicKey = entry.contractorPublicKey ?: cryptoManager.getMyPublicKey()

                        _qrState.value = QrState.Success("RES|$payloadData|SIG:$signature|PUB:$publicKey")
                    } catch (e: Exception) {
                        _qrState.value = QrState.Error("Crypto Error: ${e.localizedMessage ?: "Unknown key error"}")
                    }
                }
            } else {
                _qrState.value = QrState.Error("Work entry not found")
            }
        }
    }
}
