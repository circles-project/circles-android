package org.futo.circles.feature.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.content.getSystemService
import androidx.work.*
import org.futo.circles.R
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.session.sync.job.SyncAndroidService

class CirclesSyncAndroidService : SyncAndroidService() {

    companion object {

        const val DEFAULT_SYNC_DELAY_SECONDS = 60
        const val DEFAULT_SYNC_TIMEOUT_SECONDS = 6

        fun newOneShotIntent(
            context: Context,
            sessionId: String
        ): Intent {
            return Intent(context, CirclesSyncAndroidService::class.java).also {
                it.putExtra(EXTRA_SESSION_ID, sessionId)
                it.putExtra(EXTRA_TIMEOUT_SECONDS, 0)
                it.putExtra(EXTRA_PERIODIC, false)
            }
        }

        fun newPeriodicIntent(
            context: Context,
            sessionId: String,
            syncTimeoutSeconds: Int,
            syncDelaySeconds: Int,
            isNetworkBack: Boolean
        ): Intent {
            return Intent(context, CirclesSyncAndroidService::class.java).also {
                it.putExtra(EXTRA_SESSION_ID, sessionId)
                it.putExtra(EXTRA_TIMEOUT_SECONDS, syncTimeoutSeconds)
                it.putExtra(EXTRA_PERIODIC, true)
                it.putExtra(EXTRA_DELAY_SECONDS, syncDelaySeconds)
                it.putExtra(EXTRA_NETWORK_BACK_RESTART, isNetworkBack)
            }
        }

        fun stopIntent(context: Context): Intent {
            return Intent(context, CirclesSyncAndroidService::class.java).also {
                it.action = ACTION_STOP
            }
        }
    }

    private val notificationUtils: NotificationUtils by lazy { NotificationUtils(applicationContext) }

    override fun provideMatrix() = MatrixInstanceProvider.matrix

    override fun getDefaultSyncDelaySeconds() = DEFAULT_SYNC_DELAY_SECONDS

    override fun getDefaultSyncTimeoutSeconds() = DEFAULT_SYNC_TIMEOUT_SECONDS

    override fun onStart(isInitialSync: Boolean) {
        val notificationSubtitleRes = if (isInitialSync) {
            R.string.notification_initial_sync
        } else {
            R.string.notification_listening_for_notifications
        }
        val notification =
            notificationUtils.buildForegroundServiceNotification(notificationSubtitleRes, false)
        startForegroundCompat(NotificationUtils.NOTIFICATION_ID_FOREGROUND_SERVICE, notification)
    }

    override fun onRescheduleAsked(
        sessionId: String,
        syncTimeoutSeconds: Int,
        syncDelaySeconds: Int
    ) {
        rescheduleSyncService(
            sessionId = sessionId,
            syncTimeoutSeconds = syncTimeoutSeconds,
            syncDelaySeconds = syncDelaySeconds,
            isPeriodic = true,
            isNetworkBack = false,
            currentTimeMillis = System.currentTimeMillis()
        )
    }

    override fun onNetworkError(
        sessionId: String,
        syncTimeoutSeconds: Int,
        syncDelaySeconds: Int,
        isPeriodic: Boolean
    ) {
        val rescheduleSyncWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<RestartWhenNetworkOn>()
                .setInputData(
                    RestartWhenNetworkOn.createInputData(
                        sessionId,
                        syncTimeoutSeconds,
                        syncDelaySeconds,
                        isPeriodic
                    )
                )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

        WorkManager
            .getInstance(applicationContext)
            .enqueue(rescheduleSyncWorkRequest)
    }

    override fun onDestroy() {
        removeForegroundNotification()
        super.onDestroy()
    }

    private fun removeForegroundNotification() {
        val notificationManager = getSystemService<NotificationManager>()!!
        notificationManager.cancel(NotificationUtils.NOTIFICATION_ID_FOREGROUND_SERVICE)
    }

    // I do not move or rename this class, since I'm not sure about the side effect regarding the WorkManager
    class RestartWhenNetworkOn(
        appContext: Context,
        workerParams: WorkerParameters
    ) : Worker(appContext, workerParams) {

        override fun doWork(): Result {
            val sessionId = inputData.getString(KEY_SESSION_ID) ?: return Result.failure()
            val syncTimeoutSeconds = inputData.getInt(
                KEY_SYNC_TIMEOUT_SECONDS,
                DEFAULT_SYNC_TIMEOUT_SECONDS
            )
            val syncDelaySeconds = inputData.getInt(
                KEY_SYNC_DELAY_SECONDS,
                DEFAULT_SYNC_DELAY_SECONDS
            )
            val isPeriodic = inputData.getBoolean(KEY_IS_PERIODIC, false)

            applicationContext.rescheduleSyncService(
                sessionId = sessionId,
                syncTimeoutSeconds = syncTimeoutSeconds,
                syncDelaySeconds = syncDelaySeconds,
                isPeriodic = isPeriodic,
                isNetworkBack = true,
                currentTimeMillis = System.currentTimeMillis()
            )
            // Indicate whether the work finished successfully with the Result
            return Result.success()
        }

        companion object {
            fun createInputData(
                sessionId: String,
                syncTimeoutSeconds: Int,
                syncDelaySeconds: Int,
                isPeriodic: Boolean
            ): Data {
                return Data.Builder()
                    .putString(KEY_SESSION_ID, sessionId)
                    .putInt(KEY_SYNC_TIMEOUT_SECONDS, syncTimeoutSeconds)
                    .putInt(KEY_SYNC_DELAY_SECONDS, syncDelaySeconds)
                    .putBoolean(KEY_IS_PERIODIC, isPeriodic)
                    .build()
            }

            private const val KEY_SESSION_ID = "sessionId"
            private const val KEY_SYNC_TIMEOUT_SECONDS = "timeout"
            private const val KEY_SYNC_DELAY_SECONDS = "delay"
            private const val KEY_IS_PERIODIC = "isPeriodic"
        }
    }
}

private fun Context.rescheduleSyncService(
    sessionId: String,
    syncTimeoutSeconds: Int,
    syncDelaySeconds: Int,
    isPeriodic: Boolean,
    isNetworkBack: Boolean,
    currentTimeMillis: Long
) {
    val intent = if (isPeriodic) {
        CirclesSyncAndroidService.newPeriodicIntent(
            context = this,
            sessionId = sessionId,
            syncTimeoutSeconds = syncTimeoutSeconds,
            syncDelaySeconds = syncDelaySeconds,
            isNetworkBack = isNetworkBack
        )
    } else {
        CirclesSyncAndroidService.newOneShotIntent(
            context = this,
            sessionId = sessionId
        )
    }

    if (isNetworkBack || syncDelaySeconds == 0) {
        // Do not wait, do the sync now (more reactivity if network back is due to user action)
        startService(intent)
    } else {
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        val firstMillis = currentTimeMillis + syncDelaySeconds * 1000L
        val alarmMgr = getSystemService<AlarmManager>()!!
        alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, firstMillis, pendingIntent)
    }
}

fun Service.startForegroundCompat(
    id: Int,
    notification: Notification,
    provideForegroundServiceType: (() -> Int)? = null
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        startForeground(
            id,
            notification,
            provideForegroundServiceType?.invoke() ?: ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
        )
    } else {
        startForeground(id, notification)
    }
}
