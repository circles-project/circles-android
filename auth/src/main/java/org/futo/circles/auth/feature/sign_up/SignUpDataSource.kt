package org.futo.circles.auth.feature.sign_up

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.bsspeke.BSSpekeClientProvider
import org.futo.circles.auth.feature.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.auth.feature.uia.UIADataSource
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignUpDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val createPassPhraseDataSource: CreatePassPhraseDataSource,
    private val preferencesProvider: PreferencesProvider
) : UIADataSource(context) {


    val finishRegistrationLiveData = SingleEventLiveData<Response<Unit>>()
    val passPhraseLoadingLiveData = createPassPhraseDataSource.loadingLiveData


    suspend fun startSignUpStages(
        stages: List<Stage>,
        serverDomain: String
    ) {
        currentStage = null
        stagesToComplete.clear()
        domain = serverDomain
        stagesToComplete.addAll(stages)
        navigateToNextStage()
    }

    suspend fun performRegistrationStage(
        authParams: JsonDict,
        name: String? = null
    ): Response<RegistrationResult> {
        val wizard = MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
        val result = createResult {
            wizard.registrationCustom(
                authParams,
                context.getString(R.string.initial_device_name),
                true
            )
        }

        (result as? Response.Success)?.let {
            name?.let { userName = it }
            stageCompleted(result.data)
        }
        return result
    }

    fun clearSubtitle() {
        subtitleLiveData.postValue("")
    }

    private suspend fun finishRegistration(session: Session) = createResult {
        MatrixInstanceProvider.matrix.authenticationService().reset()
        MatrixSessionProvider.awaitForSessionStart(session)
        preferencesProvider.setShouldShowAllExplanations()
        createPassPhraseDataSource.createPassPhraseBackup()
        BSSpekeClientProvider.clear()
    }

}