package org.futo.circles.feature.room.invite

import android.content.Context
import org.futo.circles.R
import org.futo.circles.extensions.createResult
import org.futo.circles.mapping.nameOrId
import org.futo.circles.model.UserListItem
import org.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.matrix.android.sdk.api.session.getRoom

class InviteMembersDataSource(
    private val roomId: String,
    private val context: Context
) {

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)

    fun getInviteTitle() = context.getString(
        R.string.invite_to_format,
        room?.roomSummary()?.nameOrId() ?: roomId
    )

    suspend fun inviteUsers(scope: CoroutineScope, usersIds: List<String>) = createResult {
        usersIds.map {
            scope.async { room?.membershipService()?.invite(it, null) }
        }.awaitAll()
    }
}