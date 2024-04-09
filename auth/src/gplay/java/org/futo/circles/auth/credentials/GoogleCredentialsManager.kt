package org.futo.circles.auth.credentials

import android.content.Context
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential

class GoogleCredentialsManager : CredentialsManager {
    override suspend fun getPasswordCredentials(activityContext: Context, userId: String): String? {
        val credentialManager = CredentialManager.create(activityContext)
        val request = GetCredentialRequest(
            listOf(GetPasswordOption(allowedUserIds = setOf(userId)))
        )

        val result = credentialManager.getCredential(
            context = activityContext,
            request = request
        ).credential

        return (result as? PasswordCredential)?.password
    }

    override suspend fun savePasswordCredentials(
        activityContext: Context,
        userId: String,
        password: String
    ) {
        val createPasswordRequest = CreatePasswordRequest(
            id = userId,
            password = password
        )
        CredentialManager.create(activityContext).createCredential(
            activityContext,
            createPasswordRequest
        )
    }

}