package org.futo.circles.extensions

import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.core.view.children


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.setIsVisible(isVisible: Boolean) {
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

fun View.setSelectableItemBackground() {
    val outValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
    setBackgroundResource(outValue.resourceId)
}

fun ViewGroup.setEnabledChildren(enabled: Boolean, viewsToExclude: List<View> = emptyList()) {
    children.forEach { view ->
        val isViewExcluded = viewsToExclude.firstOrNull { it.id == view.id } != null
        if (!isViewExcluded) {
            if (view.isClickable) view.isEnabled = enabled
            (view as? ViewGroup)?.setEnabledChildren(enabled, viewsToExclude)
        }
    }
}