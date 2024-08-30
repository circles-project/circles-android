package org.futo.circles.feature.direct.create.list


import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.CirclesUserSummary

class CreateDMListAdapter(
    private val onUserClicked: (String) -> Unit,
    private val onStartDmClicked: (String) -> Unit
) : BaseRvAdapter<CirclesUserSummary, CreateDMUserViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateDMUserViewHolder {
        return CreateDMUserViewHolder(
            parent,
            onUserClicked = { position -> onUserClicked(getItem(position).id) },
            onStartDmClicked = { position -> onStartDmClicked(getItem(position).id) },
        )
    }


    override fun onBindViewHolder(holder: CreateDMUserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}