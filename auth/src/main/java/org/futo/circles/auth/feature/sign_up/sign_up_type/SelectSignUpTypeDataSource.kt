package org.futo.circles.auth.feature.sign_up.sign_up_type

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.sign_up.SignUpDataSource
import org.futo.circles.auth.feature.sign_up.SignUpDataSource.Companion.REGISTRATION_SUBSCRIPTION_TYPE
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfigFromDomain
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

class SelectSignUpTypeDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val signUpDataSource: SignUpDataSource
) {

    fun clearSubtitle() {
        signUpDataSource.clearSubtitle()
    }

    suspend fun startNewRegistration(domain: String) = createResult {
        val authService = MatrixInstanceProvider.matrix.authenticationService().apply {
            cancelPendingLoginOrRegistration()
            initiateAuth(buildHomeServerConfigFromDomain(domain))
        }
        val stages = authService.getRegistrationWizard().getAllRegistrationFlows().firstOrNull()
            ?: throw IllegalArgumentException(context.getString(R.string.wrong_signup_config))

        val list = mutableListOf<Stage>().apply {
            add(
                Stage.Other(
                    true, REGISTRATION_SUBSCRIPTION_TYPE, mapOf(
                        "subscription_ids" to listOf(
                            "org.futo.circles.standard01month",
                            "org.futo.circles.standard12month",
                            "org.futo.circles.standard01month.eu",
                            "org.futo.circles.standard12month.eu"
                        )
                    )
                )
            )
            addAll(stages)
        }

        signUpDataSource.startSignUpStages(list, domain)
    }

}