package org.futo.circles.feature.log_in.stages.terms

import org.futo.circles.core.auth.BaseAcceptTermsDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.feature.log_in.stages.LoginStagesDataSource
import org.futo.circles.model.TermsListItem

class LoginAcceptTermsDataSource(
    private val loginStagesDataSource: LoginStagesDataSource
) : BaseAcceptTermsDataSource() {


    override suspend fun acceptTerms(): Response<Unit> {
        //TODO("login accept terms api call here")
        //(result as? Response.Success)?.let { loginStagesDataSource.stageCompleted(result.data) }
        return Response.Success(Unit)
    }

    override fun getTermsList() = emptyList<TermsListItem>()

}