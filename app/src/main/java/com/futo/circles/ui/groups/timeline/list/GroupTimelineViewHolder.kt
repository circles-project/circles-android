package com.futo.circles.ui.groups.timeline.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.base.ViewBindingHolder
import com.futo.circles.databinding.GroupPostListItemBinding
import com.futo.circles.ui.groups.timeline.model.GroupMessage
import org.matrix.android.sdk.api.session.content.ContentUrlResolver

class GroupTimelineViewHolder(
    parent: ViewGroup,
    private val urlResolver: ContentUrlResolver?
) :
    RecyclerView.ViewHolder(inflate(parent, GroupPostListItemBinding::inflate)) {

    private companion object : ViewBindingHolder<GroupPostListItemBinding>

    fun bind(data: GroupMessage) {
        binding.postView.setData(data, urlResolver)
    }
}