package org.futo.circles.auth.feature.uia.stages.terms

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class AcceptTermsViewModel @Inject constructor(
    private val dataSource: AcceptTermsDataSource
) : ViewModel() {


    val acceptTermsLiveData = SingleEventLiveData<Response<Unit>>()
    val termsListLiveData = MutableLiveData(dataSource.getTermsList())

    fun acceptTerms() {
        launchBg {
            acceptTermsLiveData.postValue(dataSource.acceptTerms())
        }
    }

}