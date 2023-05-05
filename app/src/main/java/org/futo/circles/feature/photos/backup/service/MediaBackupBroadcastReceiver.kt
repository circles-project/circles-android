package org.futo.circles.feature.photos.backup.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class MediaBackupBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        context.startService(MediaBackupService.getIntent(context))
    }
}

