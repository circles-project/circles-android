package org.futo.circles.gallery.feature.select

import androidx.lifecycle.MutableLiveData
import org.futo.circles.core.mapping.toSelectableRoomListItem
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.utils.getGalleries
import org.matrix.android.sdk.api.session.room.model.Membership
import javax.inject.Inject

class SelectGalleriesDataSource @Inject constructor() {
    
    val galleriesLiveData = MutableLiveData(getInitialGalleriesList())

    private fun getInitialGalleriesList(): List<SelectableRoomListItem> =
        getGalleries(membershipFilter = listOf(Membership.JOIN)).map { it.toSelectableRoomListItem() }

    fun toggleGallerySelect(gallery: SelectableRoomListItem) {
        val newList = galleriesLiveData.value?.toMutableList()?.map {
            if (it.id == gallery.id) it.copy(isSelected = !it.isSelected) else it
        }
        galleriesLiveData.postValue(newList)
    }
}