package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.children

class PollOptionsLayout(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    private var onChangeListener: (() -> Unit)? = null

    init {
        orientation = VERTICAL
        addOption()
        addOption()
    }

    fun setOnChangeListener(listener: () -> Unit) {
        onChangeListener = listener
    }

    fun getOptionsList() = children.mapNotNull { child ->
        (child as? PollOptionView)?.getText()?.takeIf { it.isNotEmpty() }
    }.toList()

    fun addOption() {
        val option = PollOptionView(context).apply {
            setup(
                this@PollOptionsLayout.childCount + 1,
                ::onRemoveOption
            ) { onChangeListener?.invoke() }
        }
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 8, 0, 0)
        addView(option, layoutParams)
    }

    private fun onRemoveOption(view: PollOptionView) {
        removeView(view)
        children.forEachIndexed { i, child ->
            (child as? PollOptionView)?.setHint(i + 1)
        }
        onChangeListener?.invoke()
    }

    fun isValidInput() = getOptionsList().size >= 2
}