package org.futo.circles.feature.timeline.list

import org.futo.circles.R
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.view.LoadingView
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker

object UploadMediaProgressHelper {

    fun getListener(loadingView: LoadingView): ContentUploadStateTracker.UpdateListener =
        object : ContentUploadStateTracker.UpdateListener {
            override fun onUpdate(state: ContentUploadStateTracker.State) {
                when (state) {
                    ContentUploadStateTracker.State.CompressingImage -> {
                        loadingView.visible()
                        loadingView.setMessage(R.string.compressing)
                    }

                    is ContentUploadStateTracker.State.CompressingVideo -> {
                        loadingView.visible()
                        loadingView.setProgress(
                            LoadingData(
                                messageId = R.string.compressing,
                                progress = state.percent.toInt(),
                                total = 100
                            )
                        )
                    }

                    is ContentUploadStateTracker.State.Encrypting -> {
                        loadingView.visible()
                        loadingView.setProgress(
                            LoadingData(
                                messageId = R.string.encrypting,
                                progress = state.current.toInt(),
                                total = state.total.toInt()
                            )
                        )
                    }

                    is ContentUploadStateTracker.State.Uploading -> {
                        loadingView.visible()
                        loadingView.setProgress(
                            LoadingData(
                                messageId = R.string.uploading,
                                progress = state.current.toInt(),
                                total = state.total.toInt()
                            )
                        )
                    }

                    else -> loadingView.gone()
                }
            }

        }
}