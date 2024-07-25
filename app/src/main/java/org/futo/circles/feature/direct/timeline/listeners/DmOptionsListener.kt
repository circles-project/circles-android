package org.futo.circles.feature.direct.timeline.listeners

import org.futo.circles.core.model.PostContent

interface DmOptionsListener {

    fun onShowMenuClicked(eventId: String)
    fun onShare(content: PostContent)
    fun onReply(message: String)
    fun onShowPreview(eventId: String)
    fun onShowEmoji(eventId: String, onAddEmoji: (String) -> Unit)
    fun onEmojiChipClicked(eventId: String, emoji: String, isUnSend: Boolean)
    fun onSaveToDevice(content: PostContent)
    fun onRemove(eventId: String)
    fun onEditActionClicked(eventId: String, message: String)

}