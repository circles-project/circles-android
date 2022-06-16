package org.futo.circles.feature.timeline.post.report.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.ReportCategoryListItem

class ReportCategoryAdapter(
    private val onCategorySelected: (id: Int) -> Unit
) : BaseRvAdapter<ReportCategoryListItem, ReportCategoryViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportCategoryViewHolder = ReportCategoryViewHolder(
        parent = parent,
        onCategorySelected = { position -> onCategorySelected(getItem(position).id) }
    )

    override fun onBindViewHolder(holder: ReportCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}