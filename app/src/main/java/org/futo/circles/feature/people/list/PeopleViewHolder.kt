package org.futo.circles.feature.people.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemPeopleCategoryBinding
import org.futo.circles.core.databinding.ListItemPeopleDefaultBinding
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.databinding.ListItemPeopleIgnoredBinding
import org.futo.circles.model.PeopleCategoryListItem
import org.futo.circles.model.PeopleIgnoredUserListItem
import org.futo.circles.model.PeopleListItem
import org.futo.circles.model.PeopleUserListItem
import org.futo.circles.model.PeopleUserListItemPayload

abstract class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: PeopleListItem)
    open fun bindPayload(data: PeopleUserListItemPayload) {}
}

class PeopleDefaultUserViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit
) : PeopleViewHolder(inflate(parent, ListItemPeopleDefaultBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleDefaultBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
    }

    override fun bind(data: PeopleListItem) {
        val userItem = (data as? PeopleUserListItem) ?: return
        if (userItem.isIgnored) setUnBlurClick(userItem)
        with(binding) {
            tvUserName.text = userItem.user.name
            tvUserId.text = userItem.user.id
            ivUserImage.loadUserProfileIcon(
                userItem.user.avatarUrl,
                userItem.user.id,
                applyBlur = userItem.isIgnored
            )
            tvIgnoredLabel.setIsVisible(userItem.isIgnored)
            tvShowProfileImage.setIsVisible(userItem.isIgnored)
        }
    }

    override fun bindPayload(data: PeopleUserListItemPayload) {
        with(binding) {
            data.user?.let {
                tvUserName.text = it.name
                tvUserId.text = it.id
                ivUserImage.loadUserProfileIcon(it.avatarUrl, it.id)
            }
        }
    }

    private fun setUnBlurClick(userItem: PeopleUserListItem) {
        binding.ivUserImage.setOnClickListener {
            binding.ivUserImage.loadUserProfileIcon(
                userItem.user.avatarUrl,
                userItem.user.id,
                applyBlur = false
            )
            binding.tvShowProfileImage.gone()
        }
    }
}

class IgnoredUsersViewHolder(
    parent: ViewGroup,
    private val onUnIgnore: (Int) -> Unit
) : PeopleViewHolder(inflate(parent, ListItemPeopleIgnoredBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleIgnoredBinding

    init {
        onClick(binding.btnUnIgnore) { position -> onUnIgnore(position) }
    }

    override fun bind(data: PeopleListItem) {
        val userItem = (data as? PeopleIgnoredUserListItem) ?: return
        with(binding) {
            tvUserName.text = userItem.user.name
            tvUserId.text = userItem.user.id
            ivUserImage.loadUserProfileIcon(userItem.user.avatarUrl, userItem.user.id)
        }
    }

    override fun bindPayload(data: PeopleUserListItemPayload) {
        with(binding) {
            data.user?.let {
                tvUserName.text = it.name
                tvUserId.text = it.id
                ivUserImage.loadUserProfileIcon(it.avatarUrl, it.id)
            }
        }
    }

}

class PeopleCategoryViewHolder(
    parent: ViewGroup,
    onClicked: (Int) -> Unit
) : PeopleViewHolder(inflate(parent, ListItemPeopleCategoryBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleCategoryBinding

    init {
        onClick(itemView) { position -> onClicked(position) }
    }

    override fun bind(data: PeopleListItem) {
        if (data !is PeopleCategoryListItem) return
        with(binding) {
            tvCategoryName.text = context.getString(data.titleRes)
            ivCategoryIcon.setImageResource(data.iconRes)
            tvCount.text = data.count.toString()
        }
    }

    override fun bindPayload(data: PeopleUserListItemPayload) {
        binding.tvCount.text = data.categoryCount.toString()
    }
}
