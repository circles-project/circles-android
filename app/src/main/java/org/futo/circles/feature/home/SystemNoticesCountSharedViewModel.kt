package org.futo.circles.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import org.futo.circles.core.DEFAULT_USER_PREFIX
import org.futo.circles.core.SYSTEM_NOTICES_TAG
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserUtils
import org.futo.circles.extensions.launchBg
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class SystemNoticesCountSharedViewModel : ViewModel() {

    val systemNoticesCountLiveData = MatrixSessionProvider.currentSession?.roomService()
        ?.getRoomSummariesLive(roomSummaryQueryParams())
        ?.map { list -> list.firstOrNull { it.hasTag(SYSTEM_NOTICES_TAG) }?.notificationCount }

    init {
        autoAcceptNoticeRoomInvite()
    }

    private fun autoAcceptNoticeRoomInvite() {
        val session = MatrixSessionProvider.currentSession ?: return
        session.roomService().getRoomSummaries(roomSummaryQueryParams {
            memberships = listOf(Membership.INVITE)
        }).firstOrNull { it.inviterId == getNoticesUserId(session) }?.let {
            launchBg { session.roomService().joinRoom(it.roomId) }
        }
    }

    private fun getNoticesUserId(session: Session): String {
        val domain = UserUtils.getServerDomain(session.myUserId)
        return DEFAULT_USER_PREFIX + domain
    }

}