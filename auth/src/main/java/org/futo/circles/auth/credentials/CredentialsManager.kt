package org.futo.circles.auth.credentials

import android.content.Context

interface CredentialsManager {
    suspend fun getPasswordCredentials(activityContext: Context, userId: String): String?

    suspend fun savePasswordCredentials(activityContext: Context, userId: String, password: String)

}