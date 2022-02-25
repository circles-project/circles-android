package com.futo.circles.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Size
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.databinding.GroupPostHeaderViewBinding
import com.futo.circles.extensions.loadImage
import org.matrix.android.sdk.api.session.room.sender.SenderInfo

class GroupPostHeaderView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        GroupPostHeaderViewBinding.inflate(LayoutInflater.from(context), this)

    fun setData(sender: SenderInfo) {
        with(binding) {
            ivSenderImage.loadImage(
                sender.avatarUrl,
                Size(ivSenderImage.width, ivSenderImage.height)
            )
            tvUserName.text = sender.disambiguatedDisplayName
            tvUserId.text = sender.userId
        }
    }

}