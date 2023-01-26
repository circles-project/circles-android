package org.futo.circles.di

import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.notifications.FdroidFcmHelper
import org.koin.dsl.module

val flavourModule = module {
    factory<FcmHelper> { FdroidFcmHelper(get(), get()) }
}