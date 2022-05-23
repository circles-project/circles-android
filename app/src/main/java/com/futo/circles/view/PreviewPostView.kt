package com.futo.circles.view


import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import com.futo.circles.R
import com.futo.circles.databinding.PreviewPostViewBinding
import com.futo.circles.extensions.getText
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.provider.MatrixSessionProvider
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
        binding.postFooter.bindViewData(
            System.currentTimeMillis(),
            isEncrypted = true,
            isReply = false
        )

        binding.ivImageContent.setOnClickListener { listener?.onPickImageClicked() }
        binding.tilTextPost.editText?.doAfterTextChanged {
            listener?.onTextChanged(it?.toString() ?: "")
        }
        binding.ivText.setOnClickListener { setIsImagePost(false) }
        binding.ivImage.setOnClickListener { setIsImagePost(true) }
    }

    fun setListener(previewPostListener: PreviewPostListener) {
        listener = previewPostListener
    }

    private fun setIsImagePost(isImagePost: Boolean) {
        isImageContentSelected = isImagePost
        listener?.onPostTypeChanged(false)
        binding.tilTextPost.setIsVisible(!isImagePost)
        binding.ivImageContent.setIsVisible(isImagePost)
        val activeColor = context?.getColor(R.color.blue) ?: return
        val inActiveColor = context?.getColor(R.color.gray) ?: return
        binding.ivText.setColorFilter(if (isImagePost) inActiveColor else activeColor)
        binding.ivImage.setColorFilter(if (isImagePost) activeColor else inActiveColor)
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

    fun getText() = binding.tilTextPost.getText()

}