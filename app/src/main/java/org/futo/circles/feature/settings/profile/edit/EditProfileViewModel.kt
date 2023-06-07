package org.futo.circles.feature.settings.profile.edit

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.feature.sign_up.setup_profile.SetupProfileDataSource
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.user.model.User
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val dataSource: SetupProfileDataSource
) : ViewModel() {

    val selectedImageLiveData = MutableLiveData<Uri>()
    val editProfileResponseLiveData = SingleEventLiveData<Response<Unit?>>()
    val profileLiveData = SingleEventLiveData<User?>().apply {
        postValue(dataSource.getUserData())
    }
    val threePidLiveData = SingleEventLiveData<List<ThreePid>>().apply {
        postValue(dataSource.getThreePidData())
    }
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