package com.futo.circles.di

import com.futo.circles.ui.sign_in.LogInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val uiModule = module {
    viewModel {
        LogInViewModel()
    }
}

val applicationModules = listOf(uiModule)