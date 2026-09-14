package com.pourush.saakh.core.crypto
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
class SaakhCryptoManager {
    // The secure vault
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    // The name tag for our specific key
    private val KEY_ALIAS = "saakh_user_identity"

    init {
        createKeyIfNotExists()
    }

    // 1. Generate the Identity (Runs once on first app launch)
    private fun createKeyIfNotExists() {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC, // Elliptic Curve (Fast & Small, perfect for QR)
                "AndroidKeyStore"
            )

            val spec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
                .setDigests(KeyProperties.DIGEST_SHA256)
                // We don't require user authentication (Biometrics/PIN) for the MVP
                // to keep it accessible for laborers who might not use lock screens.
                .build()

            keyGenerator.initialize(spec)
            keyGenerator.generateKeyPair()
        }
    }

    // 2. Get the Public Key (To share with others via QR)
    fun getMyPublicKey(): String {
        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        val publicKeyBytes = entry.certificate.publicKey.encoded
        // Convert to Base64 so it can be passed as a normal String
        return Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP)
    }

    // 3. Sign the Data (Contractor uses this to approve hours)
    fun signData(data: String): String {
        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        val privateKey: PrivateKey = entry.privateKey

        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(data.toByteArray(Charsets.UTF_8))

        val signatureBytes = signature.sign()
        return Base64.encodeToString(signatureBytes, Base64.NO_WRAP)
    }

    // 4. Verify the Data (Laborer uses this to verify the Contractor's signature)
    fun verifySignature(data: String, signatureBase64: String, publicKeyBase64: String): Boolean {
        val signatureBytes = Base64.decode(signatureBase64, Base64.NO_WRAP)
        val publicKeyBytes = Base64.decode(publicKeyBase64, Base64.NO_WRAP)

        // Reconstruct the Public Key from the Base64 string
        val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC)
        val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(publicKeyBytes))

        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initVerify(publicKey)
        signature.update(data.toByteArray(Charsets.UTF_8))

        return signature.verify(signatureBytes)
    }
}