package org.futo.circles.feature.direct.timeline.list.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.model.DmTimelineListItem

abstract class DmTimelineListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: DmTimelineListItem)
}