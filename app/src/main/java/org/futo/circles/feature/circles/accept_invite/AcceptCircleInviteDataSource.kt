package org.futo.circles.feature.circles.accept_invite

import androidx.lifecycle.MutableLiveData
import org.futo.circles.core.matrix.room.RoomRelationsBuilder
import org.futo.circles.extensions.createResult
import org.futo.circles.mapping.toSelectableRoomListItem
import org.futo.circles.model.CIRCLE_TAG
import org.futo.circles.model.SelectableRoomListItem
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class AcceptCircleInviteDataSource(
    private val roomId: String,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    private val session by lazy { MatrixSessionProvider.currentSession }

    val circlesLiveData = MutableLiveData(getInitialCirclesList())

    private fun getInitialCirclesList(): List<SelectableRoomListItem> =
        session?.roomService()?.getRoomSummaries(roomSummaryQueryParams {
            excludeType = null
        })?.mapNotNull { summary ->
            if (summary.hasTag(CIRCLE_TAG) && summary.membership == Membership.JOIN)
                summary.toSelectableRoomListItem()
            else null
        } ?: emptyList()

    fun getSelectedCircles() = circlesLiveData.value?.filter { it.isSelected } ?: emptyList()

    fun toggleCircleSelect(circle: SelectableRoomListItem) {
        val newList = circlesLiveData.value?.toMutableList()?.map {
            if (it.id == circle.id) it.copy(isSelected = !it.isSelected) else it
        }
        circlesLiveData.postValue(newList)
    }

    suspend fun acceptCircleInvite() = createResult {
        session?.roomService()?.joinRoom(roomId)
        getSelectedCircles().forEach { circle ->
            roomRelationsBuilder.setInvitedCircleRelations(roomId, circle.id)
        }
    }

}