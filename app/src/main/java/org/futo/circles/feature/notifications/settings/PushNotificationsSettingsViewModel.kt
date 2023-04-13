package org.futo.circles.feature.notifications.settings

import androidx.lifecycle.ViewModel
import org.futo.circles.feature.notifications.UnifiedPushHelper

class PushNotificationsSettingsViewModel(
    private val unifiedPushHelper: UnifiedPushHelper
) : ViewModel() {


    fun getAvailableDistributors() = unifiedPushHelper.getAvailableDistributorsNames()
    fun onDistributorSelected(index: Int) {

    }

    fun saveSelectedDistributor() {

    }

    fun getCurrentDistributorName() = unifiedPushHelper.getCurrentDistributorName()
}