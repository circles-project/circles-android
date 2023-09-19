package org.futo.circles.auth.feature.log_in.log_out

import androidx.lifecycle.MutableLiveData
import org.futo.circles.auth.R
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

class LogoutDataSource @Inject constructor() {

    private val session = MatrixSessionProvider.getSessionOrThrow()

    val loadingLiveData = MutableLiveData<LoadingData>()

    suspend fun logOut() = createResult {
        loadingLiveData.postValue(LoadingData(messageId = R.string.log_out))
        session.signOutService().signOut(true)
        loadingLiveData.postValue(LoadingData(isLoading = false))
    }

}