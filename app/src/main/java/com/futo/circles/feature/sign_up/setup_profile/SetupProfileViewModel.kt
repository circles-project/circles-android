package com.futo.circles.feature.sign_up.setup_profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.sign_up.setup_profile.data_source.SetupProfileDataSource

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