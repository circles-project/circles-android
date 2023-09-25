package org.futo.circles.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.feature.workspace.data_source.ConfigureWorkspaceDataSource
import org.futo.circles.auth.feature.workspace.data_source.WorkspaceTasksProvider
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.CIRCLE_TAG
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.feature.notifications.ShortcutsHandler
import org.futo.circles.gallery.feature.backup.RoomAccountDataSource
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pushersManager: PushersManager,
    private val workspaceTasksProvider: WorkspaceTasksProvider,
    private val workspaceDataSource: ConfigureWorkspaceDataSource,
    roomAccountDataSource: RoomAccountDataSource,
    shortcutsHandler: ShortcutsHandler
) : ViewModel() {

    val validateWorkspaceResultLiveData = SingleEventLiveData<Response<Unit>>()
    val mediaBackupSettingsLiveData = roomAccountDataSource.getMediaBackupSettingsLive()
    val inviteIntoSharedSpaceLiveData = MatrixSessionProvider.currentSession?.roomService()
        ?.getRoomSummariesLive(roomSummaryQueryParams {
            excludeType = null
            memberships = listOf(Membership.INVITE)
        })?.map { it.filter { it.roomType == RoomType.SPACE }.map { it.roomId } }

    init {
        shortcutsHandler.observeRoomsAndBuildShortcuts(viewModelScope)
        validateWorkspace()
    }

    private fun validateWorkspace() = launchBg {
        val tasks = workspaceTasksProvider.getMandatoryTasks()
        tasks.forEachIndexed { i, item ->
            when (val validationResponse =
                createResult { workspaceDataSource.validate(item.room) }) {
                is Response.Error -> {
                    validateWorkspaceResultLiveData.postValue(Response.Error(""))
                    return@launchBg
                }

                is Response.Success -> if (!validationResponse.data) {
                    validateWorkspaceResultLiveData.postValue(Response.Error(""))
                    return@launchBg
                }


            }
        }
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


    private fun getParentSpaceIdForRoom(summary: RoomSummary): String? {
        val circles = MatrixSessionProvider.currentSession?.roomService()
            ?.getRoomSummaries(roomSummaryQueryParams { excludeType = null })
            ?.filter { item -> item.hasTag(CIRCLE_TAG) } ?: emptyList()

        val parentCircle =
            circles.firstOrNull { it.spaceChildren?.firstOrNull { it.childRoomId == summary.roomId } != null }

        return parentCircle?.roomId
    }

    fun autoAcceptInviteOnKnock(roomIds: List<String>) {
        MatrixSessionProvider.currentSession?.let { session ->
            roomIds.forEach { launchBg { session.roomService().joinRoom(it) } }
        }
    }
}