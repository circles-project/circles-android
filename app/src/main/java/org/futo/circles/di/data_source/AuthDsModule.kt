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
    //factory { LoginDataSource(get(), get()) }
    //single { SignUpDataSource(get(), get(), get()) }
    //single { LoginStagesDataSource(get(), get(), get()) }
    //single { ReAuthStagesDataSource(get()) }
    //factory { ValidateTokenDataSource(get()) }
    //factory { SelectSignUpTypeDataSource(get(), get()) }
    //factory { SignupAcceptTermsDataSource(get()) }
    factory { (isReAuth: Boolean) ->
        if (isReAuth) LoginAcceptTermsDataSource(get<ReAuthStagesDataSource>())
        else LoginAcceptTermsDataSource(get<LoginStagesDataSource>())
    }
    // factory { ValidateEmailDataSource(get()) }
    //factory { SetupProfileDataSource(get()) }
    //factory { SetupCirclesDataSource(get()) }
    //factory { AuthConfirmationProvider(get()) }
    //factory { SubscriptionStageDataSource(get()) }
    //factory { DirectLoginPasswordDataSource(get(), get()) }
    factory { (isReAuth: Boolean) ->
        if (isReAuth) LoginPasswordDataSource(get<ReAuthStagesDataSource>())
        else LoginPasswordDataSource(get<LoginStagesDataSource>())
    }
    //factory { SignupPasswordDataSource(get()) }
    // factory { SignupBsSpekeDataSource(get(), get()) }
    factory { (isReAuth: Boolean, isChangePasswordEnroll: Boolean) ->
        if (isReAuth) LoginBsSpekeDataSource(
            get(), isChangePasswordEnroll, get<ReAuthStagesDataSource>()
        )
        else LoginBsSpekeDataSource(get(), false, get<LoginStagesDataSource>())
    }
    //factory { CrossSigningDataSource(get()) }
}