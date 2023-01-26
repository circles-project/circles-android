package org.futo.circles.di.data_source

import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.feature.notifications.UnifiedPushHelper
import org.koin.dsl.module

val notificationsDsModule = module {
    factory { PushersManager(get(), get()) }
    factory { UnifiedPushHelper(get(), get(), get()) }

}