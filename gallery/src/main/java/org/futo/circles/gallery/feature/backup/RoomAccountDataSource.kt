package org.futo.circles.gallery.feature.backup

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.model.PHOTOS_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.feature.workspace.SpacesTreeAccountDataSource
import org.futo.circles.gallery.model.MediaBackupSettingsData
import org.futo.circles.gallery.model.toMediaBackupSettingsData
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.accountdata.RoomAccountDataEvent
import org.matrix.android.sdk.api.util.Optional
import javax.inject.Inject

class RoomAccountDataSource @Inject constructor(
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource
) {

    private fun getPhotosSpaceId() = spacesTreeAccountDataSource.getRoomIdByKey(
        PHOTOS_SPACE_ACCOUNT_DATA_KEY
    ) ?: ""

    fun getMediaBackupSettingsLive() =
        getAccountDataEventLive(getPhotosSpaceId(), ROOM_BACKUP_EVENT_TYPE)?.map {
            it.getOrNull()?.content.toMediaBackupSettingsData()
        }

    fun getMediaBackupSettings() =
        getAccountDataEvent(getPhotosSpaceId(), ROOM_BACKUP_EVENT_TYPE)?.content
            .toMediaBackupSettingsData()

    suspend fun saveMediaBackupSettings(data: MediaBackupSettingsData) = updateRoomAccountData(
        getPhotosSpaceId(), ROOM_BACKUP_EVENT_TYPE, data.toMap()
    )

    fun getMediaBackupDateModified(roomId: String) =
        (getAccountDataEvent(
            roomId,
            ROOM_BACKUP_DATE_MODIFIED_EVENT_TYPE
        )?.content?.get(dateModifiedKey) as? Double)?.toLong()

    suspend fun saveMediaBackupDateModified(roomId: String, dateModified: Long) {
        val savedModifiedDate = getMediaBackupDateModified(roomId) ?: 0L
        if (dateModified > savedModifiedDate) updateRoomAccountData(
            roomId, ROOM_BACKUP_DATE_MODIFIED_EVENT_TYPE, mapOf(dateModifiedKey to dateModified)
        )
    }

    private fun getAccountDataEventLive(
        roomId: String,
        eventType: String
    ): LiveData<Optional<RoomAccountDataEvent>>? =
        MatrixSessionProvider.currentSession?.getRoom(roomId)
            ?.roomAccountDataService()
            ?.getLiveAccountDataEvent(eventType)

    private fun getAccountDataEvent(
        roomId: String,
        eventType: String
    ): RoomAccountDataEvent? = MatrixSessionProvider.currentSession?.getRoom(roomId)
        ?.roomAccountDataService()
        ?.getAccountDataEvent(eventType)


    private suspend fun updateRoomAccountData(
        roomId: String,
        eventType: String,
        data: Map<String, Any>
    ) = createResult {
        MatrixSessionProvider.currentSession?.getRoom(roomId)
            ?.roomAccountDataService()
            ?.updateAccountData(eventType, data)
    }

    companion object {
        private const val dateModifiedKey = "date_modified"
        private const val ROOM_BACKUP_EVENT_TYPE = "m.room.media_backup"
        private const val ROOM_BACKUP_DATE_MODIFIED_EVENT_TYPE = "m.room.media_backup.date_modified"
    }

}