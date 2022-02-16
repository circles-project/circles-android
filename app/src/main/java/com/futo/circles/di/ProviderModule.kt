package com.futo.circles.di

import com.futo.circles.provider.MatrixSessionProvider
import org.koin.dsl.module

val providerModule = module {
    single { MatrixSessionProvider(context = get()) }
}