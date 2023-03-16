package org.futo.circles.model

import org.futo.circles.core.list.IdEntity

data class MediaFolderListItem(
    val displayName: String,
    val path: String,
    val size: Long,
    val isSelected: Boolean
) : IdEntity<String> {
    override val id: String = path
}