package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import org.futo.circles.core.model.ReactionsData
import org.futo.circles.databinding.ListItemTimelineReactionBinding

class ReactionItemView(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val binding =
        ListItemTimelineReactionBinding.inflate(LayoutInflater.from(context), this)

    fun setup(data: ReactionsData, onClick: (ReactionsData) -> Unit) {
        binding.emojiChip.setOnClickListener { onClick(data) }
        bindReactionData(data)
    }

    fun bindReactionData(data: ReactionsData) {
        binding.emojiChip.apply {
            val title = "${data.key} ${data.count}"
            text = title
            isChecked = data.addedByMe
        }
    }

}