package org.futo.circles.core.provider

import org.matrix.android.sdk.api.extensions.tryOrNull
import java.security.KeyStore
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class KeyStoreProvider @Inject constructor() {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore")

    fun storeBsSpekePrivateKey(keyBytes: ByteArray, keyId: String) {
        storeKey(keyBytes, "$ORG_FUTO_SSSS_KEY_PREFIX.$keyId")
    }

    fun getBsSpekePrivateKey(keyId: String): ByteArray? = getKey("$ORG_FUTO_SSSS_KEY_PREFIX.$keyId")

    private fun storeKey(keyBytes: ByteArray, alias: String) = tryOrNull {
        val secretKey: SecretKey = SecretKeySpec(keyBytes, "AES")
        keyStore.load(null)
        val protectionParameter = KeyStore.PasswordProtection(null)
        val secretKeyEntry = KeyStore.SecretKeyEntry(secretKey)
        keyStore.setEntry(alias, secretKeyEntry, protectionParameter)
    }


    private fun getKey(alias: String): ByteArray? = tryOrNull {
        keyStore.load(null)
        val secretKey = keyStore.getKey(alias, null) as SecretKey
        secretKey.encoded
    }

    companion object {
        private const val ORG_FUTO_SSSS_KEY_PREFIX = "org.futo.ssss.key"
    }
}
