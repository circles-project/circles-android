package com.futo.circles.feature.group_invite.data_source

import android.content.Context
import androidx.lifecycle.asFlow
import com.futo.circles.R
import com.futo.circles.extensions.nameOrId
import com.futo.circles.mapping.toRoomMember
import com.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.matrix.android.sdk.api.session.user.model.User

class InviteMembersDataSource(
    private val roomId: String,
    private val context: Context
) {

    private val session = MatrixSessionProvider.currentSession
    private val room = session?.getRoom(roomId)

    private val existingMembersIds = room?.roomSummary()?.otherMemberIds?.toSet().orEmpty()

    fun getInviteTitle() = context.getString(
        R.string.invite_members_to_format,
        room?.roomSummary()?.nameOrId() ?: roomId
    )

    suspend fun search(query: String) = combine(searchKnownUsers(query), searchSuggestions(query))
    { knowUsers, suggestions ->
        (knowUsers + suggestions).distinctBy { it.userId }.map { it.toRoomMember() }
    }.flowOn(Dispatchers.IO).distinctUntilChanged()


    private fun searchKnownUsers(query: String) = session?.getUsersLive()?.asFlow()
        ?.map { list ->
            list.filter { user ->
                (user.displayName?.contains(query, true) ?: false
                        || user.userId.contains(query, true))
                        && existingMembersIds.contains(user.userId).not()
            }
        } ?: flowOf()


    private suspend fun searchSuggestions(query: String): Flow<List<User>> = flow {
        val users = session?.searchUsersDirectory(query, MAX_SUGGESTION_COUNT, existingMembersIds)
        emit(users ?: emptyList())
    }


    private companion object {
        private const val MAX_SUGGESTION_COUNT = 50
    }

}