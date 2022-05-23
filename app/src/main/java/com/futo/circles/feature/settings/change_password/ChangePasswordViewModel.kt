package com.futo.circles.feature.settings.change_password

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.core.matrix.pass_phrase.create.CreatePassPhraseDataSource
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.launchBg

class ChangePasswordViewModel(
    private val changePasswordDataSource: ChangePasswordDataSource,
    private val createPassPhraseDataSource: CreatePassPhraseDataSource
) : ViewModel() {

    val responseLiveData = SingleEventLiveData<Response<Unit?>>()
    val passPhraseLoadingLiveData = createPassPhraseDataSource.loadingLiveData

    fun changePassword(oldPassword: String, newPassword: String) {
        launchBg {
            when (val changePasswordResult =
                changePasswordDataSource.changePassword(oldPassword, newPassword)) {
                is Response.Error -> responseLiveData.postValue(changePasswordResult)
                is Response.Success -> createNewBackup(newPassword)
            }
        }
    }

    private suspend fun createNewBackup(newPassword: String) {
        val createBackupResult =
            createResult { createPassPhraseDataSource.replacePassPhraseBackup(newPassword) }
        responseLiveData.postValue(createBackupResult)
    }
}