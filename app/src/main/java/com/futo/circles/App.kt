package com.futo.circles

import android.app.Application
import com.futo.circles.di.applicationModules
import com.futo.circles.provider.MatrixSessionProvider
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
    }
}