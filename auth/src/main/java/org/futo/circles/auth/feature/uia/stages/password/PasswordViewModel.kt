package org.futo.circles.auth.feature.uia.stages.password

import android.content.Context
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.matrix.android.sdk.api.extensions.tryOrNull
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val passwordDataSource: PasswordDataSource
) : ViewModel() {

    private var isPasswordWarningConfirmed: Boolean = false
    val passwordResponseLiveData = SingleEventLiveData<Response<Unit>>()
    val passwordSelectedEventLiveData = SingleEventLiveData<String>()

    fun processPasswordStage(password: String, isSignup: Boolean, activityContext: Context) {
        launchBg { handlePasswordRequest(password, isSignup, activityContext) }
    }

    fun getCredentials(activityContext: Context) {
        launchBg {
            tryOrNull {
                val credentialManager = CredentialManager.create(activityContext)
                val userId = UIADataSourceProvider.getDataSourceOrThrow().getUserId()
                val request = GetCredentialRequest(
                    listOf(GetPasswordOption(allowedUserIds = setOf(userId)))
                )

                val result = credentialManager.getCredential(
                    context = activityContext,
                    request = request
                ).credential

                if (result is PasswordCredential) {
                    val password = result.password
                    passwordSelectedEventLiveData.postValue(password)
                    handlePasswordRequest(password, false, activityContext)
                }
            }
        }
    }

    private suspend fun handlePasswordRequest(
        password: String,
        isSignup: Boolean,
        activityContext: Context
    ) {
        if (isSignup) registerPassword(activityContext, password)
        val result = passwordDataSource.processPasswordStage(password)
        passwordResponseLiveData.postValue(result)
    }

    private suspend fun registerPassword(activityContext: Context, password: String) {
        tryOrNull {
            val uiaDataSource = UIADataSourceProvider.getDataSourceOrThrow()
            val createPasswordRequest = CreatePasswordRequest(
                id = uiaDataSource.getUserId(),
                password = password
            )
            CredentialManager.create(activityContext).createCredential(
                activityContext,
                createPasswordRequest
            )
        }
    }

    fun isPasswordWarningConfirmed() = isPasswordWarningConfirmed
    fun confirmPasswordWarning() {
        isPasswordWarningConfirmed = true
    }

}