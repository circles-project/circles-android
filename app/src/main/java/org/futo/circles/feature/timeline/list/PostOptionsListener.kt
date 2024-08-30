package org.futo.circles.feature.timeline.list

import android.view.View
import org.futo.circles.core.model.PostContent


interface PostOptionsListener {
    fun onShowMenuClicked(roomId: String, eventId: String)
    fun onUserClicked(userId: String)
    fun onShare(content: PostContent, view: View)
    fun onReply(roomId: String, eventId: String)
    fun onShowPreview(roomId: String, eventId: String)
    fun onLikeClicked(roomId: String, eventId: String, emoji: String, isUnSend: Boolean)
    fun onPollOptionSelected(roomId: String, eventId: String, optionId: String)
}