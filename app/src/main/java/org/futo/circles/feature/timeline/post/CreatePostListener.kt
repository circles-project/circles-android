package org.futo.circles.feature.timeline.post

import org.futo.circles.model.CreatePostContent

interface CreatePostListener {
    fun onSendPost(roomId: String, postContent: CreatePostContent, threadEventId: String?)
    fun onEditTextPost(eventId: String, roomId: String, newMessage: String)
}