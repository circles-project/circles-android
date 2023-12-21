package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.databinding.ViewPeopleTabUserListItemBinding

class PeopleTabUserListItemView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    val binding = ViewPeopleTabUserListItemBinding.inflate(LayoutInflater.from(context), this)

    fun bind(user: CirclesUserSummary) {
        with(binding) {
            tvUserName.text = user.name
            tvUserId.text = user.id
            ivUserImage.loadUserProfileIcon(user.avatarUrl, user.id)
        }
    }
}