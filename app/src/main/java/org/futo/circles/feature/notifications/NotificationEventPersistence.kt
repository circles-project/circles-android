package org.futo.circles.feature.notifications

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.model.NotifiableMessageEvent
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

private const val ROOMS_NOTIFICATIONS_FILE_NAME = "org.futo.notifications.cache"
private const val KEY_ALIAS_SECRET_STORAGE = "notificationMgr"

class NotificationEventPersistence @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun loadEvents(factory: (List<NotifiableMessageEvent>) -> NotificationEventQueue): NotificationEventQueue {
        try {
            val file = File(context.applicationContext.cacheDir, ROOMS_NOTIFICATIONS_FILE_NAME)
            if (file.exists()) {
                file.inputStream().use {
                    val events: ArrayList<NotifiableMessageEvent>? =
                        MatrixInstanceProvider.matrix.secureStorageService()
                            .loadSecureSecret(it, KEY_ALIAS_SECRET_STORAGE)
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
                MatrixInstanceProvider.matrix.secureStorageService()
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