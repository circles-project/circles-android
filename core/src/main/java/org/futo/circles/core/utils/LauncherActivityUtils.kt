package org.futo.circles.core.utils

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.futo.circles.core.R
import org.futo.circles.core.provider.MatrixSessionListenerProvider
import org.futo.circles.core.provider.MatrixSessionProvider

object LauncherActivityUtils {

    var isReloadAfterClearCache = false
    private const val IS_CLEAR_CACHE = "is_clear_cache"

    fun clearCacheAndRestart(activity: AppCompatActivity) {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            MatrixSessionProvider.currentSession?.clearCache()
            activity.startActivity(createRestartIntent(activity.intent).apply {
                putExtra(IS_CLEAR_CACHE, true)
            })
        }
    }

    fun clearSessionAndRestart(activity: Activity, launcherActivityIntent: Intent) {
        MatrixSessionProvider.clearSession()
        activity.startActivity(createRestartIntent(launcherActivityIntent))
    }

    fun stopSyncAndRestart(activity: Activity, launcherActivityIntent: Intent) {
        MatrixSessionProvider.removeListenersAndStopSync()
        activity.startActivity(createRestartIntent(launcherActivityIntent))
    }

    fun syncSessionIfCashWasCleared(activity: AppCompatActivity) {
        val isClearCashReload = activity.intent?.getBooleanExtra(IS_CLEAR_CACHE, false) ?: false
        isReloadAfterClearCache = isClearCashReload
        if (isClearCashReload) {
            MatrixSessionProvider.currentSession?.syncService()?.startSync(true)
            activity.intent?.removeExtra(IS_CLEAR_CACHE)
        }
    }


    private fun createRestartIntent(intent: Intent): Intent {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        return intent
    }

    fun setInvalidTokenListener(activity: Activity, launcherActivityIntent: Intent) {
        MatrixSessionListenerProvider.setOnInvalidTokenListener {
            activity.runOnUiThread {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.you_are_signed_out),
                    Toast.LENGTH_LONG
                ).show()
                clearSessionAndRestart(activity, launcherActivityIntent)
            }
        }
    }

}