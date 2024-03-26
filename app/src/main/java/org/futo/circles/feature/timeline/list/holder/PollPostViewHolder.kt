package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import org.futo.circles.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.feature.markdown.MarkdownParser
import org.futo.circles.core.model.PollContent
import org.futo.circles.core.model.Post
import org.futo.circles.databinding.ViewPollPostBinding
import org.futo.circles.feature.timeline.list.PostOptionsListener
import org.futo.circles.view.PostLayout

class PollPostViewHolder(
    parent: ViewGroup,
    private val postOptionsListener: PostOptionsListener,
    isThread: Boolean
) : PostViewHolder(inflate(parent, ViewPollPostBinding::inflate), isThread) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewPollPostBinding
    override val postLayout: PostLayout = binding.lPollPost

    init {
        binding.lPollPost.setListener(postOptionsListener)
    }

    override fun bind(post: Post) {
        super.bind(post)
        val content = (post.content as? PollContent) ?: return
        binding.pollContentView.setup(content) { optionId ->
            postOptionsListener.onPollOptionSelected(post.postInfo.roomId, post.id, optionId)
        }
        setMentionBorder(content)
    }

    private fun setMentionBorder(content: PollContent) {
        val hasMention = MarkdownParser.hasCurrentUserMention(content.question)
        if (hasMention) binding.lCard.setBackgroundResource(R.drawable.bg_mention_highlight)
        else binding.lCard.background = null
    }
}