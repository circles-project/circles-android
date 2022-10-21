package org.futo.circles.feature.log_in.stages.terms

import org.futo.circles.core.TYPE_PARAM_KEY
import org.futo.circles.core.auth.BaseAcceptTermsDataSource
import org.futo.circles.core.auth.BaseLoginStagesDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.toTermsListItems
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.Stage

class LoginAcceptTermsDataSource(
    private val loginStagesDataSource: BaseLoginStagesDataSource
) : BaseAcceptTermsDataSource() {

    override suspend fun acceptTerms(): Response<Unit> {
        val result = loginStagesDataSource.performLoginStage(
            mapOf(TYPE_PARAM_KEY to LoginFlowTypes.TERMS)
        )
        return when (result) {
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> result
        }
    }

    override fun getTermsList() =
        (loginStagesDataSource.currentStage as? Stage.Terms)?.policies?.toTermsListItems()
            ?: emptyList()

}