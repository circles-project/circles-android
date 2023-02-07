package org.futo.circles.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.model.GROUP_TYPE
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class HomeViewModel(
    private val pushersManager: PushersManager
) : ViewModel() {

    val notificationLiveData = SingleEventLiveData<String>()

    fun registerPushNotifications(context: Context) {
        pushersManager.registerPushNotifications(context)
    }

    fun postNotificationData(summary: RoomSummary) {
        if (summary.roomType == GROUP_TYPE) {
            if (summary.membership == Membership.JOIN) notificationLiveData.postValue(summary.roomId)
        } else {
            if (summary.membership == Membership.JOIN) {
                val circleId = summary.spaceParents?.firstOrNull()?.roomSummary?.roomId ?: return
                notificationLiveData.postValue(circleId)
            }
        }
    }
}