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
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.feature.workspace.SharedCircleDataSource
import org.futo.circles.core.model.LoadingData
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.feature.notifications.ShortcutsHandler
import org.futo.circles.gallery.feature.backup.RoomAccountDataSource
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pushersManager: PushersManager,
    private val workspaceTasksProvider: WorkspaceTasksProvider,
    private val workspaceDataSource: ConfigureWorkspaceDataSource,
    roomAccountDataSource: RoomAccountDataSource,
    shortcutsHandler: ShortcutsHandler,
    sharedCircleDataSource: SharedCircleDataSource
) : ViewModel() {

    val validateWorkspaceLoadingLiveData = SingleEventLiveData<LoadingData>()
    val validateWorkspaceResultLiveData = SingleEventLiveData<Response<Unit>>()
    val mediaBackupSettingsLiveData = roomAccountDataSource.getMediaBackupSettingsLive()

    init {
        shortcutsHandler.observeRoomsAndBuildShortcuts(viewModelScope)
        sharedCircleDataSource.observeAndAutoAcceptSharedSpaceInvites(viewModelScope)
        validateWorkspace()
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
        summary.spaceParents?.firstOrNull { it.roomSummary?.membership == Membership.JOIN }
            ?.roomSummary?.roomId

}