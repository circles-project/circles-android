package org.futo.circles.feature.room.invite

import android.content.Context
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.futo.circles.R
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom

class InviteMembersDataSource @AssistedInject constructor(
    @Assisted private val roomId: String,
    @ApplicationContext private val context: Context
) {

    @AssistedFactory
    interface Factory {
        fun create(roomId: String): InviteMembersDataSource
    }

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