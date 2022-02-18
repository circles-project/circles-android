package com.futo.circles.ui.groups

import androidx.lifecycle.ViewModel
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.group.groupSummaryQueryParams

class GroupsViewModel(
    matrixSessionProvider: MatrixSessionProvider
) : ViewModel() {

    val groupsLiveData =
        matrixSessionProvider.currentSession?.getGroupSummariesLive(groupSummaryQueryParams())

}