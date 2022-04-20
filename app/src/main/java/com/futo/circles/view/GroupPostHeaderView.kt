package com.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.R
import com.futo.circles.databinding.GroupPostHeaderViewBinding
import com.futo.circles.extensions.getAttributes
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.setIsVisible
import org.matrix.android.sdk.api.session.room.sender.SenderInfo

class GroupPostHeaderView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        GroupPostHeaderViewBinding.inflate(LayoutInflater.from(context), this)

    init {
        getAttributes(attrs, R.styleable.GroupPostHeaderView) {
            val isMenuVisible = getBoolean(R.styleable.GroupPostHeaderView_optionsVisible, true)
            binding.btnMore.setIsVisible(isMenuVisible)
        }
    }

    fun setData(sender: SenderInfo) {
        setData(sender.userId, sender.disambiguatedDisplayName, sender.avatarUrl)
    }

    fun setData(userId: String, displayName: String, avatarUrl: String?) {
        with(binding) {
            ivSenderImage.loadProfileIcon(avatarUrl, displayName)
            tvUserName.text = displayName
            tvUserId.text = userId
        }
    }

}