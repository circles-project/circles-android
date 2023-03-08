package org.futo.circles.feature.people.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.collectLatest
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.matrix.room.RoomRelationsBuilder
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.launchBg
import org.futo.circles.extensions.launchUi
import org.futo.circles.model.TimelineListItem
import org.futo.circles.provider.MatrixSessionProvider

class UserViewModel(
    private val userDataSource: UserDataSource,
    private val roomRelationsBuilder: RoomRelationsBuilder
) : ViewModel() {

    val userLiveData = userDataSource.userLiveData
    val timelineLiveDataLiveData = MutableLiveData<List<TimelineListItem>>()
    val requestFollowLiveData = SingleEventLiveData<Response<Unit?>>()

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

    fun unFollow(timelineId: String) {
        launchBg {
            createResult {
                roomRelationsBuilder.removeFromAllParents(timelineId)
                MatrixSessionProvider.currentSession?.roomService()?.leaveRoom(timelineId)
            }
        }
    }

}