package com.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.R
import com.futo.circles.databinding.GroupPostHeaderViewBinding
import com.futo.circles.extensions.getAttributes
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.model.ImageContent
import com.futo.circles.model.Post
import org.matrix.android.sdk.api.session.room.powerlevels.Role

class GroupPostHeaderView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        GroupPostHeaderViewBinding.inflate(LayoutInflater.from(context), this)

    private var listener: GroupPostListener? = null
    private var post: Post? = null
    private var userPowerLevel: Int = Role.Default.value

    init {
        parseAttributes(attrs)
        setupViews()
    }

    fun setListener(postListener: GroupPostListener) {
        listener = postListener
    }

    fun setData(data: Post, powerLevel: Int) {
        userPowerLevel = powerLevel
        post = data
        val sender = data.postInfo.sender
        bindViewData(sender.userId, sender.disambiguatedDisplayName, sender.avatarUrl)
    }

    fun bindViewData(userId: String, displayName: String, avatarUrl: String?) {
        with(binding) {
            ivSenderImage.loadProfileIcon(avatarUrl, displayName)
            tvUserName.text = displayName
            tvUserId.text = userId
        }
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        getAttributes(attrs, R.styleable.GroupPostHeaderView) {
            val isMenuVisible = getBoolean(R.styleable.GroupPostHeaderView_optionsVisible, true)
            binding.btnMore.setIsVisible(isMenuVisible)
        }
    }

    private fun setupViews() {
        binding.btnMore.setOnClickListener { showMenu() }
    }

    private fun showMenu() {
        val unwrappedPost = post ?: return
        PopupMenu(context, binding.btnMore).apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> listener?.onRemove(unwrappedPost.id)
                    R.id.ignore -> listener?.onIgnore(unwrappedPost.postInfo.sender.userId)
                    R.id.save -> (unwrappedPost.content as? ImageContent)?.let {
                        listener?.onSaveImage(it.fileUrl)
                    }
                    R.id.report -> listener?.onReport(unwrappedPost.id)
                }
                true
            }
            inflate(R.menu.timeline_item_menu)
            menu.findItem(R.id.save).isVisible = unwrappedPost.content is ImageContent
            menu.findItem(R.id.ignore).isVisible = !unwrappedPost.isMyPost()
            menu.findItem(R.id.report).isVisible = !unwrappedPost.isMyPost()
            menu.findItem(R.id.delete).isVisible =
                unwrappedPost.isMyPost() || userPowerLevel >= Role.Moderator.value
            
            show()
        }
    }

}