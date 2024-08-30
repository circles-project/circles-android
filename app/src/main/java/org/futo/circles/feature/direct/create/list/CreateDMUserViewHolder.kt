package org.futo.circles.feature.direct.create.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.databinding.ListItemCreateDmBinding

class CreateDMUserViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit,
    private val onStartDmClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemCreateDmBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemCreateDmBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
        onClick(binding.btnDirectMessages) { position ->
            binding.btnDirectMessages.setIsLoading(true)
            onStartDmClicked(position)
        }
    }

    fun bind(userItem: CirclesUserSummary) {
        with(binding.lUser) {
            tvUserName.text = userItem.name
            tvUserId.text = userItem.id
            ivUserImage.loadUserProfileIcon(userItem.avatarUrl, userItem.id)
        }
    }
}
