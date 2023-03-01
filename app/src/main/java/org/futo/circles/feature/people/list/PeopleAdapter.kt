package org.futo.circles.feature.people.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.feature.people.list.PeopleListViewType.*
import org.futo.circles.model.*

private enum class PeopleListViewType { Header, Following, Request, Suggestion, Ignored }

class PeopleAdapter(
    private val onUserClicked: (String) -> Unit,
    private val onFollow: (String) -> Unit,
    private val onUnIgnore: (String) -> Unit,
    private val onRequestClicked: (String, Boolean) -> Unit
) : BaseRvAdapter<PeopleListItem, PeopleViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PeopleHeaderItem -> Header.ordinal
        is PeopleFollowingUserListItem -> Following.ordinal
        is PeopleSuggestionUserListItem -> Suggestion.ordinal
        is PeopleIgnoredUserListItem -> Ignored.ordinal
        is PeopleRequestUserListItem -> Request.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        return when (values()[viewType]) {
            Header -> PeopleHeaderViewHolder(parent)
            Following -> PeopleFollowingUserViewHolder(
                parent,
                onUserClicked = { position -> onUserClicked(getItem(position).id) }
            )
            Request -> PeopleRequestUserViewHolder(
                parent,
                onRequestClicked = { position, isAccepted ->
                    onRequestClicked(getItem(position).id, isAccepted)
                }
            )
            Suggestion -> PeopleSuggestionUserViewHolder(
                parent,
                onFollow = { position -> onFollow(getItem(position).id) },
                onUserClicked = { position -> onUserClicked(getItem(position).id) }
            )
            Ignored -> PeopleIgnoredUserViewHolder(
                parent,
                onUnIgnore = { position -> onUnIgnore(getItem(position).id) }
            )
        }
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}