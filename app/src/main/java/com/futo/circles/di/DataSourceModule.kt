package com.futo.circles.di

import com.futo.circles.ui.groups.timeline.data_source.GroupTimelineDatasource
import com.futo.circles.ui.log_in.data_source.LoginDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    factory { LoginDataSource(get(), get()) }

    factory { (roomId: String) -> GroupTimelineDatasource(roomId, get()) }
}