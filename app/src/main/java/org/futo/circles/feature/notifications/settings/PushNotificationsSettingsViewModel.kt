package org.futo.circles.feature.notifications.settings

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.notifications.PushersManager

class PushNotificationsSettingsViewModel(
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
            pushersManager.registerUnifiedPush(selectedDistributor)
            pushDistributorChangedEventLiveData.postValue(Unit)
        }
    }

    fun getCurrentDistributorName() = pushersManager.getCurrentDistributorName()
}