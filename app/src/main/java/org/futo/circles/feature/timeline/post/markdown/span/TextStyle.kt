package org.futo.circles.feature.timeline.post.markdown.span

import android.text.style.StrikethroughSpan
import io.noties.markwon.core.spans.BulletListItemSpan
import io.noties.markwon.core.spans.EmphasisSpan
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.core.spans.StrongEmphasisSpan
import io.noties.markwon.ext.tasklist.TaskListSpan

enum class TextStyle {
    BOLD,
    ITALIC,
    STRIKE,
    LINK,
    UNORDERED_LIST,
    ORDERED_LIST,
    TASKS_LIST
}

fun TextStyle.toSpanClass() = when (this) {
    TextStyle.BOLD -> StrongEmphasisSpan::class.java
    TextStyle.ITALIC -> EmphasisSpan::class.java
    TextStyle.STRIKE -> StrikethroughSpan::class.java
    TextStyle.LINK -> LinkSpan::class.java
    TextStyle.UNORDERED_LIST -> BulletListItemSpan::class.java
    TextStyle.ORDERED_LIST -> OrderedListItemSpan::class.java
    TextStyle.TASKS_LIST -> TaskListSpan::class.java
}