package org.futo.circles.view


interface Callback : ComposerEditText.Callback {
    fun onCloseRelatedMessage()
    fun onSendMessage(text: CharSequence)
    fun onAddAttachment()
    fun onExpandOrCompactChange()
    fun onFullScreenModeChanged()
    fun onSetLink(isTextSupported: Boolean, initialLink: String?)
}