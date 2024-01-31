package org.futo.circles

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.base.NetworkObserver
import org.futo.circles.core.provider.MatrixNotificationSetupListener
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.GuardServiceStarter
import org.futo.circles.feature.notifications.NotificationUtils
import org.futo.circles.feature.notifications.PushRuleTriggerListener
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.feature.timeline.post.emoji.RecentEmojisProvider
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

    @Inject
    lateinit var pushersManager: PushersManager

    override fun onCreate() {
        super.onCreate()
        NetworkObserver.register(applicationContext)
        CirclesAppConfig.Initializer()
            .buildConfigInfo(
                BuildConfig.APPLICATION_ID,
                BuildConfig.VERSION_NAME,
                BuildConfig.FLAVOR
            )
            .appName(getString(R.string.app_name))
            .serverDomains(
                applicationContext.resources.getStringArray(
                    if (BuildConfig.DEBUG) R.array.debug_domains
                    else R.array.release_domains
                ).toList()
            )
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
                    MainScope().launch(Dispatchers.IO) { pushersManager.unregisterUnifiedPush() }
                }
            }
        ) { RecentEmojisProvider.initWithDefaultRecentEmojis(applicationContext) }
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