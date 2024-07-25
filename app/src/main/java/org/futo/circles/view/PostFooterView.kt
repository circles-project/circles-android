package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.vanniktech.ui.parentViewGroup
import org.futo.circles.core.extensions.getCurrentUserPowerLevel
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.ReactionsData
import org.futo.circles.databinding.ViewPostFooterBinding
import org.futo.circles.feature.timeline.list.PostOptionsListener
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
                post?.let {
                    optionsListener?.onShare(it.content, this@PostFooterView.parentViewGroup())
                }
            }
            btnLike.setOnClickListener {
                post?.let {
                    optionsListener?.onShowEmoji(
                        it.postInfo.roomId,
                        it.id
                    ) { addEmojiFromPickerLocalUpdate(it) }
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
        bindViewData(data.repliesCount)
        bindReactionsList(data.reactionsData)
    }

    fun bindPayload(repliesCount: Int, reactions: List<ReactionsData>) {
        post = post?.copy(repliesCount = repliesCount, reactionsData = reactions)
        setRepliesCount(repliesCount)
        bindReactionsList(reactions)
    }

    private fun bindViewData(repliesCount: Int) {
        with(binding) {
            btnReply.apply {
                isVisible = !isThreadPost
                setRepliesCount(repliesCount)
            }
        }
    }

    private fun setRepliesCount(repliesCount: Int) {
        binding.btnReply.text = if (repliesCount > 0) repliesCount.toString() else ""
    }

    private fun bindReactionsList(reactions: List<ReactionsData>) {
        binding.vEmojisList.bindReactionsList(
            reactions,
            isAbleToPost(),
            object : ReactionChipClickListener {
                override fun onReactionChipClicked(emoji: String, isAddedByMe: Boolean) {
                    post?.let {
                        optionsListener?.onEmojiChipClicked(
                            it.postInfo.roomId,
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

    private fun isAbleToPost() =
        getCurrentUserPowerLevel(post?.postInfo?.roomId ?: "") >= Role.Default.value

}