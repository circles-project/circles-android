package org.futo.circles.gallery.feature.backup.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.gallery.model.MediaFolderListItem

class MediaFoldersListAdapter(
    private val onItemCheckChanged: (String, Boolean) -> Unit,
) : BaseRvAdapter<MediaFolderListItem, MediaFolderViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MediaFolderViewHolder(parent,
            onCheckChanged = { position, isSelected ->
                onItemCheckChanged(getItem(position).id, isSelected)
            }
        )

    override fun onBindViewHolder(holder: MediaFolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}