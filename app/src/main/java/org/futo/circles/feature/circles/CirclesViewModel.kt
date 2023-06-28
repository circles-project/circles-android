package org.futo.circles.feature.circles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.RequestCircleListItem
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

@HiltViewModel
class CirclesViewModel @Inject constructor(
    private val dataSource: CirclesDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.getCirclesFlow().asLiveData()
    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun rejectInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.rejectInvite(roomId)) }
    }

    fun inviteUser(room: RequestCircleListItem) {
        launchBg {
            val result = createResult {
                MatrixSessionProvider.currentSession?.getRoom(room.id)?.membershipService()
                    ?.invite(room.requesterId)
            }
            inviteResultLiveData.postValue(result)
        }
    }

    fun kickUser(room: RequestCircleListItem) {
        launchBg {
            MatrixSessionProvider.currentSession?.getRoom(room.id)?.membershipService()
                ?.remove(room.requesterId)
        }
    }

}