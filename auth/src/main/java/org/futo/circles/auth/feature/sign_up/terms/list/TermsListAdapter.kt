package org.futo.circles.auth.feature.sign_up.terms.list


import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.auth.model.TermsListItem

class TermsListAdapter(
    private val onViewTerms: (TermsListItem) -> Unit,
    private val onCheckChanged: (TermsListItem) -> Unit
) : BaseRvAdapter<TermsListItem, TermsItemViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TermsItemViewHolder = TermsItemViewHolder(
        parent = parent,
        onCheckChanged = { position -> onCheckChanged(getItem(position)) },
        onItemClicked = { position -> onViewTerms(getItem(position)) },
    )

    override fun onBindViewHolder(holder: TermsItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}