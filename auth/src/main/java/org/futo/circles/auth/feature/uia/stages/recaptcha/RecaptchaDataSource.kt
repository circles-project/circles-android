package org.futo.circles.auth.feature.uia.stages.recaptcha

import org.futo.circles.auth.feature.uia.UIADataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject


class RecaptchaDataSource @Inject constructor() {

    private val uiaDataSource = UIADataSourceProvider.getDataSourceOrThrow()

    suspend fun handleRecaptcha(response: String): Response<Unit> {
        val result = uiaDataSource.performUIAStage(
            mapOf(
                TYPE_PARAM_KEY to LoginFlowTypes.RECAPTCHA,
                "response" to response
            )
        )
        return when (result) {
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> result
        }
    }


    fun getRecaptchaParams() = (uiaDataSource.currentStage as? Stage.ReCaptcha)?.publicKey ?: ""

}