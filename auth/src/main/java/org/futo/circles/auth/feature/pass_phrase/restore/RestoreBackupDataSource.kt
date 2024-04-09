package org.futo.circles.auth.feature.pass_phrase.restore

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.cross_signing.CrossSigningDataSource
import org.futo.circles.auth.model.KeyData
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.listeners.StepProgressListener
import org.matrix.android.sdk.api.session.crypto.keysbackup.BackupRecoveryKey
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupService
import org.matrix.android.sdk.api.session.crypto.keysbackup.toKeysVersionResult
import javax.inject.Inject

class RestoreBackupDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ssssDataSource: SSSSDataSource,
    private val crossSigningDataSource: CrossSigningDataSource
) {

    val loadingLiveData = MutableLiveData(LoadingData(isLoading = false))

    private val progressObserver = object : StepProgressListener {
        override fun onStepProgress(step: StepProgressListener.Step) {
            when (step) {
                is StepProgressListener.Step.ComputingKey -> {
                    loadingLiveData.postValue(
                        LoadingData(
                            R.string.computing_recovery_key,
                            step.progress,
                            step.total,
                            true
                        )
                    )
                }

                is StepProgressListener.Step.DownloadingKey -> {
                    loadingLiveData.postValue(
                        LoadingData(
                            messageId = R.string.downloading_keys,
                            isLoading = true
                        )
                    )
                }

                is StepProgressListener.Step.ImportingKey -> {
                    loadingLiveData.postValue(
                        LoadingData(
                            R.string.importing_keys,
                            step.progress,
                            step.total,
                            true
                        )
                    )
                }

                is StepProgressListener.Step.DecryptingKey -> {
                    loadingLiveData.postValue(
                        LoadingData(
                            R.string.decrypting_key,
                            step.progress,
                            step.total,
                            true
                        )
                    )
                }
            }
        }
    }

    suspend fun restoreWithBsSpekeKey() {
        try {
            val keyData = ssssDataSource.getBsSpekeRecoveryKey(progressObserver)
            restoreKeysWithRecoveryKey(keyData)
        } catch (e: Throwable) {
            loadingLiveData.postValue(LoadingData(isLoading = false))
            throw e
        }
        loadingLiveData.postValue(LoadingData(isLoading = false))
    }

    suspend fun restoreKeysWithPassPhase(passphrase: String) {
        try {
            val keyData =
                ssssDataSource.getRecoveryKeyFromPassphrase(passphrase, progressObserver)
            restoreKeysWithRecoveryKey(keyData)
        } catch (e: Throwable) {
            loadingLiveData.postValue(LoadingData(isLoading = false))
            throw e
        }
        loadingLiveData.postValue(LoadingData(isLoading = false))
    }

    private suspend fun restoreKeysWithRecoveryKey(keyData: KeyData) {
        val keysBackupService = getKeysBackupService()
        try {
            val keyVersion = getKeysVersion(keysBackupService)
            keysBackupService.restoreKeysWithRecoveryKey(
                keyVersion,
                BackupRecoveryKey.fromBase58(keyData.recoveryKey),
                null,
                MatrixSessionProvider.currentSession?.myUserId,
                progressObserver
            )
            crossSigningDataSource.configureCrossSigning(keyData.keySpec)
            keysBackupService.trustKeysBackupVersion(keyVersion, true)
        } catch (e: Throwable) {
            loadingLiveData.postValue(LoadingData(isLoading = false))
            throw e
        }
        loadingLiveData.postValue(LoadingData(isLoading = false))
    }

    suspend fun restoreKeysWithRawKey(rawKey: String) {
        try {
            val keyData = ssssDataSource.getRecoveryKeyFromFileKey(rawKey, progressObserver)
            restoreKeysWithRecoveryKey(keyData)
        } catch (e: Throwable) {
            loadingLiveData.postValue(LoadingData(isLoading = false))
            throw e
        }
        loadingLiveData.postValue(LoadingData(isLoading = false))
    }

    suspend fun restoreKeysWithRecoveryKey(uri: Uri) {
        try {
            val key = readRecoveryKeyFile(uri)
            val keyData = ssssDataSource.getRecoveryKeyFromFileKey(key, progressObserver)
            restoreKeysWithRecoveryKey(keyData)
        } catch (e: Throwable) {
            loadingLiveData.postValue(LoadingData(isLoading = false))
            throw e
        }
        loadingLiveData.postValue(LoadingData(isLoading = false))
    }

    @SuppressLint("Recycle")
    private fun readRecoveryKeyFile(uri: Uri): String {
        val recoveryKey = context.contentResolver
            .openInputStream(uri)
            ?.bufferedReader()
            ?.use { it.readText() }

        return recoveryKey?.takeIf { it.isNotEmpty() }
            ?: throw Exception(context.getString(R.string.backup_could_not_be_decrypted_with_key))
    }

    private fun getKeysBackupService() =
        MatrixSessionProvider.getSessionOrThrow().cryptoService().keysBackupService()

    private suspend fun getKeysVersion(keysBackupService: KeysBackupService) =
        tryOrNull { keysBackupService.getCurrentVersion() }?.toKeysVersionResult()
            ?: throw Exception(context.getString(R.string.failed_to_get_restore_keys_version))
}