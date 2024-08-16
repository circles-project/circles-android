package org.futo.circles.settings.feature.ignored_users.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.CirclesUserSummary

class IgnoredUsersAdapter(
    private val onUnIgnore: (String) -> Unit = {}
) : BaseRvAdapter<CirclesUserSummary, IgnoredUserViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IgnoredUserViewHolder =
        IgnoredUserViewHolder(
            parent,
            onUnIgnore = { position -> onUnIgnore(getItem(position).id) }
        )

    override fun onBindViewHolder(holder: IgnoredUserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}