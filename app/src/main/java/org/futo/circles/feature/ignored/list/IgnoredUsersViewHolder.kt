package org.futo.circles.feature.ignored.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.databinding.ListItemPeopleIgnoredBinding

class IgnoredUsersViewHolder(
    parent: ViewGroup,
    private val onUnIgnore: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemPeopleIgnoredBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleIgnoredBinding

    init {
        onClick(binding.btnUnIgnore) { position -> onUnIgnore(position) }
    }

    fun bind(data: CirclesUserSummary) {
        binding.userItem.bind(data)
    }
}