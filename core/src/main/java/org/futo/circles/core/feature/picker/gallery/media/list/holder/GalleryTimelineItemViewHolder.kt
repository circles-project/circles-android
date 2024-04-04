package org.futo.circles.core.feature.picker.gallery.media.list.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.model.GalleryTimelineListItem

abstract class GalleryTimelineItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: GalleryTimelineListItem)

}