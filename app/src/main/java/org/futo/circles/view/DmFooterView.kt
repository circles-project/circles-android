package org.futo.circles.view

import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import org.futo.circles.R
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.DmTimelineMessage
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.ReactionsData
import org.futo.circles.databinding.ViewDmFooterBinding
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import java.util.Date


class DmFooterView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewDmFooterBinding.inflate(LayoutInflater.from(context), this)

    private var optionsListener: DmOptionsListener? = null
    private var dmMessage: DmTimelineMessage? = null


    init {
        context.obtainStyledAttributes(
            attrs, R.styleable.DmFooterView, 0, 0
        ).use { ta ->
            ta.getColorStateList(
                R.styleable.DmFooterView_timeTextColor
            )?.let {
                binding.tvTime.setTextColor(it)
                binding.tvEditedLabel.setTextColor(it)
            }
        }
    }


    fun setListener(optionsListener: DmOptionsListener) {
        this.optionsListener = optionsListener
    }

    fun setData(data: DmTimelineMessage) {
        dmMessage = data
        binding.tvTime.text =
            DateFormat.format("MMM dd, h:mm a", Date(data.info.getLastModifiedTimestamp()))
        binding.tvEditedLabel.setIsVisible(data.info.isEdited)
        bindReactionsList(data.reactionsData)
    }

    fun bindPayload(reactions: List<ReactionsData>) {
        dmMessage = dmMessage?.copy(reactionsData = reactions)
        bindReactionsList(reactions)
    }


    private fun bindReactionsList(reactions: List<ReactionsData>) {
        binding.vEmojisList.bindReactionsList(
            reactions,
            true,
            object : ReactionChipClickListener {
                override fun onReactionChipClicked(emoji: String, isAddedByMe: Boolean) {
                    dmMessage?.let {
                        optionsListener?.onEmojiChipClicked(
                            it.id,
                            emoji,
                            isAddedByMe
                        )
                    }
                }
            })
    }

    fun addEmojiFromPickerLocalUpdate(emoji: String) {
        binding.vEmojisList.addEmojiFromPickerLocalUpdate(emoji)
    }

}