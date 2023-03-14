package org.futo.circles.feature.photos.backup.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.MediaFolderListItem

class MediaFoldersListAdapter(
    private val onItemCheckChanged: (Long) -> Unit,
) : BaseRvAdapter<MediaFolderListItem, MediaFolderViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MediaFolderViewHolder(parent,
            onCheckChanged = { position -> onItemCheckChanged(getItemId(position)) }
        )

    override fun onBindViewHolder(holder: MediaFolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}