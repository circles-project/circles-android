package org.futo.circles.feature.circles.setup.list


import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.SetupCircleListItem

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