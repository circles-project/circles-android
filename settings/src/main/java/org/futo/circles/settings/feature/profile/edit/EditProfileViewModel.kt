package org.futo.circles.settings.feature.profile.edit

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.feature.setup.profile.SetupProfileDataSource
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.user.model.User
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val dataSource: SetupProfileDataSource
) : ViewModel() {

    val selectedImageLiveData = MutableLiveData<Uri>()
    val editProfileResponseLiveData = SingleEventLiveData<Response<Unit?>>()
    val isProfileDataChangedLiveData = MutableLiveData(false)
    val removeEmailResultLiveData = SingleEventLiveData<Response<Unit>>()
    val startReAuthEventLiveData = dataSource.startReAuthEventLiveData
    val addEmailLiveData = SingleEventLiveData<Response<Unit?>>()
    val profileLiveData = SingleEventLiveData<User?>().apply {
        postValue(dataSource.getUserData())
    }
    val emailsLiveData =
        MatrixSessionProvider.getSessionOrThrow().profileService().getThreePidsLive(true)

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

    fun handleAddEmailFlow() {
        launchBg {
            val result = dataSource.addEmailUIA()
            addEmailLiveData.postValue(result)
        }
    }

    fun removeEmail(email: String) {
        launchBg {
            val result = dataSource.deleteEmailUIA(email)
            removeEmailResultLiveData.postValue(result)
        }
    }

    fun refreshEmails() {
        MatrixSessionProvider.getSessionOrThrow().profileService().refreshThreePids()
    }

}