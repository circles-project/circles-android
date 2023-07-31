package org.futo.circles.auth.feature.pass_phrase.create

import android.content.Context
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.cross_signing.CrossSigningDataSource
import org.futo.circles.auth.feature.pass_phrase.restore.SSSSDataSource
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysVersion
import org.matrix.android.sdk.api.session.crypto.keysbackup.MegolmBackupCreationInfo
import org.matrix.android.sdk.api.session.crypto.keysbackup.toKeysVersionResult
import org.matrix.android.sdk.api.util.awaitCallback
import javax.inject.Inject

class CreatePassPhraseDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ssssDataSource: SSSSDataSource,
    private val crossSigningDataSource: CrossSigningDataSource
) {

    private val keysBackupService by lazy {
        MatrixSessionProvider.getSessionOrThrow().cryptoService().keysBackupService()
    }
    val loadingLiveData = MutableLiveData<LoadingData>()
    private val passPhraseLoadingData = LoadingData()

    suspend fun createPassPhraseBackup(userName: String, passphrase: String) {
        loadingLiveData.postValue(passPhraseLoadingData.apply {
            this.total = 0
            messageId = R.string.generating_recovery_key
        })
        val backupCreationInfo = awaitCallback {
            keysBackupService.prepareBcryptKeysBackupVersion(userName, passphrase, it)
        }
        createKeyBackup(backupCreationInfo)
        val keyData = ssssDataSource.storeIntoSSSSWithPassphrase(passphrase, userName, true)
        crossSigningDataSource.initCrossSigningIfNeed(keyData.keySpec)
        loadingLiveData.postValue(passPhraseLoadingData.apply { isLoading = false })
    }

    suspend fun replacePassPhraseBackup(userId: String, passphrase: String) {
        val userName = userId.replace("@", "").substringBefore(":")
        removeCurrentBackupIfExist()
        createPassPhraseBackup(userName, passphrase)
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
            awaitCallback { keysBackupService.deleteBackup(version, it) }
        }
    }

    private suspend fun getCurrentBackupVersion() =
        awaitCallback { keysBackupService.getCurrentVersion(it) }.toKeysVersionResult()
}

