package org.futo.circles.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.PushersManager

class HomeViewModel(
    private val pushersManager: PushersManager
) : ViewModel() {

    fun registerPushNotifications(context: Context) {
        pushersManager.registerPushNotifications(context)
    }
}