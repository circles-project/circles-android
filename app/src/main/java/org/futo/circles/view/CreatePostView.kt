package org.futo.circles.view


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.visible
import org.futo.circles.databinding.ViewCreatePostBinding
import org.matrix.android.sdk.api.session.user.model.User

interface CreatePostViewListener {
    fun onCreatePoll()
    fun onCreatePost()
}

class CreatePostView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = ViewCreatePostBinding.inflate(LayoutInflater.from(context), this)

    private var listener: CreatePostViewListener? = null
    private var isUserAbleToPost = true

    init {
        setBackgroundColor(
            ContextCompat.getColor(
                context,
                org.futo.circles.core.R.color.post_card_background_color
            )
        )
        setOnClickListener { listener?.onCreatePost() }
        binding.ivCreatePoll.setOnClickListener { listener?.onCreatePoll() }
    }

    fun setUp(callback: CreatePostViewListener, recycler: RecyclerView, isThread: Boolean) {
        listener = callback
        bindToRecyclerView(recycler)
        setupButtons(isThread)
    }

    fun setUserAbleToPost(isAbleToPost: Boolean) {
        isUserAbleToPost = isAbleToPost
        setIsVisible(isUserAbleToPost)
    }

    fun setUserInfo(user: User) {
        binding.ivProfile.loadUserProfileIcon(user.avatarUrl, user.userId)
    }

    private fun setupButtons(isThread: Boolean) {
        binding.ivCreatePoll.setIsVisible(isThread.not())
    }

    private fun bindToRecyclerView(recycler: RecyclerView) {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isUserAbleToPost)
                    if (dy > 0) gone() else if (dy < 0) visible()
            }
        })
    }

}