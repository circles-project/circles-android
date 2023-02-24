package org.futo.circles.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.feature.notifications.ShortcutsHandler
import org.futo.circles.model.CIRCLE_TAG
import org.futo.circles.model.GROUP_TYPE
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class HomeViewModel(
    private val pushersManager: PushersManager,
    shortcutsHandler: ShortcutsHandler
) : ViewModel() {

    val notificationLiveData = SingleEventLiveData<String>()

    init {
        shortcutsHandler.observeRoomsAndBuildShortcuts(viewModelScope)
        autoAcceptInviteOnKnock()
    }

    fun registerPushNotifications(context: Context) {
        pushersManager.registerPushNotifications(context)
    }

    fun postNotificationData(summary: RoomSummary) {
        if (summary.roomType == GROUP_TYPE) {
            if (summary.membership == Membership.JOIN) notificationLiveData.postValue(summary.roomId)
        } else {
            if (summary.membership == Membership.JOIN) {
                getParentSpaceForRoom(summary)?.let { notificationLiveData.postValue(it) }
            }
        }
    }

    private fun getParentSpaceForRoom(summary: RoomSummary): String? {
        val circles = MatrixSessionProvider.currentSession?.roomService()
            ?.getRoomSummaries(roomSummaryQueryParams { excludeType = null })
            ?.filter { item -> item.hasTag(CIRCLE_TAG) } ?: emptyList()

        val parentCircle =
            circles.firstOrNull { it.spaceChildren?.firstOrNull { it.childRoomId == summary.roomId } != null }

        return parentCircle?.roomId
    }

    private fun autoAcceptInviteOnKnock() {
        val session = MatrixSessionProvider.currentSession ?: return
        session.roomService().getRoomSummaries(roomSummaryQueryParams {
            memberships = listOf(Membership.INVITE)
        }).firstOrNull { it.roomType == RoomType.SPACE }?.let {
            launchBg { session.roomService().joinRoom(it.roomId) }
        }
    }
}