package org.futo.circles.feature.settings.change_password

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.matrix.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.core.matrix.pass_phrase.restore.RestorePassPhraseDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.launchBg
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.crypto.BCRYPT_ALGORITHM_BACKUP

class ChangePasswordViewModel(
    private val changePasswordDataSource: ChangePasswordDataSource,
    private val createPassPhraseDataSource: CreatePassPhraseDataSource,
    private val restorePassPhraseDataSource: RestorePassPhraseDataSource
) : ViewModel() {

    val responseLiveData = SingleEventLiveData<Response<Unit?>>()
    val passPhraseLoadingLiveData = createPassPhraseDataSource.loadingLiveData

    fun changePassword(oldPassword: String, newPassword: String) {
        launchBg {
            when (val changePasswordResult =
                changePasswordDataSource.changePassword(oldPassword, newPassword)) {
                is Response.Error -> responseLiveData.postValue(changePasswordResult)
                is Response.Success -> createNewBackupInNeeded(newPassword)
            }
        }
    }

    private suspend fun createNewBackupInNeeded(newPassword: String) {
        val algorithm = restorePassPhraseDataSource.getEncryptionAlgorithm()
        val createBackupResult = if (algorithm == BCRYPT_ALGORITHM_BACKUP) {
            createResult {
                createPassPhraseDataSource.replacePassPhraseBackup(
                    MatrixSessionProvider.currentSession?.myUserId ?: "", newPassword
                )
            }
        } else {
            Response.Success(Unit)
        }
        responseLiveData.postValue(createBackupResult)
    }
}