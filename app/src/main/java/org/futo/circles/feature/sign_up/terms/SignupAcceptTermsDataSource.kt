package org.futo.circles.feature.sign_up.terms


import org.futo.circles.core.auth.BaseAcceptTermsDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.toTermsListItems
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.Stage

class SignupAcceptTermsDataSource(
    private val signUpDataSource: SignUpDataSource
) : BaseAcceptTermsDataSource() {

    private val wizard by lazy {
        MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
    }

    override suspend fun acceptTerms(): Response<Unit> =
        when (val result = createResult { wizard.acceptTerms() }) {
            is Response.Success -> {
                signUpDataSource.stageCompleted(result.data)
                Response.Success(Unit)
            }
            is Response.Error -> result
        }

    override fun getTermsList() =
        (signUpDataSource.currentStage as? Stage.Terms)?.policies?.toTermsListItems() ?: emptyList()

}