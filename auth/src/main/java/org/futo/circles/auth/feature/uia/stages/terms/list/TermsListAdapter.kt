package org.futo.circles.auth.feature.uia.stages.terms.list


import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.auth.model.TermsListItem

class TermsListAdapter(
    private val onViewTerms: (TermsListItem) -> Unit
) : BaseRvAdapter<TermsListItem, TermsItemViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TermsItemViewHolder = TermsItemViewHolder(
        parent = parent,
        onItemClicked = { position -> onViewTerms(getItem(position)) },
    )

    override fun onBindViewHolder(holder: TermsItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}