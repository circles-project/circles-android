package org.futo.circles.feature.settings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.futo.circles.R
import org.futo.circles.core.matrix.auth.AuthConfirmationProvider
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.model.LoadingData
import org.futo.circles.provider.MatrixSessionProvider

class SettingsDataSource(
    context: Context,
    private val authConfirmationProvider: AuthConfirmationProvider
) {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )
    val startReAuthEventLiveData = authConfirmationProvider.startReAuthEventLiveData
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

    suspend fun deactivateAccount(): Response<Unit> = createResult {
        session.accountService().deactivateAccount(false, authConfirmationProvider)
    }
}