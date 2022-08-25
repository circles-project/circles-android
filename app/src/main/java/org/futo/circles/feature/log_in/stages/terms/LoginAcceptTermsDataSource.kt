package org.futo.circles.feature.log_in.stages.terms

import android.content.Context
import org.futo.circles.R
import org.futo.circles.core.TERMS_URL_EXTENSION
import org.futo.circles.core.auth.BaseAcceptTermsDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.feature.log_in.stages.LoginStagesDataSource
import org.futo.circles.model.TermsListItem

class LoginAcceptTermsDataSource(
    private val context: Context,
    private val loginStagesDataSource: LoginStagesDataSource
) : BaseAcceptTermsDataSource() {


    override suspend fun acceptTerms(): Response<Unit> {
        //TODO("login accept terms api call here")
        //(result as? Response.Success)?.let { loginStagesDataSource.stageCompleted(result.data) }
        return Response.Success(Unit)
    }

    override fun getTermsList() = listOf(
        TermsListItem(
            1, context.getString(R.string.terms_and_conditions),
            loginStagesDataSource.currentHomeServerUrl + TERMS_URL_EXTENSION
        )
    )

}