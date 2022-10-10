package org.futo.circles.feature.sign_up.password

import android.content.Context
import org.futo.circles.bsspeke.BSSpekeClient
import org.futo.circles.core.LOGIN_BSSPEKE_VERIFY_TYPE
import org.futo.circles.core.REGISTRATION_BSSPEKE_OPRF_TYPE
import org.futo.circles.core.REGISTRATION_BSSPEKE_SAVE_TYPE
import org.futo.circles.core.TYPE_PARAM_KEY
import org.futo.circles.core.auth.BsSpekeStageDataSource
import org.futo.circles.core.auth.BsSpekeStageDataSource.Companion.CURVE_PARAM_KEY
import org.futo.circles.core.auth.BsSpekeStageDataSource.Companion.PHF_PARAM_KEY
import org.futo.circles.core.auth.PasswordDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class SignupBsSpekeDataSource(
    private val context: Context,
    private val signUpDataSource: SignUpDataSource
) : PasswordDataSource, BsSpekeStageDataSource {

    override fun getMinimumPasswordLength(): Int = 1

    override suspend fun processPasswordStage(password: String): Response<Unit> {
        val bsSpekeClient = BSSpekeClient(
            "@${signUpDataSource.userName}:${signUpDataSource.domain}",
            signUpDataSource.domain,
            password
        )
        val phfParams = getPhf(signUpDataSource.currentStage)
        val blocks: Int
        val iterations: Int
        try {
            blocks = getBlocks(context, phfParams)
            iterations = getIterations(context, phfParams)
        } catch (e: Exception) {
            return Response.Error(e.message ?: "")
        }
        return when (val oprfResult = performOprfStage(bsSpekeClient)) {
            is Response.Success -> processOprfSuccessResponse(
                oprfResult.data,
                bsSpekeClient,
                password,
                blocks,
                iterations,
                phfParams
            )
            is Response.Error -> oprfResult
        }
    }

    private suspend fun performOprfStage(
        bsSpekeClient: BSSpekeClient
    ): Response<RegistrationResult> = signUpDataSource.performRegistrationStage(
        mapOf(
            TYPE_PARAM_KEY to REGISTRATION_BSSPEKE_OPRF_TYPE,
            CURVE_PARAM_KEY to getCurve(signUpDataSource.currentStage),
            BLIND_PARAM_KEY to bsSpekeClient.generateBase64Blind()
        )
    )

    private suspend fun processOprfSuccessResponse(
        oprfResult: RegistrationResult,
        bsSpekeClient: BSSpekeClient,
        password: String,
        blocks: Int,
        iterations: Int,
        phfParams: Map<String, Any?>
    ): Response<Unit> {
        val PandV: Pair<String, String>
        try {
            val blindSalt = getBlindSalt(context, oprfResult, LOGIN_BSSPEKE_VERIFY_TYPE)
            PandV = bsSpekeClient.generateBase64PandV(blindSalt, blocks, iterations)
        } catch (e: Exception) {
            return Response.Error(e.message ?: "")
        }
        return when (val saveResult = performSaveStage(PandV, password, phfParams)
        ) {
            is Response.Error -> saveResult
            is Response.Success -> Response.Success(Unit)
        }
    }

    private suspend fun performSaveStage(
        PandV: Pair<String, String>,
        password: String,
        phfParams: Map<String, Any?>
    ): Response<RegistrationResult> = signUpDataSource.performRegistrationStage(
        mapOf(
            TYPE_PARAM_KEY to REGISTRATION_BSSPEKE_SAVE_TYPE,
            P_PARAM_KEY to PandV.first,
            V_PARAM_KEY to PandV.second,
            PHF_PARAM_KEY to phfParams
        ), password = password
    )

    companion object {
        private const val BLIND_PARAM_KEY = "blind"
        private const val P_PARAM_KEY = "P"
        private const val V_PARAM_KEY = "V"
    }
}