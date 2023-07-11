package org.futo.circles.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class FrameInterceptableLayout(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private var clickListener: (() -> Unit)? = null

    fun setOnClickListener(listener: () -> Unit) {
        clickListener = listener
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev ?: return super.dispatchTouchEvent(ev)
        val duration: Long = ev.eventTime - ev.downTime
        if (ev.action == MotionEvent.ACTION_UP && duration < 100) onClickDetected()
        return super.dispatchTouchEvent(ev)
    }


    private fun onClickDetected() {
        clickListener?.invoke()
    }
}