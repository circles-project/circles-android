package org.futo.circles.view

import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.R
import org.futo.circles.core.extensions.getAttributes
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.extensions.setIsEncryptedIcon
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserUtils
import org.futo.circles.databinding.ViewPostHeaderBinding
import org.futo.circles.feature.timeline.list.PostOptionsListener
import java.util.Date

class PostHeaderView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding = ViewPostHeaderBinding.inflate(LayoutInflater.from(context), this)

    private var optionsListener: PostOptionsListener? = null
    private var post: Post? = null

    init {
        parseAttributes(attrs)
        setupViews()
    }

    fun setListener(postOptionsListener: PostOptionsListener) {
        optionsListener = postOptionsListener
    }

    fun setData(data: Post) {
        post = data
        val sender = data.postInfo.sender
        bindViewData(
            sender.userId,
            sender.notEmptyDisplayName(),
            sender.avatarUrl,
            data.postInfo.getLastModifiedTimestamp(),
            data.postInfo.isEncrypted,
            data.timelineName,
            data.timelineOwnerName
        )
    }

    fun bindViewData(
        userId: String,
        name: String,
        avatarUrl: String?,
        timestamp: Long,
        isEncrypted: Boolean,
        roomName: String? = null,
        timelineOwnerName: String? = null
    ) {
        with(binding) {
            ivSenderImage.apply {
                loadUserProfileIcon(avatarUrl, userId)
                setOnClickListener {
                    if (userId != MatrixSessionProvider.currentSession?.myUserId)
                        optionsListener?.onUserClicked(userId)
                }
            }
            tvUserName.text = name
            tvUserId.text = UserUtils.removeDomainSuffix(userId)
            ivEncrypted.setIsEncryptedIcon(isEncrypted)
            tvMessageTime.text = DateFormat.format("MMM dd, h:mm a", Date(timestamp))

            roomName?.let {
                val nameFormat =
                    context.getString(R.string.in_timeline_format, timelineOwnerName ?: "", it)
                tvRoomName.text = nameFormat
            }
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

    fun showMenu() {
        val unwrappedPost = post ?: return
        optionsListener?.onShowMenuClicked(unwrappedPost.postInfo.roomId, unwrappedPost.id)
    }

}