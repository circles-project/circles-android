package org.futo.circles.feature.timeline

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.MotionEvent
import android.widget.TextView
import org.futo.circles.feature.timeline.list.OnLinkClickedListener

class InternalLinkMovementMethod(private val onLinkClickedListener: OnLinkClickedListener) :
    LinkMovementMethod() {
    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        val action = event.action

        if (action == MotionEvent.ACTION_UP) {
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY
            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())
            val link = buffer.getSpans(off, off, URLSpan::class.java)

            link.getOrNull(0)?.let { span ->
                val url = span.url
                return if (url.contains("circlesproject.net")) {
                    super.onTouchEvent(widget, buffer, event)
                } else {
                    onLinkClickedListener.onLinkClicked(url)
                    true
                }

            }
        }
        return super.onTouchEvent(widget, buffer, event)
    }
}

