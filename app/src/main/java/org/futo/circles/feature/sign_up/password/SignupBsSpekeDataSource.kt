package org.futo.circles.feature.sign_up.password

import android.content.Context
import android.util.Base64
import org.futo.circles.R
import org.futo.circles.bsspeke.BSSpekeClient
import org.futo.circles.core.REGISTRATION_BSSPEKE_OPRF_TYPE
import org.futo.circles.core.REGISTRATION_BSSPEKE_SAVE_TYPE
import org.futo.circles.core.TYPE_PARAM_KEY
import org.futo.circles.core.auth.PasswordDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage

class SignupBsSpekeDataSource(
    private val context: Context,
    private val signUpDataSource: SignUpDataSource
) : PasswordDataSource {

    private val phfParams = mapOf(
        "name" to "argon2i",
        PHF_ITERATIONS_PARAM_KEY to 3,
        PHF_BLOCKS_PARAM_KEY to 100_000
    )

    override fun getMinimumPasswordLength(): Int = 1

    override suspend fun processPasswordStage(password: String): Response<Unit> {
        val bsSpekeClient = BSSpekeClient(
            "@${signUpDataSource.userName}:${signUpDataSource.domain}",
            signUpDataSource.domain,
            password
        )
        return when (val oprfResult = performOprfStage(bsSpekeClient)) {
            is Response.Success -> {
                when (val saveResult =
                    performSaveStage(bsSpekeClient, getBlindSalt(oprfResult.data), password)) {
                    is Response.Error -> saveResult
                    is Response.Success -> Response.Success(Unit)
                }
            }
            is Response.Error -> oprfResult
        }
    }

    private suspend fun performOprfStage(
        bsSpekeClient: BSSpekeClient
    ): Response<RegistrationResult> = signUpDataSource.performRegistrationStage(
        mapOf(
            TYPE_PARAM_KEY to REGISTRATION_BSSPEKE_OPRF_TYPE,
            CURVE_PARAM_KEY to getCurve(),
            BLIND_PARAM_KEY to bsSpekeClient.generateBase64Blind()
        )
    )

    private suspend fun performSaveStage(
        bsSpekeClient: BSSpekeClient,
        blindSalt: ByteArray,
        password: String
    ): Response<RegistrationResult> {
        val PandV = bsSpekeClient.generateBase64PandV(
            blindSalt,
            (phfParams.getOrDefault(PHF_BLOCKS_PARAM_KEY, 0) as? Int) ?: 0,
            (phfParams.getOrDefault(PHF_ITERATIONS_PARAM_KEY, 0) as? Int) ?: 0
        )
        return signUpDataSource.performRegistrationStage(
            mapOf(
                TYPE_PARAM_KEY to REGISTRATION_BSSPEKE_SAVE_TYPE,
                P_PARAM_KEY to PandV.first,
                V_PARAM_KEY to PandV.second,
                PHF_PARAM_KEY to phfParams
            ), password
        )
    }

    private fun getCurve(): String =
        ((signUpDataSource.currentStage as? Stage.Other)?.params?.getOrDefault(
            CURVE_PARAM_KEY, ""
        ))?.toString() ?: ""

    private fun getBlindSalt(oprfResult: RegistrationResult): ByteArray {
        val saveStage =
            ((oprfResult as? RegistrationResult.FlowResponse)?.flowResult?.missingStages?.firstOrNull {
                (it as? Stage.Other)?.type == REGISTRATION_BSSPEKE_SAVE_TYPE
            } as? Stage.Other)
                ?: throw IllegalArgumentException(context.getString(R.string.blind_salt_is_missing))

        val blindSaltString = saveStage.params?.getOrDefault(BLIND_SALT_PARAM_KEY, "")?.toString()
        return Base64.decode(blindSaltString, Base64.NO_WRAP)
    }

    companion object {
        private const val CURVE_PARAM_KEY = "curve"
        private const val BLIND_PARAM_KEY = "blind"
        private const val BLIND_SALT_PARAM_KEY = "blind_salt"
        private const val P_PARAM_KEY = "P"
        private const val V_PARAM_KEY = "V"
        private const val PHF_PARAM_KEY = "phf_params"
        private const val PHF_ITERATIONS_PARAM_KEY = "iterations"
        private const val PHF_BLOCKS_PARAM_KEY = "blocks"
    }
}