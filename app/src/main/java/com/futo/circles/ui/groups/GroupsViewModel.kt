package com.futo.circles.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.futo.circles.extensions.toGroupsList
import com.futo.circles.provider.MatrixSessionProvider
import com.futo.circles.utils.GROUP_TAG
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams


class GroupsViewModel(
    private val matrixSessionProvider: MatrixSessionProvider
) : ViewModel() {

    val groupsLiveData =
        matrixSessionProvider.currentSession?.getRoomSummariesLive(roomSummaryQueryParams())
            ?.map { list -> list.toGroupsList(GROUP_TAG) }

    fun getContentResolver() = matrixSessionProvider.currentSession?.contentUrlResolver()
}
