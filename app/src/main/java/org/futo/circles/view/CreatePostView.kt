package org.futo.circles.view


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.notEmptyDisplayName
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

    init {
        setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        setOnClickListener { listener?.onCreatePost() }
        binding.ivCreatePoll.setOnClickListener { listener?.onCreatePoll() }
    }

    fun setUp(callback: CreatePostViewListener, recycler: RecyclerView, isThread: Boolean) {
        listener = callback
        bindToRecyclerView(recycler)
        setupButtons(isThread)
    }

    fun setUserInfo(user: User) {
        binding.ivProfile.loadProfileIcon(user.avatarUrl, user.notEmptyDisplayName())
    }

    private fun setupButtons(isThread: Boolean) {
        binding.ivCreatePoll.setIsVisible(isThread.not())
    }

    private fun bindToRecyclerView(recycler: RecyclerView) {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    visible()
                    return
                }
                if (dy > 0) gone() else if (dy < 0) visible()
            }
        })
    }

}