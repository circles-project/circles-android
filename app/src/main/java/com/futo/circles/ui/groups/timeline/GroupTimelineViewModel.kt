package com.futo.circles.ui.groups.timeline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.extensions.nameOrId
import com.futo.circles.provider.MatrixSessionProvider

class GroupTimelineViewModel(
    private val roomId: String,
    private val matrixSessionProvider: MatrixSessionProvider
) : ViewModel() {

    val titleLiveData = MutableLiveData(getRoom()?.roomSummary()?.nameOrId() ?: roomId)

    private fun getRoom() = matrixSessionProvider.currentSession?.getRoom(roomId)

}