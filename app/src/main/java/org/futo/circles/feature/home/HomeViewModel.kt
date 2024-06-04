package org.futo.circles.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.feature.workspace.data_source.ConfigureWorkspaceDataSource
import org.futo.circles.auth.feature.workspace.data_source.WorkspaceTasksProvider
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.notifications.PushersManager
import org.futo.circles.core.feature.notifications.ShortcutsHandler
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.LauncherActivityUtils
import org.futo.circles.core.utils.getJoinedRoomById
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pushersManager: PushersManager,
    private val workspaceTasksProvider: WorkspaceTasksProvider,
    private val workspaceDataSource: ConfigureWorkspaceDataSource,
    shortcutsHandler: ShortcutsHandler
) : ViewModel() {

    val validateWorkspaceLoadingLiveData = SingleEventLiveData<LoadingData>()
    val validateWorkspaceResultLiveData = SingleEventLiveData<Response<Unit>>()
    val syncStateLiveData =
        MatrixSessionProvider.getSessionOrThrow().syncService().getSyncStateLive()

    init {
        shortcutsHandler.observeRoomsAndBuildShortcuts(viewModelScope)
        if (!LauncherActivityUtils.isReloadAfterClearCache) validateWorkspace()
        else LauncherActivityUtils.isReloadAfterClearCache = false
    }

    private fun validateWorkspace() = launchBg {
        validateWorkspaceLoadingLiveData.postValue(LoadingData(org.futo.circles.auth.R.string.validating_workspace))
        val tasks = workspaceTasksProvider.getMandatoryTasks()
        tasks.forEach { item ->
            val validationResponse = createResult { workspaceDataSource.validate(item.room) }
            (validationResponse as? Response.Error)?.let {
                validateWorkspaceLoadingLiveData.postValue(LoadingData(isLoading = false))
                validateWorkspaceResultLiveData.postValue(Response.Error(""))
                return@launchBg
            }
        }
        validateWorkspaceLoadingLiveData.postValue(LoadingData(isLoading = false))
        validateWorkspaceResultLiveData.postValue(Response.Success(Unit))
    }


    fun registerPushNotifications() {
        pushersManager.registerPushNotifications()
    }

    fun getNotificationGroupOrCircleId(summary: RoomSummary): String? {
        if (summary.membership != Membership.JOIN) return null
        return if (summary.roomType == GROUP_TYPE) summary.roomId
        else getParentSpaceIdForRoom(summary)
    }

    private fun getParentSpaceIdForRoom(summary: RoomSummary): String? =
        summary.flattenParentIds.mapNotNull { getJoinedRoomById(it)?.roomSummary() }
            .firstOrNull {
                it.spaceChildren?.map { it.childRoomId }?.contains(summary.roomId) == true
            }?.roomId
}