package org.futo.circles.gallery.feature.gallery.full_screen.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.gallery.model.GalleryContentListItem


class FullScreenGalleryListAdapter(
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<GalleryContentListItem, FullScreenGalleryItemViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FullScreenGalleryItemViewHolder(parent)

    override fun onBindViewHolder(holder: FullScreenGalleryItemViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }
}
