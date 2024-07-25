package org.futo.circles.feature.timeline.list.holder

interface VideoPlaybackViewHolder : ImageMediaViewHolder {

    fun stopVideo(shouldNotify: Boolean = true)

    fun playVideo()
}