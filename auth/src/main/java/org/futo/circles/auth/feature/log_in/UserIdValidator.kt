package org.futo.circles.auth.feature.log_in

import org.futo.circles.auth.model.EmptyUserId
import org.futo.circles.auth.model.InvalidUserId
import org.futo.circles.auth.model.SuggestedUserId
import org.futo.circles.auth.model.ValidUserId
import org.futo.circles.auth.model.ValidateUserIdStatus
import org.futo.circles.core.base.CirclesAppConfig
import org.matrix.android.sdk.api.MatrixPatterns

object UserIdValidator {

    private val defaultDomain = CirclesAppConfig.usDomain

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
        else "@$input:$defaultDomain"
        return SuggestedUserId(suggestion)
    }

    private fun handleEmailToUserIdTransform(input: String): ValidateUserIdStatus {
        val parts = input.split("@")
            .takeIf { it.size == 2 && !it.first().contains(":") } ?: return InvalidUserId
        return SuggestedUserId("@${parts.first()}:${parts[1]}")
    }

    private fun handleNoDomainInput(input: String): ValidateUserIdStatus {
        return SuggestedUserId("$input$defaultDomain")
    }
}