package com.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.R
import com.futo.circles.databinding.GroupPostHeaderViewBinding
import com.futo.circles.extensions.getAttributes
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.setEnabledChildren
import org.matrix.android.sdk.api.session.room.sender.SenderInfo

class GroupPostHeaderView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        GroupPostHeaderViewBinding.inflate(LayoutInflater.from(context), this)

    init {
        getAttributes(attrs, R.styleable.GroupPostHeaderView) {
            val isEnabled = getBoolean(R.styleable.GroupPostHeaderView_android_enabled, true)
            setEnabledChildren(isEnabled)
        }
    }


    fun setData(sender: SenderInfo) {
        with(binding) {
            ivSenderImage.loadProfileIcon(sender.avatarUrl, sender.displayName ?: "")
            tvUserName.text = sender.disambiguatedDisplayName
            tvUserId.text = sender.userId
        }
    }

}