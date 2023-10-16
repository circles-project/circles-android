package org.futo.circles.auth.feature.change_password

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordDataSource: ChangePasswordDataSource
) : ViewModel() {

    val responseLiveData = SingleEventLiveData<Response<Unit?>>()
    val passPhraseLoadingLiveData = changePasswordDataSource.passPhraseLoadingLiveData

    fun changePassword(oldPassword: String, newPassword: String) {
        launchBg {
            when (val changePasswordResult =
                changePasswordDataSource.changePassword(oldPassword, newPassword)) {
                is Response.Error -> responseLiveData.postValue(changePasswordResult)
                is Response.Success -> createNewBackupInNeeded()
            }
        }
    }

    private suspend fun createNewBackupInNeeded() {
        val createBackupResult = changePasswordDataSource.createNewBackupInNeeded()
        responseLiveData.postValue(createBackupResult)
    }
}