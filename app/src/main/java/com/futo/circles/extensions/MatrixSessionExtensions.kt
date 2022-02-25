package com.futo.circles.extensions

import com.bumptech.glide.request.target.Target
import com.futo.circles.provider.MatrixSessionProvider
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

fun Session.resolveUrl(url: String?, size: Int = Target.SIZE_ORIGINAL): String? {
    val resolver = MatrixSessionProvider.currentSession?.contentUrlResolver()

    return if (size == Target.SIZE_ORIGINAL) {
        resolver?.resolveFullSize(url)
    } else {
        resolver?.resolveThumbnail(
            url,
            size, size,
            ContentUrlResolver.ThumbnailMethod.SCALE
        )
    }
}
