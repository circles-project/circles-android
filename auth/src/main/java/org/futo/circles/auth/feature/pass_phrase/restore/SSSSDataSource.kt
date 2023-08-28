package org.futo.circles.auth.feature.pass_phrase.restore

import org.futo.circles.auth.bsspeke.BSSpekeClientProvider
import org.futo.circles.auth.model.KeyData
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.crypto.BCRYPT_ALGORITHM_BACKUP
import org.matrix.android.sdk.api.crypto.BSSPEKE_ALGORITHM_BACKUP
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM_BACKUP
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.listeners.StepProgressListener
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.crosssigning.KEYBACKUP_SECRET_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.keysbackup.computeRecoveryKey
import org.matrix.android.sdk.api.session.crypto.keysbackup.extractCurveKeyFromRecoveryKey
import org.matrix.android.sdk.api.session.securestorage.EmptyKeySigner
import org.matrix.android.sdk.api.session.securestorage.KeyInfo
import org.matrix.android.sdk.api.session.securestorage.KeyInfoResult
import org.matrix.android.sdk.api.session.securestorage.KeyRef
import org.matrix.android.sdk.api.session.securestorage.RawBytesKeySpec
import org.matrix.android.sdk.api.session.securestorage.SsssKeyCreationInfo
import org.matrix.android.sdk.api.util.fromBase64
import org.matrix.android.sdk.api.util.toBase64NoPadding
import java.util.UUID
import javax.inject.Inject

class SSSSDataSource @Inject constructor() {

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
        val session = MatrixSessionProvider.getSessionOrThrow()
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

    suspend fun storeBsSpekeKeyIntoSSSS(passphrase: String, key: ByteArray):KeyData {
        val session = MatrixSessionProvider.getSessionOrThrow()
        val keyId = BSSpekeClientProvider.getClientOrThrow().generateKeyId(passphrase)
        val keyInfo = session.sharedSecretStorageService()
            .generateBsSpekeKeyInfo(keyId, key, EmptyKeySigner())
        storeSecret(session, keyInfo)
        return KeyData(keyInfo.recoveryKey, keyInfo.keySpec)
    }

    suspend fun storeIntoSSSSWithPassphrase(
        passphrase: String,
        userName: String,
        algo: String
    ): KeyData {
        val session =
            MatrixSessionProvider.getSessionOrThrow()
        val quadS = session.sharedSecretStorageService()
        val keyInfo = when (algo) {
            MXCRYPTO_ALGORITHM_MEGOLM_BACKUP -> quadS.generateKeyWithPassphrase(
                UUID.randomUUID().toString(), "", passphrase, EmptyKeySigner(), null
            )

            BCRYPT_ALGORITHM_BACKUP -> quadS.generateBCryptKeyWithPassphrase(
                UUID.randomUUID().toString(), passphrase, EmptyKeySigner(), null, userName
            )

            else -> throw Exception("Unsupported algorithm $algo")
        }

        storeSecret(session, keyInfo)
        return KeyData(keyInfo.recoveryKey, keyInfo.keySpec)
    }

    suspend fun getRecoveryKeyFromPassphrase(
        passphrase: String,
        progressObserver: StepProgressListener,
        algo: String
    ): KeyData {
        val session = MatrixSessionProvider.getSessionOrThrow()

        val keyInfo = getKeyInfo(session)

        progressObserver.onStepProgress(
            StepProgressListener.Step.ComputingKey(0, 0)
        )
        val salt = keyInfo.content.passphrase?.salt ?: ""
        val iterations = keyInfo.content.passphrase?.iterations ?: 0

        val keySpec = when (algo) {
            MXCRYPTO_ALGORITHM_MEGOLM_BACKUP ->
                RawBytesKeySpec.fromPassphrase(
                    passphrase, salt, iterations,
                    object : ProgressListener {
                        override fun onProgress(progress: Int, total: Int) {
                            progressObserver.onStepProgress(
                                StepProgressListener.Step.ComputingKey(progress, total)
                            )
                        }
                    })

            BCRYPT_ALGORITHM_BACKUP -> RawBytesKeySpec.fromBCryptPassphrase(
                passphrase, salt, iterations
            )

            BSSPEKE_ALGORITHM_BACKUP -> RawBytesKeySpec(
                BSSpekeClientProvider.getClientOrThrow().generateHashKey(passphrase)
            )

            else -> throw Exception("Unsupported algorithm $algo")
        }

        val secret = getSecret(session, keyInfo, keySpec)
            ?: throw Exception("Backup could not be decrypted with this passphrase")

        return KeyData(computeRecoveryKey(secret.fromBase64()), keySpec)
    }

    suspend fun getRecoveryKeyFromFileKey(
        recoveryKey: String,
        progressObserver: StepProgressListener
    ): KeyData {
        val session = MatrixSessionProvider.getSessionOrThrow()

        val keyInfo = getKeyInfo(session)

        progressObserver.onStepProgress(
            StepProgressListener.Step.ComputingKey(0, 0)
        )
        val keySpec = RawBytesKeySpec.fromRecoveryKey(recoveryKey)
            ?: throw Exception("It's not a valid recovery key")

        val secret = getSecret(session, keyInfo, keySpec)
            ?: throw Exception("Backup could not be decrypted with this recovery key")

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

    private fun getKeyInfo(session: Session): KeyInfo {
        val keyInfoResult = session.sharedSecretStorageService().getDefaultKey()
        if (!keyInfoResult.isSuccess())
            throw Exception("Failed to access secure storage")

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