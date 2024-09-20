package org.futo.circles.auth.feature.uia.stages.terms

import org.futo.circles.auth.extensions.toTermsListItems
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

class AcceptTermsDataSource @Inject constructor() {

    private val uiaDataSource = UIADataSourceProvider.getDataSourceOrThrow()

    suspend fun acceptTerms(): Response<Unit> {
        val result = uiaDataSource.performUIAStage(
            mapOf(TYPE_PARAM_KEY to LoginFlowTypes.TERMS)
        )
        return when (result) {
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> result
        }
    }

    fun getTermsList() =
        (uiaDataSource.currentStage as? Stage.Terms)?.policies?.toTermsListItems()
            ?: emptyList()

}