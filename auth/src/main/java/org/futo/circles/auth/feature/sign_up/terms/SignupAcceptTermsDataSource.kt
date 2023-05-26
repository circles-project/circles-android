package org.futo.circles.auth.feature.sign_up.terms


import org.futo.circles.auth.base.BaseAcceptTermsDataSource
import org.futo.circles.auth.base.BaseLoginStagesDataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.extensions.toTermsListItems
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.Stage

class SignupAcceptTermsDataSource(
    private val signUpDataSource: org.futo.circles.auth.feature.sign_up.SignUpDataSource
) : BaseAcceptTermsDataSource() {

    override suspend fun acceptTerms(): Response<Unit> =
        when (val result = signUpDataSource.performRegistrationStage(
            mapOf(TYPE_PARAM_KEY to LoginFlowTypes.TERMS)
        )) {
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> result
        }

    override fun getTermsList() =
        (signUpDataSource.currentStage as? Stage.Terms)?.policies?.toTermsListItems() ?: emptyList()

}