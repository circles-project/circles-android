package org.futo.circles.core.extensions

import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import org.futo.circles.core.R


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
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

fun ViewGroup.addNoInternetConnectionView(): TextView {
    val noInternetView = TextView(context).apply {
        text = context.getString(R.string.no_internet_connection)
        setTextColor(ContextCompat.getColor(context, R.color.white))
        textAlignment = View.TEXT_ALIGNMENT_CENTER
        textSize = 13f
        setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
    }
    addView(noInternetView, 0)
    return noInternetView
}