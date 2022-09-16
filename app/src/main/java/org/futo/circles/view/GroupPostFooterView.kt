package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import org.futo.circles.R
import org.futo.circles.databinding.ViewGroupPostFooterBinding
import org.futo.circles.extensions.setIsEncryptedIcon
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.model.Post
import org.futo.circles.model.ReactionsData
import java.text.DateFormat
import java.util.*


class GroupPostFooterView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewGroupPostFooterBinding.inflate(LayoutInflater.from(context), this)

    private var optionsListener: PostOptionsListener? = null
    private var post: Post? = null

    init {
        setupViews()
    }

    private fun setupViews() {
        with(binding) {
            btnReply.setOnClickListener {
                post?.let {
                    optionsListener?.onReply(
                        it.postInfo.roomId,
                        it.id,
                        it.postInfo.sender.disambiguatedDisplayName
                    )
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

    fun setData(data: Post, isReply: Boolean) {
        post = data
        bindViewData(data.postInfo.timestamp, data.postInfo.isEncrypted, isReply, data.canShare())
        bindReactionsList(data.postInfo.reactionsData)
    }

    private fun bindViewData(
        timestamp: Long,
        isEncrypted: Boolean,
        isReply: Boolean,
        canShare: Boolean
    ) {
        with(binding) {
            btnShare.setIsVisible(canShare)
            btnReply.setIsVisible(!isReply)
            ivEncrypted.setIsEncryptedIcon(isEncrypted)
            tvMessageTime.text = DateFormat.getDateTimeInstance().format(Date(timestamp))
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

}