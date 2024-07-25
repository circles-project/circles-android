package org.futo.circles.feature.direct.timeline.listeners

interface SendDmMessageListener {

    fun onAddEmojiToMessageClicked()

    fun onSendTextMessageClicked(message: String)

    fun onSendMediaButtonClicked()

}