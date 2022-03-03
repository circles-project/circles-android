package com.futo.circles.feature.manage_group_members.data_source


import android.content.Context
import com.futo.circles.R
import com.futo.circles.extensions.nameOrId
import com.futo.circles.provider.MatrixSessionProvider

class ManageGroupMembersDataSource(
    private val roomId: String,
    private val context: Context
) {

    private val session = MatrixSessionProvider.currentSession
    private val room = session?.getRoom(roomId)


    fun getManageMembersTittle() = context.getString(
        R.string.group_members_format,
        room?.roomSummary()?.nameOrId() ?: roomId
    )

}