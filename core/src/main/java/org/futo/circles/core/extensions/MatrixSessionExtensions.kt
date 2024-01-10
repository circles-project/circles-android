package org.futo.circles.core.extensions

import android.util.Size
import androidx.lifecycle.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.mapLatest
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.getUser
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import org.matrix.android.sdk.api.session.user.model.User

private val sessionCoroutineScopes = HashMap<String, CoroutineScope>(1)

val Session.coroutineScope: CoroutineScope
    get() {
        return synchronized(sessionCoroutineScopes) {
            sessionCoroutineScopes.getOrPut(sessionId) {
                CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
            }
        }
    }

fun Session.resolveUrl(
    url: String?,
    size: Size? = null
): String? {
    val resolver = contentUrlResolver()

    return size?.let {
        resolver.resolveThumbnail(
            url,
            size.width, size.height,
            ContentUrlResolver.ThumbnailMethod.SCALE
        )
    } ?: run {
        resolver.resolveFullSize(url)
    }
}

fun Session.getUserIdsToExclude() = mutableListOf(
    myUserId,
    "@notices:" + getServerDomain()
).toSet()

fun Session.getServerDomain() = myUserId.substringAfter(":")


fun Session.getKnownUsersFlow() = roomService().getRoomSummariesLive(roomSummaryQueryParams {
    excludeType = null
    memberships = listOf(Membership.JOIN)
}).asFlow()
    .mapLatest { roomSummaries ->
        val knowUsers = mutableSetOf<User>()
        roomSummaries.forEach { summary ->
            val joinedMembersIds = getRoom(summary.roomId)?.membershipService()
                ?.getRoomMembers(roomMemberQueryParams {
                    memberships = listOf(Membership.JOIN)
                })?.map { it.userId } ?: emptyList()
            joinedMembersIds.forEach {
                getUser(it)?.let { user -> knowUsers.add(user) }
            }
        }
        knowUsers.toList().filterNot { getUserIdsToExclude().contains(it.userId) }
    }
