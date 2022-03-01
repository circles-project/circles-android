package com.futo.circles.feature.groups.timeline.invite.data_source

import android.content.Context
import com.futo.circles.R
import com.futo.circles.extensions.nameOrId
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.timeline.Timeline

class InviteMembersDataSource(
    private val roomId: String,
    private val context: Context
) : Timeline.Listener {

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)

    fun getInviteTitle() = context.getString(
        R.string.invite_members_to_format,
        room?.roomSummary()?.nameOrId() ?: roomId
    )

}