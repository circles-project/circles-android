package com.futo.circles.feature.settings.change_password

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.settings.change_password.data_source.ChangePasswordDataSource

class ChangePasswordViewModel(
    private val dataSource: ChangePasswordDataSource
) : ViewModel() {

    val changePasswordResponseLiveData = SingleEventLiveData<Response<Unit?>>()

    fun changePassword(oldPassword: String, newPassword: String) {
        launchBg {
            val result = dataSource.changePassword(oldPassword, newPassword)
            changePasswordResponseLiveData.postValue(result)
        }
    }
}