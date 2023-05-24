package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.databinding.ViewPeopleTabUserListItemBinding
import org.futo.circles.extensions.loadProfileIcon
import org.futo.circles.model.CirclesUserSummary

class PeopleTabUserListItemView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    val binding = ViewPeopleTabUserListItemBinding.inflate(LayoutInflater.from(context), this)

    fun bind(user: CirclesUserSummary) {
        with(binding) {
            tvUserName.text = user.name
            tvUserId.text = user.id
            ivUserImage.loadProfileIcon(user.avatarUrl, user.name)
        }
    }
}