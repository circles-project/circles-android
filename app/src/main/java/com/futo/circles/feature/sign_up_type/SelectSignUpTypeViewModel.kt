package com.futo.circles.feature.sign_up_type

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.sign_up_type.data_source.SelectSignUpTypeDataSource

class SelectSignUpTypeViewModel(
    private val dataSource: SelectSignUpTypeDataSource
) : ViewModel() {

    val startSignUpEventLiveData = SingleEventLiveData<Response<Unit?>>()

    fun startSignUp() {
        launchBg {
            startSignUpEventLiveData.postValue(dataSource.startNewRegistration())
        }
    }

    fun clearSubtitle() {
        dataSource.clearSubtitle()
    }
}