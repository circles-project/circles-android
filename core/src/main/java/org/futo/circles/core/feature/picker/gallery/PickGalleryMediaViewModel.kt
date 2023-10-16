package org.futo.circles.core.feature.picker.gallery

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.GalleryContentListItem
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.PickGalleryMediaResultItem
import org.futo.circles.core.utils.FileUtils
import javax.inject.Inject

@HiltViewModel
class PickGalleryMediaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val isMultiselect: Boolean =
        savedStateHandle[PickGalleryMediaDialogFragment.IS_MULTI_SELECT] ?: false

    val selectGalleryEventLiveData = SingleEventLiveData<String>()
    val selectedMediaItemsLiveData = MutableLiveData<List<GalleryContentListItem>>()
    val mediaChosenEventLiveData = SingleEventLiveData<List<PickGalleryMediaResultItem>>()

    fun onGalleryChosen(id: String) {
        selectedMediaItemsLiveData.value = emptyList()
        selectGalleryEventLiveData.value = id
    }


    fun onMediaItemClicked(
        context: Context,
        item: GalleryContentListItem,
    ) {
        if (isMultiselect) updateSelectedItems(item)
        else selectMediaForPicker(context, item.mediaContent)
    }

    private fun updateSelectedItems(item: GalleryContentListItem) {
        val list = selectedMediaItemsLiveData.value?.toMutableList() ?: mutableListOf()
        if (item.isSelected) list.removeIf { it.id == item.id }
        else list.add(item)
        selectedMediaItemsLiveData.value = list
    }

    fun picksSelectedItems(context: Context) {
        launchBg {
            val selectedMedia = selectedMediaItemsLiveData.value?.map { item ->
                async {
                    val uri =
                        FileUtils.downloadEncryptedFileToContentUri(
                            context,
                            item.mediaContent.mediaFileData
                        )
                    uri?.let {
                        PickGalleryMediaResultItem(
                            uri.toString(),
                            item.mediaContent.getMediaType().ordinal
                        )
                    }
                }
            }?.awaitAll()?.filterNotNull() ?: return@launchBg
            mediaChosenEventLiveData.postValue(selectedMedia)
        }
    }

    private fun selectMediaForPicker(
        context: Context,
        mediaContent: MediaContent
    ) = launchBg {
        val uri = FileUtils.downloadEncryptedFileToContentUri(context, mediaContent.mediaFileData)
            ?: return@launchBg
        mediaChosenEventLiveData.postValue(
            listOf(PickGalleryMediaResultItem(uri.toString(), mediaContent.getMediaType().ordinal))
        )
    }
}