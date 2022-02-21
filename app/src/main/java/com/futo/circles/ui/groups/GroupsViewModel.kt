package com.futo.circles.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.futo.circles.extensions.containsTag
import com.futo.circles.provider.MatrixSessionProvider
import com.futo.circles.utils.GROUP_TAG
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams


class GroupsViewModel(
    matrixSessionProvider: MatrixSessionProvider
) : ViewModel() {

    val groupsLiveData =
        matrixSessionProvider.currentSession?.getRoomSummariesLive(roomSummaryQueryParams())
            ?.map { list -> list.containsTag(GROUP_TAG) }
}
