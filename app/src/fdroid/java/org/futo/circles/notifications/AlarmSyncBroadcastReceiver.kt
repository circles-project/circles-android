package org.futo.circles.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import org.futo.circles.feature.notifications.CirclesSyncAndroidService
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.sync.job.SyncAndroidService

class AlarmSyncBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (MatrixSessionProvider.currentSession == null) return
        val sessionId = intent.getStringExtra(SyncAndroidService.EXTRA_SESSION_ID) ?: return
        CirclesSyncAndroidService.newPeriodicIntent(
            context = context,
            sessionId = sessionId,
            syncTimeoutSeconds = BackgroundSyncStarter.DEFAULT_SYNC_TIMEOUT_SECONDS,
            syncDelaySeconds = BackgroundSyncStarter.DEFAULT_SYNC_DELAY_SECONDS,
            isNetworkBack = false
        )
            .let {
                try {
                    ContextCompat.startForegroundService(context, it)
                } catch (ex: Throwable) {
                    scheduleAlarm(
                        context,
                        sessionId,
                        BackgroundSyncStarter.DEFAULT_SYNC_DELAY_SECONDS
                    )
                }
            }
    }

    companion object {
        private const val REQUEST_CODE = 0

        @SuppressLint("WrongConstant") // PendingIntentCompat.FLAG_IMMUTABLE is a false positive
        fun scheduleAlarm(context: Context, sessionId: String, delayInSeconds: Int) {
            val intent = Intent(context, AlarmSyncBroadcastReceiver::class.java).apply {
                putExtra(SyncAndroidService.EXTRA_SESSION_ID, sessionId)
                putExtra(SyncAndroidService.EXTRA_PERIODIC, true)
            }
            val pIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val firstMillis = System.currentTimeMillis() + delayInSeconds * 1000L
            val alarmMgr = context.getSystemService<AlarmManager>()!!
            alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, firstMillis, pIntent)
        }

        @SuppressLint("WrongConstant") // PendingIntentCompat.FLAG_IMMUTABLE is a false positive
        fun cancelAlarm(context: Context) {
            val intent = Intent(context, AlarmSyncBroadcastReceiver::class.java)
            val pIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmMgr = context.getSystemService<AlarmManager>()!!
            alarmMgr.cancel(pIntent)

            // Stop current service to restart
            CirclesSyncAndroidService.stopIntent(context).let {
                try {
                    ContextCompat.startForegroundService(context, it)
                } catch (ignore: Throwable) {
                }
            }
        }
    }
}
