package org.futo.circles.auth.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.auth.base.BaseLoginStagesDataSource
import org.futo.circles.auth.base.PasswordDataSource
import org.futo.circles.auth.feature.log_in.stages.password.DirectLoginPasswordDataSource
import org.futo.circles.auth.feature.log_in.stages.password.LoginBsSpekeDataSource
import org.futo.circles.auth.feature.log_in.stages.password.LoginPasswordDataSource
import org.futo.circles.auth.feature.sign_up.password.SignupBsSpekeDataSource
import org.futo.circles.auth.feature.sign_up.password.SignupPasswordDataSource
import org.futo.circles.auth.model.PasswordModeArg
import org.futo.circles.core.extensions.getOrThrow

@Module
@InstallIn(ViewModelComponent::class)
object AuthModule {

    @Provides
    @ViewModelScoped
    fun providePasswordDataSource(
        savedStateHandle: SavedStateHandle,
        loginStagesDataSourceFactory: BaseLoginStagesDataSource.Factory,
        loginBsSpekeStageDataSourceFactory: LoginBsSpekeDataSource.Factory,
        directLoginPasswordDataSource: DirectLoginPasswordDataSource,
        signupPasswordDataSource: SignupPasswordDataSource,
        signupBsSpekeDataSource: SignupBsSpekeDataSource
    ): PasswordDataSource = when (savedStateHandle.getOrThrow<PasswordModeArg>("mode")) {
        PasswordModeArg.LoginPasswordStage -> LoginPasswordDataSource(
            loginStagesDataSourceFactory.create(false)
        )

        PasswordModeArg.ReAuthPassword -> LoginPasswordDataSource(
            loginStagesDataSourceFactory.create(true)
        )

        PasswordModeArg.LoginBsSpekeStage -> loginBsSpekeStageDataSourceFactory.create(
            isReauth = false,
            isChangePasswordEnroll = false
        )

        PasswordModeArg.ReAuthBsSpekeLogin -> loginBsSpekeStageDataSourceFactory.create(
            isReauth = true,
            isChangePasswordEnroll = false
        )

        PasswordModeArg.ReAuthBsSpekeSignup -> loginBsSpekeStageDataSourceFactory.create(
            isReauth = true,
            isChangePasswordEnroll = true
        )

        PasswordModeArg.LoginDirect -> directLoginPasswordDataSource
        PasswordModeArg.SignupPasswordStage -> signupPasswordDataSource
        PasswordModeArg.SignupBsSpekeStage -> signupBsSpekeDataSource
    }

}