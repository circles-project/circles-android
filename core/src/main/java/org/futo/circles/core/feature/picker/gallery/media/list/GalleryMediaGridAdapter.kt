package org.futo.circles.core.feature.picker.gallery.media.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.feature.picker.gallery.media.list.holder.GalleryMediaItemViewHolder
import org.futo.circles.core.feature.picker.gallery.media.list.holder.GalleryTimelineItemViewHolder
import org.futo.circles.core.feature.picker.gallery.media.list.holder.GalleryTimelineLoadingViewHolder
import org.futo.circles.core.feature.picker.gallery.media.list.holder.MultiSelectGalleryMediaItemViewHolder
import org.futo.circles.core.model.GalleryContentListItem
import org.futo.circles.core.model.GalleryTimelineListItem
import org.futo.circles.core.model.GalleryTimelineLoadingListItem


private enum class GalleryTimelineItemViewType { MEDIA, LOADING }

class GalleryMediaGridAdapter(
    private val isMultiSelect: Boolean,
    private val onMediaItemClicked: (item: GalleryContentListItem, transitionView: View, position: Int) -> Unit
) : BaseRvAdapter<GalleryTimelineListItem, GalleryTimelineItemViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is GalleryContentListItem -> GalleryTimelineItemViewType.MEDIA.ordinal
        is GalleryTimelineLoadingListItem -> GalleryTimelineItemViewType.LOADING.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (GalleryTimelineItemViewType.entries[viewType]) {
            GalleryTimelineItemViewType.MEDIA -> if (isMultiSelect)
                MultiSelectGalleryMediaItemViewHolder(
                    parent,
                    onItemClicked = { position, view ->
                        (getItem(position) as? GalleryContentListItem)?.let {
                            onMediaItemClicked(it, view, position)
                        }
                    })
            else
                GalleryMediaItemViewHolder(
                    parent,
                    onItemClicked = { position, view ->
                        (getItem(position) as? GalleryContentListItem)?.let {
                            onMediaItemClicked(it, view, position)
                        }
                    }
                )

            GalleryTimelineItemViewType.LOADING -> GalleryTimelineLoadingViewHolder(parent)
        }


    override fun onBindViewHolder(holder: GalleryTimelineItemViewHolder, position: Int) {
        holder.bind(getItem(position))
        val layoutParams = (holder as? GalleryTimelineLoadingViewHolder)?.itemView?.layoutParams
        (layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.isFullSpan = true
    }
}
