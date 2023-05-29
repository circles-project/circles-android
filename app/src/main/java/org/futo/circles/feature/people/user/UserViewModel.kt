package org.futo.circles.feature.people.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import kotlinx.coroutines.flow.collectLatest
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.launchUi
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.RoomRelationsBuilder
import org.futo.circles.feature.people.UserOptionsDataSource
import org.futo.circles.model.TimelineListItem

class UserViewModel(
    private val userId: String,
    private val userDataSource: UserDataSource,
    private val userOptionsDataSource: UserOptionsDataSource,
    private val roomRelationsBuilder: RoomRelationsBuilder
) : ViewModel() {

    val userLiveData = userDataSource.userLiveData
    val timelineLiveDataLiveData = MutableLiveData<List<TimelineListItem>>()
    val requestFollowLiveData = SingleEventLiveData<Response<Unit?>>()
    val ignoreUserLiveData = SingleEventLiveData<Response<Unit?>>()
    val unIgnoreUserLiveData = SingleEventLiveData<Response<Unit?>>()
    val unFollowUserLiveData = SingleEventLiveData<Response<Unit?>>()
    val isUserIgnoredLiveData = userOptionsDataSource.ignoredUsersLiveData?.map {
        it.firstOrNull { it.userId == userId } != null
    }

    init {
        getUsersTimelines()
    }

    private fun getUsersTimelines() {
        launchUi {
            userDataSource.getTimelinesFlow().collectLatest {
                timelineLiveDataLiveData.postValue(it)
            }
        }
    }

    fun requestFollowTimeline(timelineId: String) {
        launchBg {
            val result = createResult {
                MatrixSessionProvider.currentSession?.roomService()?.knock(timelineId)
            }
            requestFollowLiveData.postValue(result)
        }
    }

    fun unFollowTimeline(timelineId: String) {
        launchBg {
            createResult {
                roomRelationsBuilder.removeFromAllParents(timelineId)
                MatrixSessionProvider.currentSession?.roomService()?.leaveRoom(timelineId)
            }
        }
    }

    fun ignoreUser() {
        launchBg {
            ignoreUserLiveData.postValue(userOptionsDataSource.ignoreSender(userId))
        }
    }

    fun unIgnoreUser() {
        launchBg {
            unIgnoreUserLiveData.postValue(userOptionsDataSource.unIgnoreSender(userId))
        }
    }

    fun unFollowUser() {
        launchBg {
            unFollowUserLiveData.postValue(userOptionsDataSource.unFollowUser(userId))
        }
    }

    fun amIFollowingUser(): Boolean = userOptionsDataSource.amIFollowingUser(userId)

}