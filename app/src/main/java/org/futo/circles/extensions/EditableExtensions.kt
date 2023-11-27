package org.futo.circles.extensions

import android.text.Editable
import android.text.Spanned
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.getSystemService
import org.futo.circles.feature.timeline.post.markdown.span.TextStyle
import org.futo.circles.feature.timeline.post.markdown.span.toSpanClass

fun Editable.getGivenSpansAt(
    vararg span: TextStyle,
    start: Int = 0,
    end: Int = length
): MutableList<Any> {
    val spanList = mutableListOf<Any>()
    for (selectedSpan in span) {
        getSpans(start, end, selectedSpan.toSpanClass()).forEach { spanList.add(it) }
    }
    return spanList
}

fun View.showKeyboard(andRequestFocus: Boolean = false) {
    if (andRequestFocus) {
        requestFocus()
    }
    val imm = context?.getSystemService<InputMethodManager>()
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText.setTextIfDifferent(newText: CharSequence?): Boolean {
    if (!isTextDifferent(newText, text)) {
        // Previous text is the same. No op
        return false
    }
    setText(newText)
    // Since the text changed we move the cursor to the end of the new text.
    // This allows us to fill in text programmatically with a different value,
    // but if the user is typing and the view is rebound we won't lose their cursor position.
    setSelection(newText?.length ?: 0)
    return true
}

private fun isTextDifferent(str1: CharSequence?, str2: CharSequence?): Boolean {
    if (str1 === str2) {
        return false
    }
    if (str1 == null || str2 == null) {
        return true
    }
    val length = str1.length
    if (length != str2.length) {
        return true
    }
    if (str1 is Spanned) {
        return str1 != str2
    }
    for (i in 0 until length) {
        if (str1[i] != str2[i]) {
            return true
        }
    }
    return false
}
