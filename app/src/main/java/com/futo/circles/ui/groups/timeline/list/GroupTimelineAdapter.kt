package com.futo.circles.ui.groups.timeline.list

import android.view.ViewGroup
import com.futo.circles.base.BaseRvAdapter
import com.futo.circles.ui.groups.timeline.model.GroupMessage
import org.matrix.android.sdk.api.session.content.ContentUrlResolver

class GroupTimelineAdapter(
    private val urlResolver: ContentUrlResolver?,
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<GroupMessage, GroupTimelineViewHolder>(DefaultDiffUtilCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupTimelineViewHolder {
        return GroupTimelineViewHolder(parent, urlResolver)
    }

    override fun onBindViewHolder(holder: GroupTimelineViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }

}