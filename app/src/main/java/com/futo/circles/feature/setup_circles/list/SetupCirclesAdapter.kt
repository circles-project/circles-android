package com.futo.circles.feature.setup_circles.list


import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.SetupCircleListItem

class SetupCirclesAdapter(
    private val onCircleClicked: (SetupCircleListItem) -> Unit
) : BaseRvAdapter<SetupCircleListItem, SetupCirclesViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SetupCirclesViewHolder = SetupCirclesViewHolder(
        parent = parent,
        onCircleClicked = { position -> onCircleClicked(getItem(position)) }
    )

    override fun onBindViewHolder(holder: SetupCirclesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}