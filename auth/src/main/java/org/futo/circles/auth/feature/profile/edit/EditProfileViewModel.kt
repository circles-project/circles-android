package org.futo.circles.auth.feature.profile.edit

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.feature.profile.setup.SetupProfileDataSource
import org.futo.circles.core.base.SingleEventLiveData
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
            val result = dataSource.saveProfileData(selectedImageLiveData.value, name)
            editProfileResponseLiveData.postValue(result)
        }
    }

    fun handleProfileDataUpdate(name: String) {
        val isDataUpdated = dataSource.isNameChanged(name) ||
                selectedImageLiveData.value != null
        isProfileDataChangedLiveData.postValue(isDataUpdated)
    }

}