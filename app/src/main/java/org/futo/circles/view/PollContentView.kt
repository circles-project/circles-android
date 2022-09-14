package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.futo.circles.databinding.ViewPollContentBinding
import org.futo.circles.model.PollContent

class PollContentView(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    init {
        orientation = VERTICAL
    }

    private val binding =
        ViewPollContentBinding.inflate(LayoutInflater.from(context), this)


    fun setup(poll: PollContent) {
        with(binding) {
            tvPollQuestions.text = poll.question
            lvOptionsContainer.removeAllViews()
            poll.options.forEach {
                lvOptionsContainer.addView(PollOptionView(context).apply { setup(it) })
            }
            tvPollStatus.text = createPollStatusMessage()
        }
    }

    private fun createPollStatusMessage(): String = "Todo"
}