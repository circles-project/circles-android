package org.futo.circles.feature.timeline.list

import androidx.cardview.widget.CardView
import org.futo.circles.R
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.model.ResLoadingData
import org.futo.circles.core.view.LoadingView
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker

object MediaProgressHelper {

    fun getUploadListener(loadingCardView: CardView?, loadingView: LoadingView?): ContentUploadStateTracker.UpdateListener =
        object : ContentUploadStateTracker.UpdateListener {
            override fun onUpdate(state: ContentUploadStateTracker.State) {
                when (state) {
                    ContentUploadStateTracker.State.CompressingImage -> {
                        loadingCardView?.visible()
                        loadingView?.setProgress(ResLoadingData(R.string.compressing))
                    }

                    is ContentUploadStateTracker.State.CompressingVideo -> {
                        loadingCardView?.visible()
                        loadingView?.setProgress(
                            ResLoadingData(
                                messageId = R.string.compressing,
                                progress = state.percent.toInt(),
                                total = 100
                            )
                        )
                    }

                    is ContentUploadStateTracker.State.Encrypting -> {
                        loadingCardView?.visible()
                        loadingView?.setProgress(
                            ResLoadingData(
                                messageId = R.string.encrypting,
                                progress = state.current.toInt(),
                                total = state.total.toInt()
                            )
                        )
                    }

                    is ContentUploadStateTracker.State.Uploading -> {
                        loadingCardView?.visible()
                        loadingView?.setProgress(
                            ResLoadingData(
                                messageId = R.string.uploading,
                                progress = state.current.toInt(),
                                total = state.total.toInt()
                            )
                        )
                    }

                    else -> loadingCardView?.gone()
                }
            }
        }
}