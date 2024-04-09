package org.futo.circles.auth.feature.pass_phrase

import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.crypto.BSSPEKE_ALGORITHM_BACKUP
import org.matrix.android.sdk.api.session.securestorage.KeyInfoResult
import javax.inject.Inject

class EncryptionAlgorithmHelper @Inject constructor() {

    fun isBsSpekePassPhrase(): Boolean {
        val keyInfoResult = MatrixSessionProvider.getSessionOrThrow()
            .sharedSecretStorageService().getDefaultKey()
        if (!keyInfoResult.isSuccess()) return false
        val algo = (keyInfoResult as KeyInfoResult.Success).keyInfo.content.passphrase?.algorithm
        return algo == BSSPEKE_ALGORITHM_BACKUP
    }

}