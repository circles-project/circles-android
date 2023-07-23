package org.futo.circles.core.picker.gallery.media.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.core.model.GalleryContentListItem

class GalleryMediaGridAdapter(
    private val isMultiSelect: Boolean,
    private val onMediaItemClicked: (item: GalleryContentListItem) -> Unit,
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<GalleryContentListItem, GridMediaItemViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (isMultiSelect)
        MultiSelectGalleryMediaItemViewHolder(
            parent,
            onItemClicked = { position -> onMediaItemClicked(getItem(position)) })
    else
        GalleryMediaItemViewHolder(
            parent,
            onItemClicked = { position, _ -> onMediaItemClicked(getItem(position)) }
        )

    override fun onBindViewHolder(holder: GridMediaItemViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }
}
