package org.futo.circles.di.data_source

import org.futo.circles.auth.feature.cross_signing.CrossSigningDataSource
import org.futo.circles.auth.feature.log_in.LoginDataSource
import org.futo.circles.auth.feature.log_in.stages.LoginStagesDataSource
import org.futo.circles.auth.feature.log_in.stages.password.DirectLoginPasswordDataSource
import org.futo.circles.auth.feature.log_in.stages.password.LoginBsSpekeDataSource
import org.futo.circles.auth.feature.log_in.stages.password.LoginPasswordDataSource
import org.futo.circles.auth.feature.log_in.stages.terms.LoginAcceptTermsDataSource
import org.futo.circles.auth.feature.reauth.AuthConfirmationProvider
import org.futo.circles.auth.feature.reauth.ReAuthStagesDataSource
import org.futo.circles.auth.feature.sign_up.SignUpDataSource
import org.futo.circles.auth.feature.sign_up.password.SignupBsSpekeDataSource
import org.futo.circles.auth.feature.sign_up.password.SignupPasswordDataSource
import org.futo.circles.auth.feature.sign_up.setup_profile.SetupProfileDataSource
import org.futo.circles.auth.feature.sign_up.sign_up_type.SelectSignUpTypeDataSource
import org.futo.circles.auth.feature.sign_up.subscription_stage.SubscriptionStageDataSource
import org.futo.circles.auth.feature.sign_up.terms.SignupAcceptTermsDataSource
import org.futo.circles.auth.feature.sign_up.validate_email.ValidateEmailDataSource
import org.futo.circles.auth.feature.sign_up.validate_token.ValidateTokenDataSource
import org.futo.circles.feature.circles.setup.SetupCirclesDataSource
import org.koin.dsl.module

val authDsModule = module {
    factory { LoginDataSource(get(), get()) }
    single { SignUpDataSource(get(), get(), get()) }
    single { LoginStagesDataSource(get(), get(), get()) }
    single { ReAuthStagesDataSource(get()) }
    factory { ValidateTokenDataSource(get()) }
    factory { SelectSignUpTypeDataSource(get(), get()) }
    factory { SignupAcceptTermsDataSource(get()) }
    factory { (isReAuth: Boolean) ->
        if (isReAuth) LoginAcceptTermsDataSource(get<ReAuthStagesDataSource>())
        else LoginAcceptTermsDataSource(get<LoginStagesDataSource>())
    }
    factory { ValidateEmailDataSource(get()) }
    factory { SetupProfileDataSource(get()) }
    factory { SetupCirclesDataSource(get()) }
    factory { AuthConfirmationProvider(get()) }
    factory { SubscriptionStageDataSource(get()) }
    factory { DirectLoginPasswordDataSource(get(), get()) }
    factory { (isReAuth: Boolean) ->
        if (isReAuth) LoginPasswordDataSource(get<ReAuthStagesDataSource>())
        else LoginPasswordDataSource(get<LoginStagesDataSource>())
    }
    factory { SignupPasswordDataSource(get()) }
    factory { SignupBsSpekeDataSource(get(), get()) }
    factory { (isReAuth: Boolean, isChangePasswordEnroll: Boolean) ->
        if (isReAuth) LoginBsSpekeDataSource(
            get(), isChangePasswordEnroll, get<ReAuthStagesDataSource>()
        )
        else LoginBsSpekeDataSource(get(), false, get<LoginStagesDataSource>())
    }
    factory { CrossSigningDataSource(get()) }
}