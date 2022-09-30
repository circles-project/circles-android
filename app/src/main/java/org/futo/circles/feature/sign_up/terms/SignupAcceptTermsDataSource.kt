package org.futo.circles.feature.sign_up.terms


import org.futo.circles.core.auth.BaseAcceptTermsDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.toTermsListItems
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.Stage

class SignupAcceptTermsDataSource(
    private val signUpDataSource: SignUpDataSource
) : BaseAcceptTermsDataSource() {

    override suspend fun acceptTerms(): Response<Unit> =
        when (val result = signUpDataSource.performRegistrationStage(
            mapOf(SignUpDataSource.TYPE_PARAM_KEY to LoginFlowTypes.TERMS)
        )) {
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> result
        }

    override fun getTermsList() =
        (signUpDataSource.currentStage as? Stage.Terms)?.policies?.toTermsListItems() ?: emptyList()

}