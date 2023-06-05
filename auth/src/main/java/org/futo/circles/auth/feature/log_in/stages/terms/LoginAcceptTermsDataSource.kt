package org.futo.circles.auth.feature.log_in.stages.terms

import org.futo.circles.auth.base.BaseAcceptTermsDataSource
import org.futo.circles.auth.base.BaseLoginStagesDataSource
import org.futo.circles.auth.base.BaseLoginStagesDataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.extensions.toTermsListItems
import org.futo.circles.core.extensions.Response
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