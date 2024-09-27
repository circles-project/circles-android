package org.futo.circles.auth.feature.sign_up.uia

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.feature.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.auth.model.AuthUIAScreenNavigationEvent
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.Session
import javax.inject.Inject


@HiltViewModel
class SignupUIAViewModel @Inject constructor(
    private val createPassPhraseDataSource: CreatePassPhraseDataSource
) : ViewModel() {

    private val uiaDataSource = SignupUIADataSourceProvider.getDataSourceOrThrow()

    val stagesNavigationLiveData = uiaDataSource.stagesNavigationLiveData
    val passPhraseLoadingLiveData = createPassPhraseDataSource.loadingLiveData
    val finishUIAEventLiveData = uiaDataSource.finishUIAEventLiveData
    val createBackupResultLiveData = SingleEventLiveData<Response<Unit>>()


    fun finishSignup(session: Session) {
        launchBg {
            val result = createResult {
                MatrixInstanceProvider.matrix.authenticationService().reset()
                MatrixSessionProvider.awaitForSessionStart(session)
                createPassPhraseDataSource.createPassPhraseBackup()
                clearProviders()
            }
            createBackupResultLiveData.postValue(result)
        }
    }

    private fun clearProviders() {
        SignupUIADataSourceProvider.clear()
    }
}
