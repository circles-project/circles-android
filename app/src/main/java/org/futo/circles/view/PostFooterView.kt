package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import org.futo.circles.R
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
    }

    fun bindPayload(repliesCount: Int, reactions: List<ReactionsData>) {
        post = post?.copy(repliesCount = repliesCount, reactionsData = reactions)
        bindReplyButton(repliesCount)
        bindLikeButton(reactions)
    }

    private fun bindReplyButton(repliesCount: Int) {
        binding.btnReply.apply {
            isVisible = !isThreadPost
            binding.btnReply.text = if (repliesCount > 0) repliesCount.toString() else ""
        }
    }

    private fun bindLikeButton(reactions: List<ReactionsData>) {
        val reactionsCount = totalReactionsCount(reactions)
        val hasMyReaction = hasMyReaction(reactions)
        with(binding.btnLike) {
            text = if (reactionsCount > 0) reactionsCount.toString() else ""
            setIconResource(
                if (hasMyReaction) R.drawable.ic_like_selected
                else R.drawable.ic_like_not_selected
            )

            setOnClickListener { onLikeIconClicked(reactions, hasMyReaction) }
        }
    }

    private fun onLikeIconClicked(
        reactions: List<ReactionsData>,
        hasMyReaction: Boolean
    ) {
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
        post = post?.copy(reactionsData = newReactionsData)
        post?.let {
            optionsListener?.onLikeClicked(
                it.postInfo.roomId,
                it.id,
                defaultLikeEmoji,
                hasMyReaction
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