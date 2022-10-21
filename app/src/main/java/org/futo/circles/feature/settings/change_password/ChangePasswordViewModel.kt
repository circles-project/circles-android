package org.futo.circles.feature.settings.change_password

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

class ChangePasswordViewModel(
    private val changePasswordDataSource: ChangePasswordDataSource
) : ViewModel() {

    val responseLiveData = SingleEventLiveData<Response<Unit?>>()
    val passPhraseLoadingLiveData = changePasswordDataSource.passPhraseLoadingLiveData

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
        val createBackupResult = changePasswordDataSource.createNewBackupInNeeded(newPassword)
        responseLiveData.postValue(createBackupResult)
    }
}