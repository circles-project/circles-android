package com.futo.circles.feature.report.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.databinding.ReportCategoryListItemBinding
import com.futo.circles.extensions.onClick
import com.futo.circles.model.ReportCategoryListItem

class ReportCategoryViewHolder(
    parent: ViewGroup,
    onCategorySelected: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ReportCategoryListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ReportCategoryListItemBinding

    init {
        onClick(binding.lItem) { position -> onCategorySelected(position) }
    }

    fun bind(data: ReportCategoryListItem) {
        binding.radio.text = data.name
        binding.radio.isChecked = data.isSelected
    }
}