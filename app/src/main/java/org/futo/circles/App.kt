package org.futo.circles

import android.app.Application
import org.futo.circles.di.applicationModules
import org.futo.circles.provider.MatrixSessionProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("bsspeke")
        startKoin {
            androidContext(this@App)
            modules(applicationModules)
        }
        MatrixSessionProvider.initSession(applicationContext)
    }
}