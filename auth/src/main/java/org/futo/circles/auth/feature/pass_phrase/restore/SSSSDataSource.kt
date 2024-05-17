package org.futo.circles.auth.feature.pass_phrase.restore

import org.futo.circles.auth.bsspeke.BSSpekeClientProvider
import org.futo.circles.auth.model.SecretKeyData
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.listeners.StepProgressListener
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.crosssigning.KEYBACKUP_SECRET_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.keysbackup.extractCurveKeyFromRecoveryKey
import org.matrix.android.sdk.api.session.securestorage.EmptyKeySigner
import org.matrix.android.sdk.api.session.securestorage.KeyInfo
import org.matrix.android.sdk.api.session.securestorage.KeyInfoResult
import org.matrix.android.sdk.api.session.securestorage.KeyRef
import org.matrix.android.sdk.api.session.securestorage.RawBytesKeySpec
import org.matrix.android.sdk.api.session.securestorage.SsssKeyCreationInfo
import org.matrix.android.sdk.api.session.securestorage.SsssKeySpec
import org.matrix.android.sdk.api.util.toBase64NoPadding
import javax.inject.Inject

class SSSSDataSource @Inject constructor() {

    suspend fun storeBsSpekeKeyIntoSSSS(keyBackupPrivateKey: ByteArray): SsssKeySpec {
        val session = MatrixSessionProvider.getSessionOrThrow()
        val bsSpekeClient = BSSpekeClientProvider.getClientOrThrow()
        val keyId = bsSpekeClient.generateKeyId()
        val key = bsSpekeClient.generateHashKey()
        val keyInfo = session.sharedSecretStorageService()
            .generateBsSpekeKeyInfo(keyId, key, EmptyKeySigner())
        storeSecret(session, keyBackupPrivateKey, keyInfo)
        return keyInfo.keySpec
    }

    suspend fun getBsSpekeSecretKeyData(progressObserver: StepProgressListener): SecretKeyData {
        progressObserver.onStepProgress(
            StepProgressListener.Step.ComputingKey(0, 0)
        )
        val keyInfo = getKeyInfo()
        val keySpec = RawBytesKeySpec(
            BSSpekeClientProvider.getClientOrThrow().generateHashKey()
        )

        val secret = getSecret(keyInfo, keySpec)
            ?: throw Exception("Backup could not be decrypted with this passphrase")

        return SecretKeyData(secret, keySpec)
    }

    suspend fun replaceBsSpeke4SKey() {
        val recoveryKey = MatrixSessionProvider.getSessionOrThrow()
            .cryptoService().keysBackupService()
            .getKeyBackupRecoveryKeyInfo()?.recoveryKey?.toBase58()
            ?: throw Exception("Recovery Key not found")
        val secret = extractCurveKeyFromRecoveryKey(recoveryKey)
            ?: throw Exception("Can not get secret from recovery key")
        storeBsSpekeKeyIntoSSSS(secret)
    }

    suspend fun getSecretKeyDataFromPassphrase(
        passphrase: String,
        progressObserver: StepProgressListener
    ): SecretKeyData {
        val keyInfo = getKeyInfo()

        progressObserver.onStepProgress(
            StepProgressListener.Step.ComputingKey(0, 0)
        )
        val salt = keyInfo.content.passphrase?.salt ?: ""
        val iterations = keyInfo.content.passphrase?.iterations ?: 0

        val keySpec = RawBytesKeySpec.fromPassphrase(
            passphrase, salt, iterations,
            object : ProgressListener {
                override fun onProgress(progress: Int, total: Int) {
                    progressObserver.onStepProgress(
                        StepProgressListener.Step.ComputingKey(progress, total)
                    )
                }
            })

        val secret = getSecret(keyInfo, keySpec)
            ?: throw Exception("Backup could not be decrypted with this passphrase")

        return SecretKeyData(secret, keySpec)
    }

    suspend fun getSecretKeyDataKeyFromFileKey(
        recoveryKey: String,
        progressObserver: StepProgressListener
    ): SecretKeyData {
        val keyInfo = getKeyInfo()

        progressObserver.onStepProgress(
            StepProgressListener.Step.ComputingKey(0, 0)
        )
        val keySpec = RawBytesKeySpec.fromRecoveryKey(recoveryKey)
            ?: throw Exception("It's not a valid recovery key")

        val secret = getSecret(keyInfo, keySpec)
            ?: throw Exception("Backup could not be decrypted with this recovery key")

        return SecretKeyData(secret, keySpec)
    }

    private suspend fun storeSecret(
        session: Session,
        keyBackupPrivateKey: ByteArray,
        keyInfo: SsssKeyCreationInfo
    ) {
        session.sharedSecretStorageService().storeSecret(
            name = KEYBACKUP_SECRET_SSSS_NAME,
            secretBase64 = keyBackupPrivateKey.toBase64NoPadding(),
            keys = listOf(KeyRef(keyInfo.keyId, keyInfo.keySpec))
        )
        session.cryptoService().keysBackupService()
            .onSecretKeyGossip(keyBackupPrivateKey.toBase64NoPadding())

        session.sharedSecretStorageService().setDefaultKey(keyInfo.keyId)
    }

    private fun getKeyInfo(): KeyInfo {
        val keyInfoResult = MatrixSessionProvider.getSessionOrThrow()
            .sharedSecretStorageService().getDefaultKey()
        if (!keyInfoResult.isSuccess())
            throw Exception("Failed to access secure storage")

        return (keyInfoResult as KeyInfoResult.Success).keyInfo
    }

    private suspend fun getSecret(
        keyInfo: KeyInfo,
        keySpec: RawBytesKeySpec
    ): String? = MatrixSessionProvider.currentSession?.sharedSecretStorageService()?.getSecret(
        name = KEYBACKUP_SECRET_SSSS_NAME,
        keyId = keyInfo.id,
        secretKey = keySpec
    )
}