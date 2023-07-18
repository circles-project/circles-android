package org.futo.circles.core.notices.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.databinding.ListItemSystemNoticesBinding
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.model.SystemNoticeListItem
import java.text.DateFormat
import java.util.Date

class SystemNoticesViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(inflate(parent, ListItemSystemNoticesBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemSystemNoticesBinding

    fun bind(data: SystemNoticeListItem) {
        with(binding) {
            tvMessage.text = data.message
            tvTime.text = DateFormat.getDateTimeInstance().format(Date(data.time))
        }
    }
}