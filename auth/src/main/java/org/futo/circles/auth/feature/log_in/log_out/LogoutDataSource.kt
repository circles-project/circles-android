package org.futo.circles.auth.feature.log_in.log_out

import android.content.Context
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

class LogoutDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )

    val loadingLiveData = MutableLiveData<LoadingData>()
    private val loadingData = LoadingData(total = 0)


    suspend fun logOut() = createResult {
        loadingLiveData.postValue(
            loadingData.apply {
                messageId = R.string.log_out
                isLoading = true
            }
        )
        session.signOutService().signOut(true)
        loadingLiveData.postValue(loadingData.apply { isLoading = false })
    }


}