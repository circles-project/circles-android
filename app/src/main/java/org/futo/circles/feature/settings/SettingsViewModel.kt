package org.futo.circles.feature.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.feature.log_in.log_out.LogoutDataSource
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.utils.LauncherActivityUtils
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    private val logoutDataSource: LogoutDataSource
) : ViewModel() {

    val profileLiveData = settingsDataSource.profileLiveData
    val loadingLiveData = logoutDataSource.loadingLiveData
    val passPhraseLoadingLiveData = settingsDataSource.passPhraseLoadingLiveData
    val startReAuthEventLiveData = settingsDataSource.startReAuthEventLiveData
    val logOutLiveData = SingleEventLiveData<Response<Unit?>>()
    val deactivateLiveData = SingleEventLiveData<Response<Unit?>>()
    val navigateToMatrixChangePasswordEvent = SingleEventLiveData<Unit>()
    val changePasswordResponseLiveData = SingleEventLiveData<Response<Unit?>>()
    val clearCacheLiveData = SingleEventLiveData<Unit>()

    fun logOut() {
        launchBg {
            val result = logoutDataSource.logOut()
            logOutLiveData.postValue(result)
        }
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

    fun clearCash(context: Context) {
        launchBg { LauncherActivityUtils.clearCache(context) }
        clearCacheLiveData.postValue(Unit)
    }
}