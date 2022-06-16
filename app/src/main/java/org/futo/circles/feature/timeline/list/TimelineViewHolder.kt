package org.futo.circles.feature.timeline.list

import android.util.Size
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ImagePostViewBinding
import org.futo.circles.databinding.TextPostViewBinding
import org.futo.circles.extensions.UriContentScheme
import org.futo.circles.extensions.gone
import org.futo.circles.extensions.loadEncryptedImage
import org.futo.circles.model.ImageContent
import org.futo.circles.model.Post
import org.futo.circles.model.PostItemPayload
import org.futo.circles.model.TextContent
import org.futo.circles.provider.MatrixSessionProvider
import org.futo.circles.view.PostOptionsListener
import org.futo.circles.view.PostLayout
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker

sealed class PostViewHolder(view: View, private val userPowerLevel: Int) :
    RecyclerView.ViewHolder(view) {

    abstract val postLayout: PostLayout

    open fun bind(post: Post) {
        postLayout.setData(post, userPowerLevel)
    }

    fun bindPayload(payload: PostItemPayload) {
        postLayout.setPayload(payload)
    }
}

class TextPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    userPowerLevel: Int
) : PostViewHolder(inflate(parent, TextPostViewBinding::inflate), userPowerLevel) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as TextPostViewBinding
    override val postLayout: PostLayout = binding.lTextPost

    init {
        binding.lTextPost.setListener(postOptionsListener)
    }

    override fun bind(post: Post) {
        super.bind(post)

        (post.content as? TextContent)?.let {
            binding.tvContent.text = it.message
        }
    }
}

class ImagePostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    userPowerLevel: Int
) : PostViewHolder(inflate(parent, ImagePostViewBinding::inflate), userPowerLevel) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ImagePostViewBinding
    override val postLayout: PostLayout = binding.lImagePost
    private val tracker = MatrixSessionProvider.currentSession?.contentUploadProgressTracker()
    private var postId: String? = null
    private var listener: ContentUploadStateTracker.UpdateListener? = null

    init {
        binding.lImagePost.setListener(postOptionsListener)
    }

    override fun bind(post: Post) {
        super.bind(post)
        binding.vLoadingImage.gone()
        tracker?.track(
            post.id.also { postId = it },
            UploadImageProgressHelper.getListener(binding.vLoadingImage).also { listener = it }
        )
        (post.content as? ImageContent)?.let {
            if (it.fileUrl.startsWith(UriContentScheme)) {
                binding.ivContent.setImageResource(R.drawable.blurred_placeholder)
            } else {
                val imageWith = binding.ivContent.width
                val size = Size(imageWith, (imageWith / it.aspectRatio).toInt())
                binding.ivContent.loadEncryptedImage(it, size)
            }
        }
    }

    fun unTrack() {
        val id = postId ?: return
        val callback = listener ?: return
        tracker?.untrack(id, callback)
    }

}