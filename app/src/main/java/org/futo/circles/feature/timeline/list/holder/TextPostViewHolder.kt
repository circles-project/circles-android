package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import android.widget.TextView
import org.futo.circles.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.feature.markdown.MarkdownParser
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.TextContent
import org.futo.circles.databinding.ViewTextPostBinding
import org.futo.circles.feature.timeline.list.PostOptionsListener
import org.futo.circles.view.PostLayout

class TextPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    isThread: Boolean
) : PostViewHolder(inflate(parent, ViewTextPostBinding::inflate), isThread) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewTextPostBinding
    override val postLayout: PostLayout = binding.lPost

    init {
        binding.lPost.setListener(postOptionsListener)
        handleLinkClick(binding.tvTextContent)
    }


    override fun bind(post: Post) {
        super.bind(post)
        val content = (post.content as? TextContent) ?: return
        binding.tvTextContent.setText(content.messageSpanned, TextView.BufferType.SPANNABLE)
        bindMentionBorder(content)
    }

    private fun bindMentionBorder(content: TextContent) {
        val hasMention = MarkdownParser.hasCurrentUserMention(content.message)
        if (hasMention) binding.lCard.setBackgroundResource(R.drawable.bg_mention_highlight)
        else binding.lCard.background = null
    }

}