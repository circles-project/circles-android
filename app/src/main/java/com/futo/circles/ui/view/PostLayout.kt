package com.futo.circles.ui.view

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
import com.futo.circles.extensions.setVisibility
import com.futo.circles.model.Post
import com.futo.circles.model.PostItemPayload
import com.futo.circles.model.ReplyPost
import com.futo.circles.model.RootPost
import org.matrix.android.sdk.api.session.content.ContentUrlResolver


interface GroupPostListener {
    fun onShowRepliesClicked(eventId: String)
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
    }

    fun setListener(groupPostListener: GroupPostListener) {
        listener = groupPostListener
    }


    fun setData(data: Post, urlResolver: ContentUrlResolver?) {
        post = data
        setGeneralMessageData(data, urlResolver)
        bindRepliesButton(data)
    }

    fun setPayload(payload: PostItemPayload) {
        bindRepliesButton(payload.hasReplies, payload.repliesCount, payload.isRepliesVisible)
    }

    private fun setGeneralMessageData(
        data: Post,
        urlResolver: ContentUrlResolver?
    ) {
        val isReply = data is ReplyPost
        binding.vReplyMargin.setVisibility(isReply)
        binding.postHeader.setData(data.postInfo.sender, urlResolver)
        binding.postFooter.setData(data.postInfo, isReply)
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
            setVisibility(hasReplies)
            setClosedText(
                context.resources.getQuantityString(
                    R.plurals.show__replies_plurals,
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