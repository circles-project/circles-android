package org.futo.circles.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.R
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.feature.markdown.MarkdownParser
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.PollContent
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.TextContent
import org.futo.circles.databinding.LayoutPostBinding
import org.futo.circles.model.PostItemPayload
import org.matrix.android.sdk.api.session.room.send.SendState


interface PostOptionsListener {
    fun onShowMenuClicked(roomId: String, eventId: String)
    fun onUserClicked(userId: String)
    fun onShare(content: PostContent)
    fun onReply(roomId: String, eventId: String)
    fun onShowPreview(roomId: String, eventId: String)
    fun onShowEmoji(roomId: String, eventId: String, onAddEmoji: (String) -> Unit)
    fun onEmojiChipClicked(roomId: String, eventId: String, emoji: String, isUnSend: Boolean)
    fun onPollOptionSelected(roomId: String, eventId: String, optionId: String)
}

@SuppressLint("ClickableViewAccessibility")
class PostLayout(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val binding =
        LayoutPostBinding.inflate(LayoutInflater.from(context), this)

    private var optionsListener: PostOptionsListener? = null
    private var post: Post? = null

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (binding.postFooter.areUserAbleToPost())
                    post?.let {
                        optionsListener?.onShowEmoji(it.postInfo.roomId, it.id) { emoji ->
                            binding.postFooter.addEmojiFromPickerLocalUpdate(emoji)
                        }
                    }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                binding.postHeader.showMenu()
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                openReplies()
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent) = true
            override fun onDown(e: MotionEvent) = true

        }).apply {
            setIsLongpressEnabled(true)
        }

    init {
        binding.lCard.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    fun setListener(postOptionsListener: PostOptionsListener) {
        optionsListener = postOptionsListener
        binding.postFooter.setListener(postOptionsListener)
        binding.postHeader.setListener(postOptionsListener)
        setupClickListeners()
    }


    fun setData(data: Post, userPowerLevel: Int, isThread: Boolean) {
        post = data
        setGeneralMessageData(data, userPowerLevel, isThread)
    }

    fun setPayload(payload: PostItemPayload) {
        setSendStatus(payload.sendState, payload.readByCount)
        binding.postFooter.bindPayload(payload.repliesCount, payload.reactions)
    }

    private fun setupClickListeners() {
        binding.lvContent.findViewById<ConstraintLayout>(R.id.vMediaContent)?.apply {
            setOnClickListener {
                post?.let { optionsListener?.onShowPreview(it.postInfo.roomId, it.id) }
            }
            setOnLongClickListener {
                binding.postHeader.showMenu()
                true
            }
        }
        binding.lvContent.findViewById<ReadMoreTextView>(R.id.tvTextContent)?.apply {
            setNotCollapsableClickAction { openReplies() }
            setOnLongClickListener {
                binding.postHeader.showMenu()
                true
            }
        }
    }

    private fun setGeneralMessageData(data: Post, userPowerLevel: Int, isThread: Boolean) {
        binding.postHeader.setData(data)
        binding.postFooter.setData(data, userPowerLevel, isThread)
        setMentionBorder(data.content)
        setIsEdited(data.postInfo.isEdited)
        setSendStatus(data.sendState, data.readByCount)
    }

    private fun setIsEdited(isEdited: Boolean) {
        binding.tvEditedLabel.setIsVisible(isEdited)
    }

    private fun openReplies() {
        if (binding.postFooter.areUserAbleToReply())
            post?.let { optionsListener?.onReply(it.postInfo.roomId, it.id) }
    }

    private fun setMentionBorder(content: PostContent) {
        val hasMention = when (content) {
            is MediaContent -> content.caption?.let {
                MarkdownParser.hasCurrentUserMention(it.toString())
            } ?: false

            is TextContent -> MarkdownParser.hasCurrentUserMention(content.message.toString())
            is PollContent -> false
        }
        if (hasMention) binding.lCard.setBackgroundResource(R.drawable.bg_mention_highlight)
        else binding.lCard.background = null
    }

    private fun setSendStatus(sendState: SendState, readByCount: Int) {
        when {
            sendState.isSending() -> {
                binding.ivSendStatus.setImageResource(R.drawable.ic_sending)
                binding.tvReadByCount.text = ""
            }

            sendState.hasFailed() -> {
                binding.ivSendStatus.setImageResource(R.drawable.ic_send_failed)
                binding.tvReadByCount.text = ""
            }

            sendState.isSent() -> {
                if (readByCount > 0) {
                    binding.ivSendStatus.setImageResource(org.futo.circles.core.R.drawable.ic_seen)
                    binding.tvReadByCount.text = readByCount.toString()
                } else {
                    binding.ivSendStatus.setImageResource(R.drawable.ic_sent)
                    binding.tvReadByCount.text = ""
                }
            }
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        if (child.id == R.id.lCard) {
            super.addView(child, index, params)
        } else {
            findViewById<FrameLayout>(R.id.lvContent).addView(child, index, params)
        }
    }
}