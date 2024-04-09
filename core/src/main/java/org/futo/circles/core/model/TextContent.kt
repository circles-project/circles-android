package org.futo.circles.core.model

import android.text.Spanned
import org.futo.circles.core.feature.markdown.MarkdownParser
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getTextEditableContent

data class TextContent(
    val message: String,
    val messageSpanned: Spanned
) : PostContent(PostContentType.TEXT_CONTENT) {

    // to optimize payload calculation (spanned==spanned will return false and trigger unnecessary list update)
    override fun equals(other: Any?): Boolean = this.message == (other as? TextContent)?.message

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + messageSpanned.hashCode()
        return result
    }
}

fun TimelineEvent.toTextContent(): TextContent {
    val text = getTextEditableContent(false)
    val spanned = MarkdownParser.parse(text)
    return TextContent(text, spanned)
}