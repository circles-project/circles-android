package org.futo.circles.core.feature.timeline.builder

import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import javax.inject.Inject

class MultiTimelineBuilder @Inject constructor(
    preferencesProvider: PreferencesProvider
) : BaseTimelineBuilder(preferencesProvider) {

    private var currentSnapshotMap: MutableMap<String, List<Post>> = mutableMapOf()
    private var readReceiptMap: MutableMap<String, List<Long>> = mutableMapOf()

    override suspend fun List<TimelineEvent>.processSnapshot(
        roomId: String,
        isThread: Boolean
    ): List<Post> {
        val room = MatrixSessionProvider.currentSession?.getRoom(roomId)
            ?: return getCurrentTimelinesPostsList()
        val roomName = room.roomSummary()?.nameOrId()
        val roomOwner = getRoomOwner(roomId)
        val receipts = getReadReceipts(room).also { readReceiptMap[roomId] = it }
        currentSnapshotMap[roomId] =
            this.filterRootPostNotFromOwner(isThread, receipts, roomName, roomOwner)
        return sortList(getCurrentTimelinesPostsList(), isThread)
    }

    private fun List<TimelineEvent>.filterRootPostNotFromOwner(
        isThread: Boolean,
        receipts: List<Long>,
        roomName: String?,
        roomOwner: RoomMemberSummary?
    ): List<Post> {
        val roomOwnerId = roomOwner?.userId
        val roomOwnerName = roomOwner?.notEmptyDisplayName()

        return mapNotNull {
            val senderId = it.senderInfo.userId
            if (roomOwnerId == senderId && !isThread)
                it.toPost(receipts, roomName, roomOwnerName)
            else null
        }
    }


    private fun getCurrentTimelinesPostsList() = currentSnapshotMap.flatMap { (_, value) -> value }

}