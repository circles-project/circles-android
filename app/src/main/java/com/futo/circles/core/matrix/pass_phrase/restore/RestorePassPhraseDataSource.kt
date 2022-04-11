package com.futo.circles.core.matrix.pass_phrase.restore

import android.content.Context
import com.futo.circles.R
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.session.crypto.crosssigning.KEYBACKUP_SECRET_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupService
import org.matrix.android.sdk.api.session.securestorage.KeyInfo
import org.matrix.android.sdk.api.session.securestorage.KeyInfoResult
import org.matrix.android.sdk.api.session.securestorage.RawBytesKeySpec
import org.matrix.android.sdk.api.session.securestorage.SharedSecretStorageService
import org.matrix.android.sdk.internal.crypto.crosssigning.fromBase64
import org.matrix.android.sdk.internal.crypto.crosssigning.toBase64NoPadding
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.KeysVersionResult
import org.matrix.android.sdk.internal.crypto.keysbackup.util.computeRecoveryKey
import org.matrix.android.sdk.internal.crypto.model.ImportRoomKeysResult
import org.matrix.android.sdk.internal.util.awaitCallback
import java.io.ByteArrayOutputStream

class RestorePassPhraseDataSource(private val context: Context) {

    private val session by lazy { MatrixSessionProvider.currentSession }

    private suspend fun restoreKeysWithPassPhase(passphrase: String) {
        val secretStorageService = session?.sharedSecretStorageService
            ?: throw Exception(context.getString(R.string.session_is_not_created))

        val keyInfo = getDefaultKeyInfo(secretStorageService)
        val keySpec = restoreKeySpec(passphrase, keyInfo)
        getCypherResult(secretStorageService, keyInfo, keySpec)
    }

    private fun getDefaultKeyInfo(secretStorageService: SharedSecretStorageService): KeyInfo {
        val keyInfoResult = secretStorageService.getDefaultKey()
        return (keyInfoResult as? KeyInfoResult.Success)?.keyInfo
            ?: throw Exception(context.getString(R.string.can_not_find_default_key))
    }

    private fun restoreKeySpec(passphrase: String, keyInfo: KeyInfo): RawBytesKeySpec {
        val salt = keyInfo.content.passphrase?.salt ?: ""
        val iterations = keyInfo.content.passphrase?.iterations ?: 0

        return RawBytesKeySpec.fromPassphrase(passphrase, salt, iterations,
            object : ProgressListener {
                override fun onProgress(progress: Int, total: Int) {

                }
            }
        )
    }

    private suspend fun getCypherResult(
        secretStorageService: SharedSecretStorageService,
        keyInfo: KeyInfo,
        keySpec: RawBytesKeySpec
    ): String {
        try {
            val decryptedSecretMap = mutableMapOf<String, String>()
            session?.accountDataService()?.getUserAccountDataEvent(KEYBACKUP_SECRET_SSSS_NAME)
                ?.let {
                    decryptedSecretMap[KEYBACKUP_SECRET_SSSS_NAME] = secretStorageService.getSecret(
                        KEYBACKUP_SECRET_SSSS_NAME,
                        keyInfo.id,
                        keySpec
                    )
                }
            return ByteArrayOutputStream().also {
                it.use { session?.securelyStoreObject(decryptedSecretMap, resultKeyStoreAlias, it) }
            }.toByteArray().toBase64NoPadding()

        } catch (e: Exception) {
            throw Exception(context.getString(R.string.backup_could_not_be_decrypted_with_passphrase))
        }
    }

    private suspend fun handleGotSecretFromSSSS(cipherData: String) {
        cipherData.fromBase64().inputStream().use { ins ->
            val res = session?.loadSecureSecret<Map<String, String>>(ins, resultKeyStoreAlias)
            val secret = res?.get(KEYBACKUP_SECRET_SSSS_NAME)
                ?: throw Exception(context.getString(R.string.backup_could_not_be_decrypted_with_passphrase))

            loadingEvent.value =
                WaitingViewData(stringProvider.getString(R.string.keys_backup_restore_is_getting_backup_version))

            recoverUsingBackupRecoveryKey(computeRecoveryKey(secret.fromBase64()))
        }
    }

    private suspend fun recoverUsingBackupRecoveryKey(recoveryKey: String) {
        val keysBackup = session?.cryptoService()?.keysBackupService()
            ?: throw Exception(context.getString(R.string.session_is_not_created))
        val keyVersion = awaitCallback<KeysVersionResult?> {
            keysBackup.getCurrentVersion(it)
        } ?: throw Exception(context.getString(R.string.failed_to_get_restore_keys_version))


        //loadingEvent.postValue(WaitingViewData(stringProvider.getString(R.string.loading)))


        val result = awaitCallback<ImportRoomKeysResult> {
            keysBackup.restoreKeysWithRecoveryKey(
                keyVersion,
                recoveryKey,
                null,
                session?.myUserId,
                progressObserver,
                it
            )
        }

        trustOnDecrypt(keysBackup, keyVersion)
    }

    private suspend fun trustOnDecrypt(
        keysBackup: KeysBackupService,
        keysVersionResult: KeysVersionResult
    ) {
        awaitCallback<Unit> {
            keysBackup.trustKeysBackupVersion(keysVersionResult, true, it)
        }
    }


    companion object {
        private const val resultKeyStoreAlias = "RestorePassPhraseDataSource"
    }
}