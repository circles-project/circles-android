package org.futo.circles.feature.settings.profile.share

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.utils.getSharedCirclesSpaceId
import javax.inject.Inject

@HiltViewModel
class ShareProfileViewModel @Inject constructor() : ViewModel() {

    val qrProfileLiveData = MutableLiveData<String?>(getSharedCirclesSpaceId())
}