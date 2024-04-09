package org.futo.circles.auth.feature.uia.stages.password

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.credentials.CredentialsProvider
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.matrix.android.sdk.api.extensions.tryOrNull
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val passwordDataSource: PasswordDataSource,
    private val credentialsProvider: CredentialsProvider
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
                val userId = UIADataSourceProvider.getDataSourceOrThrow().getUserId()
                credentialsProvider.getManager()
                    ?.getPasswordCredentials(activityContext, userId)
                    ?.let { password ->
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
            val userId = UIADataSourceProvider.getDataSourceOrThrow().getUserId()
            credentialsProvider.getManager()
                ?.savePasswordCredentials(activityContext, userId, password)
        }
    }

    fun isPasswordWarningConfirmed() = isPasswordWarningConfirmed
    fun confirmPasswordWarning() {
        isPasswordWarningConfirmed = true
    }

}