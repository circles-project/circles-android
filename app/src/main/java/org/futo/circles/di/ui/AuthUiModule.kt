package org.futo.circles.di.ui

import org.futo.circles.feature.home.HomeViewModel
import org.futo.circles.feature.log_in.LogInViewModel
import org.futo.circles.feature.log_in.stages.LoginStagesViewModel
import org.futo.circles.feature.log_in.stages.password.DirectLoginPasswordDataSource
import org.futo.circles.feature.log_in.stages.password.LoginBsSpekeDataSource
import org.futo.circles.feature.log_in.stages.password.LoginPasswordDataSource
import org.futo.circles.feature.log_in.stages.terms.LoginAcceptTermsDataSource
import org.futo.circles.feature.reauth.ReAuthStageViewModel
import org.futo.circles.feature.sign_up.SignUpViewModel
import org.futo.circles.feature.sign_up.password.PasswordViewModel
import org.futo.circles.feature.sign_up.password.SignupBsSpekeDataSource
import org.futo.circles.feature.sign_up.password.SignupPasswordDataSource
import org.futo.circles.feature.sign_up.setup_circles.SetupCirclesViewModel
import org.futo.circles.feature.sign_up.setup_profile.SetupProfileViewModel
import org.futo.circles.feature.sign_up.sign_up_type.SelectSignUpTypeViewModel
import org.futo.circles.feature.sign_up.subscription_stage.SubscriptionStageViewModel
import org.futo.circles.feature.sign_up.terms.AcceptTermsViewModel
import org.futo.circles.feature.sign_up.terms.SignupAcceptTermsDataSource
import org.futo.circles.feature.sign_up.validate_email.ValidateEmailViewModel
import org.futo.circles.feature.sign_up.validate_token.ValidateTokenViewModel
import org.futo.circles.feature.timeline.poll.CreatePollViewModel
import org.futo.circles.feature.timeline.post.create.CreatePostViewModel
import org.futo.circles.feature.timeline.post.info.PostInfoViewModel
import org.futo.circles.model.PasswordModeArg
import org.futo.circles.model.TermsModeArg
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val authUiModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { LogInViewModel(get(), get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { ValidateTokenViewModel(get()) }
    viewModel { SelectSignUpTypeViewModel(get()) }
    viewModel { (mode: TermsModeArg) ->
        AcceptTermsViewModel(
            when (mode) {
                TermsModeArg.Login -> get<LoginAcceptTermsDataSource> { parametersOf(false) }
                TermsModeArg.Signup -> get<SignupAcceptTermsDataSource>()
                TermsModeArg.ReAuth -> get<LoginAcceptTermsDataSource> { parametersOf(true) }
            }
        )
    }
    viewModel { ValidateEmailViewModel(get()) }
    viewModel { SetupProfileViewModel(get()) }
    viewModel { SetupCirclesViewModel(get(), get()) }
    viewModel { SubscriptionStageViewModel(get()) }
    viewModel { (passwordMode: PasswordModeArg) ->
        PasswordViewModel(
            when (passwordMode) {
                PasswordModeArg.LoginPasswordStage -> get<LoginPasswordDataSource> {
                    parametersOf(false)
                }
                PasswordModeArg.ReAuthPassword -> get<LoginPasswordDataSource> { parametersOf(true) }
                PasswordModeArg.LoginBsSpekeStage -> get<LoginBsSpekeDataSource> {
                    parametersOf(false, false)
                }
                PasswordModeArg.ReAuthBsSpekeLogin -> get<LoginBsSpekeDataSource> {
                    parametersOf(true, false)
                }
                PasswordModeArg.ReAuthBsSpekeSignup -> get<LoginBsSpekeDataSource> {
                    parametersOf(true, true)
                }
                PasswordModeArg.LoginDirect -> get<DirectLoginPasswordDataSource>()
                PasswordModeArg.SignupPasswordStage -> get<SignupPasswordDataSource>()
                PasswordModeArg.SignupBsSpekeStage -> get<SignupBsSpekeDataSource>()
            }
        )
    }
    viewModel { LoginStagesViewModel(get()) }
    viewModel { ReAuthStageViewModel(get()) }
    viewModel { (roomId: String, eventId: String?, isEdit: Boolean) ->
        CreatePostViewModel(roomId, eventId, isEdit)
    }
    viewModel { (roomId: String, eventId: String?) -> CreatePollViewModel(roomId, eventId) }
    viewModel { (roomId: String, eventId: String) -> PostInfoViewModel(roomId, eventId) }
}
