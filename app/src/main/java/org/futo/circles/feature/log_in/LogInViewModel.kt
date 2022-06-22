package org.futo.circles.feature.log_in

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.matrix.android.sdk.api.auth.data.LoginFlowResult
import org.matrix.android.sdk.api.session.Session

enum class SuccessLoginNavigationEvent { Main, SetupCircles }

class LogInViewModel(
    private val loginDataSource: LoginDataSource
) : ViewModel() {

    val successLoginNavigationLiveData =
        SingleEventLiveData<SuccessLoginNavigationEvent>()
    val loginResultLiveData = SingleEventLiveData<Response<Session>>()
    val restoreKeysLiveData = SingleEventLiveData<Response<Unit>>()
    val passPhraseLoadingLiveData = loginDataSource.passPhraseLoadingLiveData

    fun logIn(name: String, password: String) {
        launchBg {
            val loginResult = loginDataSource.logIn(name, password)
            loginResultLiveData.postValue(loginResult)
            (loginResult as? Response.Success)?.let { handleSuccessLogin(this, password) }
        }
    }

    private suspend fun handleSuccessLogin(scope: CoroutineScope, password: String) {
        val isCirclesCreated = loginDataSource.isCirclesTreeCreated()
        with(scope) {
            listOfNotNull(
                async { restoreKeysLiveData.postValue(loginDataSource.restoreKeys(password)) },
                if (!isCirclesCreated) async { loginDataSource.createSpacesTree() } else null
            ).awaitAll()
        }

        successLoginNavigationLiveData.postValue(
            if (isCirclesCreated) SuccessLoginNavigationEvent.Main
            else SuccessLoginNavigationEvent.SetupCircles
        )
    }
}