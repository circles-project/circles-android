package org.futo.circles.feature.notices.list


import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.SystemNoticeListItem

class SystemNoticesTimelineAdapter(
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<SystemNoticeListItem, SystemNoticesViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SystemNoticesViewHolder {
        return SystemNoticesViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SystemNoticesViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
    }


    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }

}