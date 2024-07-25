package org.futo.circles.feature.timeline.list

import org.futo.circles.feature.timeline.list.holder.VideoPlaybackViewHolder

interface OnVideoPlayBackStateListener {

    fun onVideoPlaybackStateChanged(holder: VideoPlaybackViewHolder, isPlaying: Boolean)
}