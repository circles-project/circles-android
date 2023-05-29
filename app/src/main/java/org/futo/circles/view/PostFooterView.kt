package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import org.futo.circles.R
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.databinding.ViewPostFooterBinding
import org.futo.circles.model.Post
import org.futo.circles.model.ReactionsData
import org.matrix.android.sdk.api.session.room.powerlevels.Role


class PostFooterView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewPostFooterBinding.inflate(LayoutInflater.from(context), this)

    private var optionsListener: PostOptionsListener? = null
    private var post: Post? = null
    private var userPowerLevel: Int = Role.Default.value

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
        }
    }

    fun setListener(postOptionsListener: PostOptionsListener) {
        optionsListener = postOptionsListener
    }

    fun setData(data: Post, powerLevel: Int, isThread: Boolean) {
        post = data
        userPowerLevel = powerLevel
        bindViewData(data.repliesCount, data.canShare(), isThread)
        bindReactionsList(data.postInfo.reactionsData)
    }

    fun setRepliesCount(repliesCount: Int) {
        binding.btnReply.text = if (repliesCount > 0) repliesCount.toString() else ""
    }

    private fun bindViewData(repliesCount: Int, canShare: Boolean, isThread: Boolean) {
        with(binding) {
            btnShare.setIsVisible(canShare)
            btnLike.isEnabled = areUserAbleToPost()
            btnReply.apply {
                isVisible = !isThread
                isEnabled = areUserAbleToPost()
                setRepliesCount(repliesCount)
            }
        }
    }

    private fun bindReactionsList(reactions: List<ReactionsData>) {
        binding.chipsScrollView.setIsVisible(reactions.isNotEmpty())
        with(binding.lReactions) {
            removeAllViews()
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 0, 4, 0)

            reactions.forEach { reaction ->
                val title = "${reaction.key} ${reaction.count}"
                addView(Chip(context).apply {
                    text = title
                    setOnClickListener {
                        post?.let {
                            optionsListener?.onEmojiChipClicked(
                                it.postInfo.roomId,
                                it.id,
                                reaction.key,
                                reaction.addedByMe
                            )
                        }
                    }
                    isCheckable = true
                    isCheckedIconVisible = false
                    chipBackgroundColor =
                        ContextCompat.getColorStateList(context, R.color.emoji_chip_background)
                    setEnsureMinTouchTargetSize(false)
                    isChecked = reaction.addedByMe
                }, layoutParams)
            }

        }
    }

    private fun areUserAbleToPost() = userPowerLevel >= Role.Default.value

}