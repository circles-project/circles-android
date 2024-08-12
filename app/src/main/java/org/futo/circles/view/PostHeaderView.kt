package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.R
import org.futo.circles.core.extensions.getAttributes
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.TimeAgoFormatUtils
import org.futo.circles.databinding.ViewPostHeaderBinding
import org.futo.circles.feature.timeline.list.PostOptionsListener

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

    fun showMenu() {
        val unwrappedPost = post ?: return
        optionsListener?.onShowMenuClicked(unwrappedPost.postInfo.roomId, unwrappedPost.id)
    }

    fun setData(data: Post) {
        post = data
        val sender = data.postInfo.sender
        bindViewData(
            sender.userId,
            sender.notEmptyDisplayName(),
            sender.avatarUrl,
            data.postInfo.getLastModifiedTimestamp(),
            data.timelineName,
            data.timelineOwnerName
        )
    }

    fun bindViewData(
        userId: String,
        name: String,
        avatarUrl: String?,
        timestamp: Long,
        roomName: String? = null,
        timelineOwnerName: String? = null
    ) {
        setUserIcon(userId, avatarUrl)
        binding.tvUserName.text = name
        binding.tvTime.text = TimeAgoFormatUtils.getTimeAgoString(context, timestamp)
        setCircleRoomIndicator(roomName, timelineOwnerName)
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

    private fun setUserIcon(userId: String, avatarUrl: String?) {
        binding.ivSenderImage.apply {
            loadUserProfileIcon(avatarUrl, userId)
            setOnClickListener {
                if (userId != MatrixSessionProvider.currentSession?.myUserId)
                    optionsListener?.onUserClicked(userId)
            }
        }
    }

    private fun setCircleRoomIndicator(roomName: String?, timelineOwnerName: String?) {
        binding.chCircleIndicator.setIsVisible(roomName != null)
        roomName?.let {
            val nameFormat =
                context.getString(R.string.in_timeline_format, timelineOwnerName ?: "", it)
            binding.chCircleIndicator.text = nameFormat
        }
    }
}