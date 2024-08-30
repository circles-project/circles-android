package org.futo.circles.feature.timeline.list


interface PostOptionsListener {
    fun onShowMenuClicked(roomId: String, eventId: String)
    fun onUserClicked(userId: String)
    fun onReply(roomId: String, eventId: String)
    fun onShowPreview(roomId: String, eventId: String)
    fun onLikeClicked(roomId: String, eventId: String, emoji: String, isUnSend: Boolean)
    fun onPollOptionSelected(roomId: String, eventId: String, optionId: String)
}