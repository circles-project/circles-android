package org.futo.circles.feature.timeline.post.report.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.databinding.ListItemReportCategoryBinding
import org.futo.circles.model.ReportCategoryListItem

class ReportCategoryViewHolder(
    parent: ViewGroup,
    onCategorySelected: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemReportCategoryBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemReportCategoryBinding

    init {
        onClick(binding.lItem) { position -> onCategorySelected(position) }
    }

    fun bind(data: ReportCategoryListItem) {
        binding.radio.text = data.name
        binding.radio.isChecked = data.isSelected
    }
}