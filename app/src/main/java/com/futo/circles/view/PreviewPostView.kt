package com.futo.circles.view


import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import com.futo.circles.databinding.PreviewPostViewBinding
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.user.model.User

interface PreviewPostListener {

    fun onTextChanged(text: String)

    fun onPickImageClicked()
}

class PreviewPostView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        PreviewPostViewBinding.inflate(LayoutInflater.from(context), this)

    private var listener: PreviewPostListener? = null

    init {
        getMyUser()?.let {
            binding.postHeader.setData(it.userId, it.displayName ?: "", it.avatarUrl)
        }
        binding.postFooter.bindViewData(System.currentTimeMillis(), isEncrypted = true, isReply = false)

        binding.ivImageContent.setOnClickListener { listener?.onPickImageClicked() }
        binding.tilTextPost.editText?.doAfterTextChanged {
            listener?.onTextChanged(it?.toString() ?: "")
        }
    }

    fun setListener(previewPostListener: PreviewPostListener) {
        listener = previewPostListener
    }

    fun setIsImagePost(isImagePost: Boolean) {
        binding.tilTextPost.setIsVisible(!isImagePost)
        binding.ivImageContent.setIsVisible(isImagePost)
    }

    private fun getMyUser(): User? {
        val session = MatrixSessionProvider.currentSession
        return session?.myUserId?.let { session.getUser(it) }
    }

    fun setImage(uri: Uri) {
        binding.ivImageContent.setImageURI(uri)
    }

    fun getText() = binding.tilTextPost.editText?.text?.toString()?.trim() ?: ""

}