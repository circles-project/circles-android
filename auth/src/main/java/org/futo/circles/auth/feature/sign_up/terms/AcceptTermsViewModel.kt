package org.futo.circles.auth.feature.sign_up.terms

import androidx.lifecycle.ViewModel
import org.futo.circles.auth.base.BaseAcceptTermsDataSource
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.auth.model.TermsListItem

class AcceptTermsViewModel(
    private val dataSource: BaseAcceptTermsDataSource
) : ViewModel() {

    val acceptTermsLiveData = org.futo.circles.core.SingleEventLiveData<Response<Unit>>()
    val termsListLiveData = dataSource.termsListLiveData

    fun acceptTerms() {
        launchBg {
            acceptTermsLiveData.postValue(dataSource.acceptTerms())
        }
    }

    fun changeTermCheck(item: TermsListItem) {
        dataSource.changeTermCheck(item)
    }

}