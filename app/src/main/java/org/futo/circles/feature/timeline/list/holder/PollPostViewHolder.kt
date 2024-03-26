package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import org.futo.circles.core.base.list.ViewBindingHolder
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
        (post.content as? PollContent)?.let {
            binding.pollContentView.setup(it) { optionId ->
                postOptionsListener.onPollOptionSelected(post.postInfo.roomId, post.id, optionId)
            }
        }
    }
}