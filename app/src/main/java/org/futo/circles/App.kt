package org.futo.circles

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import org.futo.circles.di.applicationModules
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.NotificationUtils
import org.futo.circles.provider.MatrixSessionProvider
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

    private val notificationUtils: NotificationUtils by inject()
    private val fcmHelper: FcmHelper by inject()
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(applicationModules)
        }
        MatrixSessionProvider.initSession(applicationContext)
        EmojiManager.install(GoogleEmojiProvider())
        notificationUtils.createNotificationChannels()
        setupLifecycleObserver()
    }

    private fun setupLifecycleObserver() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                fcmHelper.onEnterForeground()
                MatrixSessionProvider.currentSession?.syncService()?.stopAnyBackgroundSync()
            }

            override fun onPause(owner: LifecycleOwner) {
                fcmHelper.onEnterBackground()
            }
        })
    }
}