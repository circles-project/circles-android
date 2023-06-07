package org.futo.circles.auth.feature.sign_up.setup_profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class SetupProfileViewModel @Inject constructor(
    private val dataSource: SetupProfileDataSource
) : ViewModel() {

    val profileImageLiveData = MutableLiveData<Uri>()
    val saveProfileResponseLiveData = org.futo.circles.core.SingleEventLiveData<Response<Unit?>>()

    fun setProfileImageUri(uri: Uri) {
        profileImageLiveData.value = uri
    }


    fun saveProfileInfo(displayName: String?) {
        launchBg {
            saveProfileResponseLiveData.postValue(
                dataSource.saveProfileData(profileImageLiveData.value, displayName)
            )
        }
    }

    fun isProfileImageChosen(): Boolean = profileImageLiveData.value != null
}