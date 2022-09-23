package org.futo.circles.feature.photos.gallery.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.GalleryContentListItem
import org.futo.circles.model.PostContentType

class GalleryItemsAdapter(
    private val onGalleryItemClicked: (item: GalleryContentListItem) -> Unit,
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<GalleryContentListItem, GalleryContentViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryContentViewHolder =
        when (PostContentType.values()[viewType]) {
            PostContentType.IMAGE_CONTENT -> GalleryImageViewHolder(
                parent,
                onItemClicked = { position -> onGalleryItemClicked(getItem(position)) }
            )
            PostContentType.VIDEO_CONTENT -> GalleryVideoViewHolder(
                parent,
                onItemClicked = { position -> onGalleryItemClicked(getItem(position)) }
            )
            else -> throw IllegalArgumentException("Wrong view type")
        }


    override fun onBindViewHolder(holder: GalleryContentViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }
}
