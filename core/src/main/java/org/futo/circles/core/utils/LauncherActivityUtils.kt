package org.futo.circles.core.utils

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.futo.circles.core.R
import org.futo.circles.core.provider.MatrixSessionListenerProvider
import org.futo.circles.core.provider.MatrixSessionProvider

object LauncherActivityUtils {

    fun clearSessionAndRestart(activity: Activity, launcherActivityIntent: Intent) {
        MatrixSessionProvider.clearSession()
        activity.startActivity(createRestartIntent(launcherActivityIntent))
    }

    fun stopSyncAndRestart(activity: Activity, launcherActivityIntent: Intent) {
        MatrixSessionProvider.removeListenersAndStopSync()
        activity.startActivity(createRestartIntent(launcherActivityIntent))
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