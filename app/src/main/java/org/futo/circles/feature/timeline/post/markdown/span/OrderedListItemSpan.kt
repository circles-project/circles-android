package org.futo.circles.feature.timeline.post.markdown.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.utils.LeadingMarginUtils


class OrderedListItemSpan(
    private val theme: MarkwonTheme,
    val number: String
) : LeadingMarginSpan {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var margin = 0
    override fun getLeadingMargin(first: Boolean): Int = margin.coerceAtLeast(theme.blockMargin)

    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout
    ) {
        if (!first || !LeadingMarginUtils.selfStart(start, text, this)) return

        paint.set(p)
        theme.applyListItemStyle(paint)

        val numberWidth = (paint.measureText(number) + .5f).toInt()

        var width = theme.blockMargin
        if (numberWidth > width) {
            width = numberWidth
            margin = numberWidth
        } else {
            margin = 0
        }
        val left: Int = if (dir > 0) {
            x + width * dir - numberWidth
        } else {
            x + width * dir + (width - numberWidth)
        }

        c.drawText(number, left.toFloat(), baseline.toFloat(), paint)
    }
}