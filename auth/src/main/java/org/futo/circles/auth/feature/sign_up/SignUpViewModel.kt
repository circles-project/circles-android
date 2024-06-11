package org.futo.circles.auth.feature.sign_up

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.model.ServerDomainArg
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataSource: SignUpDataSource
) : ViewModel() {

    private val domainArg: ServerDomainArg = savedStateHandle.getOrThrow("domainArg")
    val startSignUpEventLiveData = SingleEventLiveData<Response<Unit?>>()
    val signupFlowsLiveData = MutableLiveData<Response<Pair<Boolean, Boolean>>>()
    val flowsLoadingData = SingleEventLiveData<Boolean>()

    init {
        loadSignupFlowsForDomain(getDomain())
    }

    fun startSignUp(isSubscription: Boolean) {
        launchBg {
            val result = dataSource.startNewRegistration(isSubscription)
            startSignUpEventLiveData.postValue(result)
        }
    }

    private fun getDomain() = when (domainArg) {
        ServerDomainArg.US -> CirclesAppConfig.usDomain
        ServerDomainArg.EU -> CirclesAppConfig.euDomain
    }

    private fun loadSignupFlowsForDomain(domain: String) {
        flowsLoadingData.value = true
        launchBg {
            val mappedResult = when (val result = dataSource.getAuthFlowsFor(domain)) {
                is Response.Error -> result
                is Response.Success -> Response.Success(
                    hasFreeFlow(result.data) to hasSubscriptionFlow(result.data)
                )
            }
            flowsLoadingData.postValue(false)
            signupFlowsLiveData.postValue(mappedResult)
        }
    }

    private fun hasSubscriptionFlow(flows: List<List<Stage>>): Boolean =
        dataSource.getSubscriptionSignupStages(flows) != null

    private fun hasFreeFlow(flows: List<List<Stage>>): Boolean =
        dataSource.getFreeSignupStages(flows) != null

}