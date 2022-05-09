package com.futo.circles.feature.timeline.list

import com.futo.circles.R
import com.futo.circles.extensions.gone
import com.futo.circles.extensions.visible
import com.futo.circles.model.LoadingData
import com.futo.circles.view.LoadingView
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker

object UploadImageProgressHelper {

    fun getListener(loadingView: LoadingView): ContentUploadStateTracker.UpdateListener =
        object : ContentUploadStateTracker.UpdateListener {
            override fun onUpdate(state: ContentUploadStateTracker.State) {
                when (state) {
                    ContentUploadStateTracker.State.CompressingImage -> {
                        loadingView.visible()
                        loadingView.setMessage(R.string.compressing_image)
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