package org.futo.circles

import android.app.Application
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import org.futo.circles.di.applicationModules
import org.futo.circles.provider.MatrixSessionProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(applicationModules)
        }
        MatrixSessionProvider.initSession(applicationContext)
        EmojiManager.install(GoogleEmojiProvider())
    }
}