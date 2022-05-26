package com.futo.circles.feature.settings.active_sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class ActiveSessionsViewModel(
    private val dataSource: ActiveSessionsDataSource
) : ViewModel() {

    val activeSessionsLiveData = dataSource.getActiveSessionsFlow().asLiveData()

    fun onSessionClicked(deviceId: String) {
        dataSource.toggleOptionsVisibilityFor(deviceId)
    }

    fun verifySession(deviceId: String) {
        dataSource.verifyDevice(deviceId)
    }

    fun removeSession(deviceId: String) {

    }
}