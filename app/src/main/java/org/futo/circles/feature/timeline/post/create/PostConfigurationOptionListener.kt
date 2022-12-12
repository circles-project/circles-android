package org.futo.circles.feature.timeline.post.create

import org.futo.circles.feature.timeline.post.markdown.span.TextStyle

interface PostConfigurationOptionListener {
    fun onUploadMediaClicked()
    fun onEmojiClicked()
    fun onMentionClicked()
    fun onTextStyleSelected(textStyle: TextStyle, isSelected: Boolean)
    fun onAddLinkClicked()
}