package org.futo.circles.auth.feature.sign_up.terms.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.auth.databinding.ListItemTermsBinding
import org.futo.circles.core.extensions.onClick
import org.futo.circles.auth.model.TermsListItem

class TermsItemViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int) -> Unit,
    onCheckChanged: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemTermsBinding::inflate)) {

    private companion object : org.futo.circles.core.list.ViewBindingHolder

    private val binding = baseBinding as ListItemTermsBinding

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