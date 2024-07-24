package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.HorizontalScrollView
import androidx.core.view.children
import org.futo.circles.core.base.NetworkObserver
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.ReactionsData
import org.futo.circles.databinding.ViewEmijiListBinding

interface ReactionChipClickListener {

    fun onReactionChipClicked(emoji: String, isAddedByMe: Boolean)
}


class EmojiListView(
    context: Context,
    attrs: AttributeSet? = null,
) : HorizontalScrollView(context, attrs) {

    private val binding =
        ViewEmijiListBinding.inflate(LayoutInflater.from(context), this)

    private var reactionChipClickListener: ReactionChipClickListener? = null
    private var isUserAbleToPost: Boolean = true


    fun bindReactionsList(
        reactionsData: List<ReactionsData>,
        isUserAbleToPost: Boolean,
        reactionChipClickedCallback: ReactionChipClickListener
    ) {
        this.isUserAbleToPost = isUserAbleToPost
        this.reactionChipClickListener = reactionChipClickedCallback
        this.setIsVisible(reactionsData.isNotEmpty())
        binding.lEmojisContainer.removeAllViews()
        reactionsData.forEach { addReactionItem(it) }
    }

    private fun addReactionItem(reactionsData: ReactionsData) {
        binding.lEmojisContainer.addView(ReactionItemView(context).apply {
            setup(reactionsData) { reaction ->
                locallyUpdateEmojisList(this, reaction)
                reactionChipClickListener?.onReactionChipClicked(
                    reaction.key,
                    reaction.addedByMe
                )
            }
        })
    }

    private fun locallyUpdateEmojisList(view: ReactionItemView, reaction: ReactionsData) {
        if (!NetworkObserver.isConnected()) return
        if (!isUserAbleToPost) return
        if (reaction.addedByMe) {
            if (reaction.count == 1) {
                binding.lEmojisContainer.removeView(view)
                if (binding.lEmojisContainer.children.count() == 0)
                    this.setIsVisible(false)
            } else view.bindReactionData(
                reaction.copy(
                    addedByMe = false,
                    count = reaction.count - 1
                )
            )
        } else {
            view.bindReactionData(reaction.copy(addedByMe = true, count = reaction.count + 1))
        }
    }

    fun addEmojiFromPickerLocalUpdate(emoji: String) {
        val view = binding.lEmojisContainer.findViewWithTag<ReactionItemView>(emoji)
        view?.let {
            val data = it.reactionsData ?: return
            it.bindReactionData(data.copy(count = data.count + 1, addedByMe = true))
        } ?: kotlin.run {
            this.setIsVisible(true)
            addReactionItem(ReactionsData(emoji, 1, true))
        }
    }
}