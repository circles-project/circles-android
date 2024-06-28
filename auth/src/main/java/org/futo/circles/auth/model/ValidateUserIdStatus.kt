package org.futo.circles.auth.model

sealed class ValidateUserIdStatus

data object EmptyUserId : ValidateUserIdStatus()
data object InvalidUserId : ValidateUserIdStatus()
data class ValidUserId(val userId: String) : ValidateUserIdStatus()
data class SuggestedUserId(val suggestedUserId: String) : ValidateUserIdStatus()
