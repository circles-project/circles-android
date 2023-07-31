package org.futo.circles.feature.timeline.post.create

import org.futo.circles.model.CreatePostContent

interface CreatePostListener {
    fun onSendPost(roomId: String, postContent: CreatePostContent, threadEventId: String?)
    fun onEditPost(roomId: String, postContent: CreatePostContent, eventId: String)
}