package com.futo.circles.di

import com.futo.circles.ui.log_in.LogInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { LogInViewModel() }
}