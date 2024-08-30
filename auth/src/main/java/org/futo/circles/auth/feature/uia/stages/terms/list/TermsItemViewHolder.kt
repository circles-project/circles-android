package org.futo.circles.auth.feature.uia.stages.terms.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.auth.databinding.ListItemTermsBinding
import org.futo.circles.auth.model.TermsListItem
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.onClick

class TermsItemViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemTermsBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemTermsBinding

    init {
        onClick(itemView) { position -> onItemClicked(position) }
    }

    fun bind(data: TermsListItem) {
        with(binding) {
            tvName.text = data.name
            tvUrl.text = data.url
        }
    }

}