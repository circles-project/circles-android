package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity

sealed class GalleryTimelineListItem : IdEntity<String>

data class GalleryContentListItem(
    override val id: String,
    val postInfo: PostInfo,
    val mediaContent: MediaContent,
    val isSelected: Boolean = false
) : GalleryTimelineListItem()

data class GalleryTimelineLoadingListItem(
    override val id: String = "GalleryTimelineLoadingListItem"
) : GalleryTimelineListItem()