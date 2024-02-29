package org.futo.circles.auth.feature.uia.stages.password

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.DIRECT_LOGIN_PASSWORD_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.LOGIN_BSSPEKE_OPRF_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.LOGIN_PASSWORD_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.ENROLL_BSSPEKE_OPRF_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.ENROLL_BSSPEKE_SAVE_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.ENROLL_PASSWORD_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.LOGIN_BSSPEKE_VERIFY_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

class PasswordDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bsSpekeStageDataSource: BsSpekeStageDataSource
) {

    private val uiaDataSource = UIADataSourceProvider.getDataSourceOrThrow()

    suspend fun processPasswordStage(password: String): Response<Unit> =
        when (uiaDataSource.getCurrentStageKey()) {
            LOGIN_BSSPEKE_OPRF_TYPE, ENROLL_BSSPEKE_OPRF_TYPE,
            LOGIN_BSSPEKE_VERIFY_TYPE, ENROLL_BSSPEKE_SAVE_TYPE->
                bsSpekeStageDataSource.processPasswordStage(password)

            ENROLL_PASSWORD_TYPE -> processRegistrationPasswordStage(password)
            LOGIN_PASSWORD_TYPE -> processPasswordStageL(password)
            DIRECT_LOGIN_PASSWORD_TYPE -> processDirectPasswordStage(password)
            else -> throw IllegalArgumentException("Unsupported password stage")
        }


    private suspend fun processRegistrationPasswordStage(password: String): Response<Unit> =
        when (val result = uiaDataSource.performUIAStage(
            mapOf(
                TYPE_PARAM_KEY to ENROLL_PASSWORD_TYPE,
                REGISTRATION_PASSWORD_PARAM_KEY to password
            )
        )) {
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> result
        }


    private suspend fun processPasswordStageL(password: String): Response<Unit> {
        val result = uiaDataSource.performUIAStage(
            mapOf(
                TYPE_PARAM_KEY to LOGIN_PASSWORD_TYPE,
                LOGIN_PASSWORD_PARAM_KEY to password
            ), password
        )
        return when (result) {
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> result
        }
    }

    private suspend fun processDirectPasswordStage(password: String): Response<Unit> {
        val result = createResult {
            MatrixInstanceProvider.matrix.authenticationService().getLoginWizard().login(
                login = uiaDataSource.userName,
                password = password,
                initialDeviceName = context.getString(R.string.initial_device_name)
            )
        }
        return when (result) {
            is Response.Success -> {
                uiaDataSource.stageCompleted(RegistrationResult.Success(result.data))
                Response.Success(Unit)
            }

            is Response.Error -> result
        }
    }

    companion object {
        private const val REGISTRATION_PASSWORD_PARAM_KEY = "new_password"
        private const val LOGIN_PASSWORD_PARAM_KEY = "password"
    }

}