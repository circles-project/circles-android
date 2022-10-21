package org.futo.circles.feature.log_in.stages.password

import android.content.Context
import org.futo.circles.core.auth.BaseBsSpekeStageDataSource
import org.futo.circles.core.auth.BaseLoginStagesDataSource
import org.futo.circles.extensions.Response
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.util.JsonDict

class LoginBsSpekeDataSource(
    context: Context,
    private val isChangePasswordEnroll: Boolean,
    private val loginStagesDataSource: BaseLoginStagesDataSource
) : BaseBsSpekeStageDataSource(context) {

    override val userName: String get() = loginStagesDataSource.userName
    override val domain: String get() = loginStagesDataSource.domain
    override val isLoginMode: Boolean get() = !isChangePasswordEnroll
    override fun getCurrentStage(): Stage? = loginStagesDataSource.currentStage

    override suspend fun performAuthStage(
        authParams: JsonDict,
        password: String?
    ): Response<RegistrationResult> = loginStagesDataSource.performLoginStage(authParams, password)

}