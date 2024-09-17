package org.futo.circles.auth.utils

import org.futo.circles.auth.model.EmptyUserId
import org.futo.circles.auth.model.InvalidUserId
import org.futo.circles.auth.model.SuggestedUserId
import org.futo.circles.auth.model.ValidUserId
import org.futo.circles.auth.model.ValidateUserIdStatus
import org.futo.circles.core.base.DEFAULT_DOMAIN
import org.matrix.android.sdk.api.MatrixPatterns


object UserIdUtils {

    fun getNameAndDomainFromId(userId: String): Pair<String, String> {
        if (!MatrixPatterns.isUserId(userId)) throw IllegalArgumentException("Invalid userId $userId")

        return userId.split(":").takeIf { it.size == 2 }?.let {
            val userName = it.first().replace("@", "")
            val domain = it[1]
            userName to domain
        } ?: throw IllegalArgumentException("Invalid userId $userId")
    }


    fun validateUserId(input: String): ValidateUserIdStatus {
        if (input.isEmpty()) return EmptyUserId
        if (MatrixPatterns.isUserId(input)) return ValidUserId(input)

        return if (!input.contains("@")) handleMissingLeadingAtSymbol(input)
        else if (!input.startsWith("@")) handleEmailToUserIdTransform(input)
        else if (input.contains(":")) handleNoDomainInput(input)
        else InvalidUserId
    }

    private fun handleMissingLeadingAtSymbol(input: String): ValidateUserIdStatus {
        val suggestion = if (input.contains(":")) "@$input"
        else "@$input:$DEFAULT_DOMAIN"
        return SuggestedUserId(suggestion)
    }

    private fun handleEmailToUserIdTransform(input: String): ValidateUserIdStatus {
        val parts = input.split("@")
            .takeIf { it.size == 2 && !it.first().contains(":") }
            ?: return InvalidUserId
        return SuggestedUserId("@${parts.first()}:${parts[1]}")
    }

    private fun handleNoDomainInput(input: String): ValidateUserIdStatus {
        return SuggestedUserId("$input$DEFAULT_DOMAIN")
    }

}