package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.ReactionsData
import org.futo.circles.databinding.ViewPostFooterBinding
import org.futo.circles.feature.timeline.post.emoji.EmojisTimelineAdapter
import org.matrix.android.sdk.api.session.room.powerlevels.Role


class PostFooterView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewPostFooterBinding.inflate(LayoutInflater.from(context), this)

    private var optionsListener: PostOptionsListener? = null
    private var post: Post? = null
    private var isThreadPost = false
    private var userPowerLevel: Int = Role.Default.value
    private val emojisTimelineAdapter = EmojisTimelineAdapter { reaction ->
        post?.let {
            optionsListener?.onEmojiChipClicked(
                it.postInfo.roomId,
                it.id,
                reaction.key,
                reaction.addedByMe
            )
        }
    }

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
            btnShare.setOnClickListener {
                post?.let { optionsListener?.onShare(it.content) }
            }
            btnLike.setOnClickListener {
                post?.let { optionsListener?.onShowEmoji(it.postInfo.roomId, it.id) }
            }
            rvEmojis.adapter = emojisTimelineAdapter
        }
    }

    fun setListener(postOptionsListener: PostOptionsListener) {
        optionsListener = postOptionsListener
    }

    fun setData(data: Post, powerLevel: Int, isThread: Boolean) {
        post = data
        userPowerLevel = powerLevel
        isThreadPost = isThread
        bindViewData(data.repliesCount, data.canShare())
        bindReactionsList(data.reactionsData)
    }

    fun bindPayload(repliesCount: Int, reactions: List<ReactionsData>) {
        post = post?.copy(repliesCount = repliesCount, reactionsData = reactions)
        setRepliesCount(repliesCount)
        bindReactionsList(reactions)
    }

    fun areUserAbleToPost() = userPowerLevel >= Role.Default.value

    fun areUserAbleToReply() = !isThreadPost && areUserAbleToPost()

    private fun bindViewData(repliesCount: Int, canShare: Boolean) {
        with(binding) {
            btnShare.setIsVisible(canShare)
            btnLike.isEnabled = areUserAbleToPost()
            btnReply.apply {
                isVisible = !isThreadPost
                isEnabled = areUserAbleToPost()
                setRepliesCount(repliesCount)
            }
        }
    }

    private fun setRepliesCount(repliesCount: Int) {
        binding.btnReply.text = if (repliesCount > 0) repliesCount.toString() else ""
    }

    private fun bindReactionsList(reactions: List<ReactionsData>) {
        binding.rvEmojis.setIsVisible(reactions.isNotEmpty())
        binding.rvEmojis.post {
            emojisTimelineAdapter.submitList(reactions)
        }
    }

}