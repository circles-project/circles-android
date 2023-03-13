package org.futo.circles.feature.people.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.PeopleItemType
import org.futo.circles.model.PeopleListItem

class PeopleAdapter(
    private val onUserClicked: (String) -> Unit,
    private val onUnIgnore: (String) -> Unit,
    private val onRequestClicked: (String, Boolean) -> Unit
) : BaseRvAdapter<PeopleListItem, PeopleViewHolder>(DefaultIdEntityCallback()) {

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
            PeopleItemType.Ignored -> PeopleIgnoredUserViewHolder(
                parent,
                onUnIgnore = { position -> onUnIgnore(getItem(position).id) }
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

}