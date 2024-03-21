package org.futo.circles.auth.feature.uia.stages.password

import android.content.Context
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

    fun processPasswordStage(password: String) {
        launchBg { handlePasswordRequest(password) }
    }

    fun getCredentials(activityContext: Context) {
        launchBg {
            tryOrNull {
                val credentialManager = CredentialManager.create(activityContext)
                val userName = UIADataSourceProvider.getDataSourceOrThrow().userName
                val request = GetCredentialRequest(
                    listOf(GetPasswordOption(allowedUserIds = setOf(userName)))
                )

                val result = credentialManager.getCredential(
                    context = activityContext,
                    request = request
                ).credential

                if (result is PasswordCredential) {
                    val password = result.password
                    passwordSelectedEventLiveData.postValue(password)
                    handlePasswordRequest(password)
                }
            }
        }
    }

    private suspend fun handlePasswordRequest(password: String) {
        val result = passwordDataSource.processPasswordStage(password)
        passwordResponseLiveData.postValue(result)
    }

    fun isPasswordWarningConfirmed() = isPasswordWarningConfirmed
    fun confirmPasswordWarning() {
        isPasswordWarningConfirmed = true
    }

}