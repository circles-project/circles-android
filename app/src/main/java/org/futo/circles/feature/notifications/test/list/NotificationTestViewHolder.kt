package org.futo.circles.feature.notifications.test.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.ListItemNotificationTestBinding
import org.futo.circles.extensions.gone
import org.futo.circles.extensions.onClick
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.extensions.visible
import org.futo.circles.model.NotificationTestListItem
import org.futo.circles.model.NotificationTestStatus

class NotificationTestViewHolder(
    parent: ViewGroup,
    onFixClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemNotificationTestBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemNotificationTestBinding

    init {
        onClick(binding.btnFix) { position -> onFixClicked(position) }
    }

    fun bind(data: NotificationTestListItem) {
        with(binding) {
            tvName.text = context.getString(data.titleId)
            tvMessage.text = data.message
            btnFix.setIsVisible(data.hasFix)
            when (data.status) {
                NotificationTestStatus.RUNNING -> {
                    ivTestStatus.gone()
                    testProgress.visible()
                }
                else -> {
                    ivTestStatus.visible()
                    testProgress.gone()
                    ivTestStatus.setImageResource(
                        if (data.status == NotificationTestStatus.FAILED) R.drawable.ic_error
                        else R.drawable.ic_check_circle
                    )
                }
            }
        }
    }
}