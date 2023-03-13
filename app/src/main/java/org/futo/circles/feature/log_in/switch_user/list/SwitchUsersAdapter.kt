package org.futo.circles.feature.log_in.switch_user.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.SwitchUserListItem

class SwitchUsersAdapter(
    private val onResumeClicked: (String) -> Unit,
    private val onRemoveClicked: (String) -> Unit
) : BaseRvAdapter<SwitchUserListItem, SwitchUsersViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SwitchUsersViewHolder = SwitchUsersViewHolder(
        parent = parent,
        onUserClicked = { position -> onResumeClicked(getItem(position).id) },
        onRemoveClicked = { position -> onRemoveClicked(getItem(position).id) },
    )

    override fun onBindViewHolder(holder: SwitchUsersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}