package com.futo.circles.feature.terms.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.databinding.TermsListItemBinding
import com.futo.circles.extensions.onClick
import com.futo.circles.model.TermsListItem

class TermsItemViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int) -> Unit,
    onCheckChanged: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, TermsListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as TermsListItemBinding

    init {
        onClick(itemView) { position -> onItemClicked(position) }
        onClick(binding.termsCheck) { position -> onCheckChanged(position) }
    }

    fun bind(data: TermsListItem) {
        with(binding) {
            termsCheck.isChecked = data.isChecked
            tvName.text = data.name
            tvUrl.text = data.url
        }
    }

}