package org.futo.circles.auth.feature.sign_up.password

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.base.BaseBsSpekeStageDataSource
import org.futo.circles.auth.feature.sign_up.SignUpDataSource
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject

class SignupBsSpekeDataSource @Inject constructor(
    @ApplicationContext context: Context,
    private val signUpDataSource: SignUpDataSource
) : BaseBsSpekeStageDataSource(context) {

    override val userName: String get() = signUpDataSource.userName
    override val domain: String get() = signUpDataSource.domain
    override val isLoginMode: Boolean get() = false
    override fun getStages(): List<Stage> = signUpDataSource.stagesToComplete

    override suspend fun performAuthStage(
        authParams: JsonDict,
        password: String?
    ): Response<RegistrationResult> = signUpDataSource.performRegistrationStage(
        authParams = authParams,
        password = password
    )
}