package org.futo.circles.auth.feature.sign_up.terms

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.base.BaseAcceptTermsDataSource
import org.futo.circles.auth.model.TermsListItem
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class AcceptTermsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    dataSourceFactory: BaseAcceptTermsDataSource.Factory
) : ViewModel() {

    private val dataSource = dataSourceFactory.create(savedStateHandle.getOrThrow("mode"))

    val acceptTermsLiveData = SingleEventLiveData<Response<Unit>>()
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