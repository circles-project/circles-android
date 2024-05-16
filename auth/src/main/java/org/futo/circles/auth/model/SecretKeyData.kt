package org.futo.circles.auth.model

import org.matrix.android.sdk.api.session.crypto.keysbackup.BackupRecoveryKey
import org.matrix.android.sdk.api.session.crypto.keysbackup.computeRecoveryKey
import org.matrix.android.sdk.api.session.securestorage.SsssKeySpec
import org.matrix.android.sdk.api.util.fromBase64

data class SecretKeyData(
    val secretBase64: String,
    val keySpec: SsssKeySpec
) {
    fun getBackupRecoveryKey(): BackupRecoveryKey {
        val recoveryBase58 = computeRecoveryKey(secretBase64.fromBase64())
        return BackupRecoveryKey.fromBase58(recoveryBase58)
    }

}