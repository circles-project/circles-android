package org.futo.circles.auth.feature.log_in

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.uia.UIADataSource
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.DIRECT_LOGIN_PASSWORD_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.ENROLL_BSSPEKE_SAVE_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.LOGIN_BSSPEKE_VERIFY_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.LOGIN_EMAIL_SUBMIT_TOKEN_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.LOGIN_PASSWORD_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.LOGIN_PASSWORD_USER_ID_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.USER_PARAM_KEY
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.auth.model.UIAFlowType
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfigFromDomain
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

class LoginDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val uiaFactory: UIADataSource.Factory
) {


    private val authService by lazy { MatrixInstanceProvider.matrix.authenticationService() }

    suspend fun startLogin(
        userName: String,
        domain: String,
        isForgotPassword: Boolean
    ) = createResult {
        authService.cancelPendingLoginOrRegistration()
        val stages = prepareLoginStages(userName, domain, isForgotPassword)
        val uiaDataSource = UIADataSourceProvider.create(
            if (isForgotPassword) UIAFlowType.ForgotPassword else UIAFlowType.Login,
            uiaFactory
        )
        uiaDataSource.startUIAStages(stages, domain, userName)
    }

    private suspend fun prepareLoginStages(
        userName: String,
        domain: String,
        isForgotPassword: Boolean
    ): List<Stage> {
        val homeServerConfig = buildHomeServerConfigFromDomain(domain)
        val supportedLoginMethods =
            authService.getLoginFlow(homeServerConfig).supportedLoginTypes
        val stages =
            getCircleStages(userName, domain, isForgotPassword)
                ?: getPasswordStagesIfAvailable(supportedLoginMethods, isForgotPassword)

        return stages
            ?: throw IllegalArgumentException(context.getString(R.string.unsupported_login_method))
    }

    private fun isPasswordLogin(methods: List<String>) = methods.contains(LOGIN_PASSWORD_TYPE)

    private fun getPasswordStagesIfAvailable(
        supportedLoginMethods: List<String>,
        isForgotPassword: Boolean
    ): List<Stage>? = if (isPasswordLogin(supportedLoginMethods) && !isForgotPassword) {
        listOf(Stage.Other(true, DIRECT_LOGIN_PASSWORD_TYPE, null))
    } else null

    private suspend fun getCircleStages(
        userName: String,
        domain: String,
        isForgotPassword: Boolean
    ): List<Stage>? {
        val identifierParams = mapOf(
            USER_PARAM_KEY to "@$userName:$domain",
            TYPE_PARAM_KEY to LOGIN_PASSWORD_USER_ID_TYPE
        )
        val flows = authService.getLoginWizard()
            .getAllLoginFlows(identifierParams, context.getString(R.string.initial_device_name))

        return if (isForgotPassword) getCircleStagesForForgotPassword(flows)
        else getCircleStagesForLogin(flows)
    }

    private fun getCircleStagesForLogin(flows: List<List<Stage>>): List<Stage>? =
        flows.firstOrNull { stages ->
            stages.firstOrNull { stage ->
                (stage as? Stage.Other)?.type == LOGIN_BSSPEKE_VERIFY_TYPE
            } != null
        }

    private fun getCircleStagesForForgotPassword(flows: List<List<Stage>>): List<Stage>? =
        flows.firstOrNull { stages ->
            val containsEmailStage = stages.firstOrNull { stage ->
                (stage as? Stage.Other)?.type == LOGIN_EMAIL_SUBMIT_TOKEN_TYPE
            } != null
            val containsSetPassword = stages.firstOrNull { stage ->
                (stage as? Stage.Other)?.type == ENROLL_BSSPEKE_SAVE_TYPE
            } != null
            containsEmailStage && containsSetPassword
        }
}