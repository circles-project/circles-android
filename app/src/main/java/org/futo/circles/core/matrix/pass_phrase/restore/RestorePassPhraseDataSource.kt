package org.futo.circles.core.matrix.pass_phrase.restore

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.futo.circles.R
import org.futo.circles.model.LoadingData
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.listeners.StepProgressListener
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupLastVersionResult
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupService
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysVersionResult
import org.matrix.android.sdk.api.session.crypto.keysbackup.toKeysVersionResult
import org.matrix.android.sdk.api.util.awaitCallback

class RestorePassPhraseDataSource(private val context: Context) {

    val loadingLiveData = MutableLiveData<LoadingData>()
    private val passPhraseLoadingData = LoadingData()

    private val progressObserver = object : StepProgressListener {
        override fun onStepProgress(step: StepProgressListener.Step) {
            when (step) {
                is StepProgressListener.Step.ComputingKey -> {
                    loadingLiveData.postValue(passPhraseLoadingData.apply {
                        this.progress = step.progress
                        this.total = step.total
                        messageId = R.string.computing_recovery_key
                    })
                }
                is StepProgressListener.Step.DownloadingKey -> {
                    loadingLiveData.postValue(passPhraseLoadingData.apply {
                        messageId = R.string.downloading_keys
                    })
                }
                is StepProgressListener.Step.ImportingKey -> {
                    loadingLiveData.postValue(passPhraseLoadingData.apply {
                        this.progress = step.progress
                        this.total = step.total
                        messageId = R.string.importing_keys
                    })
                }
            }
        }
    }

    suspend fun getEncryptionAlgorithm(): String? {
        val keyVersion = awaitCallback {
            MatrixSessionProvider.currentSession?.cryptoService()?.keysBackupService()
                ?.getCurrentVersion(it)
        }.toKeysVersionResult()

        return keyVersion?.algorithm
    }

    suspend fun restoreKeysWithPassPhase(passphrase: String) {
        val keysBackupService =
            MatrixSessionProvider.currentSession?.cryptoService()?.keysBackupService()
                ?: throw Exception(context.getString(R.string.session_is_not_created))
        val keyVersion = awaitCallback<KeysBackupLastVersionResult> {
            keysBackupService.getCurrentVersion(it)
        }.toKeysVersionResult()
            ?: throw Exception(context.getString(R.string.failed_to_get_restore_keys_version))

        try {
            awaitCallback {
                keysBackupService.restoreKeyBackupWithPassword(
                    keyVersion,
                    passphrase,
                    null,
                    MatrixSessionProvider.currentSession?.myUserId,
                    progressObserver,
                    it
                )
            }
            trustOnDecrypt(keysBackupService, keyVersion)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.backup_could_not_be_decrypted_with_passphrase))
        }
        loadingLiveData.postValue(passPhraseLoadingData.apply { isLoading = false })
    }

    private suspend fun trustOnDecrypt(
        keysBackup: KeysBackupService,
        keysVersionResult: KeysVersionResult
    ) {
        awaitCallback {
            keysBackup.trustKeysBackupVersion(keysVersionResult, true, it)
        }
    }
}