package com.futo.circles.feature.validate_email.data_source

import com.futo.circles.feature.sign_up.data_source.SignUpDataSource
import com.futo.circles.provider.MatrixInstanceProvider

class ValidateEmailDataSource(
    private val signUpDataSource: SignUpDataSource
) {

    private val wizard by lazy {
        MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
    }

    suspend fun sendValidationCode(email: String) {}

    suspend fun validateEmail(code:String){}

}