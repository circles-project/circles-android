package org.futo.circles.feature.timeline.list

import org.futo.circles.provider.MatrixSessionProvider
import org.futo.circles.view.LoadingView
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker

interface UploadMediaViewHolder {

    private val tracker: ContentUploadStateTracker?
        get() = MatrixSessionProvider.currentSession?.contentUploadProgressTracker()


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

    private companion object {
        private var postId: String? = null
        private var listener: ContentUploadStateTracker.UpdateListener? = null
    }
}