package org.futo.circles.extensions

import android.text.Editable
import org.futo.circles.view.markdown.TextStyle

fun Editable.getGivenSpans(vararg span: TextStyle): MutableList<Any> {
    val spanList = mutableListOf<Any>()
    for (selectedSpan in span) {
        getSpans(0, length, selectedSpan::class.java).forEach { spanList.add(it) }
    }
    return spanList
}

fun Editable.getGivenSpansAt(
    vararg span: Any,
    start: Int,
    end: Int
): MutableList<Any> {
    val spanList = mutableListOf<Any>()
    for (selectedSpan in span) {
        getSpans(start, end, selectedSpan::class.java).forEach { spanList.add(it) }
    }
    return spanList
}
