package org.futo.circles.auth.feature.uia.flow

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.uia.UIADataSource
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginStagesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : UIADataSource() {


    var userPassword: String = ""

    override suspend fun startUIAStages(
        stages: List<Stage>,
        serverDomain: String,
        name: String?
    ) {
        userPassword = ""
        name ?: throw IllegalArgumentException("Username is required for login")
        super.startUIAStages(stages, serverDomain, name)
    }

    override suspend fun performUIAStage(
        authParams: JsonDict,
        name: String?,
        password: String?
    ): Response<RegistrationResult> {
        val wizard = MatrixInstanceProvider.matrix.authenticationService().getLoginWizard()
        val result = createResult {
            wizard.loginStageCustom(
                authParams,
                getIdentifier(),
                context.getString(R.string.initial_device_name)
            )
        }
        (result as? Response.Success)?.let {
            password?.let { userPassword = it }
            stageCompleted(result.data)
        }
        return result
    }

    private fun getIdentifier() = mapOf(
        USER_PARAM_KEY to getUserId(),
        TYPE_PARAM_KEY to LOGIN_PASSWORD_USER_ID_TYPE
    )
}