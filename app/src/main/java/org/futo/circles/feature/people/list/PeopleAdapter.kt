package org.futo.circles.feature.people.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.model.PeopleCategoryListItem
import org.futo.circles.model.PeopleCategoryTypeArg
import org.futo.circles.model.PeopleIgnoredUserListItem
import org.futo.circles.model.PeopleListItem
import org.futo.circles.model.PeopleRequestNotificationListItem
import org.futo.circles.model.PeopleUserListItem
import org.futo.circles.model.PeopleUserListItemPayload

enum class PeopleItemViewType { Category, Default, RequestNotification, Ignored }

class PeopleAdapter(
    private val onUserClicked: (String) -> Unit,
    private val onOpenRequestsClicked: () -> Unit = {},
    private val onCategoryClicked: (PeopleCategoryTypeArg) -> Unit = {},
    private val onUnIgnore: (String) -> Unit = {},
) : BaseRvAdapter<PeopleListItem, PeopleViewHolder>(PayloadIdEntityCallback { old, new ->
    if (new is PeopleUserListItem && old is PeopleUserListItem) {
        if (new.isIgnored != old.isIgnored) null
        else PeopleUserListItemPayload(user = new.user.takeIf { it != old.user }, null)
    } else if (new is PeopleCategoryListItem && old is PeopleCategoryListItem) {
        new.count.takeIf { it != old.count }?.let { PeopleUserListItemPayload(null, it) }
    } else null
}) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PeopleCategoryListItem -> PeopleItemViewType.Category.ordinal
        is PeopleIgnoredUserListItem -> PeopleItemViewType.Ignored.ordinal
        is PeopleRequestNotificationListItem -> PeopleItemViewType.RequestNotification.ordinal
        is PeopleUserListItem -> PeopleItemViewType.Default.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        return when (PeopleItemViewType.entries[viewType]) {
            PeopleItemViewType.Category -> PeopleCategoryViewHolder(
                parent = parent, onClicked = { position ->
                    (getItem(position) as? PeopleCategoryListItem)?.let {
                        onCategoryClicked(it.typeArg)
                    }
                }
            )

            PeopleItemViewType.RequestNotification -> FollowRequestNotificationViewHolder(
                parent = parent, onClicked = { onOpenRequestsClicked() }
            )

            PeopleItemViewType.Ignored -> IgnoredUsersViewHolder(
                parent,
                onUnIgnore = { position -> onUnIgnore(getItem(position).id) }
            )

            PeopleItemViewType.Default -> PeopleDefaultUserViewHolder(
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