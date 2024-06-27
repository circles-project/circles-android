package org.futo.circles.auth.feature.log_in.suggestion

interface LoginSuggestionListener {

    fun onLoginSuggestionApplied(userId: String, isForgotPassword: Boolean)

}