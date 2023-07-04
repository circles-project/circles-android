package org.futo.circles.auth.feature.log_in.stages.password

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.base.BaseBsSpekeStageDataSource
import org.futo.circles.auth.base.BaseLoginStagesDataSource
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject

class LoginBsSpekeDataSource(
    context: Context,
    private val isChangePasswordEnroll: Boolean,
    private val loginStagesDataSource: BaseLoginStagesDataSource
) : BaseBsSpekeStageDataSource(context) {

    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val loginStagesDataSourceFactory: BaseLoginStagesDataSource.Factory
    ) {
        fun create(isReauth: Boolean, isChangePasswordEnroll: Boolean): LoginBsSpekeDataSource =
            LoginBsSpekeDataSource(
                context,
                isChangePasswordEnroll,
                loginStagesDataSourceFactory.create(isReauth)
            )
    }

    override val userName: String get() = loginStagesDataSource.userName
    override val domain: String get() = loginStagesDataSource.domain
    override val isLoginMode: Boolean get() = !isChangePasswordEnroll
    override fun getStages(): List<Stage> = loginStagesDataSource.stagesToComplete

    override suspend fun performAuthStage(
        authParams: JsonDict,
        password: String?
    ): Response<RegistrationResult> = loginStagesDataSource.performLoginStage(authParams, password)

}