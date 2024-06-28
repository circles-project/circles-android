package org.futo.circles.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.workspace.ConfigureWorkspaceDataSource
import org.futo.circles.auth.feature.workspace.WorkspaceTasksProvider
import org.futo.circles.auth.model.WorkspaceTask
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.notifications.PushersManager
import org.futo.circles.core.feature.notifications.ShortcutsHandler
import org.futo.circles.core.model.CirclesRoom
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.model.ResLoadingData
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

    val validateWorkspaceLoadingLiveData = SingleEventLiveData<ResLoadingData>()
    val validateWorkspaceResultLiveData = SingleEventLiveData<Response<Unit>>()
    val syncStateLiveData =
        MatrixSessionProvider.getSessionOrThrow().syncService().getSyncStateLive()

    init {
        shortcutsHandler.observeRoomsAndBuildShortcuts(viewModelScope)
        if (!LauncherActivityUtils.isReloadAfterClearCache) validateWorkspace()
        else LauncherActivityUtils.isReloadAfterClearCache = false
    }

    private fun validateWorkspace() = launchBg {
        validateWorkspaceLoadingLiveData.postValue(ResLoadingData(R.string.validating_workspace))
        val tasks = workspaceTasksProvider.getMandatoryTasks()
        tasks.forEach { item ->
            val validationResponse = createResult { workspaceDataSource.validate(item) }
            (validationResponse as? Response.Error)?.let {
                fixWorkspaceSetup(tasks)
                return@launchBg
            }
        }
        validateWorkspaceLoadingLiveData.postValue(ResLoadingData(isLoading = false))
        validateWorkspaceResultLiveData.postValue(Response.Success(Unit))
    }

    private suspend fun fixWorkspaceSetup(tasks: List<CirclesRoom>) {
        tasks.forEachIndexed { i, item ->
            validateWorkspaceLoadingLiveData.postValue(
                ResLoadingData(
                    messageId = R.string.initializing_your_account,
                    isLoading = true,
                    progress = i,
                    total = tasks.size
                )
            )
            val result =
                createResult { workspaceDataSource.performCreateOrFix(WorkspaceTask(item)) }
            (result as? Response.Error)?.let {
                validateWorkspaceLoadingLiveData.postValue(ResLoadingData(isLoading = false))
                validateWorkspaceResultLiveData.postValue(result)
                return@forEachIndexed
            }
        }
        validateWorkspaceLoadingLiveData.postValue(ResLoadingData(isLoading = false))
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