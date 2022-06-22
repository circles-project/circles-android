package org.futo.circles.feature.sign_up.terms


import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.futo.circles.R
import org.futo.circles.core.TERMS_URL_EXTENSION
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.toTermsListItems
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.futo.circles.model.TermsListItem
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage

class AcceptTermsDataSource(
    private val context: Context,
    private val signUpDataSource: SignUpDataSource
) {

    val termsListLiveData = MutableLiveData(getTermsList())

    private val wizard by lazy {
        MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
    }

    suspend fun acceptTerms(): Response<RegistrationResult> {
        val result = createResult { wizard.acceptTerms() }

        (result as? Response.Success)?.let { signUpDataSource.stageCompleted(result.data) }

        return result
    }

    fun changeTermCheck(item: TermsListItem) {
        termsListLiveData.value = termsListLiveData.value
            ?.map { if (it.id == item.id) it.copy(isChecked = !it.isChecked) else it }
    }

    private fun getTermsList() =
        (signUpDataSource.currentStage as? Stage.Terms)?.policies?.toTermsListItems()
            ?.takeIf { it.isNotEmpty() }
            ?: listOf(
                TermsListItem(
                    1, context.getString(R.string.terms_and_conditions),
                    signUpDataSource.currentHomeServerUrl + TERMS_URL_EXTENSION
                )
            )

}