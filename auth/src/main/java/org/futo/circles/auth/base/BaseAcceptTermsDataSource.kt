package org.futo.circles.auth.base

import androidx.lifecycle.MutableLiveData
import org.futo.circles.auth.feature.log_in.stages.terms.LoginAcceptTermsDataSource
import org.futo.circles.auth.feature.sign_up.terms.SignupAcceptTermsDataSource
import org.futo.circles.auth.model.TermsListItem
import org.futo.circles.auth.model.TermsModeArg
import org.futo.circles.core.extensions.Response
import javax.inject.Inject

abstract class BaseAcceptTermsDataSource {

    class Factory @Inject constructor(
        private val loginStagesDataSourceFactory: BaseLoginStagesDataSource.Factory,
        private val signupAcceptTermsDataSource: SignupAcceptTermsDataSource
    ) {
        fun create(mode: TermsModeArg): BaseAcceptTermsDataSource = when (mode) {
            TermsModeArg.Login -> LoginAcceptTermsDataSource(
                loginStagesDataSourceFactory.create(false)
            )

            TermsModeArg.Signup -> signupAcceptTermsDataSource
            TermsModeArg.ReAuth -> LoginAcceptTermsDataSource(
                loginStagesDataSourceFactory.create(true)
            )
        }
    }

    protected abstract fun getTermsList(): List<TermsListItem>
    abstract suspend fun acceptTerms(): Response<Unit>

    val termsListLiveData by lazy { MutableLiveData(getTermsList()) }

    fun changeTermCheck(item: TermsListItem) {
        termsListLiveData.value =
            termsListLiveData.value?.map { if (it.id == item.id) it.copy(isChecked = !it.isChecked) else it }
    }

}