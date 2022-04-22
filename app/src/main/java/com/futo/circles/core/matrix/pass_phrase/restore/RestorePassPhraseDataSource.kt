package com.futo.circles.core.matrix.pass_phrase.restore

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.futo.circles.R
import com.futo.circles.model.LoadingData
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.listeners.StepProgressListener
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupService
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.KeysVersionResult
import org.matrix.android.sdk.internal.crypto.model.ImportRoomKeysResult
import org.matrix.android.sdk.internal.util.awaitCallback

class RestorePassPhraseDataSource(private val context: Context) {

    private val session by lazy { MatrixSessionProvider.currentSession }
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


    suspend fun restoreKeysWithPassPhase(passphrase: String) {
        val keysBackupService = session?.cryptoService()?.keysBackupService()
            ?: throw Exception(context.getString(R.string.session_is_not_created))
        val keyVersion = awaitCallback<KeysVersionResult?> {
            keysBackupService.getCurrentVersion(it)
        } ?: throw Exception(context.getString(R.string.failed_to_get_restore_keys_version))

        try {
            awaitCallback<ImportRoomKeysResult> {
                keysBackupService.restoreKeyBackupWithPassword(
                    keyVersion,
                    passphrase,
                    null,
                    session?.myUserId,
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
        awaitCallback<Unit> {
            keysBackup.trustKeysBackupVersion(keysVersionResult, true, it)
        }
    }
}