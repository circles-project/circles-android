package org.futo.circles.feature.bottom_navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class SystemNoticesCountSharedViewModel : ViewModel() {

    val systemNoticesCountLiveData = MatrixSessionProvider.currentSession?.roomService()
        ?.getRoomSummariesLive(roomSummaryQueryParams())
        ?.map { list -> list.firstOrNull { it.hasTag(SYSTEM_NOTICES_TAG) }?.notificationCount }


    companion object {
        private const val SYSTEM_NOTICES_TAG = "m.server_notice"
    }
}