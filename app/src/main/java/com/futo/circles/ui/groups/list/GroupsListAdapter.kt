package com.futo.circles.ui.groups.list

import android.view.ViewGroup
import com.futo.circles.base.BaseRvAdapter
import org.matrix.android.sdk.api.session.group.model.GroupSummary

class GroupsListAdapter(
    private val onGroupClicked: (GroupSummary) -> Unit
) : BaseRvAdapter<GroupSummary, GroupViewHolder>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupViewHolder = GroupViewHolder(
        parent = parent,
        onGroupClicked = { position -> onGroupClicked(getItemAs(position)) }
    )

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItemAs(position))
    }

}