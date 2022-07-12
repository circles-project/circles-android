package org.futo.circles.view


import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.databinding.PreviewPostViewBinding
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getUser
import org.matrix.android.sdk.api.session.user.model.User

interface PreviewPostListener {

    fun onTextChanged(text: String)

    fun onPickImageClicked()

    fun onPostTypeChanged(isImagePost: Boolean)
}

class PreviewPostView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        PreviewPostViewBinding.inflate(LayoutInflater.from(context), this)

    private var listener: PreviewPostListener? = null
    private var imageUri: Uri? = null
    private var isImageContentSelected = false

    init {
        getMyUser()?.let {
            binding.postHeader.bindViewData(it.userId, it.displayName ?: "", it.avatarUrl)
        }
        binding.ivImageContent.setOnClickListener { listener?.onPickImageClicked() }
        binding.etTextPost.doAfterTextChanged {
            listener?.onTextChanged(it?.toString() ?: "")
        }
    }

    fun setListener(previewPostListener: PreviewPostListener) {
        listener = previewPostListener
    }

    private fun setIsImagePost(isImagePost: Boolean) {
        isImageContentSelected = isImagePost
        listener?.onPostTypeChanged(false)
        binding.etTextPost.setIsVisible(!isImagePost)
        binding.ivImageContent.setIsVisible(isImagePost)
    }

    private fun getMyUser(): User? {
        val session = MatrixSessionProvider.currentSession
        return session?.myUserId?.let { session.getUser(it) }
    }

    fun setImage(uri: Uri) {
        imageUri = uri
        binding.ivImageContent.setImageURI(uri)
    }

    fun isImageContentSelected() = isImageContentSelected

    fun getImageUri(): Uri? = imageUri

    fun getText() = binding.etTextPost.text.toString().trim()

}