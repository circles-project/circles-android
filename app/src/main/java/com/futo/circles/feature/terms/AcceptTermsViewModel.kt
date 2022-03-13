package com.futo.circles.feature.terms

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.terms.data_source.AcceptTermsDataSource
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class AcceptTermsViewModel(
    private val dataSource: AcceptTermsDataSource
) : ViewModel() {

    val acceptTermsLiveData = SingleEventLiveData<Response<RegistrationResult>>()

    fun acceptTerms() {
        launchBg {
            acceptTermsLiveData.postValue(dataSource.acceptTerms())
        }
    }

}