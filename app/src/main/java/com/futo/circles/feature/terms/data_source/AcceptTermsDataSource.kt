package com.futo.circles.feature.terms.data_source


import androidx.lifecycle.MutableLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.toTermsListItems
import com.futo.circles.feature.sign_up.data_source.SignUpDataSource
import com.futo.circles.model.TermsListItem
import com.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage

class AcceptTermsDataSource(
    private val signUpDataSource: SignUpDataSource
) {

    val termsListLiveData = MutableLiveData<List<TermsListItem>>(getTermsList())

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

}