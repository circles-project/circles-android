package org.futo.circles.core.feature.markdown.mentions

import android.content.Context
import io.element.android.wysiwyg.display.LinkDisplayHandler
import io.element.android.wysiwyg.display.TextDisplay
import org.futo.circles.core.feature.markdown.span.MentionSpan
import org.matrix.android.sdk.api.MatrixPatterns
import org.matrix.android.sdk.api.session.permalinks.PermalinkService

class MentionsLinkDisplayHandler(private val context: Context) : LinkDisplayHandler {

    override fun resolveLinkDisplay(text: String, url: String): TextDisplay {
        val userId = url.removePrefix(PermalinkService.MATRIX_TO_URL_BASE)
        return if (MatrixPatterns.isUserId(userId))
            TextDisplay.Custom(
                MentionSpan(
                    context,
                    text.replace("@", "")
                )
            ) else TextDisplay.Plain
    }
}