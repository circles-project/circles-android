package org.futo.circles.core.matrix.pass_phrase.create

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.futo.circles.R
import org.futo.circles.model.LoadingData
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupLastVersionResult
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysVersion
import org.matrix.android.sdk.api.session.crypto.keysbackup.MegolmBackupCreationInfo
import org.matrix.android.sdk.api.session.crypto.keysbackup.toKeysVersionResult
import org.matrix.android.sdk.api.util.awaitCallback

class CreatePassPhraseDataSource(private val context: Context) {

    private val keysBackupService by lazy {
        MatrixSessionProvider.currentSession?.cryptoService()?.keysBackupService()
            ?: throw Exception(context.getString(R.string.session_is_not_created))
    }
    val loadingLiveData = MutableLiveData<LoadingData>()
    private val passPhraseLoadingData = LoadingData()

    suspend fun createPassPhraseBackup(passphrase: String) {
        val backupCreationInfo = awaitCallback<MegolmBackupCreationInfo> {
            keysBackupService.prepareKeysBackupVersion(
                passphrase,
                object : ProgressListener {
                    override fun onProgress(progress: Int, total: Int) {
                        loadingLiveData.postValue(passPhraseLoadingData.apply {
                            this.progress = progress
                            this.total = total
                            messageId = R.string.generating_recovery_key
                        })
                    }
                }, it
            )
        }
        createKeyBackup(backupCreationInfo)
        loadingLiveData.postValue(passPhraseLoadingData.apply { isLoading = false })
    }

    suspend fun replacePassPhraseBackup(passphrase: String) {
        removeCurrentBackupIfExist()
        createPassPhraseBackup(passphrase)
    }

    private suspend fun createKeyBackup(
        backupCreationInfo: MegolmBackupCreationInfo
    ) {
        loadingLiveData.postValue(passPhraseLoadingData.apply {
            messageId = R.string.creating_backup
        })
        val versionData = getCurrentBackupVersion()

        if (versionData?.version.isNullOrBlank()) {
            awaitCallback<KeysVersion> {
                keysBackupService.createKeysBackupVersion(backupCreationInfo, it)
            }
        } else throw Exception(context.getString(R.string.backup_already_exist))
    }

    private suspend fun removeCurrentBackupIfExist() {
        loadingLiveData.postValue(passPhraseLoadingData.apply {
            messageId = R.string.removing_backup
        })
        getCurrentBackupVersion()?.version?.let { version ->
            awaitCallback<Unit> { keysBackupService.deleteBackup(version, it) }
        }
    }

    private suspend fun getCurrentBackupVersion() =
        awaitCallback<KeysBackupLastVersionResult> { keysBackupService.getCurrentVersion(it) }
            .toKeysVersionResult()
}

