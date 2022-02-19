package com.futo.circles.ui.groups.list

import android.view.ViewGroup
import com.futo.circles.base.BaseRvAdapter
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class GroupsListAdapter(
    private val onGroupClicked: (RoomSummary) -> Unit
) : BaseRvAdapter<RoomSummary, GroupViewHolder>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupViewHolder = GroupViewHolder(
        parent = parent,
        onGroupClicked = { position -> getItem(position)?.let { onGroupClicked(it) } }
    )

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}