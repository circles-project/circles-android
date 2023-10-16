package org.futo.circles.gallery.model

import org.futo.circles.core.base.list.IdEntity

data class MediaFolderListItem(
    override val id: String,
    val displayName: String,
    val size: Long,
    val isSelected: Boolean = false
) : IdEntity<String>