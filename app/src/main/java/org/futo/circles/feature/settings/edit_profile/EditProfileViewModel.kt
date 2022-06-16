package org.futo.circles.feature.settings.edit_profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.sign_up.setup_profile.SetupProfileDataSource

class EditProfileViewModel(
    private val dataSource: SetupProfileDataSource
) : ViewModel() {

    val selectedImageLiveData = MutableLiveData<Uri>()
    val editProfileResponseLiveData = SingleEventLiveData<Response<Unit?>>()
    val profileLiveData = dataSource.profileLiveData
    val threePidLiveData = dataSource.threePidLiveData
    val isProfileDataChangedLiveData = MutableLiveData(false)

    fun setImageUri(uri: Uri) {
        selectedImageLiveData.value = uri
    }

    fun update(name: String) {
        launchBg {
            editProfileResponseLiveData.postValue(
                dataSource.saveProfileData(selectedImageLiveData.value, name)
            )
        }
    }

    fun handleProfileDataUpdate(name: String) {
        val isDataUpdated = dataSource.isNameChanged(name) ||
                selectedImageLiveData.value != null
        isProfileDataChangedLiveData.postValue(isDataUpdated)
    }

}