package org.futo.circles.feature.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.futo.circles.core.ROOM_BACKUP_DATE_ADDED_EVENT_TYPE
import org.futo.circles.core.ROOM_BACKUP_EVENT_TYPE
import org.futo.circles.core.utils.getPhotosSpaceId
import org.futo.circles.extensions.createResult
import org.futo.circles.model.MediaBackupSettingsData
import org.futo.circles.model.toMediaBackupSettingsData
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.accountdata.RoomAccountDataEvent
import org.matrix.android.sdk.api.util.Optional

class RoomAccountDataSource {

    fun getMediaBackupSettingsLive() =
        getAccountDataEventLive(getPhotosSpaceId() ?: "", ROOM_BACKUP_EVENT_TYPE)?.map {
            it.getOrNull()?.content.toMediaBackupSettingsData()
        }

    fun getMediaBackupSettings() =
        getAccountDataEvent(getPhotosSpaceId() ?: "", ROOM_BACKUP_EVENT_TYPE)?.content
            .toMediaBackupSettingsData()

    suspend fun saveMediaBackupSettings(data: MediaBackupSettingsData) = updateRoomAccountData(
        getPhotosSpaceId() ?: "", ROOM_BACKUP_EVENT_TYPE, data.toMap()
    )

    fun getMediaBackupDateAdded(roomId: String) =
        getAccountDataEvent(
            roomId,
            ROOM_BACKUP_DATE_ADDED_EVENT_TYPE
        )?.content?.get(dateAddedKey) as? Long

    suspend fun saveMediaBackupDateAdded(roomId: String, dateAdded: Long) = updateRoomAccountData(
        roomId, ROOM_BACKUP_DATE_ADDED_EVENT_TYPE, mapOf(dateAddedKey to dateAdded)
    )

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
        private const val dateAddedKey = "date_added"
    }

}