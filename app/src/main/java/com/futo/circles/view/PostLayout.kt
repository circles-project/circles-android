package com.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.R
import com.futo.circles.databinding.PostLayoutBinding
import com.futo.circles.extensions.gone
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.model.*


interface GroupPostListener {
    fun onShowRepliesClicked(eventId: String)
    fun onReply(eventId: String, userName: String)
    fun onShare(content: PostContent)
    fun onRemove(eventId: String)
    fun onIgnore(senderId: String)
    fun onSaveImage(imageContent: ImageContent)
    fun onReport(eventId: String)
}

class PostLayout(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        PostLayoutBinding.inflate(LayoutInflater.from(context), this)

    private var listener: GroupPostListener? = null
    private var post: Post? = null

    init {
        binding.btnShowReplies.setOnClickListener {
            post?.let { listener?.onShowRepliesClicked(it.id) }
        }
        binding.postFooter.setOnClickListener {
            post?.let { listener?.onShowRepliesClicked(it.id) }
        }
    }

    fun setListener(groupPostListener: GroupPostListener) {
        listener = groupPostListener
        binding.postFooter.setListener(groupPostListener)
        binding.postHeader.setListener(groupPostListener)
    }


    fun setData(data: Post, userPowerLevel: Int) {
        post = data
        setGeneralMessageData(data, userPowerLevel)
        bindRepliesButton(data)
    }

    fun setPayload(payload: PostItemPayload) {
        bindRepliesButton(payload.hasReplies, payload.repliesCount, payload.isRepliesVisible)
    }

    private fun setGeneralMessageData(data: Post, userPowerLevel: Int) {
        val isReply = data is ReplyPost
        binding.vReplyMargin.setIsVisible(isReply)
        binding.postHeader.setData(data, userPowerLevel)
        binding.postFooter.setData(data, isReply)
    }

    private fun bindRepliesButton(post: Post) {
        val rootPost = (post as? RootPost) ?: kotlin.run { binding.btnShowReplies.gone(); return }

        bindRepliesButton(
            rootPost.hasReplies(), rootPost.getRepliesCount(), rootPost.isRepliesVisible
        )
    }

    private fun bindRepliesButton(
        hasReplies: Boolean,
        repliesCount: Int,
        isRepliesVisible: Boolean
    ) {
        with(binding.btnShowReplies) {
            setIsVisible(hasReplies)
            setClosedText(
                context.resources.getQuantityString(
                    R.plurals.show_replies_plurals,
                    repliesCount, repliesCount
                )
            )
            setIsOpened(isRepliesVisible)
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        if (child.id == R.id.postCard || child.id == R.id.btnShowReplies || child.id == R.id.vReplyMargin) {
            super.addView(child, index, params)
        } else {
            findViewById<FrameLayout>(R.id.lvContent).addView(child, index, params)
        }
    }
}