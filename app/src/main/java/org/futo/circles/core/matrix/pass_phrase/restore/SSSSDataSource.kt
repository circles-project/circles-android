package org.futo.circles.core.matrix.pass_phrase.restore

import android.content.Context
import org.futo.circles.R
import org.futo.circles.model.KeyData
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.listeners.StepProgressListener
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.crosssigning.KEYBACKUP_SECRET_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.keysbackup.computeRecoveryKey
import org.matrix.android.sdk.api.session.crypto.keysbackup.extractCurveKeyFromRecoveryKey
import org.matrix.android.sdk.api.session.securestorage.*
import org.matrix.android.sdk.api.util.fromBase64
import org.matrix.android.sdk.api.util.toBase64NoPadding
import java.util.*

class SSSSDataSource(private val context: Context) {

    fun isBackupKeyInQuadS(): Boolean {
        val session = MatrixSessionProvider.currentSession ?: return false
        val sssBackupSecret = session.accountDataService().getUserAccountDataEvent(
            KEYBACKUP_SECRET_SSSS_NAME
        ) ?: return false
        val defaultKeyResult = session.sharedSecretStorageService().getDefaultKey()
        val keyInfo = (defaultKeyResult as? KeyInfoResult.Success)?.keyInfo ?: return false
        return (sssBackupSecret.content["encrypted"] as? Map<*, *>)?.containsKey(keyInfo.id) == true
    }

    suspend fun storeIntoSSSSWithKey(recoveryKey: String): KeyData {
        val session = MatrixSessionProvider.currentSession
            ?: throw Exception(context.getString(R.string.session_is_not_created))
        val quadS = session.sharedSecretStorageService()
        val keyInfo = quadS.generateKey(
            UUID.randomUUID().toString(),
            RawBytesKeySpec.fromRecoveryKey(recoveryKey),
            "ssss_key",
            EmptyKeySigner()
        )
        storeSecret(session, keyInfo)
        return KeyData(keyInfo.recoveryKey, keyInfo.keySpec)
    }

    suspend fun storeIntoSSSSWithPassphrase(
        passphrase: String,
        userName: String,
        isBsSpeke: Boolean
    ): KeyData {
        val session = MatrixSessionProvider.currentSession
            ?: throw Exception(context.getString(R.string.session_is_not_created))
        val quadS = session.sharedSecretStorageService()
        val keyInfo = quadS.generateKeyWithPassphrase(
            UUID.randomUUID().toString(),
            "ssss_key",
            passphrase,
            EmptyKeySigner(),
            null,
            userName, isBsSpeke
        )
        storeSecret(session, keyInfo)
        return KeyData(keyInfo.recoveryKey, keyInfo.keySpec)
    }

    suspend fun getRecoveryKeyFromPassphrase(
        context: Context,
        passphrase: String,
        progressObserver: StepProgressListener,
        isBsSpeke: Boolean
    ): KeyData {
        val session = MatrixSessionProvider.currentSession
            ?: throw Exception(context.getString(R.string.session_is_not_created))

        val keyInfo = getKeyInfo(session, context)

        progressObserver.onStepProgress(
            StepProgressListener.Step.ComputingKey(0, 0)
        )

        val keySpec = RawBytesKeySpec.fromPassphrase(
            passphrase,
            keyInfo.content.passphrase?.salt ?: "",
            keyInfo.content.passphrase?.iterations ?: 0,
            object : ProgressListener {
                override fun onProgress(progress: Int, total: Int) {
                    progressObserver.onStepProgress(
                        StepProgressListener.Step.ComputingKey(progress, total)
                    )
                }
            },
            isBsSpeke
        )
        val secret = getSecret(session, keyInfo, keySpec)
            ?: throw Exception(context.getString(R.string.backup_could_not_be_decrypted_with_passphrase))

        return KeyData(computeRecoveryKey(secret.fromBase64()), keySpec)
    }

    suspend fun getRecoveryKeyFromFileKey(
        context: Context,
        recoveryKey: String,
        progressObserver: StepProgressListener
    ): KeyData {
        val session = MatrixSessionProvider.currentSession
            ?: throw Exception(context.getString(R.string.session_is_not_created))

        val keyInfo = getKeyInfo(session, context)

        progressObserver.onStepProgress(
            StepProgressListener.Step.ComputingKey(0, 0)
        )
        val keySpec = RawBytesKeySpec.fromRecoveryKey(recoveryKey)
            ?: throw Exception(context.getString(R.string.invalid_recovery_key))

        val secret = getSecret(session, keyInfo, keySpec)
            ?: throw Exception(context.getString(R.string.backup_could_not_be_decrypted_with_key))

        return KeyData(computeRecoveryKey(secret.fromBase64()), keySpec)
    }

    private suspend fun storeSecret(
        session: Session,
        keyInfo: SsssKeyCreationInfo
    ) {
        val secret =
            extractCurveKeyFromRecoveryKey(keyInfo.recoveryKey)?.toBase64NoPadding() ?: return
        session.sharedSecretStorageService().storeSecret(
            name = KEYBACKUP_SECRET_SSSS_NAME,
            secretBase64 = secret,
            keys = listOf(KeyRef(keyInfo.keyId, keyInfo.keySpec))
        )
        session.sharedSecretStorageService().setDefaultKey(keyInfo.keyId)
    }

    private fun getKeyInfo(session: Session, context: Context): KeyInfo {
        val keyInfoResult = session.sharedSecretStorageService().getDefaultKey()
        if (!keyInfoResult.isSuccess())
            throw Exception(context.getString(R.string.failed_to_access_secure_storage))

        return (keyInfoResult as KeyInfoResult.Success).keyInfo
    }

    private suspend fun getSecret(
        session: Session,
        keyInfo: KeyInfo,
        keySpec: RawBytesKeySpec
    ): String? = session.accountDataService()
        .getUserAccountDataEvent(KEYBACKUP_SECRET_SSSS_NAME)?.let {
            session.sharedSecretStorageService().getSecret(
                name = KEYBACKUP_SECRET_SSSS_NAME,
                keyId = keyInfo.id,
                secretKey = keySpec
            )
        }
}