package org.futo.circles.feature.timeline.post.create

import org.futo.circles.view.markdown.TextStyle

interface PostConfigurationOptionListener {
    fun onUploadMediaClicked()
    fun onEmojiClicked()
    fun onMentionClicked()
    fun onTextStyleSelected(textStyle: TextStyle)
    fun onAddLinkClicked()
}