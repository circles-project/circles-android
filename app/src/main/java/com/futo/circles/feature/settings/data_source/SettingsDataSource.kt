package com.futo.circles.feature.settings.data_source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.futo.circles.R
import com.futo.circles.extensions.createResult
import com.futo.circles.model.LoadingData
import com.futo.circles.provider.MatrixSessionProvider

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

}