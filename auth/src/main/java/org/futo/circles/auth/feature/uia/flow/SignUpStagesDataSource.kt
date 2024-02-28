package org.futo.circles.auth.feature.uia.flow

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.uia.UIADataSource
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignUpStagesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : UIADataSource(context) {

    override suspend fun performUIAStage(
        authParams: JsonDict,
        name: String?,
        password: String?
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

}