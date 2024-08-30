package org.futo.circles.auth.feature.sign_up

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val signUpDataSource: SignUpDataSource
) : ViewModel() {

    val startSignUpEventLiveData = SingleEventLiveData<Response<Unit?>>()

    fun startSignUp(domain: String) {
        launchBg {
            val result = signUpDataSource.startNewRegistration(domain)
            startSignUpEventLiveData.postValue(result)
        }
    }

}