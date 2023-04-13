package org.futo.circles.feature.notifications.settings

import androidx.lifecycle.ViewModel
import org.futo.circles.feature.notifications.UnifiedPushHelper

class PushNotificationsSettingsViewModel(
    private val unifiedPushHelper: UnifiedPushHelper
) : ViewModel() {


    fun geAvailableDistributors() = unifiedPushHelper.getAvailableDistributorsNames()
}