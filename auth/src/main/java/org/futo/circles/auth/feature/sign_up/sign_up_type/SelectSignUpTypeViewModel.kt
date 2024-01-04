package org.futo.circles.auth.feature.sign_up.sign_up_type

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

@HiltViewModel
class SelectSignUpTypeViewModel @Inject constructor(
    private val dataSource: SelectSignUpTypeDataSource
) : ViewModel() {

    val startSignUpEventLiveData = SingleEventLiveData<Response<Unit?>>()
    val signupFlowsLiveData = SingleEventLiveData<Response<List<List<Stage>>>>()

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
        launchBg {
            val result = dataSource.getAuthFlowsFor(domain)
            signupFlowsLiveData.postValue(result)
        }
    }

    fun hasSubscriptionFlow(flows: List<List<Stage>>): Boolean =
        dataSource.getSubscriptionSignupStages(flows) != null

    fun hasFreeFlow(flows: List<List<Stage>>): Boolean =
        dataSource.getFreeSignupStages(flows) != null

}