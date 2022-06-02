package com.futo.circles.feature.photos.gallery.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.GalleryImageListItem

class GalleryImagesAdapter(
    private val onGalleryImageClicked: (postId: String) -> Unit,
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<GalleryImageListItem, GalleryImageViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryImageViewHolder =
        GalleryImageViewHolder(
            parent,
            onItemClicked = { position -> onGalleryImageClicked(getItem(position).id) }
        )


    override fun onBindViewHolder(holder: GalleryImageViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }
}
