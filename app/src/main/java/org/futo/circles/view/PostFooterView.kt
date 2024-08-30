package org.futo.circles.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import org.futo.circles.R
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.ReactionsData
import org.futo.circles.databinding.ViewPostFooterBinding
import org.futo.circles.feature.timeline.list.PostOptionsListener


class PostFooterView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewPostFooterBinding.inflate(LayoutInflater.from(context), this)

    private var optionsListener: PostOptionsListener? = null
    private var post: Post? = null
    private var isThreadPost = false
    private val defaultLikeEmoji = "❤️"

    init {
        setupViews()
    }

    private fun setupViews() {
        with(binding) {
            btnReply.setOnClickListener {
                post?.let {
                    optionsListener?.onReply(it.postInfo.roomId, it.id)
                }
            }
            btnLike.setOnClickListener { onLikeIconClicked() }
        }
    }

    fun setListener(postOptionsListener: PostOptionsListener) {
        optionsListener = postOptionsListener
    }

    fun setData(data: Post, isThread: Boolean) {
        post = data
        isThreadPost = isThread
        bindReplyButton(data.repliesCount)
        bindLikeButton(data.reactionsData)
        bindIsEditedLabel(data.postInfo.isEdited)
    }

    fun bindPayload(repliesCount: Int, reactions: List<ReactionsData>) {
        post = post?.copy(repliesCount = repliesCount, reactionsData = reactions)
        bindReplyButton(repliesCount)
        bindLikeButton(reactions)
    }

    fun onLikeIconClicked() {
        val postData = post ?: return
        val reactions = postData.reactionsData
        val hasMyReaction = hasMyReaction(reactions)
        val newReactionsData = if (hasMyReaction) {
            reactions.map {
                if (it.key == defaultLikeEmoji && it.addedByMe) it.copy(
                    count = it.count - 1,
                    addedByMe = false
                ) else it
            }
        } else {
            reactions.map {
                if (it.key == defaultLikeEmoji) it.copy(count = it.count + 1, addedByMe = true)
                else it
            }
        }
        bindLikeButton(newReactionsData)
        post = postData.copy(reactionsData = newReactionsData)
        optionsListener?.onLikeClicked(
            postData.postInfo.roomId,
            postData.id,
            defaultLikeEmoji,
            hasMyReaction
        )
    }

    private fun bindReplyButton(repliesCount: Int) {
        binding.btnReply.apply {
            isVisible = !isThreadPost
            binding.btnReply.text = repliesCount.toString()
        }
    }

    private fun bindIsEditedLabel(isEdited: Boolean) {
        binding.tvEditedLabel.setIsVisible(isEdited)
    }

    private fun bindLikeButton(reactions: List<ReactionsData>) {
        val reactionsCount = totalReactionsCount(reactions)
        val hasMyReaction = hasMyReaction(reactions)
        with(binding.btnLike) {
            text = reactionsCount.toString()
            setIconResource(
                if (hasMyReaction) R.drawable.ic_like_selected
                else R.drawable.ic_like_not_selected
            )
            iconTint = if (hasMyReaction) ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    org.futo.circles.core.R.color.primary
                )
            )
            else ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    org.futo.circles.core.R.color.grey_cool_1000
                )
            )
        }
    }

    private fun hasMyReaction(reactions: List<ReactionsData>): Boolean =
        reactions.firstOrNull { it.addedByMe } != null

    private fun totalReactionsCount(reactions: List<ReactionsData>): Int {
        var count = 0
        reactions.forEach { count += it.count }
        return count
    }
}