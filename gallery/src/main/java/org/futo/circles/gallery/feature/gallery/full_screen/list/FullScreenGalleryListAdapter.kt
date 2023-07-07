package org.futo.circles.gallery.feature.gallery.full_screen.list

import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayer
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.core.model.PostContentType
import org.futo.circles.gallery.model.GalleryContentListItem


class FullScreenGalleryListAdapter(
    private val videoPlayer: ExoPlayer,
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<GalleryContentListItem, FullScreenGalleryItemViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int) = getItem(position).mediaContent.type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == PostContentType.VIDEO_CONTENT.ordinal)
            FullScreenVideoItemViewHolder(parent, videoPlayer)
        else FullScreenImageItemViewHolder(parent)

    override fun onBindViewHolder(holder: FullScreenGalleryItemViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }
}
