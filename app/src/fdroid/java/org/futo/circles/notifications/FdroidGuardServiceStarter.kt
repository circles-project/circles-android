package org.futo.circles.notifications

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.feature.notifications.GuardServiceStarter
import org.matrix.android.sdk.api.extensions.tryOrNull
import javax.inject.Inject

class FdroidGuardServiceStarter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesProvider: PreferencesProvider
) : GuardServiceStarter {

    override fun start() {
        if (!preferencesProvider.isFdroidBackgroundSyncEnabled()) return
        tryOrNull {
            val intent = Intent(context, GuardAndroidService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    override fun stop() {
        val intent = Intent(context, GuardAndroidService::class.java)
        context.stopService(intent)
    }
}
