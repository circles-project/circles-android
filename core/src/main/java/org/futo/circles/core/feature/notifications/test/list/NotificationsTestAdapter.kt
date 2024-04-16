package org.futo.circles.core.feature.notifications.test.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.NotificationTestListItem

class NotificationsTestAdapter(
    private val onFixClicked: (Int) -> Unit,
) : BaseRvAdapter<NotificationTestListItem, NotificationTestViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationTestViewHolder = NotificationTestViewHolder(
        parent,
        onFixClicked = { position -> onFixClicked(getItem(position).id) }
    )

    override fun onBindViewHolder(holder: NotificationTestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}