package org.futo.circles.feature.notices.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.SystemNoticesListItemBinding
import org.futo.circles.model.SystemNoticeListItem
import java.text.DateFormat
import java.util.*

class SystemNoticesViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(inflate(parent, SystemNoticesListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as SystemNoticesListItemBinding

    fun bind(data: SystemNoticeListItem) {
        with(binding) {
            tvMessage.text = data.message
            tvTime.text = DateFormat.getDateTimeInstance().format(Date(data.time))
        }
    }
}