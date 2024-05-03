package org.futo.circles.auth.feature.pass_phrase.create

import android.content.Context
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.cross_signing.CrossSigningDataSource
import org.futo.circles.auth.feature.pass_phrase.restore.SSSSDataSource
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.crypto.keysbackup.MegolmBackupCreationInfo
import org.matrix.android.sdk.api.session.crypto.keysbackup.toKeysVersionResult
import java.security.SecureRandom
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

    suspend fun createPassPhraseBackup() {
        loadingLiveData.postValue(LoadingData(messageId = R.string.generating_recovery_key))
        val keyBackupPrivateKey = generateRandomPrivateKey()
        val backupCreationInfo =
            keysBackupService.prepareKeysBackupVersion(keyBackupPrivateKey, null)
        createKeyBackup(backupCreationInfo)
        val keyData = ssssDataSource.storeBsSpekeKeyIntoSSSS(keyBackupPrivateKey)
        crossSigningDataSource.initCrossSigningIfNeed(keyData.keySpec)
        MatrixSessionProvider.currentSession?.cryptoService()?.createDehydratedDevice(keyBackupPrivateKey)
        loadingLiveData.postValue(LoadingData(isLoading = false))
    }

    suspend fun replaceToNewKeyBackup() {
        removeCurrentBackupIfExist()
        createPassPhraseBackup()
    }

    suspend fun changeBsSpekePassword4SKey() {
        loadingLiveData.postValue(LoadingData(messageId = R.string.creating_backup))
        ssssDataSource.replaceBsSpeke4SKey()
        loadingLiveData.postValue(LoadingData(isLoading = false))
    }

    private fun generateRandomPrivateKey(): ByteArray {
        val privateKey = ByteArray(32) { 0 }
        SecureRandom().nextBytes(privateKey)
        return privateKey
    }

    private suspend fun createKeyBackup(
        backupCreationInfo: MegolmBackupCreationInfo
    ) {
        loadingLiveData.postValue(LoadingData(messageId = R.string.creating_backup))
        val versionData = getCurrentBackupVersion()

        if (versionData?.version.isNullOrBlank())
            keysBackupService.createKeysBackupVersion(backupCreationInfo)
        else throw Exception(context.getString(R.string.backup_already_exist))
    }

    private suspend fun removeCurrentBackupIfExist() {
        loadingLiveData.postValue(LoadingData(messageId = R.string.removing_backup))
        getCurrentBackupVersion()?.version?.let { version ->
            keysBackupService.deleteBackup(version)
        }
    }

    private suspend fun getCurrentBackupVersion() =
        tryOrNull { keysBackupService.getCurrentVersion() }?.toKeysVersionResult()
}

