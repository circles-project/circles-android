package org.futo.circles.feature.sign_up.password

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
    private val signUpDataSource: SignUpDataSource
) : PasswordDataSource {

    override fun getMinimumPasswordLength(): Int = 1

    override suspend fun processPasswordStage(password: String): Response<Unit> {
        val bsSpekeClient = BSSpekeClient(
            "@${signUpDataSource.userName}:${signUpDataSource.domain}",
            signUpDataSource.domain,
            password
        )
        return when (val oprfResult = performOprfStage(bsSpekeClient)) {
            is Response.Success -> {
                when (val saveResult = performSaveStage(bsSpekeClient, password)) {
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
        password: String
    ): Response<RegistrationResult> {
        val phf = getPpf()
        val PandV = bsSpekeClient.generatePandV(
            getBlindSalt().toByteArray(),
            (phf.getOrDefault(PHF_ITERATIONS_PARAM_KEY, 0) as? Int) ?: 0,
            (phf.getOrDefault(PHF_BLOCKS_PARAM_KEY, 0) as? Int) ?: 0
        )
        return signUpDataSource.performRegistrationStage(
            mapOf(
                TYPE_PARAM_KEY to REGISTRATION_BSSPEKE_SAVE_TYPE,
                P_PARAM_KEY to PandV.first,
                V_PARAM_KEY to PandV.second,
                PHF_PARAM_KEY to phf
            ), password
        )
    }

    private fun getCurve(): String =
        ((signUpDataSource.currentStage as? Stage.Other)?.params?.getOrDefault(
            CURVE_PARAM_KEY, ""
        ))?.toString() ?: ""

    private fun getPpf(): Map<String, String> =
        ((signUpDataSource.currentStage as? Stage.Other)?.params?.getOrDefault(
            PHF_PARAM_KEY, emptyMap<String, String>()
        ) as? Map<*, *>)?.map { it.key.toString() to it.value.toString() }?.toMap()
            ?: emptyMap()

    private fun getBlindSalt(): String =
        ((signUpDataSource.currentStage as? Stage.Other)?.params?.getOrDefault(
            BLIND_SALT_PARAM_KEY, ""
        ))?.toString() ?: ""

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