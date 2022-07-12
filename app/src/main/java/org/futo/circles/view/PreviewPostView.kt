package org.futo.circles.view


import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.databinding.PreviewPostViewBinding
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.mapping.notEmptyDisplayName
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.ImagePostContent
import org.futo.circles.model.TextPostContent
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getUser
import org.matrix.android.sdk.api.session.user.model.User

interface PreviewPostListener {
    fun onPostContentAvailable(isAvailable: Boolean)
}

class PreviewPostView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        PreviewPostViewBinding.inflate(LayoutInflater.from(context), this)

    private var listener: PreviewPostListener? = null

    private var postContent: CreatePostContent? = null

    init {
        getMyUser()?.let {
            binding.postHeader.bindViewData(it.userId, it.notEmptyDisplayName(), it.avatarUrl)
        }
        binding.etTextPost.doAfterTextChanged {
            listener?.onPostContentAvailable(it?.toString()?.isNotBlank() == true)
        }
        binding.lImageContent.ivRemoveImage.setOnClickListener {
            setTextContent()
        }
        updateContentView()
    }

    fun setListener(previewPostListener: PreviewPostListener) {
        listener = previewPostListener
    }

    fun setImage(uri: Uri) {
        postContent = ImagePostContent(uri)
        binding.lImageContent.ivImageContent.setImageURI(uri)
        updateContentView()
        listener?.onPostContentAvailable(true)
    }

    fun getPostContent() = postContent ?: TextPostContent(binding.etTextPost.text.toString().trim())

    private fun updateContentView() {
        binding.lImageContent.root.setIsVisible(postContent is ImagePostContent)
        binding.etTextPost.setIsVisible(postContent is TextPostContent || postContent == null)
    }

    private fun setTextContent() {
        postContent = null
        updateContentView()
        listener?.onPostContentAvailable(binding.etTextPost.text?.toString()?.isNotBlank() == true)
    }

    private fun getMyUser(): User? {
        val session = MatrixSessionProvider.currentSession
        return session?.myUserId?.let { session.getUser(it) }
    }
}