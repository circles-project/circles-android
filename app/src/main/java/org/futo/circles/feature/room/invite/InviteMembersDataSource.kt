package org.futo.circles.feature.room.invite

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.futo.circles.R
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

@ViewModelScoped
class InviteMembersDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

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