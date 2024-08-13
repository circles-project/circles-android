package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.OtherEventContent
import org.futo.circles.core.model.Post
import org.futo.circles.databinding.ViewOtherEventPostBinding
import org.futo.circles.feature.timeline.list.PostOptionsListener
import org.futo.circles.view.PostFooterView
import org.futo.circles.view.PostHeaderView
import org.futo.circles.view.ReadMoreTextView

class OtherEventPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener
) : PostViewHolder(
    inflate(parent, ViewOtherEventPostBinding::inflate),
    postOptionsListener,
    false
) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewOtherEventPostBinding

    override val postLayout: ViewGroup? = null
    override val postFooter: PostFooterView? = null
    override val readMoreTextView: ReadMoreTextView? = null

    override val postHeader: PostHeaderView
        get() = binding.postHeader

    init {
        setListeners()
        binding.lCard.setOnLongClickListener {
            postHeader.showMenu()
            true
        }
    }

    override fun bindHolderSpecific(post: Post) {
        val content = (post.content as? OtherEventContent) ?: return
        binding.tvTextContent.text = content.eventType
    }

}