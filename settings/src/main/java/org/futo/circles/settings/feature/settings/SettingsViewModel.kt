package org.futo.circles.settings.feature.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.feature.token.RefreshTokenManager
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.internal.session.media.MediaUsageInfo
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    private val refreshTokenManager: RefreshTokenManager
) : ViewModel() {

    val passPhraseLoadingLiveData = settingsDataSource.passPhraseLoadingLiveData
    val startReAuthEventLiveData = settingsDataSource.startReAuthEventLiveData
    val logOutLiveData = SingleEventLiveData<Response<Unit?>>()
    val deactivateLiveData = SingleEventLiveData<Response<Unit?>>()
    val addEmailLiveData = SingleEventLiveData<Response<Unit?>>()
    val navigateToMatrixChangePasswordEvent = SingleEventLiveData<Unit>()
    val changePasswordResponseLiveData = SingleEventLiveData<Response<Unit?>>()
    val mediaUsageInfoLiveData = SingleEventLiveData<Response<MediaUsageInfo?>>()

    fun logOut() {
        launchBg {
            MatrixSessionProvider.currentSession?.let { refreshTokenManager.cancelTokenRefreshing(it) }
            val result = createResult {
                MatrixSessionProvider.getSessionOrThrow().signOutService().signOut(true)
            }
            logOutLiveData.postValue(result)
        }
    }

    fun deactivateAccount() {
        launchBg {
            MatrixSessionProvider.currentSession?.let { refreshTokenManager.cancelTokenRefreshing(it) }
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

    fun handleChangeEmailFlow() {
        launchBg {
            val result = settingsDataSource.addEmailUIA()
            addEmailLiveData.postValue(result)
        }
    }

    private suspend fun createNewBackupInNeeded() {
        val createBackupResult = settingsDataSource.createNewBackupIfNeeded()
        changePasswordResponseLiveData.postValue(createBackupResult)
    }

    fun updateMediaUsageInfo() {
        launchBg {
            val mediaUsageInfoResult = createResult {
                MatrixSessionProvider.getSessionOrThrow().mediaService().getMediaUsage()
            }
            mediaUsageInfoLiveData.postValue(mediaUsageInfoResult)
        }
    }
}