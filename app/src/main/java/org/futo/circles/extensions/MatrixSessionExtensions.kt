package org.futo.circles.extensions

import android.util.Size
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.futo.circles.core.DEFAULT_USER_PREFIX
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.session.getUserOrDefault
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
    val resolver = MatrixSessionProvider.currentSession?.contentUrlResolver()

    return size?.let {
        resolver?.resolveThumbnail(
            url,
            size.width, size.height,
            ContentUrlResolver.ThumbnailMethod.SCALE
        )
    } ?: run {
        resolver?.resolveFullSize(url)
    }
}

fun Session.getUserIdsToExclude() = mutableListOf(
    myUserId,
    DEFAULT_USER_PREFIX + myUserId.substringAfter(":")
).toSet()

fun Session.getKnownUsersLive(): LiveData<List<User>> =
    roomService().getRoomSummariesLive(roomSummaryQueryParams { excludeType = null })
        .map { roomSummaries ->
            val knowUsers = mutableSetOf<User>()
            roomSummaries.forEach { summary ->
                summary.otherMemberIds.forEach { knowUsers.add(getUserOrDefault(it)) }
            }
            knowUsers.toList().filterNot { getUserIdsToExclude().contains(it.userId) }
        }
