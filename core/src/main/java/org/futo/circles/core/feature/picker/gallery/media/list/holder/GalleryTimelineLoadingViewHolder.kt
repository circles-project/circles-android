package org.futo.circles.core.feature.picker.gallery.media.list.holder

import android.view.ViewGroup
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.databinding.ListItemTimelineLoadingBinding
import org.futo.circles.core.model.GalleryTimelineListItem
import org.futo.circles.core.model.GalleryTimelineLoadingListItem


class GalleryTimelineLoadingViewHolder(
    parent: ViewGroup,
) : GalleryTimelineItemViewHolder(inflate(parent, ListItemTimelineLoadingBinding::inflate)) {

    private companion object : ViewBindingHolder

    override fun bind(item: GalleryTimelineListItem) {
        if (item !is GalleryTimelineLoadingListItem) return
    }
}