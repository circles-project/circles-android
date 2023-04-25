package org.futo.circles.model

import org.futo.circles.core.list.IdEntity

data class MediaFolderListItem(
    override val id: String,
    val displayName: String,
    val size: Long,
    val isSelected: Boolean = false
) : IdEntity<String>