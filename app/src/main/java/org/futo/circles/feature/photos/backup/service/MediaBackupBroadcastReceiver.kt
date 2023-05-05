package org.futo.circles.feature.photos.backup.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class MediaBackupBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val serviceIntent = Intent(context, MediaBackupService::class.java)
        context.startService(serviceIntent)
    }
}

