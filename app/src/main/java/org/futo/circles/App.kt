package org.futo.circles

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import dagger.hilt.android.HiltAndroidApp
import org.futo.circles.core.CirclesAppConfig
import org.futo.circles.core.provider.MatrixNotificationSetupListener
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.GuardServiceStarter
import org.futo.circles.feature.notifications.NotificationUtils
import org.futo.circles.feature.notifications.PushRuleTriggerListener
import org.matrix.android.sdk.api.session.Session
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var notificationUtils: NotificationUtils

    @Inject
    lateinit var fcmHelper: FcmHelper

    @Inject
    lateinit var guardServiceStarter: GuardServiceStarter

    @Inject
    lateinit var pushRuleTriggerListener: PushRuleTriggerListener

    override fun onCreate() {
        super.onCreate()
        CirclesAppConfig.Initializer()
            .buildConfigInfo(
                BuildConfig.APPLICATION_ID,
                BuildConfig.VERSION_NAME,
                BuildConfig.FLAVOR
            )
            .appName(getString(R.string.app_name))
            .euDomain(getString(if (BuildConfig.DEBUG) R.string.debug_eu_domain else R.string.release_eu_domain))
            .usDomain(getString(if (BuildConfig.DEBUG) R.string.debug_us_domain else R.string.release_us_domain))
            .init()

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