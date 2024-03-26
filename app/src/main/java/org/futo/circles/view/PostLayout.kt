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
import org.futo.circles.core.feature.markdown.MarkdownParser
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.PollContent
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.TextContent
import org.futo.circles.databinding.LayoutPostBinding
import org.futo.circles.feature.timeline.list.PostOptionsListener
import org.futo.circles.model.PostItemPayload


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


    fun setData(data: Post, isThread: Boolean) {
        post = data
        setGeneralMessageData(data, isThread)
    }

    fun setPayload(payload: PostItemPayload) {
        binding.vPostStatus.setSendStatus(payload.sendState, payload.readByCount)
        binding.postFooter.bindPayload(payload.repliesCount, payload.reactions)
    }

    private fun setupClickListeners() {
        binding.lvContent.findViewById<ConstraintLayout>(R.id.ivMediaContent)?.apply {
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

    private fun setGeneralMessageData(data: Post, isThread: Boolean) {
        binding.postHeader.setData(data)
        binding.postFooter.setData(data, isThread)
        //setMentionBorder(data.content)
        binding.vPostStatus.setIsEdited(data.postInfo.isEdited)
        binding.vPostStatus.setSendStatus(data.sendState, data.readByCount)
    }

    private fun openReplies() {
        post?.let { optionsListener?.onReply(it.postInfo.roomId, it.id) }
    }


    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        if (child.id == R.id.lCard) {
            super.addView(child, index, params)
        } else {
            findViewById<FrameLayout>(R.id.lvContent).addView(child, index, params)
        }
    }
}