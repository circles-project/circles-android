package org.futo.circles.core.feature.circles.filter.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.FilterTimelinesListItem

class FilterTimelinesAdapter(
    private val onItemSelected: (String) -> Unit
) : BaseRvAdapter<FilterTimelinesListItem, FilterTimelinesViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterTimelinesViewHolder = FilterTimelinesViewHolder(
        parent = parent,
        onItemClicked = { position -> onItemSelected(getItem(position).id) }
    )

    override fun onBindViewHolder(holder: FilterTimelinesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}