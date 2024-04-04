package org.futo.circles.core.feature.picker.gallery.media.list

import android.view.View
import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.GalleryContentListItem

class GalleryMediaGridAdapter(
    private val isMultiSelect: Boolean,
    private val onMediaItemClicked: (item: GalleryContentListItem, transitionView: View, position: Int) -> Unit,
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<GalleryContentListItem, GridMediaItemViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (isMultiSelect)
        MultiSelectGalleryMediaItemViewHolder(
            parent,
            onItemClicked = { position, view ->
                onMediaItemClicked(getItem(position), view, position)
            })
    else
        GalleryMediaItemViewHolder(
            parent,
            onItemClicked = { position, view ->
                onMediaItemClicked(getItem(position), view, position)
            }
        )

    override fun onBindViewHolder(holder: GridMediaItemViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }
}
