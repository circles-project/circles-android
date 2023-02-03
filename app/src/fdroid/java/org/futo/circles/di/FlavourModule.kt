package org.futo.circles.di

import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.GuardServiceStarter
import org.futo.circles.feature.notifications.test.task.NotificationTestsProvider
import org.futo.circles.notifications.BackgroundSyncStarter
import org.futo.circles.notifications.FdroidFcmHelper
import org.futo.circles.notifications.FdroidGuardServiceStarter
import org.futo.circles.notifications.test.FdroidNotificationTestProvider
import org.futo.circles.notifications.test.NotificationsBackgroundRestrictionsTest
import org.koin.dsl.module

val flavourModule = module {
    factory<FcmHelper> { FdroidFcmHelper(get(), get()) }
    factory { BackgroundSyncStarter(get()) }

    factory { NotificationsBackgroundRestrictionsTest(get()) }
    factory<NotificationTestsProvider> {
        FdroidNotificationTestProvider(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single<GuardServiceStarter> { FdroidGuardServiceStarter(get()) }
}