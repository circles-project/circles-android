package org.futo.circles.auth.feature.pass_phrase

import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.crypto.BCRYPT_ALGORITHM_BACKUP
import org.matrix.android.sdk.api.crypto.BSSPEKE_ALGORITHM_BACKUP
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.crypto.keysbackup.toKeysVersionResult
import org.matrix.android.sdk.api.session.securestorage.KeyInfoResult
import org.matrix.android.sdk.api.util.awaitCallback
import javax.inject.Inject

class EncryptionAlgorithmHelper @Inject constructor() {

    suspend fun isBcryptAlgorithm(): Boolean = getEncryptionAlgorithm() == BCRYPT_ALGORITHM_BACKUP

    fun isBsSpekePassPhrase(): Boolean {
        val keyInfoResult = MatrixSessionProvider.getSessionOrThrow()
            .sharedSecretStorageService().getDefaultKey()
        if (!keyInfoResult.isSuccess()) return false
        val algo = (keyInfoResult as KeyInfoResult.Success).keyInfo.content.passphrase?.algorithm
        return algo == BSSPEKE_ALGORITHM_BACKUP
    }

    private suspend fun getEncryptionAlgorithm(): String? {
        val keyVersion = tryOrNull {
            MatrixSessionProvider.currentSession?.cryptoService()?.keysBackupService()
                ?.getCurrentVersion()
        }?.toKeysVersionResult()

        return keyVersion?.algorithm
    }

}