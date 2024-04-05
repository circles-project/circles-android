package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import android.widget.TextView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.TextContent
import org.futo.circles.databinding.ViewTextPostBinding
import org.futo.circles.feature.timeline.list.PostOptionsListener
import org.futo.circles.view.PostFooterView
import org.futo.circles.view.PostHeaderView
import org.futo.circles.view.PostStatusView
import org.futo.circles.view.ReadMoreTextView

class TextPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    isThread: Boolean
) : PostViewHolder(inflate(parent, ViewTextPostBinding::inflate), postOptionsListener, isThread) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewTextPostBinding
    override val postLayout: ViewGroup
        get() = binding.lCard
    override val postHeader: PostHeaderView
        get() = binding.postHeader
    override val postFooter: PostFooterView
        get() = binding.postFooter
    override val postStatus: PostStatusView
        get() = binding.vPostStatus

    override val readMoreTextView: ReadMoreTextView
        get() = binding.tvTextContent

    init {
        setListeners()
    }

    override fun bindHolderSpecific(post: Post) {
        val content = (post.content as? TextContent) ?: return
        binding.tvTextContent.setText(content.messageSpanned, TextView.BufferType.SPANNABLE)
    }

}