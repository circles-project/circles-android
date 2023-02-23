package org.futo.circles.feature.settings.profile.share

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.extensions.getSharedCirclesSpaceId

class ShareProfileViewModel : ViewModel() {

    val qrProfileLiveData = MutableLiveData<String?>(getSharedCirclesSpaceId())
}