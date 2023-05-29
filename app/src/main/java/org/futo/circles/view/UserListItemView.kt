package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.R
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.databinding.ViewUserListItemBinding
import org.futo.circles.extensions.setSelectableItemBackground
import org.futo.circles.model.CirclesUserSummary

class UserListItemView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewUserListItemBinding.inflate(LayoutInflater.from(context), this)

    fun bind(user: CirclesUserSummary) {
        with(binding) {
            tvUserName.text = user.name
            tvUserId.text = user.id
            ivUserImage.loadProfileIcon(user.avatarUrl, user.name)
        }
    }

    fun bindSelectable(user: CirclesUserSummary, isSelected: Boolean) {
        binding.tvUserName.text = user.name
        binding.tvUserId.text = user.id
        setIcon(user, isSelected)
    }

    private fun setIcon(user: CirclesUserSummary, isSelected: Boolean) {
        if (isSelected) {
            binding.ivUserImage.setImageResource(R.drawable.ic_check_circle)
            rootView.setBackgroundColor(context.getColor(R.color.highlight_color))
        } else {
            binding.ivUserImage.loadProfileIcon(user.avatarUrl, user.name)
            rootView.setSelectableItemBackground()
        }
    }
}