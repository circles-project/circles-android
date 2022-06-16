package org.futo.circles.feature.people.user.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.JoinedCircleListItem

class UsersCirclesAdapter() :
    BaseRvAdapter<JoinedCircleListItem, UsersCircleViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersCircleViewHolder = UsersCircleViewHolder(parent)


    override fun onBindViewHolder(holder: UsersCircleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}