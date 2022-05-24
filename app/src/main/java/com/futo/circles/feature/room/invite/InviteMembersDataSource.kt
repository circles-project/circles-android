package com.futo.circles.feature.room.invite

import android.content.Context
import com.futo.circles.R
import com.futo.circles.extensions.createResult
import com.futo.circles.mapping.nameOrId
import com.futo.circles.model.UserListItem
import com.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.matrix.android.sdk.api.session.getRoom

class InviteMembersDataSource(
    private val roomId: String,
    private val context: Context
) {

    private val session = MatrixSessionProvider.currentSession
    private val room = session?.getRoom(roomId)


    fun getInviteTitle() = context.getString(
        R.string.invite_to_format,
        room?.roomSummary()?.nameOrId() ?: roomId
    )

    suspend fun inviteUsers(scope: CoroutineScope, users: List<UserListItem>) = createResult {
        users.map {
            scope.async { room?.membershipService()?.invite(it.id, null) }
        }.awaitAll()

        return@createResult
    }
}