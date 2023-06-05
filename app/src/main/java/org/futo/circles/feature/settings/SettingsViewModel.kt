package org.futo.circles.feature.settings

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.provider.MatrixSessionProvider

class SettingsViewModel(
    private val settingsDataSource: SettingsDataSource
) : ViewModel() {

    val profileLiveData = settingsDataSource.profileLiveData
    val loadingLiveData = settingsDataSource.loadingLiveData
    val passPhraseLoadingLiveData = settingsDataSource.passPhraseLoadingLiveData
    val startReAuthEventLiveData = settingsDataSource.startReAuthEventLiveData
    val logOutLiveData = SingleEventLiveData<Response<Unit?>>()
    val deactivateLiveData = SingleEventLiveData<Response<Unit?>>()
    val navigateToMatrixChangePasswordEvent = SingleEventLiveData<Unit>()
    val changePasswordResponseLiveData = SingleEventLiveData<Response<Unit?>>()
    val scanProfileQrResultLiveData = SingleEventLiveData<Response<Unit?>>()
    val clearCacheLiveData = SingleEventLiveData<Unit>()

    fun logOut() {
        launchBg { logOutLiveData.postValue(settingsDataSource.logOut()) }
    }

    fun deactivateAccount() {
        launchBg {
            val deactivateResult = settingsDataSource.deactivateAccount()
            deactivateLiveData.postValue(deactivateResult)
        }
    }

    fun handleChangePasswordFlow() {
        launchBg {
            when (settingsDataSource.changePasswordUIA()) {
                is Response.Error -> navigateToMatrixChangePasswordEvent.postValue(Unit)
                is Response.Success -> createNewBackupInNeeded()
            }
        }
    }

    private suspend fun createNewBackupInNeeded() {
        val createBackupResult = settingsDataSource.createNewBackupIfNeeded()
        changePasswordResponseLiveData.postValue(createBackupResult)
    }

    fun clearCash() {
        launchBg { settingsDataSource.clearCache() }
        clearCacheLiveData.postValue(Unit)
    }

    fun onProfileQrScanned(sharedCirclesSpaceId: String) {
        launchBg {
            val result = createResult {
                MatrixSessionProvider.currentSession?.roomService()?.knock(sharedCirclesSpaceId)
            }
            scanProfileQrResultLiveData.postValue(result)
        }
    }
}