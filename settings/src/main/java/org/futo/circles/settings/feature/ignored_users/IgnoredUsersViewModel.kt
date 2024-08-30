package org.futo.circles.settings.feature.ignored_users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.user.UserOptionsDataSource
import org.futo.circles.core.mapping.toCirclesUserSummary
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

@HiltViewModel
class IgnoredUsersViewModel @Inject constructor(
    private val userOptionsDataSource: UserOptionsDataSource
) : ViewModel() {

    val usersLiveData =
        MatrixSessionProvider.getSessionOrThrow().userService().getIgnoredUsersLive().map {
            it.map { it.toCirclesUserSummary() }
        }

    val unIgnoreUserLiveData = SingleEventLiveData<Response<Unit?>>()

    fun unIgnoreUser(userId: String) {
        launchBg {
            val result = userOptionsDataSource.unIgnoreSender(userId)
            unIgnoreUserLiveData.postValue(result)
        }
    }

}