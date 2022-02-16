package com.futo.circles.di

import com.futo.circles.ui.log_in.data_source.LoginDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    factory { LoginDataSource() }
}