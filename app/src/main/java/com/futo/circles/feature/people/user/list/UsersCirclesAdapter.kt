package com.futo.circles.feature.people.user.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.JoinedCircleListItem

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