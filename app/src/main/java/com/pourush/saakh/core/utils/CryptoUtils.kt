package com.pourush.saakh.core.utils
import android.util.Base64
import java.security.MessageDigest

object CryptoUtils {
    /**
     * Converts a long Base64 Public Key into a short, human-readable fingerprint
     * Example output: A7F9-B2C4
     */
    fun generateKeyFingerprint(publicKeyBase64: String?): String {
        if (publicKeyBase64.isNullOrEmpty()) return "UNKNOWN"
        return try {
            val bytes = Base64.decode(publicKeyBase64, Base64.NO_WRAP)
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(bytes)

            // Take the first 4 bytes and convert them to uppercase Hex
            val hexString = hashBytes.take(4).joinToString("") { "%02X".format(it) }

            // Format with a dash for readability
            "${hexString.substring(0, 4)}-${hexString.substring(4, 8)}"
        } catch (e: Exception) {
            "ERROR"
        }
    }
}