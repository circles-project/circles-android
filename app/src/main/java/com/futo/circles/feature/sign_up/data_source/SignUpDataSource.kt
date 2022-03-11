package com.futo.circles.feature.sign_up.data_source

import com.futo.circles.extensions.getPendingSignUpSessionId
import com.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.Stage

class SignUpDataSource {

    private val stagesToComplete = mutableListOf<Stage>()

    private val registrationWizard by lazy {
        MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
    }

    fun startNewRegistration(stages: List<Stage>) {
        stagesToComplete.clear()
        stagesToComplete.addAll(stages)
    }

    fun getPendingSessionId() = registrationWizard.getPendingSignUpSessionId()

    fun getCurrentStage() = stagesToComplete.first()

}