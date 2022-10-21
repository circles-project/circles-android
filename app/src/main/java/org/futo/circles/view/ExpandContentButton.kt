package org.futo.circles.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import org.futo.circles.R
import org.futo.circles.extensions.getAttributes

class ExpandContentButton(
    context: Context,
    attrs: AttributeSet? = null,
) : MaterialButton(context, attrs) {

    private var openedIcon: Drawable? = null
    private var closedIcon: Drawable? = null

    private var isOpened: Boolean = false

    init {
        getAttributes(attrs, R.styleable.ExpandContentButton) {
            getDrawable(R.styleable.ExpandContentButton_closed_icon)?.let {
                icon = it
                closedIcon = it
            }

            getDrawable(R.styleable.ExpandContentButton_opened_icon)?.let {
                openedIcon = it
            }
        }
    }

    fun setIsOpened(isOpened: Boolean) {
        if (isOpened) open()
        else close()
    }

    private fun open() {
        isOpened = true
        icon = openedIcon
    }

    private fun close() {
        isOpened = false
        icon = closedIcon
    }
}

