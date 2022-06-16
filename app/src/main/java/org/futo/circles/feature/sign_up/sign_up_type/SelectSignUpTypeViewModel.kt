package org.futo.circles.feature.sign_up.sign_up_type

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

class SelectSignUpTypeViewModel(
    private val dataSource: SelectSignUpTypeDataSource
) : ViewModel() {

    val startSignUpEventLiveData = SingleEventLiveData<Response<Unit?>>()

    fun startSignUp(name: String, password: String) {
        launchBg {
            startSignUpEventLiveData.postValue(
                dataSource.startNewRegistration(name, password)
            )
        }
    }

    fun clearSubtitle() {
        dataSource.clearSubtitle()
    }
}