package org.futo.circles.core.feature.picker.gallery.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.mapping.toJoinedGalleryListItem
import org.futo.circles.core.utils.getGalleriesLiveData
import org.matrix.android.sdk.api.session.room.model.Membership
import javax.inject.Inject

@HiltViewModel
class PickGalleryViewModel @Inject constructor() : ViewModel() {

    val galleriesLiveData = getGalleriesLiveData(membershipFilter = listOf(Membership.JOIN))
        .map { galleries -> galleries.map { it.toJoinedGalleryListItem() } }

}