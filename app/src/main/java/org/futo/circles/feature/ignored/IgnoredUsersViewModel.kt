package org.futo.circles.feature.ignored

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.mapping.toCirclesUserSummary
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.feature.people.UserOptionsDataSource
import javax.inject.Inject

@HiltViewModel
class IgnoredUsersViewModel @Inject constructor(
    private val userOptionsDataSource: UserOptionsDataSource
) : ViewModel() {

    val unIgnoreUserLiveData = SingleEventLiveData<Response<Unit?>>()

    val ignoredUsersLiveData =
        MatrixSessionProvider.getSessionOrThrow().userService().getIgnoredUsersLive()
            .map { it.map { it.toCirclesUserSummary() } }

    fun unIgnoreUser(userId: String) {
        launchBg {
            unIgnoreUserLiveData.postValue(userOptionsDataSource.unIgnoreSender(userId))
        }
    }

}