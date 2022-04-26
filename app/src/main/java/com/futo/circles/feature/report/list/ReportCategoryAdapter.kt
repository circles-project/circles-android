package com.futo.circles.feature.report.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.ReportCategoryListItem

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