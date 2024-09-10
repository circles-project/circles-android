package org.futo.circles.feature.direct.timeline.list.holder

import android.text.format.DateFormat
import android.view.ViewGroup
import org.futo.circles.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.utils.DateUtils
import org.futo.circles.databinding.ListItemDmDateHeaderBinding
import org.futo.circles.model.DmTimelineListItem
import org.futo.circles.model.DmTimelineTimeHeaderItem
import java.util.Date

class DmDateHeaderViewHolder(
    parent: ViewGroup
) : DmTimelineListItemViewHolder(
    inflate(parent, ListItemDmDateHeaderBinding::inflate)
) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemDmDateHeaderBinding

    override fun bind(item: DmTimelineListItem) {
        val time = (item as? DmTimelineTimeHeaderItem)?.date ?: return
        val timeString = if (DateUtils.isToday(time)) {
            context.getString(R.string.today)
        } else {
            val format = if (DateUtils.isCurrentYear(time)) "MMM dd"
            else "MMM dd, yyyy"

            DateFormat.format(format, Date(time))
        }

        binding.tvTime.text = timeString
    }

}