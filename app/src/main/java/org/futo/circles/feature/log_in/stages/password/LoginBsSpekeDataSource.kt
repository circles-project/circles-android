package org.futo.circles.feature.log_in.stages.password

import android.content.Context
import android.util.Base64
import org.futo.circles.R
import org.futo.circles.bsspeke.BSSpekeClient
import org.futo.circles.core.LOGIN_BSSPEKE_OPRF_TYPE
import org.futo.circles.core.LOGIN_BSSPEKE_VERIFY_TYPE
import org.futo.circles.core.TYPE_PARAM_KEY
import org.futo.circles.core.auth.BsSpekeStageDataSource
import org.futo.circles.core.auth.BsSpekeStageDataSource.Companion.CURVE_PARAM_KEY
import org.futo.circles.core.auth.PasswordDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.feature.log_in.stages.LoginStagesDataSource
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage

class LoginBsSpekeDataSource(
    private val context: Context,
    private val loginStagesDataSource: LoginStagesDataSource
) : PasswordDataSource, BsSpekeStageDataSource {

    override fun getMinimumPasswordLength(): Int = 1

    override suspend fun processPasswordStage(password: String): Response<Unit> {
        val bsSpekeClient = BSSpekeClient(
            "@${loginStagesDataSource.userName}:${loginStagesDataSource.domain}",
            loginStagesDataSource.domain,
            password
        )
        val phfParams = getPhf(loginStagesDataSource.currentStage)
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
                iterations
            )
            is Response.Error -> oprfResult
        }
    }

    private suspend fun performOprfStage(
        bsSpekeClient: BSSpekeClient
    ): Response<RegistrationResult> = loginStagesDataSource.performLoginStage(
        mapOf(
            TYPE_PARAM_KEY to LOGIN_BSSPEKE_OPRF_TYPE,
            CURVE_PARAM_KEY to getCurve(loginStagesDataSource.currentStage),
            BLIND_PARAM_KEY to bsSpekeClient.generateBase64Blind()
        )
    )

    private suspend fun processOprfSuccessResponse(
        oprfResult: RegistrationResult,
        bsSpekeClient: BSSpekeClient,
        password: String,
        blocks: Int,
        iterations: Int
    ): Response<Unit> {
        val A: String
        val verifier: String
        try {
            val blindSalt = getBlindSalt(context, oprfResult, LOGIN_BSSPEKE_VERIFY_TYPE)
            val B = getB(oprfResult)
            A = bsSpekeClient.generateABase64(blindSalt, blocks, iterations)
            bsSpekeClient.deriveSharedKey(B)
            verifier = bsSpekeClient.generateVerifierBase64()
        } catch (e: Exception) {
            return Response.Error(e.message ?: "")
        }
        return when (val verifyResult = performVerifyStage(A, verifier, password)
        ) {
            is Response.Error -> verifyResult
            is Response.Success -> Response.Success(Unit)
        }
    }

    private suspend fun performVerifyStage(
        A: String,
        verifier: String,
        password: String
    ): Response<RegistrationResult> = loginStagesDataSource.performLoginStage(
        mapOf(
            TYPE_PARAM_KEY to LOGIN_BSSPEKE_VERIFY_TYPE,
            A_PARAM_KEY to A,
            VERIFIER_PARAM_KEY to verifier
        ), password
    )

    private fun getB(oprfResult: RegistrationResult): ByteArray {
        val verifyStage =
            ((oprfResult as? RegistrationResult.FlowResponse)?.flowResult?.missingStages?.firstOrNull {
                (it as? Stage.Other)?.type == LOGIN_BSSPEKE_VERIFY_TYPE
            } as? Stage.Other)
                ?: throw IllegalArgumentException(context.getString(R.string.b_param_is_missing))

        val blindSaltString = verifyStage.params?.getOrDefault(B_PARAM_KEY, "")?.toString()
        return Base64.decode(blindSaltString, Base64.NO_WRAP)
    }

    companion object {
        private const val BLIND_PARAM_KEY = "blind"
        private const val A_PARAM_KEY = "A"
        private const val B_PARAM_KEY = "B"
        private const val VERIFIER_PARAM_KEY = "verifier"
    }
}