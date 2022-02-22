package com.futo.circles.ui.groups.list

import android.view.ViewGroup
import com.futo.circles.base.BaseRvAdapter
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class GroupsListAdapter(
    private val urlResolver: ContentUrlResolver?,
    private val onGroupClicked: (RoomSummary) -> Unit
) : BaseRvAdapter<RoomSummary, GroupViewHolder>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupViewHolder = GroupViewHolder(
        parent = parent,
        urlResolver = urlResolver,
        onGroupClicked = { position -> onGroupClicked(getItem(position)) }
    )

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}