package org.futo.circles.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Layout
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.use
import androidx.core.text.buildSpannedString
import androidx.core.text.getSpans
import androidx.core.text.inSpans
import androidx.core.text.toSpannable
import io.noties.markwon.core.spans.LinkSpan
import org.futo.circles.R
import kotlin.text.Typography.ellipsis
import kotlin.text.Typography.nbsp

class ReadMoreTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.readMoreTextViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var readMoreMaxLines: Int = 5
    private var readMoreText: String? = null
    private var readMoreTextColor: ColorStateList? = null
    private var bufferType: BufferType? = null
    private var expanded: Boolean = false
    private var originalText: CharSequence? = null
    private var collapseText: CharSequence? = null
    private var notCloseableClickListener: (() -> Unit)? = null

    init {
        context.obtainStyledAttributes(
            attrs, R.styleable.ReadMoreTextView, defStyleAttr, 0
        ).use { ta ->
            readMoreMaxLines = ta.getInt(
                R.styleable.ReadMoreTextView_readMoreMaxLines,
                readMoreMaxLines
            )
            readMoreText = ta.getString(R.styleable.ReadMoreTextView_readMoreText)
                ?.replace(' ', nbsp)
            readMoreTextColor = ta.getColorStateList(
                R.styleable.ReadMoreTextView_readMoreTextColor
            ) ?: readMoreTextColor
        }

        if (hasOnClickListeners()) throw IllegalStateException("Custom onClickListener not supported")
        super.setOnClickListener {
            toggle()
            if (collapseText == originalText &&
                originalText?.toSpannable()
                    ?.getSpans<LinkSpan>(0, originalText?.length ?: 0)?.size == 0
            ) {
                notCloseableClickListener?.invoke()
                (parent?.parent as? ViewGroup)?.performClick()
            }
        }

        super.setOnLongClickListener {
            (parent?.parent as? ViewGroup)?.performLongClick() == true
        }

        if (originalText != null) invalidateText()

    }

    fun setNotCollapsableClickAction(listener: () -> Unit) {
        notCloseableClickListener = listener
    }

    override fun setLines(lines: Int) {
        throw IllegalStateException("Use the app:readMoreMaxLines")
    }

    override fun setMaxLines(maxLines: Int) {
        throw IllegalStateException("Use the app:readMoreMaxLines")
    }

    override fun setEllipsize(where: TextUtils.TruncateAt?) {
        throw IllegalStateException("Not supported")
    }

    override fun setOnClickListener(l: OnClickListener?) {
        throw IllegalStateException("Not supported")
    }

    private fun toggle() {
        setExpanded(!expanded)
    }

    private fun setExpanded(expanded: Boolean) {
        if (this.expanded != expanded) {
            this.expanded = expanded
            invalidateText()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw) {
            originalText?.let { originalText ->
                updateText(originalText, w)
            }
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        this.originalText = text
        this.bufferType = type
        updateText(text ?: "", width)
    }

    private fun updateText(text: CharSequence, width: Int) {
        val maximumTextWidth = width - (paddingLeft + paddingRight)
        val readMoreMaxLines = readMoreMaxLines
        if (maximumTextWidth > 0 && readMoreMaxLines > 0) {
            val layout = StaticLayoutCompat.Builder(text, paint, maximumTextWidth)
                .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                .setIncludePad(includeFontPadding)
                .build()
            if (layout.lineCount <= readMoreMaxLines) {
                this.collapseText = text
            } else {
                this.collapseText = buildSpannedString {
                    val countUntilMaxLine = layout.getLineVisibleEnd(readMoreMaxLines - 1)
                    if (text.length <= countUntilMaxLine) {
                        append(text)
                    } else {
                        val overflowText = buildOverflowText()
                        val overflowTextWidth = StaticLayoutCompat.Builder(
                            overflowText,
                            paint,
                            maximumTextWidth
                        )
                            .build()
                            .getLineWidth(0).toInt()

                        val textAppearanceSpan = TextAppearanceSpan(
                            null,
                            Typeface.NORMAL,
                            textSize.toInt(),
                            readMoreTextColor,
                            null
                        )
                        val spans = listOfNotNull(textAppearanceSpan)
                        val readMoreTextWithStyle = buildReadMoreText(spans = spans.toTypedArray())
                        val readMorePaint = TextPaint().apply {
                            set(paint)
                            textAppearanceSpan.updateMeasureState(this)
                        }
                        val readMoreTextWidth = StaticLayoutCompat.Builder(
                            readMoreTextWithStyle,
                            readMorePaint,
                            maximumTextWidth
                        )
                            .build()
                            .getLineWidth(0).toInt()
                        val readMoreWidth = overflowTextWidth + readMoreTextWidth

                        val replaceCount = text
                            .substringOf(layout, line = readMoreMaxLines)
                            .calculateReplaceCountToBeSingleLineWith(maximumTextWidth - readMoreWidth)
                        append(text.subSequence(0, countUntilMaxLine - replaceCount))
                        append(overflowText)
                        append(readMoreTextWithStyle)
                    }
                }
            }
        } else {
            this.collapseText = text
        }
        invalidateText()
    }

    private fun buildOverflowText(
        text: String? = readMoreText
    ): String {
        return buildString {
            append(ellipsis)
            if (text.isNullOrEmpty().not()) append(nbsp)
        }
    }

    private fun buildReadMoreText(
        text: String? = readMoreText,
        vararg spans: Any
    ): CharSequence {
        return buildSpannedString {
            if (text.isNullOrEmpty().not()) {
                inSpans(spans = spans) {
                    append(text)
                }
            }
        }
    }

    private fun CharSequence.substringOf(layout: Layout, line: Int): CharSequence {
        val lastLineStartIndex = layout.getLineStart(line - 1)
        val lastLineEndIndex = layout.getLineEnd(line - 1)
        return subSequence(lastLineStartIndex, lastLineEndIndex)
    }

    private fun CharSequence.calculateReplaceCountToBeSingleLineWith(
        maximumTextWidth: Int
    ): Int {
        val currentTextBounds = Rect()
        var replacedCount = -1
        do {
            replacedCount++
            val replacedText = substring(0, this.length - replacedCount)
            paint.getTextBounds(replacedText, 0, replacedText.length, currentTextBounds)
        } while (replacedCount < this.length && currentTextBounds.width() >= maximumTextWidth)

        val lastVisibleChar: Char? = this.getOrNull(this.length - replacedCount - 1)
        val firstOverflowChar: Char? = this.getOrNull(this.length - replacedCount)
        if (lastVisibleChar?.isSurrogate() == true && firstOverflowChar?.isHighSurrogate() == false) {
            val subText = substring(0, this.length - replacedCount)
            if (subText.isNotEmpty()) {
                return length - subText.indexOfLast { it.isHighSurrogate() }
            }
        }
        return replacedCount
    }

    private fun invalidateText() {
        if (expanded) {
            super.setText(originalText, bufferType)
            super.setMaxLines(NO_LIMIT_LINES)
        } else {
            super.setText(collapseText, bufferType)
            super.setMaxLines(readMoreMaxLines)
        }
    }

    private companion object {
        private const val NO_LIMIT_LINES = Integer.MAX_VALUE
    }
}