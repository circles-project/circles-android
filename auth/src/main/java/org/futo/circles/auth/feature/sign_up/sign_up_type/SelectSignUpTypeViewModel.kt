package org.futo.circles.auth.feature.sign_up.sign_up_type

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class SelectSignUpTypeViewModel @Inject constructor(
    private val dataSource: SelectSignUpTypeDataSource
) : ViewModel() {

    val startSignUpEventLiveData = SingleEventLiveData<Response<Unit?>>()

    fun startSignUp(isSubscription: Boolean) {
        launchBg {
            val result = dataSource.startNewRegistration(isSubscription)
            startSignUpEventLiveData.postValue(result)
        }
    }

    fun clearSubtitle() {
        dataSource.clearSubtitle()
    }

    fun loadSignupFlowsForDomain(domain: String) {
        launchBg { dataSource.getAuthFlowsFor(domain) }
    }

}