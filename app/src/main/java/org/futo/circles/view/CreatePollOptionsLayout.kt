package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.children

class CreatePollOptionsLayout(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    private var onChangeListener: (() -> Unit)? = null

    init {
        orientation = VERTICAL
    }

    fun setOnChangeListener(listener: () -> Unit) {
        onChangeListener = listener
    }

    fun getOptionsList() = children.mapNotNull { child ->
        (child as? CreatePollOptionView)?.getText()?.takeIf { it.isNotEmpty() }
    }.toList()

    fun addOption(text: String? = null) {
        handleImeOption()
        val option = CreatePollOptionView(context).apply {
            setup(
                this@CreatePollOptionsLayout.childCount + 1,
                ::onRemoveOption
            ) { onChangeListener?.invoke() }
            text?.let { setText(text) }
        }
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 8, 0, 0)
        addView(option, layoutParams)
    }

    private fun onRemoveOption(view: CreatePollOptionView) {
        removeView(view)
        children.forEachIndexed { i, child ->
            (child as? CreatePollOptionView)?.setHint(i + 1)
        }
        onChangeListener?.invoke()
    }

    private fun handleImeOption() {
        (children.lastOrNull() as? CreatePollOptionView)?.setImeActionNext()
    }

    fun isValidInput() = getOptionsList().size >= 2
}