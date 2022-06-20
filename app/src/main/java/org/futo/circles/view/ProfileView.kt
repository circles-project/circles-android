package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.databinding.ProfileViewBinding
import org.futo.circles.extensions.loadProfileIcon
import org.futo.circles.mapping.notEmptyDisplayName
import org.matrix.android.sdk.api.session.user.model.User

class ProfileView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ProfileViewBinding.inflate(LayoutInflater.from(context), this)


    fun setData(user: User) {
        with(binding) {
            ivProfile.loadProfileIcon(user.avatarUrl, user.notEmptyDisplayName())
            tvUserName.text = user.notEmptyDisplayName()
            tvUserId.text = user.userId
        }
    }
}