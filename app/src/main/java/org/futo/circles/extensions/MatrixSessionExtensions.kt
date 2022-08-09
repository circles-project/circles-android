package org.futo.circles.extensions

import android.util.Size
import org.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.content.ContentUrlResolver

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
