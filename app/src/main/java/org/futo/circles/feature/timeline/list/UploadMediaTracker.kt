package org.futo.circles.feature.timeline.list

import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.view.LoadingView
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker


class UploadMediaTracker {

    private val tracker: ContentUploadStateTracker? =
        MatrixSessionProvider.currentSession?.contentUploadProgressTracker()

    private var postId: String? = null
    private var listener: ContentUploadStateTracker.UpdateListener? = null

    fun track(id: String, loadingView: LoadingView) {
        postId = id
        tracker?.track(id,
            UploadMediaProgressHelper.getListener(loadingView).also { listener = it }
        )
    }

    fun unTrack() {
        val id = postId ?: return
        val callback = listener ?: return
        tracker?.untrack(id, callback)
    }
}