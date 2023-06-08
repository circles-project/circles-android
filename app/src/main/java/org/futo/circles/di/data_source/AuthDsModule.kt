package org.futo.circles.di.data_source

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.auth.feature.log_in.stages.LoginStagesDataSource
import org.futo.circles.auth.feature.log_in.stages.password.LoginBsSpekeDataSource
import org.futo.circles.auth.feature.log_in.stages.password.LoginPasswordDataSource
import org.futo.circles.auth.feature.log_in.stages.terms.LoginAcceptTermsDataSource
import org.futo.circles.auth.feature.reauth.ReAuthStagesDataSource
import org.koin.dsl.module

val authDsModule = module {

    factory { (isReAuth: Boolean) ->
        if (isReAuth) LoginAcceptTermsDataSource(get<ReAuthStagesDataSource>())
        else LoginAcceptTermsDataSource(get<LoginStagesDataSource>())
    }

    factory { (isReAuth: Boolean) ->
        if (isReAuth) LoginPasswordDataSource(get<ReAuthStagesDataSource>())
        else LoginPasswordDataSource(get<LoginStagesDataSource>())
    }

    factory { (isReAuth: Boolean, isChangePasswordEnroll: Boolean) ->
        if (isReAuth) LoginBsSpekeDataSource(
            get(), isChangePasswordEnroll, get<ReAuthStagesDataSource>()
        )
        else LoginBsSpekeDataSource(get(), false, get<LoginStagesDataSource>())
    }

}