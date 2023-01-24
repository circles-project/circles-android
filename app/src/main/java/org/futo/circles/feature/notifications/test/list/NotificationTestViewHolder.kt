package org.futo.circles.feature.notifications.test.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ListItemNotificationTestBinding
import org.futo.circles.extensions.gone
import org.futo.circles.extensions.visible
import org.futo.circles.model.NotificationTestListItem
import org.futo.circles.model.NotificationTestStatus

class NotificationTestViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(inflate(parent, ListItemNotificationTestBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemNotificationTestBinding

    fun bind(data: NotificationTestListItem) {
        with(binding) {
            tvName.text = data.name
            tvMessage.text = data.message
            when (data.status) {
                NotificationTestStatus.RUNNING -> {
                    ivTestStatus.gone()
                    testProgress.visible()
                }
                else -> {
                    ivTestStatus.visible()
                    testProgress.gone()
                    ivTestStatus.setImageResource(
                        if (data.status == NotificationTestStatus.FAILURE) R.drawable.ic_error
                        else R.drawable.ic_check_circle
                    )
                }
            }
        }
    }
}