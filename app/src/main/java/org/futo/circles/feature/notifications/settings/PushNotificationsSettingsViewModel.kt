package org.futo.circles.feature.notifications.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.feature.notifications.PushersManager
import javax.inject.Inject

@HiltViewModel
class PushNotificationsSettingsViewModel @Inject constructor(
    private val pushersManager: PushersManager
) : ViewModel() {

    val pushDistributorChangedEventLiveData = SingleEventLiveData<Unit>()

    fun getSavedDistributorIndex() =
        pushersManager.getAllDistributors().indexOf(pushersManager.getCurrentDistributor())

    fun getAvailableDistributorsNames() = pushersManager.getAvailableDistributorsNames()

    fun saveSelectedDistributor(index: Int) {
        val selectedDistributor = pushersManager.getAllDistributors().getOrNull(index) ?: return
        val currentDistributor = pushersManager.getCurrentDistributor()
        if (selectedDistributor == currentDistributor) return
        launchBg {
            pushersManager.unregisterUnifiedPush()
            pushersManager.registerPushNotifications(selectedDistributor)
            pushDistributorChangedEventLiveData.postValue(Unit)
        }
    }

    fun getCurrentDistributorName() = pushersManager.getCurrentDistributorName()
}