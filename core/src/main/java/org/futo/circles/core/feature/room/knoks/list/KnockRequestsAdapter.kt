package org.futo.circles.core.feature.room.knoks.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.KnockRequestListItem

class KnockRequestsAdapter(
    private val onRequestClicked: (KnockRequestListItem, Boolean) -> Unit
) : BaseRvAdapter<KnockRequestListItem, KnockRequestViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = KnockRequestViewHolder(
        parent = parent,
        onRequestClicked = { position, isAccepted ->
            onRequestClicked(getItem(position), isAccepted)
        }
    )

    override fun onBindViewHolder(holder: KnockRequestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}