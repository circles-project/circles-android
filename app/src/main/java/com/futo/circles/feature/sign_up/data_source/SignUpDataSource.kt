package com.futo.circles.feature.sign_up.data_source

import com.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.Stage

class SignUpDataSource {

    private val registrationWizard by lazy {
        MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
    }

    private val stagesToComplete = mutableListOf<Stage>()

    fun startNewRegistration(stages: List<Stage>) {
        stagesToComplete.clear()
        stagesToComplete.addAll(stages)
    }
}