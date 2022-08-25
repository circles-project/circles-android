package org.futo.circles.core.auth

import androidx.lifecycle.MutableLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.model.TermsListItem

abstract class BaseAcceptTermsDataSource {

    protected abstract fun getTermsList(): List<TermsListItem>
    abstract suspend fun acceptTerms(): Response<Unit>

    val termsListLiveData by lazy { MutableLiveData(getTermsList()) }

    fun changeTermCheck(item: TermsListItem) {
        termsListLiveData.value = termsListLiveData.value
            ?.map { if (it.id == item.id) it.copy(isChecked = !it.isChecked) else it }
    }

}