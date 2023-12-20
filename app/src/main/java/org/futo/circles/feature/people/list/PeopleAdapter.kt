package org.futo.circles.feature.people.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.model.PeopleItemType
import org.futo.circles.model.PeopleListItem
import org.futo.circles.model.PeopleUserListItem
import org.futo.circles.model.PeopleUserListItemPayload

class PeopleAdapter(
    private val onUserClicked: (String) -> Unit,
    private val onRequestClicked: (String, Boolean) -> Unit
) : BaseRvAdapter<PeopleListItem, PeopleViewHolder>(PayloadIdEntityCallback { old, new ->
    if (new is PeopleUserListItem && old is PeopleUserListItem) {
        PeopleUserListItemPayload(user = new.user.takeIf { it != old.user })
    } else null
}) {

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        return when (PeopleItemType.values()[viewType]) {
            PeopleItemType.Header -> PeopleHeaderViewHolder(parent)
            PeopleItemType.Request -> PeopleRequestUserViewHolder(
                parent,
                onRequestClicked = { position, isAccepted ->
                    onRequestClicked(getItem(position).id, isAccepted)
                }
            )
            else -> PeopleDefaultUserViewHolder(
                parent,
                onUserClicked = { position -> onUserClicked(getItem(position).id) }
            )
        }
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: PeopleViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach { payload ->
                (payload as? PeopleUserListItemPayload)?.let { holder.bindPayload(payload) }
            }
        }
    }

}