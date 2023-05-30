package org.futo.circles

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import org.futo.circles.core.provider.MatrixNotificationSetupListener
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.di.applicationModules
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.GuardServiceStarter
import org.futo.circles.feature.notifications.NotificationUtils
import org.futo.circles.feature.notifications.PushRuleTriggerListener
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.matrix.android.sdk.api.session.Session
import timber.log.Timber


class App : Application() {

    private val notificationUtils: NotificationUtils by inject()
    private val fcmHelper: FcmHelper by inject()
    private val guardServiceStarter: GuardServiceStarter by inject()
    private val pushRuleTriggerListener: PushRuleTriggerListener by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(applicationModules)
        }
        MatrixSessionProvider.initSession(
            applicationContext,
            object : MatrixNotificationSetupListener {
                override fun onStartWithSession(session: Session) {
                    pushRuleTriggerListener.startWithSession(session)
                    guardServiceStarter.start()
                }

                override fun onStop() {
                    pushRuleTriggerListener.stop()
                    guardServiceStarter.stop()
                }
            })
        EmojiManager.install(GoogleEmojiProvider())
        notificationUtils.createNotificationChannels()
        setupLifecycleObserver()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
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