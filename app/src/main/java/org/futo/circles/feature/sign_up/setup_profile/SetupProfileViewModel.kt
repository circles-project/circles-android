package org.futo.circles.feature.sign_up.setup_profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

class SetupProfileViewModel(
    private val dataSource: SetupProfileDataSource
) : ViewModel() {

    val profileImageLiveData = MutableLiveData<Uri>()
    val saveProfileResponseLiveData = SingleEventLiveData<Response<Unit?>>()

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