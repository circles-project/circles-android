package org.futo.circles.model

import org.futo.circles.core.list.IdEntity

data class MediaFolderListItem(
    override val id: Long,
    val displayName: String,
    val isSelected: Boolean = false
) : IdEntity<Long>