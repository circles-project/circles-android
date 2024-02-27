package org.futo.circles.auth.feature.sign_up.terms

import androidx.lifecycle.MutableLiveData
import org.futo.circles.auth.extensions.toTermsListItems
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.auth.model.TermsListItem
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

class AcceptTermsDataSource @Inject constructor() {

    private val uiaDataSource = UIADataSourceProvider.getDataSourceOrThrow()
    val termsListLiveData by lazy { MutableLiveData(getTermsList()) }

    suspend fun acceptTerms(): Response<Unit> {
        val result = uiaDataSource.performUIAStage(
            mapOf(TYPE_PARAM_KEY to LoginFlowTypes.TERMS)
        )
        return when (result) {
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> result
        }
    }

    fun changeTermCheck(item: TermsListItem) {
        termsListLiveData.value =
            termsListLiveData.value?.map { if (it.id == item.id) it.copy(isChecked = !it.isChecked) else it }
    }

    private fun getTermsList() =
        (uiaDataSource.currentStage as? Stage.Terms)?.policies?.toTermsListItems()
            ?: emptyList()

}