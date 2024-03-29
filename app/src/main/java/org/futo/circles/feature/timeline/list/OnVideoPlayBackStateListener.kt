package org.futo.circles.feature.timeline.list

import org.futo.circles.feature.timeline.list.holder.VideoPostViewHolder

interface OnVideoPlayBackStateListener {

    fun onVideoPlaybackStateChanged(holder: VideoPostViewHolder, isPlaying: Boolean)
}