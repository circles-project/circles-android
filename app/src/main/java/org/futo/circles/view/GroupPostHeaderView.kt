package org.futo.circles.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.R
import org.futo.circles.core.utils.UserUtils
import org.futo.circles.databinding.ViewGroupPostHeaderBinding
import org.futo.circles.extensions.getAttributes
import org.futo.circles.extensions.loadProfileIcon
import org.futo.circles.extensions.setIsEncryptedIcon
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.mapping.notEmptyDisplayName
import org.futo.circles.model.PollContent
import org.futo.circles.model.PollState
import org.futo.circles.model.Post
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import java.lang.String.format
import java.util.*

class GroupPostHeaderView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewGroupPostHeaderBinding.inflate(LayoutInflater.from(context), this)

    private var optionsListener: PostOptionsListener? = null
    private var post: Post? = null
    private var userPowerLevel: Int = Role.Default.value


    init {
        parseAttributes(attrs)
        setupViews()
    }

    fun setListener(postOptionsListener: PostOptionsListener) {
        optionsListener = postOptionsListener
    }

    fun setData(data: Post, powerLevel: Int) {
        userPowerLevel = powerLevel
        post = data
        val sender = data.postInfo.sender
        bindViewData(
            sender.userId,
            sender.notEmptyDisplayName(),
            sender.avatarUrl,
            data.postInfo.timestamp,
            data.postInfo.isEncrypted
        )
    }

    fun bindViewData(
        userId: String,
        name: String,
        avatarUrl: String?,
        timestamp: Long,
        isEncrypted: Boolean
    ) {
        with(binding) {
            ivSenderImage.loadProfileIcon(avatarUrl, name)
            tvUserName.text = name
            tvUserId.text = UserUtils.removeDomainSuffix(userId)
            ivEncrypted.setIsEncryptedIcon(isEncrypted)
            tvMessageTime.text = DateFormat.format("MMM dd, h:mm a",Date(timestamp))
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

    @SuppressLint("RestrictedApi")
    private fun showMenu() {
        val unwrappedPost = post ?: return
        PopupMenu(context, binding.btnMore).apply {
            (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> optionsListener?.onRemove(
                        unwrappedPost.postInfo.roomId, unwrappedPost.id
                    )
                    R.id.ignore -> optionsListener?.onIgnore(unwrappedPost.postInfo.sender.userId)
                    R.id.save_to_device -> optionsListener?.onSaveToDevice(unwrappedPost.content)
                    R.id.save_to_gallery -> optionsListener?.onSaveToGallery(
                        unwrappedPost.postInfo.roomId, unwrappedPost.id
                    )

                    R.id.report -> optionsListener?.onReport(
                        unwrappedPost.postInfo.roomId,
                        unwrappedPost.id
                    )
                    R.id.end_poll -> optionsListener?.endPoll(
                        unwrappedPost.postInfo.roomId, unwrappedPost.id
                    )
                }
                true
            }
            inflate(R.menu.timeline_item_menu)

            menu.findItem(R.id.save_to_device).isVisible = unwrappedPost.content.isMedia()
            menu.findItem(R.id.save_to_gallery).isVisible = unwrappedPost.content.isMedia()
            menu.findItem(R.id.ignore).isVisible = !unwrappedPost.isMyPost()
            menu.findItem(R.id.report).isVisible = !unwrappedPost.isMyPost()
            menu.findItem(R.id.delete).isVisible =
                unwrappedPost.isMyPost() || userPowerLevel >= Role.Moderator.value

            val pollState = (unwrappedPost.content as? PollContent)?.state
            menu.findItem(R.id.end_poll).isVisible =
                unwrappedPost.content.isPoll() && pollState != PollState.Ended &&
                        (unwrappedPost.isMyPost() || userPowerLevel >= Role.Moderator.value)

            show()
        }
    }

}