package com.futo.circles.feature.group_invite.data_source

import android.content.Context
import com.futo.circles.R
import com.futo.circles.extensions.nameOrId
import com.futo.circles.provider.MatrixSessionProvider

class InviteMembersDataSource(
    private val roomId: String,
    private val context: Context
) {

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)

    fun getInviteTitle() = context.getString(
        R.string.invite_members_to_format,
        room?.roomSummary()?.nameOrId() ?: roomId
    )

}