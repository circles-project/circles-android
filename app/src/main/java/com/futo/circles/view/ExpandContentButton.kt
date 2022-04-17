package com.futo.circles.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.futo.circles.R
import com.futo.circles.extensions.getAttributes
import com.google.android.material.button.MaterialButton

class ExpandContentButton(
    context: Context,
    attrs: AttributeSet? = null,
) : MaterialButton(context, attrs) {

    private var openedText: String = ""
    private var closedText: String = ""

    private var openedIcon: Drawable? = null
    private var closedIcon: Drawable? = null

    private var isOpened: Boolean = false

    init {
        getAttributes(attrs, R.styleable.ExpandContentButton) {
            getText(R.styleable.ExpandContentButton_closed_text)?.let {
                text = it
                closedText = it.toString()
            }
            openedText = getText(R.styleable.ExpandContentButton_opened_text)?.toString() ?: ""

            getDrawable(R.styleable.ExpandContentButton_closed_icon)?.let {
                icon = it
                closedIcon = it
            }

            getDrawable(R.styleable.ExpandContentButton_opened_icon)?.let {
                openedIcon = it
            }
        }
    }

    fun setClosedText(title: String) {
        text = title.also { closedText = it }
    }

    fun setIsOpened(isOpened: Boolean) {
        if (isOpened) open()
        else close()
    }

    private fun open() {
        isOpened = true
        text = openedText
        icon = openedIcon
    }

    private fun close() {
        isOpened = false
        text = closedText
        icon = closedIcon
    }
}

