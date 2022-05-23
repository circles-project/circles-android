package com.futo.circles.feature.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.futo.circles.R
import com.futo.circles.extensions.createResult
import com.futo.circles.model.LoadingData
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import kotlin.coroutines.Continuation

class SettingsDataSource(context: Context) {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )
    val profileLiveData = session.userService().getUserLive(session.myUserId)

    val loadingLiveData = MutableLiveData<LoadingData>()
    private val loadingData = LoadingData(total = 0)

    suspend fun logOut() = createResult {
        loadingLiveData.postValue(
            loadingData.apply { messageId = R.string.log_out; isLoading = true }
        )
        session.signOutService().signOut(true)
        loadingLiveData.postValue(loadingData.apply { isLoading = false })
    }

    suspend fun deactivateAccount() {
        session.accountService().deactivateAccount(false, object : UserInteractiveAuthInterceptor {
            override fun performStage(
                flowResponse: RegistrationFlowResponse,
                errCode: String?,
                promise: Continuation<UIABaseAuth>
            ) {
                Log.d("MyLog", "deactivate")
            }
        })
    }

}