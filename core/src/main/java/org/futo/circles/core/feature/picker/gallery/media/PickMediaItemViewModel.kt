package org.futo.circles.core.feature.picker.gallery.media

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import org.futo.circles.core.feature.circles.filter.CircleFilterAccountDataManager
import org.futo.circles.core.feature.picker.gallery.PickGalleryMediaDialogFragment.Companion.IS_VIDEO_AVAILABLE
import org.futo.circles.core.feature.timeline.BaseTimelineViewModel
import org.futo.circles.core.feature.timeline.data_source.SingleTimelineDataSource
import org.futo.circles.core.model.GalleryContentListItem
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContentType
import javax.inject.Inject

@HiltViewModel
class PickMediaItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context,
    timelineDataSource: SingleTimelineDataSource,
    circleFilterAccountDataManager: CircleFilterAccountDataManager
) : BaseTimelineViewModel(
    savedStateHandle,
    context,
    timelineDataSource,
    circleFilterAccountDataManager
) {

    private val isVideoAvailable: Boolean = savedStateHandle[IS_VIDEO_AVAILABLE] ?: true

    private val selectedItemsFlow = MutableStateFlow<List<GalleryContentListItem>>(emptyList())

    val galleryItemsLiveData = combine(
        timelineDataSource.getTimelineEventFlow(),
        selectedItemsFlow
    ) { items, selectedIds ->
        items.mapNotNull { item ->
            val post = (item as? Post) ?: return@mapNotNull item
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