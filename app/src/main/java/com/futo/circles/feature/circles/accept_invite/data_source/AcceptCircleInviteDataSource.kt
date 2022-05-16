package com.futo.circles.feature.circles.accept_invite.data_source

import androidx.lifecycle.MutableLiveData
import com.futo.circles.mapping.toSelectableRoomListItem
import com.futo.circles.model.CIRCLE_TAG
import com.futo.circles.model.SelectableRoomListItem
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class AcceptCircleInviteDataSource {

    val circlesLiveData = MutableLiveData(getInitialCirclesList())

    private fun getInitialCirclesList(): List<SelectableRoomListItem> =
        MatrixSessionProvider.currentSession?.getRoomSummaries(roomSummaryQueryParams {
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

    suspend fun acceptCircleInvite() {

    }

}