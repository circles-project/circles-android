package org.futo.circles.feature.sign_up.terms


import android.content.Context
import org.futo.circles.R
import org.futo.circles.core.TERMS_URL_EXTENSION
import org.futo.circles.core.auth.BaseAcceptTermsDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.toTermsListItems
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.futo.circles.model.TermsListItem
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.Stage

class SignupAcceptTermsDataSource(
    private val context: Context,
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
        (signUpDataSource.currentStage as? Stage.Terms)?.policies?.toTermsListItems()
            ?.takeIf { it.isNotEmpty() }
            ?: listOf(
                TermsListItem(
                    1, context.getString(R.string.terms_and_conditions),
                    signUpDataSource.currentHomeServerUrl + TERMS_URL_EXTENSION
                )
            )

}