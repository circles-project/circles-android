package org.futo.circles.feature.timeline.post.create

import org.futo.circles.model.CreatePostContent

interface CreatePostListener {
    fun onSendPost(roomId: String, postContent: CreatePostContent, threadEventId: String?)
    fun onEditTextPost(roomId: String, newMessage: String, eventId: String)
}