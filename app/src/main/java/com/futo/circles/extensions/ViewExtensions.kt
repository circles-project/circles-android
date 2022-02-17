package com.futo.circles.extensions

import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.StyleRes
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

inline fun View.getAttributes(
    set: AttributeSet?,
    attrs: IntArray,
    defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
    crossinline action: TypedArray.() -> Unit
) {
    set ?: return
    val array = context.obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes)
    try {
        array.action()
    } finally {
        array.recycle()
    }
}