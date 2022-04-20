package com.futo.circles.view


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.databinding.PreviewPostViewBinding
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.user.model.User


class PreviewPostView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        PreviewPostViewBinding.inflate(LayoutInflater.from(context), this)

    init {
        getMyUser()?.let {
            binding.postHeader.setData(it.userId, it.displayName ?: "", it.avatarUrl)
        }
        binding.postFooter.setData(System.currentTimeMillis(), isEncrypted = true, isReply = false)
    }

    fun setIsImagePost(isImagePost: Boolean) {
        binding.tilTextPost.setIsVisible(!isImagePost)
        binding.ivImageContent.setIsVisible(isImagePost)
    }

    private fun getMyUser(): User? {
        val session = MatrixSessionProvider.currentSession
        return session?.myUserId?.let { session.getUser(it) }
    }

}