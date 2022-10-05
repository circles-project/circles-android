package org.futo.circles.feature.log_in.stages.terms

import org.futo.circles.core.TYPE_PARAM_KEY
import org.futo.circles.core.auth.BaseAcceptTermsDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.toTermsListItems
import org.futo.circles.feature.log_in.stages.LoginStagesDataSource
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.Stage

class LoginAcceptTermsDataSource(
    private val loginStagesDataSource: LoginStagesDataSource
) : BaseAcceptTermsDataSource() {

    override suspend fun acceptTerms(): Response<Unit> {
        val wizard = MatrixInstanceProvider.matrix.authenticationService().getLoginWizard()
        val result = createResult {
            wizard.loginStageCustom(mapOf(TYPE_PARAM_KEY to LoginFlowTypes.TERMS))
        }
        return when (result) {
            is Response.Success -> {
                loginStagesDataSource.stageCompleted(result.data)
                Response.Success(Unit)
            }
            is Response.Error -> result
        }
    }

    override fun getTermsList() =
        (loginStagesDataSource.currentStage as? Stage.Terms)?.policies?.toTermsListItems()
            ?: emptyList()

}