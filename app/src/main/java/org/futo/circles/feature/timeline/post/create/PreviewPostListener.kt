package org.futo.circles.feature.timeline.post.create

import org.futo.circles.model.CreatePostContent

interface PreviewPostListener {
    fun onUploadMediaClicked()
    fun onEmojiClicked()
    fun onAddLinkClicked()
    fun onSendClicked(content: CreatePostContent)
}