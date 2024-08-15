package org.futo.circles.feature.timeline.list

import org.futo.circles.R
import org.futo.circles.core.model.ResLoadingData
import org.futo.circles.core.view.CreatePostLoadingDialog
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker

object MediaProgressHelper {

    fun getUploadListener(loadingDialog: CreatePostLoadingDialog): ContentUploadStateTracker.UpdateListener =
        object : ContentUploadStateTracker.UpdateListener {
            override fun onUpdate(state: ContentUploadStateTracker.State) {
                when (state) {
                    ContentUploadStateTracker.State.CompressingImage -> {
                        loadingDialog.setProgress(ResLoadingData(R.string.compressing))
                    }

                    is ContentUploadStateTracker.State.CompressingVideo -> {
                        loadingDialog.setProgress(
                            ResLoadingData(
                                messageId = R.string.compressing,
                                progress = state.percent.toInt(),
                                total = 100
                            )
                        )
                    }

                    is ContentUploadStateTracker.State.Encrypting -> {
                        loadingDialog.setProgress(
                            ResLoadingData(
                                messageId = R.string.encrypting,
                                progress = state.current.toInt(),
                                total = state.total.toInt()
                            )
                        )
                    }

                    is ContentUploadStateTracker.State.Uploading -> {
                        loadingDialog.setProgress(
                            ResLoadingData(
                                messageId = R.string.uploading,
                                progress = state.current.toInt(),
                                total = state.total.toInt()
                            )
                        )
                    }

                    is ContentUploadStateTracker.State.Failure -> {
                        loadingDialog.setProgress(ResLoadingData(R.string.failed_to_send))
                    }

                    ContentUploadStateTracker.State.Success -> {
                        loadingDialog.setProgress(ResLoadingData(R.string.sent))
                    }

                    else -> {
                        loadingDialog.setProgress(ResLoadingData(R.string.sending))
                    }
                }
            }
        }
}