package org.futo.circles.feature.notifications

import android.content.Context
import org.futo.circles.feature.notifications.model.NotifiableEvent
import org.futo.circles.provider.MatrixInstanceProvider
import java.io.File
import java.io.FileOutputStream

private const val ROOMS_NOTIFICATIONS_FILE_NAME = "org.futo.notifications.cache"
private const val KEY_ALIAS_SECRET_STORAGE = "notificationMgr"

class NotificationEventPersistence(
    private val context: Context
) {

    private val matrix = MatrixInstanceProvider.matrix

    fun loadEvents(factory: (List<NotifiableEvent>) -> NotificationEventQueue): NotificationEventQueue {
        try {
            val file = File(context.applicationContext.cacheDir, ROOMS_NOTIFICATIONS_FILE_NAME)
            if (file.exists()) {
                file.inputStream().use {
                    val events: ArrayList<NotifiableEvent>? =
                        matrix.secureStorageService().loadSecureSecret(it, KEY_ALIAS_SECRET_STORAGE)
                    if (events != null) {
                        return factory(events)
                    }
                }
            }
        } catch (ignore: Throwable) {
        }
        return factory(emptyList())
    }

    fun persistEvents(queuedEvents: NotificationEventQueue) {
        if (queuedEvents.isEmpty()) {
            deleteCachedRoomNotifications(context)
            return
        }
        try {
            val file = File(context.applicationContext.cacheDir, ROOMS_NOTIFICATIONS_FILE_NAME)
            if (!file.exists()) file.createNewFile()
            FileOutputStream(file).use {
                matrix.secureStorageService()
                    .securelyStoreObject(queuedEvents.rawEvents(), KEY_ALIAS_SECRET_STORAGE, it)
            }
        } catch (ignore: Throwable) {
        }
    }

    private fun deleteCachedRoomNotifications(context: Context) {
        val file = File(context.applicationContext.cacheDir, ROOMS_NOTIFICATIONS_FILE_NAME)
        if (file.exists()) {
            file.delete()
        }
    }
}