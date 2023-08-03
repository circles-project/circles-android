package org.futo.circles.core.picker.gallery.media

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import org.futo.circles.core.model.GalleryContentListItem
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.picker.gallery.PickGalleryMediaDialogFragment.Companion.IS_VIDEO_AVAILABLE
import org.futo.circles.core.timeline.BaseTimelineViewModel
import org.futo.circles.core.timeline.data_source.BaseTimelineDataSource
import javax.inject.Inject

@HiltViewModel
class PickMediaItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    timelineDataSource: BaseTimelineDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    private val isVideoAvailable: Boolean = savedStateHandle[IS_VIDEO_AVAILABLE] ?: true

    private val selectedItemsFlow = MutableStateFlow<List<GalleryContentListItem>>(emptyList())

    val galleryItemsLiveData = combine(
        timelineDataSource.timelineEventsLiveData.asFlow(),
        selectedItemsFlow
    ) { items, selectedIds ->
        items.mapNotNull { post ->
            (post.content as? MediaContent)?.let {
                if (it.type == PostContentType.VIDEO_CONTENT && !isVideoAvailable) null
                else GalleryContentListItem(
                    post.id, post.postInfo, it, selectedIds.firstOrNull { it.id == post.id } != null
                )
            }
        }
    }.distinctUntilChanged().asLiveData()


    fun toggleItemSelected(item: GalleryContentListItem) {
        val list = selectedItemsFlow.value.toMutableList()
        if (item.isSelected) list.removeIf { it.id == item.id }
        else list.add(item)
        selectedItemsFlow.value = list
    }
}