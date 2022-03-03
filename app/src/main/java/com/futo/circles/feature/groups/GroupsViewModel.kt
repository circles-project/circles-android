package com.futo.circles.feature.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.futo.circles.extensions.toGroupsList
import com.futo.circles.provider.MatrixSessionProvider
import com.futo.circles.utils.GROUP_TAG
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams


class GroupsViewModel : ViewModel() {

    val groupsLiveData =
        MatrixSessionProvider.currentSession?.getRoomSummariesLive(roomSummaryQueryParams())
            ?.map { list -> list.toGroupsList(GROUP_TAG) }

}
