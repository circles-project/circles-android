package org.futo.circles.feature.sign_up.terms

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.model.TermsListItem
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class AcceptTermsViewModel(
    private val dataSource: AcceptTermsDataSource
) : ViewModel() {

    val acceptTermsLiveData = SingleEventLiveData<Response<RegistrationResult>>()

    val termsListLiveData = dataSource.termsListLiveData

    fun acceptTerms() {
        launchBg {
            acceptTermsLiveData.postValue(dataSource.acceptTerms())
        }
    }

    fun changeTermCheck(item: TermsListItem) {
        dataSource.changeTermCheck(item)
    }

    fun isAllTermsAccepted(list: List<TermsListItem>): Boolean {
        list.forEach { if (!it.isChecked) return false }
        return true
    }

}