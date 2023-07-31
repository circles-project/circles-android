package org.futo.circles.core.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.futo.circles.core.R
import org.futo.circles.core.provider.MatrixSessionListenerProvider
import org.futo.circles.core.provider.MatrixSessionProvider
import java.io.File

object LauncherActivityUtils {

    private const val IS_CLEAR_CACHE = "is_clear_cache"

    suspend fun clearCache(context: Context) {
        withContext(Dispatchers.Main) {
            Glide.get(context).clearMemory()
            MatrixSessionProvider.currentSession?.fileService()?.clearCache()
        }
        withContext(Dispatchers.IO) {
            Glide.get(context).clearDiskCache()
            recursiveActionOnFile(context.cacheDir, ::deleteAction)
            MatrixSessionProvider.currentSession?.clearCache()
        }
    }

    private fun deleteAction(file: File): Boolean {
        if (file.exists()) return file.delete()
        return true
    }

    private fun recursiveActionOnFile(file: File, action: (file: File) -> Boolean): Boolean {
        if (file.isDirectory) {
            file.list()?.forEach {
                val result = recursiveActionOnFile(File(file, it), action)
                if (!result) return false
            }
        }
        return action.invoke(file)
    }

    fun clearSessionAndRestart(activity: Activity, launcherActivityIntent: Intent) {
        MatrixSessionProvider.clearSession()
        activity.startActivity(createRestartIntent(launcherActivityIntent))
    }

    fun stopSyncAndRestart(activity: Activity, launcherActivityIntent: Intent) {
        MatrixSessionProvider.removeListenersAndStopSync()
        activity.startActivity(createRestartIntent(launcherActivityIntent))
    }

    fun restartForClearCache(activity: Activity, launcherActivityIntent: Intent) {
        activity.startActivity(createRestartIntent(launcherActivityIntent).apply {
            putExtra(IS_CLEAR_CACHE, true)
        })
    }

    fun syncSessionIfCashWasCleared(activity: AppCompatActivity) {
        activity.lifecycleScope.launch(Dispatchers.Main) {
            val isClearCashReload = activity.intent?.getBooleanExtra(IS_CLEAR_CACHE, false) ?: false
            if (isClearCashReload) {
                delay(500L)
                MatrixSessionProvider.currentSession?.syncService()?.startSync(true)
                activity.intent?.removeExtra(IS_CLEAR_CACHE)
            }
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
                )
                    .show()
                clearSessionAndRestart(activity, launcherActivityIntent)
            }
        }
    }

}