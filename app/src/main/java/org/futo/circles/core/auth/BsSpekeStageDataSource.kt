package org.futo.circles.core.auth

import android.content.Context
import android.util.Base64
import org.futo.circles.R
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage

interface BsSpekeStageDataSource {

    fun getCurve(oprfStage: Stage?): String =
        ((oprfStage as? Stage.Other)?.params?.getOrDefault(
            CURVE_PARAM_KEY, ""
        ))?.toString() ?: ""

    fun getPhf(oprfStage: Stage?): Map<String, Any?> =
        ((oprfStage as? Stage.Other)?.params?.getOrDefault(
            PHF_PARAM_KEY, emptyMap<String, Any>()
        ) as? Map<*, *>)?.map { it.key.toString() to it.value }?.toMap()
            ?: emptyMap()

    fun getBlocks(context: Context, phfParams: Map<String, Any?>): Int =
        (phfParams.getOrDefault(PHF_BLOCKS_PARAM_KEY, 0) as? Double)
            ?.toInt()
            ?.takeIf { it >= MIN_PHF_BLOCKS }
            ?: throw IllegalArgumentException(
                context.getString(R.string.phf_blocks_error_format, MIN_PHF_BLOCKS)
            )

    fun getIterations(context: Context, phfParams: Map<String, Any?>): Int =
        (phfParams.getOrDefault(PHF_ITERATIONS_PARAM_KEY, 0) as? Double)
            ?.toInt()
            ?.takeIf { it >= MIN_PHF_ITERATIONS }
            ?: throw IllegalArgumentException(
                context.getString(R.string.phf_iterations_error_format, MIN_PHF_ITERATIONS)
            )

    fun getBlindSalt(
        context: Context,
        oprfResult: RegistrationResult,
        stageType: String
    ): ByteArray {
        val stage =
            ((oprfResult as? RegistrationResult.FlowResponse)?.flowResult?.missingStages?.firstOrNull {
                (it as? Stage.Other)?.type == stageType
            } as? Stage.Other)
                ?: throw IllegalArgumentException(context.getString(R.string.blind_salt_is_missing))

        val blindSaltString = stage.params?.getOrDefault(BLIND_SALT_PARAM_KEY, "")?.toString()
        return Base64.decode(blindSaltString, Base64.NO_WRAP)
    }

    companion object {
        const val CURVE_PARAM_KEY = "curve"
        const val PHF_PARAM_KEY = "phf_params"
        private const val BLIND_SALT_PARAM_KEY = "blind_salt"
        private const val PHF_ITERATIONS_PARAM_KEY = "iterations"
        private const val PHF_BLOCKS_PARAM_KEY = "blocks"
        private const val MIN_PHF_ITERATIONS = 3
        private const val MIN_PHF_BLOCKS = 100_000
    }
}