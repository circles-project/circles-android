package org.futo.circles.auth.model

enum class PasswordModeArg {
    LoginPasswordStage,
    LoginDirect,
    LoginBsSpekeStage,
    ReAuthPassword,
    ReAuthBsSpekeLogin,
    ReAuthBsSpekeSignup,
    SignupPasswordStage,
    SignupBsSpekeStage
}