package com.futo.circles.core.matrix.pass_phrase

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.futo.circles.R
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupService
import org.matrix.android.sdk.internal.crypto.keysbackup.model.MegolmBackupCreationInfo
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.KeysVersion
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.KeysVersionResult
import org.matrix.android.sdk.internal.util.awaitCallback

class CreatePassPhraseDataSource(private val context: Context) {

    private val session by lazy { MatrixSessionProvider.currentSession }
    private val loadingLiveData = MutableLiveData<CreatePassPhraseLoadingData>()
    val passPhraseLoadingData = CreatePassPhraseLoadingData()

    suspend fun createPassPhraseBackup(passphrase: String) {
        val keyBackupService = session?.cryptoService()?.keysBackupService()
            ?: throw Exception(context.getString(R.string.session_is_not_created))

        val backupCreationInfo = awaitCallback<MegolmBackupCreationInfo> {
            keyBackupService.prepareKeysBackupVersion(
                passphrase,
                object : ProgressListener {
                    override fun onProgress(progress: Int, total: Int) {
                        loadingLiveData.postValue(passPhraseLoadingData.apply {
                            setProgress(progress, total)
                        })
                    }
                }, it
            )
        }

        createKeyBackup(keyBackupService, backupCreationInfo)
    }

    private suspend fun createKeyBackup(
        keysBackupService: KeysBackupService,
        backupCreationInfo: MegolmBackupCreationInfo
    ) {
        loadingLiveData.postValue(passPhraseLoadingData.apply {
            messageId = R.string.creating_backup
        })
        val versionData =
            awaitCallback<KeysVersionResult?> { keysBackupService.getCurrentVersion(it) }

        if (versionData?.version.isNullOrBlank()) {
            awaitCallback<KeysVersion> {
                keysBackupService.createKeysBackupVersion(backupCreationInfo, it)
            }
        } else throw Exception(context.getString(R.string.backup_already_exist))
    }
}

