package org.futo.circles.feature.direct.tab

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.futo.circles.core.utils.getAllDirectMessagesLiveData
import org.futo.circles.mapping.toJoinedDMListItem
import org.futo.circles.model.DMListItem
import org.futo.circles.model.DMsInvitesNotificationListItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

class DMDataSource @Inject constructor() {

    fun getDirectMessagesListFlow() = getAllDirectMessagesLiveData().asFlow().map { roomSummaries ->
        withContext(Dispatchers.IO) { buildList(roomSummaries) }
    }.distinctUntilChanged()


    private fun buildList(directMessages: List<RoomSummary>): List<DMListItem> {
        val joinedDirectMessages = directMessages
            .filter { it.membership == Membership.JOIN && (it.joinedMembersCount ?: 0) > 1 }
            .map { it.toJoinedDMListItem() }

        val invitesCount = directMessages.filter { it.membership == Membership.INVITE }.size

        return mutableListOf<DMListItem>().apply {
            if (invitesCount > 0) {
                add(DMsInvitesNotificationListItem(invitesCount))
            }

            addAll(joinedDirectMessages)
        }
    }

}